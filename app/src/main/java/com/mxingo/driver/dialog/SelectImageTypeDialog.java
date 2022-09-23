package com.mxingo.driver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.mxingo.driver.R;

import androidx.annotation.NonNull;

/**
 * Created by zhouwei on 2017/7/13.
 */

public class SelectImageTypeDialog extends Dialog {

    private View.OnClickListener onAlbumClickListener, onCameraClickListener,onCancelClickListener;

    public SelectImageTypeDialog(@NonNull Context context) {
        super(context, R.style.dialog_match);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_image_type);

        findViewById(R.id.tv_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAlbumClickListener!=null){
                    onAlbumClickListener.onClick(v);
                    dismiss();
                }
            }
        });

        findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCameraClickListener!=null){
                    onCameraClickListener.onClick(v);
                    dismiss();
                }
            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    public void setOnAlbumClickListener(View.OnClickListener onAlbumClickListener) {
        this.onAlbumClickListener = onAlbumClickListener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
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
