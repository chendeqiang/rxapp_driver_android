package com.mxingo.driver.module.base.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    @BindView(R.id.tv_update_title)
    TextView tvUpdateTitle;
    @BindView(R.id.tv_ing)
    TextView tvIng;
    @BindView(R.id.sll_info)
    ScrollView sllInfo;
    @BindView(R.id.ll_btn_update)
    LinearLayout llBtnUpdate;
    @BindView(R.id.ll_show_progress)
    LinearLayout llShowProgress;
    @BindView(R.id.ll_info_new)
    LinearLayout llInfoNew;


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
                llInfoNew.setVisibility(View.GONE);
                llShowProgress.setVisibility(View.VISIBLE);
                new UpdateService(getApplicationContext(), versionEntity).downloadAPK();
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
}
