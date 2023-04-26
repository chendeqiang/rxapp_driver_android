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


public class PushIntentService extends GTIntentService {

    public PushIntentService() {
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    //处理透传消息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        if (msg != null && msg.getPayload() != null) {
            String data = new String(msg.getPayload());
            //LogUtils.d("onReceiveMessageData:", data);
            try {
                JSONObject dataJson = new JSONObject(data);
                int pushType = dataJson.getInt(Constants.PUSH_TYPE);
                String lastActivity = MyApplication.currActivity;
//                LogUtils.d("lastActivity", lastActivity + "," + (MainActivity.class.getName().equals(lastActivity)));
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
                        handler.sendMessage(message);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.d(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        UserInfoPreferences.getInstance().setDevtoken(clientid);
    }
    // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + "online = " + online);
    }
    // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }
    // 通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }
    // 通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {

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

