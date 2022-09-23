package com.mxingo.driver.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;


import com.mxingo.driver.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import cn.qqtheme.framework.picker.WheelPicker;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by zhouwei on 2017/7/28.
 */

public class OnePicker extends WheelPicker {
    private List<String> data = new ArrayList<>();
    private int selectedIndex = 0;
    private OnWheelListener onWheelListener;
    private OnPickListener onPickListener;

    private WheelView wheelView;

    public OnePicker(Activity activity, List<String> data) {
        super(activity);
        this.data = data;

    }

    public void setSelectedIndex(int firstIndex) {
        if (firstIndex >= 0 && firstIndex < data.size()) {
            selectedIndex = firstIndex;
        }
    }

    public String getSelectedItem() {
        if (data.size() > selectedIndex) {
            return data.get(selectedIndex);
        }
        return "";
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        wheelView = createWheelView();

        int width = DisplayUtil.getWindowWidth(activity);
        wheelView.setLayoutParams(new LinearLayout.LayoutParams(width, WRAP_CONTENT));
        layout.addView(wheelView);

        wheelView.setItems(data, selectedIndex);
        wheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectedIndex = index;
                if (onWheelListener != null) {
                    onWheelListener.onFirstWheeled(selectedIndex, data.get(selectedIndex));
                }
            }
        });

        return layout;
    }

    @Override
    public void onSubmit() {
        if (onPickListener != null) {
            onPickListener.onPicked(selectedIndex);
        }
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public void setOnPickListener(OnPickListener onPickListener) {
        this.onPickListener = onPickListener;
    }

    public void updateData(List<String> data,int selectedIndex){
        this.data = data;
        this.selectedIndex = selectedIndex;
    }

    /**
     * 数据条目滑动监听器
     */
    public interface OnWheelListener {
        void onFirstWheeled(int index, String item);
    }

    /**
     * 数据选择完成监听器
     */
    public interface OnPickListener {

        void onPicked(int selectedIndex);

    }

}
