package com.mxingo.driver.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.mxingo.driver.R;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;



public class MyTagButton extends AppCompatButton {
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
