package com.mxingo.driver.module.base.download;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mxingo.driver.R;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.widget.ShowToast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UpdateVersionActivity extends BaseActivity {

    @BindView(R.id.tv_update_content)
    TextView tvUpdateContent;
    @BindView(R.id.btn_update_id_ok)
    Button btnUpdateIdOk;
    @BindView(R.id.btn_update_id_cancel)
    Button btnUpdateIdCancel;
    private VersionEntity versionEntity;

    public static void startUpdateVersionActivity(Context context, VersionEntity versionEntity) {
        context.startActivity(new Intent(context, UpdateVersionActivity.class).putExtra(Constants.RX_VERSION, versionEntity));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_version);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("");

        versionEntity = (VersionEntity) getIntent().getSerializableExtra(Constants.RX_VERSION);
        tvUpdateContent.setText("版本:" + versionEntity.version + "\n大小:" + versionEntity.size + "\n\n" + versionEntity.log);

        btnUpdateIdOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UpdateVersionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        checkInstallPermission();
                    } else {
                        new DownloadApp(versionEntity).startDownload();
                    }

                } else {
                    ActivityCompat.requestPermissions(UpdateVersionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION_SDCARD_6_0);
                }
                if (!versionEntity.isMustUpdate) {
                    finish();
                }
            }
        });

        if (versionEntity.isMustUpdate) {
            btnUpdateIdCancel.setVisibility(View.GONE);
        }
        btnUpdateIdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!versionEntity.isMustUpdate) {
                    finish();
                }
            }
        });

    }

    /**
     * 检查8.0权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkInstallPermission() {
        boolean b = UpdateVersionActivity.this.getPackageManager().canRequestPackageInstalls();
        if (b) {
            new DownloadApp(versionEntity).startDownload();
        } else {
            callPermission();
        }
    }

    private void callPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        new DownloadApp(versionEntity).startDownload();
        return;
    }

    @Override
    public void onBackPressed() {
        if (!versionEntity.isMustUpdate) {
            super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_SDCARD_6_0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    checkInstallPermission();
                } else {
                    //有权限去下载
                    new DownloadApp(versionEntity).startDownload();
                }
            }
        } else if (requestCode == Constants.REQUEST_PERMISSION_SDCARD_8_0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new DownloadApp(versionEntity).startDownload();
            }
        }
    }
}
