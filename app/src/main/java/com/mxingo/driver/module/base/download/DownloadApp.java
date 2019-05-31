package com.mxingo.driver.module.base.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.mxingo.driver.MyApplication;
import com.mxingo.driver.R;
import com.mxingo.driver.utils.TextUtil;

import java.io.File;

/**
 * Created by zhouwei on 16/9/13.
 */
public class DownloadApp {
    private VersionEntity data;
    private BroadcastReceiver receiver;
    private File file;
    private Uri uri;

    public DownloadApp(VersionEntity data) {
        this.data = data;
        if (TextUtil.isEmpty(data.version)) {
            return;
        }
        file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + MyApplication.application.getPackageName() + "_" + data.version + ".apk");
        uri = FileProvider.getUriForFile(MyApplication.application, MyApplication.application.getPackageName() + ".fileprovider", file);
    }

    public void startDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                if (!file.isFile()) {
                Log.d("tag", "下载开始");
                DownloadManager dm = (DownloadManager) MyApplication.application.getSystemService(MyApplication.application.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(data.url));
                //设置在什么网络情况下进行下载
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                //设置通知栏标题
                request.setMimeType("application/vnd.android.package-archive");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDescription(MyApplication.application.getString(R.string.app_name));
                request.setAllowedOverRoaming(true);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, MyApplication.application.getPackageName() + "_" + data.version + ".apk");
                dm.enqueue(request);
                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        loadView();
                    }
                };
                MyApplication.application.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//                }else {
//                    loadView();
//                }
            }
        }).start();
    }

    private void loadView() {
        Intent intentDownload = new Intent(Intent.ACTION_VIEW);
//        intentDownload.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentDownload.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentDownload.addCategory(Intent.CATEGORY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentDownload.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentDownload.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intentDownload.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        //跳转到系统的安装应用页面
        MyApplication.application.startActivity(intentDownload);
    }
}
