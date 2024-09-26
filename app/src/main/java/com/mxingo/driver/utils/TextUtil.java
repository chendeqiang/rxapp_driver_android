package com.mxingo.driver.utils;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mxingo.driver.model.AddressEntity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 文本工具类
 */
public class TextUtil {

    // 两次点击按钮之间的点击间隔不能少于2000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 2000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return str.length() == 0 || str.equals("null");
    }

    /**
     * 去掉文件名称中的非法字符
     *
     * @param str
     * @return
     */
    public static String escapeFileName(String str) {
        if (str == null) {
            return null;
        }
        /** 非法字符包括：/\:*?"<>| */
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '/' || c == '\\' || c == ':' || c == '*' || c == '?' || c == '"' || c == '<' || c == '>' || c == '|') {
                continue;
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    /**
     * 从url获取当前图片的id，如果url以ignoreTag开头则直接返回该url；如果ignoreTag为空，则不会判断ignoreTag
     *
     * @param ignoreTag
     * @param url
     * @return
     */
    public static String getIdFromUrl(String url, String ignoreTag) {
        if (TextUtils.isEmpty(url) || (!TextUtils.isEmpty(ignoreTag)) && url.startsWith(ignoreTag))
            return url;
        int lastIndex = url.lastIndexOf(".jpg");
        if (lastIndex < 0)
            lastIndex = url.length() - 1;
        int beginIndex = url.lastIndexOf("/") + 1;
        int slashIndex = url.lastIndexOf("%2F") + 3;
        int finalSlashIndex = url.lastIndexOf("%252F") + 5;
        beginIndex = Math.max(Math.max(beginIndex, slashIndex), finalSlashIndex);

        return url.substring(beginIndex, lastIndex);
    }

    public static String getIdFromUrl(String url) {
        return getIdFromUrl(url, null);
    }

    public static String trim(String str) {
        if (isEmpty(str))
            return null;
        return str.trim();
    }

    /**
     * 从字符串资源文件读取字符串
     *
     * @param resId
     * @return
     */
    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static CharSequence getString(Context context, int resIdX, int resIdY) {
        return getString(context, resIdX, getString(context, resIdY));
    }

    /**
     * 从字符串资源文件读取字符串
     *
     * @param resId
     * @param formatArgs
     * @return
     */
    public static String getString(Context context, int resId, Object... formatArgs) {
        return context.getResources().getString(resId, formatArgs);
    }

    /**
     * 比较两个字符串是否相同
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean equals(String first, String second) {
        if (isEmpty(first) || isEmpty(second))
            return false;
        return first.equals(second);
    }

    /**
     * 简单判断坐标经纬度是否合法
     */
    public static boolean isCoordinateEmpty(String l) {
        if (l == null) {
            return true;
        }
        l = l.trim();
        return l.length() == 0 || l.equals("null") || l.equals("0");
    }

    /**
     * 清理密码<br>
     * 将密码字符串中的中文、空格去掉
     *
     * @param password
     * @return
     */
    public static String cleanPassword(String password) {
        if (isEmpty(password))
            return "";
        return password.replaceAll("[^\\x00-\\xff]*|\\s*", "");
    }

    /**
     * 将密码输入框中的全角字符、空格过滤掉
     *
     * @param editText
     * @param textWatcher
     */
    public static void cleanPasswordEditText(final EditText editText, final TextWatcher textWatcher) {
        Object tag = editText.getTag();
        if (tag != null) {
            int selectionTag = 0;
            try {
                selectionTag = (Integer) tag;
            } catch (ClassCastException e) {
                return;
            }
            editText.setSelection(selectionTag);
            editText.setTag(null);
            return;
        }
        String password = editText.getText().toString();
        int selection = editText.getSelectionStart();
        int preLength = password.length();
        password = TextUtil.cleanPassword(password);
        int cleanedLength = password.length();
        selection = selection - (preLength - cleanedLength);
        if (selection < 0)
            selection = 0;

        editText.setTag(selection);
        editText.setText(password);
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、185、186
		电信：133、153、180、189、（1349卫通）
		总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		*/
        if (TextUtil.isEmpty(mobiles) || mobiles.length() != 11 || !mobiles.startsWith("1")) {
            return false;
        } else {
            return true;
        }

//        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobiles)) {
//            return false;
//        } else {
//            String MOBILE = "^(1[3|4|5|7|8][0-9])\\d{8}$";
//            Pattern p = Pattern.compile(MOBILE);
//            Matcher m = p.matcher(mobiles);
//            return m.matches();
//        }
    }

    public static String getMobile(String mobile) {
        return mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
    }

    /**
     * 为EditText 设置密码过滤器
     *
     * @param editText
     */
    public static void setPasswordFilter(EditText editText) {
        InputFilter lengthfilter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return cleanPassword(source.toString());
            }
        };
        editText.setFilters(new InputFilter[]{lengthfilter});
    }

    public static int length(String phone) {
        return phone == null ? 0 : phone.length();
    }

    public static String getIdString(View v) {
        return String.valueOf(v.getId());
    }

    public static String[] getStringArray(Context context, int arrayResId) {
        return context.getResources().getStringArray(arrayResId);
    }

    public static String getFormatString(long timeInminutes) {
        Date date = new Date(timeInminutes);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd号 HH:mm");//小写的mm表示的是分钟
        return sdf.format(date);
    }

    public static String getFormatString(long timeInminutes, String farmat) {
        Date date = new Date(timeInminutes);
        SimpleDateFormat format = new SimpleDateFormat(farmat);//小写的mm表示的是分钟
        return format.format(date);
    }

    public static String getFormatWeek(long timeInminutes) {
        Date date = new Date(timeInminutes);
        SimpleDateFormat sdfMoth = new SimpleDateFormat("MM月dd号");//小写的mm表示的是分钟
        SimpleDateFormat sdDay = new SimpleDateFormat("HH:mm");//小写的mm表示的是分钟

        return sdfMoth.format(date) + " (" + getWeekDay(timeInminutes) + ") " + sdDay.format(date);
    }

    public static String getFormatWeek2(long timeInminutes) {
        Date date = new Date(timeInminutes);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
        return sdfDate.format(date);
    }

    public static String getFormatString(long time, int day, String formt) {
        SimpleDateFormat sdf = new SimpleDateFormat(formt);//小写的mm表示的是分钟
        if (day != 999) {
            return sdf.format(time) + "起 (" + getWeekDay(time) + ") 共" + (day) + "天";
        } else {
            return sdf.format(time) + "起 (" + getWeekDay(time) + ") 共半天";
        }
    }

    public static String getWeekDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        String week = "";
        int cweek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

//    public static List<AddressEntity> getCity(Activity activity) {
//        Type type = new TypeToken<List<AddressEntity>>() {
//        }.getType();
//        try {
//            return new Gson().fromJson(ConvertUtils.toString(activity.getAssets().open("city.json")), type);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return new ArrayList<AddressEntity>();
//    }

}
