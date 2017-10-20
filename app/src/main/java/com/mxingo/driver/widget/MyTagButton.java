package com.mxingo.driver.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.mxingo.driver.R;

/**
 * Created by zhouwei on 2017/7/4.
 */

public class MyTagButton extends android.support.v7.widget.AppCompatButton {
    public MyTagButton(Context context) {
        super(context);
    }

    public MyTagButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTagButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected) {

            setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_blue));
        } else {
            setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_gray));
        }
        super.setSelected(selected);
    }
}
