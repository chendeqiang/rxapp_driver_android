package com.mxingo.driver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mxingo.driver.R;
import com.mxingo.driver.utils.TextUtil;

/**
 * Created by zhouwei on 2017/7/13.
 */

public class MessageDialog extends Dialog {
    private TextView msgText;
    private Button btnCancel, btnAgain;
    private String okText;

    private String messageText;
    private View.OnClickListener onOkClickListener, onCancelClickListener;

    public MessageDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
        if (!TextUtil.isEmpty(messageText) && msgText != null)
            msgText.setText(messageText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        msgText = (TextView) findViewById(R.id.tv_update_content);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnAgain = (Button) findViewById(R.id.btn_ok);
        btnCancel.setOnClickListener(onCancelClickListener);
        btnAgain.setOnClickListener(onOkClickListener);
        if (!TextUtil.isEmpty(messageText)) {
            msgText.setText(messageText);
        }

        if (!TextUtil.isEmpty(okText)) {
            btnAgain.setText(okText);
        }
    }

    public void setOkText(String okText) {
        this.okText = okText;
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
