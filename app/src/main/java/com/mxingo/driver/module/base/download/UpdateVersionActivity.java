package com.mxingo.driver.module.base.download;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mxingo.driver.R;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.utils.Constants;

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
                if (ActivityCompat.checkSelfPermission(UpdateVersionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateVersionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.permissionMain);
                } else {
                    new DownloadApp(versionEntity).startDownload();
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

    @Override
    public void onBackPressed() {
        if (!versionEntity.isMustUpdate) {
            super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.permissionMain && grantResults[0] == 0){
            new DownloadApp(versionEntity).startDownload();
        }
    }
}
