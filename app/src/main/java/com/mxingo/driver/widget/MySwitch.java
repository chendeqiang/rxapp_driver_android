package com.mxingo.driver.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.mxingo.driver.R;
import com.mxingo.driver.utils.DisplayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhouwei on 2017/7/3.
 */

public class MySwitch extends RelativeLayout {


    @BindView(R.id.right)
    View right;
    @BindView(R.id.left)
    View left;
    @BindView(R.id.rl_bg)
    RelativeLayout rlBg;

    private int leftX;
    private int rightX;
    public boolean isSwitch = false;
    private int height;

    private SwitchResult result;

    public MySwitch(Context context) {
        super(context);
        init();
    }

    public MySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_switch, this);
        ButterKnife.bind(this);
        leftX = DisplayUtil.dip2px(getContext(), 2 + 12);
        rightX = DisplayUtil.dip2px(getContext(), 70 - 2 - 12);
        height = DisplayUtil.dip2px(getContext(), 2 + 12);
    }

    @OnClick({R.id.right, R.id.left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.right: {
                changeSwitch();
                if (result != null) {
                    result.onResult(isSwitch);
                }
                break;
            }

            case R.id.left: {
                changeSwitch();
                if (result != null) {
                    result.onResult(isSwitch);
                }
                break;
            }
        }
    }

    private void changeSwitch() {
        if (isSwitch) {
            isSwitch = false;
            left.offsetLeftAndRight(leftX - rightX);
            rlBg.setSelected(false);
        } else {
            isSwitch = true;
            left.offsetLeftAndRight(rightX - leftX);
            rlBg.setSelected(true);
        }
    }

    public void setSwitch(boolean isSwitch) {
        if (isSwitch != this.isSwitch) {
            changeSwitch();
        }
    }

    public void setResult(SwitchResult result) {
        this.result = result;
    }

    public interface SwitchResult {
        void onResult(boolean isSwitch);
    }
}

