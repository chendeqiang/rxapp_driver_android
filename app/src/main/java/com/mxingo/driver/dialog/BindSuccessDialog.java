package com.mxingo.driver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mxingo.driver.R;

import androidx.annotation.NonNull;


public class BindSuccessDialog extends Dialog {
    private Button btnOk;
    private View.OnClickListener onOkClickListener;

    public BindSuccessDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bind_success);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(onOkClickListener);
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
