package com.mxingo.driver.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.mxingo.driver.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouwei on 2017/7/4.
 */

public class OrderFooterView extends RelativeLayout {
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;

    public OrderFooterView(Context context) {
        super(context);
        init();
    }

    public OrderFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrderFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_order_footer, this);
        ButterKnife.bind(this);

    }

    public void setRefresh(boolean isRefresh) {
        if (isRefresh) {
            if (rlLoading != null) {
                rlLoading.setVisibility(VISIBLE);
            }
        } else {
            if (rlLoading != null) {
                rlLoading.setVisibility(GONE);
            }
        }
    }

    public boolean getRefresh() {
        if (rlLoading == null)
            return false;
        else {
            return rlLoading.getVisibility() == View.VISIBLE;
        }

    }
}
