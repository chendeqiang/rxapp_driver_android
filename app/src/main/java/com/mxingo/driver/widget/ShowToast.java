package com.mxingo.driver.widget;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by zhouwei on 2017/6/22.
 */

public class ShowToast {
    public static void showCenter(Context context, String rspDesc) {
        Toast toast = Toast.makeText(context, rspDesc, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showBottom(Context context, String rspDesc) {
        Toast toast = Toast.makeText(context, rspDesc, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showTop(Context context, String rspDesc) {
        Toast toast = Toast.makeText(context, rspDesc, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
