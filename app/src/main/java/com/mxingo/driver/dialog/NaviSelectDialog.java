package com.mxingo.driver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mxingo.driver.R;
import com.mxingo.driver.utils.StartUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NaviSelectDialog extends Dialog {
    @BindView(R.id.ll_baidu_map)
    LinearLayout llBaiduMap;
    @BindView(R.id.ll_gaode_map)
    LinearLayout llGaodeMap;
    private View.OnClickListener onBaiduMapClickListener, onGaodeMapClickListener;
    private Context context;


    public NaviSelectDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_navi_select);
        ButterKnife.bind(this);
        llBaiduMap.setOnClickListener(onBaiduMapClickListener);
        llGaodeMap.setOnClickListener(onGaodeMapClickListener);
    }

    public void setonBaiduMapClickListener(View.OnClickListener listener) {
        this.onBaiduMapClickListener = listener;
    }

    public void setonGaodeMapClickListener(View.OnClickListener listener) {
        this.onGaodeMapClickListener = listener;
    }

    @Override
    public void dismiss() {
        if (isShowing() && context != null) {
            super.dismiss();
        }
    }

    @Override
    public void show() {
        if (!isShowing() && context != null) {
            super.show();
            if (!StartUtil.isInstallByread(context,StartUtil.baiduMapPackage) && llBaiduMap != null) {
                llBaiduMap.setVisibility(View.GONE);
            }
            if (!StartUtil.isInstallByread(context,StartUtil.gaodeMapPackage) && llGaodeMap != null) {
                llGaodeMap.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.img_cancel)
    public void onClick() {
        dismiss();
    }
}
