package com.mxingo.driver.module.base.push;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.take.MainActivity;
import com.mxingo.driver.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by zhouwei on 2017/6/23.
 */

public class PushIntentService extends GTIntentService {

    public PushIntentService() {
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        if (msg != null && msg.getPayload() != null) {
            String data = new String(msg.getPayload());
            LogUtils.d("onReceiveMessageData:", data);
            try {
                JSONObject dataJson = new JSONObject(data);
                int pushType = dataJson.getInt(Constants.PUSH_TYPE);
                String lastActivity = MyApplication.currActivity;
                LogUtils.d("lastActivity", lastActivity + "," + (MainActivity.class.getName().equals(lastActivity)));
                if (pushType == Constants.P_D_PUB) {
                    final Intent intent = new Intent();
                    intent.setAction(Constants.GETUI_ACTION);
                    intent.putExtra(Constants.PUSH_DATA, data);
                    intent.putExtra(Constants.PUSH_TYPE, pushType);
                    if (MainActivity.class.getName().equals(lastActivity)) {
                        LogUtils.d("tag", "----------直传---------");
                        EventBus.getDefault().post(intent);
                    } else {
                        LogUtils.d("tag", "----------间传---------");
                        context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        Message message = handler.obtainMessage();
                        message.obj = intent;
                        handler.sendMessageDelayed(message, 1000);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.d(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        UserInfoPreferences.getInstance().setDevtoken(clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + "online = " + online);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.obj != null) {
                Intent intent = (Intent) msg.obj;
                EventBus.getDefault().post(intent);
            }
        }
    };
}

