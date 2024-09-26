package com.mxingo.driver.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener;
import com.github.gzuliyujiang.wheelview.widget.WheelView;
import com.github.gzuliyujiang.wheelpicker.OptionPicker;
import com.mxingo.driver.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public class OnePicker extends OptionPicker {
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

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public void setOnPickListener(OnPickListener onPickListener) {

    }

    public void updateData(List<String> data,int selectedIndex){
        this.data = data;
        this.selectedIndex = selectedIndex;
    }

    @NonNull
    @Override
    protected View createBodyView() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        wheelView = new WheelView(activity);

        int width = DisplayUtil.getWindowWidth(activity);
        wheelView.setLayoutParams(new LinearLayout.LayoutParams(width, WRAP_CONTENT));
        layout.addView(wheelView);

        wheelView.setData(data, selectedIndex);
        wheelView.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onWheelScrolled(WheelView view, int offset) {

            }

            @Override
            public void onWheelSelected(WheelView view, int position) {
                    selectedIndex =position;
                    if (onWheelListener!=null){
                        onWheelListener.onFirstWheeled(selectedIndex,data.get(selectedIndex));
                    }
            }

            @Override
            public void onWheelScrollStateChanged(WheelView view, int state) {

            }

            @Override
            public void onWheelLoopFinished(WheelView view) {

            }
        });

        return layout;
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
