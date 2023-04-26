package com.mxingo.driver.module.base.map.trace;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.mxingo.driver.R;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.log.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Created by deqiangchen on 2023/4/13.
 */
public class MyTraceService extends Service {

    private NotificationManager notificationManager;
    private String CHANNEL_ID = "renxing_noti_Trace";
    private String notificationName = "renxing_Trace";

    private static MyTraceService myTraceService;

    private Trace mTrace = null;//轨迹服务

    public LBSTraceClient mClient =null;//轨迹服务客户端

    private int serviceId = 145188;//鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID

    private int gatherInterval = 5;//采集周期（单位 : 秒）
    private int packInterval = 15;

    private OnTraceListener traceListener;
    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;

    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;

    public static final int NOTIFICATION_ID = 1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static MyTraceService getInstance(){
        if (myTraceService==null){
            myTraceService = new MyTraceService();
        }
        return myTraceService;
    }

    @Override
    public void onCreate() {
        LBSTraceClient.setAgreePrivacy(getApplicationContext(),true);
        super.onCreate();

        notificationManager = getSystemService(NotificationManager.class);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, notificationName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        try {
            mClient= new LBSTraceClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mClient.setLocationMode(LocationMode.High_Accuracy);
        mClient.setInterval(gatherInterval,packInterval);
        mTrace = new Trace(serviceId, UserInfoPreferences.getInstance().getMobile(), false);


        traceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {

            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                isTraceStarted =true;
                LogUtils.d("onStartTraceCallback", String.format("errorNo:%d, message:%s ", i, s));
                mClient.startGather(traceListener);
            }

            @Override
            public void onStopTraceCallback(int i, String s) {
                isTraceStarted =false;
                isGatherStarted =false;
                LogUtils.d("onStopTraceSuccess", String.format("errorNo:%d, message:%s ", i, s));
                mClient.stopGather(traceListener);
            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                isGatherStarted=true;
                LogUtils.d("onStartGatherCallback", String.format("errorNo:%d, message:%s ", i, s));
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                isGatherStarted=false;
                LogUtils.d("onStopGatherCallback", String.format("errorNo:%d, message:%s ", i, s));
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }

            @Override
            public void onTraceDataUploadCallBack(int i, String s, int i1, int i2) {

            }
        };

        if (isTraceStarted){
            mClient.stopTrace(mTrace,traceListener);
        }else {
            mClient.startTrace(mTrace,traceListener);
        }

        startForeground(1,initNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTrace();
        return START_STICKY;
    }

    private void startTrace() {
        if (mClient != null && mTrace != null) {
            mClient.startTrace(mTrace, traceListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mClient!=null){
            stopTrace();
        }
    }

    private void stopTrace() {
        if (mClient != null && mTrace != null) {
            mClient.stopTrace(mTrace, traceListener);
        }
    }

    private Notification initNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.push) // 设置状态栏内的小图标
                .setContentTitle("行车轨迹")
                .setContentText("鹰眼服务正在运行...") // 设置上下文内容
                .setWhen(System.currentTimeMillis())// 设置该通知发生的时间
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID,notification);
        return notification;
    }

}
