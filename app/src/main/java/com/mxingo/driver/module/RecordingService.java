package com.mxingo.driver.module;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.mxingo.driver.R;
import com.mxingo.driver.model.StsEntity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.take.MainActivity;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;


import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static org.greenrobot.eventbus.EventBus.TAG;
@SuppressWarnings("deprecation")
public class RecordingService extends Service {
    private static RecordingService recordingService;
    private NotificationManager notificationManager;
    private String CHANNEL_ID = "renxing_noti_Recorder";
    private String notificationName = "renxing_Recorder";
    public static final int NOTIFICATION_ID = 2;
    private static final String LOG_TAG = "RecordingService";
    private String mFileName;
    private String mFilePath;

    private String orderNo;

    private MediaRecorder mRecorder;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private TimerTask mIncrementTimerTask = null;

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;


    @Inject
    MyPresenter presenter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        ComponentHolder.getAppComponent().inject(this);
        presenter.register(this);
        super.onCreate();
        notificationManager = getSystemService(NotificationManager.class);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, notificationName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(2, initNotification());
    }

    private Notification initNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.push) // 设置状态栏内的小图标
                .setContentTitle("任行约车")
                .setContentText("录音服务正在运行...") // 设置上下文内容
                .setWhen(System.currentTimeMillis())// 设置该通知发生的时间
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID,notification);
        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orderNo = intent.getStringExtra("orderNo");
        startRecording();
        return START_STICKY;
    }

    public static RecordingService getInstance() {
        if (recordingService == null) {
            recordingService = new RecordingService();
        }
        return recordingService;
    }

    public void startRecording() {
        setFileNameAndPath();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);


        try {
            mRecorder.prepare();
            mRecorder.start();
            Log.i(LOG_TAG, "开始录音-------");
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed" + e.getMessage());
        }
    }

    public void setFileNameAndPath() {

        //在自身目录下创建文件夹
        File recorderFile = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        String recorderFilePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();

        File file = new File(recorderFilePath + File.separator + orderNo + ".amr");

        mFileName = orderNo + ".amr";
        mFilePath = recorderFilePath + File.separator + orderNo + ".amr";

//        int count = 0;
//        File f;
//
//        do {
//            count++;
//            //mFileName = R.string.default_file_name + "_" + System.currentTimeMillis() + ".amr";
//            mFileName = orderNo + ".amr";
//            //mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            //mFilePath += "/SoundRecorder/" + mFileName;
//            f = new File(mFilePath);
//
//        } while (f.exists() && !f.isDirectory());
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void stopRecording() {
        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        Log.i(LOG_TAG, "结束录音-------");
        //保存录音文件的信息
//        getSharedPreferences("sp_name_audio", MODE_PRIVATE)
//                .edit()
//                .putString("audio_path", mFilePath)
//                .putLong("elpased", mElapsedMillis)
//                .apply();

        new Thread(new Runnable() {
            @Override
            public void run() {
                presenter.getStsServer();

            }
        }).start();

        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;
    }


    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == StsEntity.class) {
            StsEntity stsEntity = (StsEntity) object;
            accessKeyId = stsEntity.AccessKeyId;
            accessKeySecret = stsEntity.AccessKeySecret;
            securityToken = stsEntity.SecurityToken;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    upload(accessKeyId, accessKeySecret, securityToken);
                }
            }).start();

        }
    }

    private void upload(String accessKeyId, String accessKeySecret, String securityToken) {

        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        //String stsServer = "http://118.31.16.123:7080";
        // 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        //OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);

        // 在移动端建议使用STS的方式初始化OSSClient。
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);

        ClientConfiguration conf = new ClientConfiguration();

        OSS oss = new OSSClient(this.getApplicationContext(), endpoint, credentialProvider, conf);

        PutObjectRequest put = new PutObjectRequest("sztc-audio", mFileName, mFilePath);

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.i(TAG, "onSuccess: 上传成功-------");
                //删除本地录音文件
                File file = new File(mFilePath);
                file.delete();
                System.gc();
                Log.i(LOG_TAG, "文件删除-------");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常。
                if (clientException != null) {
                    // 本地异常，如网络异常等。
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }
}
