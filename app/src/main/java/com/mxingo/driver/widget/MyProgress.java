package com.mxingo.driver.widget;

import android.app.Activity;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by zhouwei on 2017/4/27.
 */

public class MyProgress {

    private KProgressHUD hud;
    private Activity activity;

    public MyProgress(Activity activity) {
        this.activity = activity;
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍等")
                .setDetailsLabel("请求中")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void setLabel(String label) {
        hud.setLabel(label);
    }

    public void setDetailsLabel(String detailsLabel) {
        hud.setDetailsLabel(detailsLabel);
    }


    public void show() {
        if (hud != null && !hud.isShowing() && activity != null && !activity.isFinishing()) {
            hud.show();
        }

    }

    public void dismiss() {
        if (hud != null && hud.isShowing() && activity != null && !activity.isFinishing()) {
            hud.dismiss();
        }
    }
}
