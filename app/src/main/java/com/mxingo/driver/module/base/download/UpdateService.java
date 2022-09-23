package com.mxingo.driver.module.base.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.mxingo.driver.MyApplication;
import com.mxingo.driver.utils.TextUtil;

import java.io.File;

import androidx.core.content.FileProvider;


public class UpdateService {
    //下载器
    private DownloadManager downloadManager;

    //上下文
    private Context mContext;
    //下载的ID
    private long downloadId;
    private File file;
    private VersionEntity data;

    public UpdateService(Context context, VersionEntity data) {
        this.mContext = context;
        this.data = data;
        if (TextUtil.isEmpty(data.version)) {
            return;
        }
    }

    //下载apk
    public void downloadAPK() {
        //此处使用mContext.getExternalFilesDir(null)  Android Q 无法访问外路径
        file = new File(mContext.getExternalFilesDir(null), MyApplication.application.getPackageName() + "_" + data.version + ".apk");
        // file = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS,"xxx.apk");
        if (file.exists()) {
            file.delete();
        }
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(data.url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(true);

        //设置在什么网络情况下进行下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("任行新版本apk");
        request.setDescription("Apk Downloading");
        request.setVisibleInDownloadsUi(true);

        //设置下载的路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, MyApplication.application.getPackageName() + "_" + data.version + ".apk");
        //获取DownloadManager
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        downloadId = downloadManager.enqueue(request);



        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    //检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(mContext, "下载延迟", Toast.LENGTH_SHORT).show();
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(mContext, "正在下载", Toast.LENGTH_SHORT).show();
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
                    //判断系统版本，高于8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean b = mContext.getPackageManager().canRequestPackageInstalls();
                        if (b) {
                            installAPK();
                        } else {
                            Intent intent = new Intent();
                            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                            intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            installAPK();
                            return;
                        }
                    } else {
                        installAPK();
                    }
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    mContext.unregisterReceiver(receiver);
                    break;
            }
        }
        c.close();
    }

    //下载到本地后执行安装
    private void installAPK() {
        //获取下载文件的Uri
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //7.0 以上需要FileProvider进行设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, "com.mxingo.driver.fileprovider", file);
            intent.setDataAndType(apkUri, mContext.getContentResolver().getType(apkUri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
        mContext.unregisterReceiver(receiver);
    }
}
