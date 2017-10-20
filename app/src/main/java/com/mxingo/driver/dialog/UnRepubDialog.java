package com.mxingo.driver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.mxingo.driver.R;

/**
 * 作者：Created by chendeqiang on 2017/9/26
 * 邮箱：keshuixiansheng@126.com
 * 描述：
 */
public class UnRepubDialog extends Dialog {

    private Button btnYes;
    private View.OnClickListener onYesClickListener;

    public UnRepubDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_unrepub);
        btnYes = (Button) findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(onYesClickListener);
    }

    public void setOnYesClickListener(View.OnClickListener listener) {
        this.onYesClickListener = listener;
    }

    @Override
    public void dismiss() {
        if (isShowing() && getContext() != null) {
            super.dismiss();
        }
    }

    @Override
    public void show() {
        if (!isShowing() && getContext() != null) {
            super.show();
        }
    }
}
