package com.mxingo.driver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mxingo.driver.R;

import androidx.annotation.NonNull;

/**
 * Created by zhouwei on 2017/7/13.
 */

public class RepubDialog extends Dialog {
    private Button btnCancel, btnAgain;

    private View.OnClickListener onOkClickListener, onCancelClickListener;

    public RepubDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_repub);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnAgain = (Button) findViewById(R.id.btn_ok);
        btnCancel.setOnClickListener(onCancelClickListener);
        btnAgain.setOnClickListener(onOkClickListener);
    }

    public void setOnCancelClickListener(View.OnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public void setOnOkClickListener(View.OnClickListener listener) {
        this.onOkClickListener = listener;
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
