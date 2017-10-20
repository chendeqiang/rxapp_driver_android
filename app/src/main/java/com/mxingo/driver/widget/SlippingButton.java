package com.mxingo.driver.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mxingo.driver.R;
import com.mxingo.driver.utils.DisplayUtil;
import com.mxingo.driver.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouwei on 2017/7/3.
 */

public class SlippingButton extends RelativeLayout {
    @BindView(R.id.btn_slipping)
    Button btnSlipping;
    @BindView(R.id.tv_hint)
    TextView tvHint;

    private int x;
    private boolean isOver;
    private int padding;
    private Position position;

    public SlippingButton(Context context) {
        super(context);
        init();
    }

    public SlippingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlippingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlippingButton, defStyleAttr, 0);
        String hintText = a.getString(R.styleable.SlippingButton_hint);
        if (!TextUtil.isEmpty(hintText)) {
            tvHint.setText(hintText);
        }
        a.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_slipping_button, this);
        ButterKnife.bind(this);
        padding = DisplayUtil.dip2px(getContext(), 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int btnSlippingX = (int) btnSlipping.getX();
                int btnSlippingWidth = btnSlipping.getWidth() / 2;
                if (btnSlippingX - btnSlippingWidth < event.getX() + padding * 5 && btnSlippingX + btnSlippingWidth > event.getX() - padding * 5) {
                    x = (int) event.getX();
                    isOver = false;
                } else {
                    isOver = true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isOver) {
                    if (event.getX() + btnSlipping.getWidth() / 2 + padding * 5 >= getWidth()) {
                        endPosition();
                    } else if (event.getX() >= padding + btnSlipping.getWidth() / 2) {
                        btnSlipping.offsetLeftAndRight((int) (event.getX() - x));
                        x = (int) event.getX();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isOver) {
                    startPosition();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (!isOver) {
                    startPosition();
                }
                break;
            }
        }
        return true;
    }

    private void endPosition() {
        btnSlipping.offsetLeftAndRight(getWidth() - btnSlipping.getWidth() / 2 - padding);
        LayoutParams params = (LayoutParams) btnSlipping.getLayoutParams();
        params.leftMargin = getWidth() - btnSlipping.getWidth() - padding;
        btnSlipping.setLayoutParams(params);
        isOver = true;
        if (position != null) {
            position.overPosition();
        }
    }

    private void startPosition() {
        LayoutParams params = (LayoutParams) btnSlipping.getLayoutParams();
        params.leftMargin = padding;
        params.rightMargin = getWidth() - btnSlipping.getWidth() - padding;
        btnSlipping.setLayoutParams(params);
        x = 0;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public interface Position {
        void overPosition();
    }

    public void setHint(String hint) {
        if (!TextUtil.isEmpty(hint) && tvHint != null)
            this.tvHint.setText(hint);
    }
}
