package com.mxingo.driver.module.base.map.trace;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.R;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.take.MainActivity;
import com.mxingo.driver.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhouwei on 2016/11/3.
 */

public class MyTrace {
    private static MyTrace myTrace;

    /**
     * 轨迹服务
     */
    private Trace trace = null;

    /**
     * 轨迹服务客户端
     */
    private LBSTraceClient client = null;

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    private int serviceId = 145188;

    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 5;

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 15;

    private Context context;

    public static MyTrace getInstance() {
        if (myTrace == null) {
            myTrace = new MyTrace();
            myTrace.initValue(MyApplication.application);
        }
        return myTrace;
    }

    public void initValue(Context context) {
        this.context = context;
        if (client == null) {
            // 初始化轨迹服务客户端
            client = new LBSTraceClient(context);
            // 设置定位模式
            client.setLocationMode(LocationMode.High_Accuracy);
            client.setInterval(gatherInterval, packInterval);
            client.setProtocolType(ProtocolType.HTTPS);
        }
        if (trace == null) {
            // 初始化轨迹服务
            trace = new Trace(serviceId, UserInfoPreferences.getInstance().getMobile(), true);
            trace.setNotification(initNotification());
        }


    }



    @TargetApi(Build.VERSION_CODES.O)
    private Notification initNotification() {
        Notification.Builder builder = new Notification.Builder(context);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.push);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(context, 2, notificationIntent, 0))
                .setLargeIcon(icon)  // 设置下拉列表中的图标(大图标)
                .setContentTitle("百度鹰眼") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.push) // 设置状态栏内的小图标
                .setContentText("服务正在运行...") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null != notificationManager) {
            NotificationChannel notificationChannel = new NotificationChannel("trace", "trace_channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("trace"); // Android OchannelId
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        return notification;
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private Notification initNotification() {
//        Notification.Builder builder = new Notification.Builder(context);
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//
//        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.push);
//
//        // 设置PendingIntent
//        builder.setContentIntent(PendingIntent.getActivity(context, 2, notificationIntent, 0))
//                .setLargeIcon(icon)  // 设置下拉列表中的图标(大图标)
//                .setContentTitle("百度鹰眼") // 设置下拉列表里的标题
//                .setSmallIcon(R.drawable.push) // 设置状态栏内的小图标
//                .setContentText("服务正在运行...") // 设置上下文内容
//                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
//
//        Notification notification = builder.build(); // 获取构建好的Notification
//        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
//        return notification;
//    }

    private OnTraceListener traceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {
            LogUtils.d("onBindServiceCallback", String.format("errorNo:%d, message:%s ", i, s));
        }

        @Override
        public void onStartTraceCallback(int i, String s) {
            LogUtils.d("onStartTraceCallback", String.format("errorNo:%d, message:%s ", i, s));
            client.startGather(traceListener);
        }

        @Override
        public void onStopTraceCallback(int i, String s) {
            LogUtils.d("onStopTraceSuccess", String.format("errorNo:%d, message:%s ", i, s));
            client.stopGather(traceListener);
        }

        @Override
        public void onStartGatherCallback(int i, String s) {
            LogUtils.d("onStartGatherCallback", String.format("errorNo:%d, message:%s ", i, s));
        }

        @Override
        public void onStopGatherCallback(int i, String s) {
            LogUtils.d("onStopGatherCallback", String.format("errorNo:%d, message:%s ", i, s));
        }

        @Override
        public void onPushCallback(byte b, PushMessage pushMessage) {
            LogUtils.d("onPushCallback", String.format("messageType:%d, messageContent:%s ", b, pushMessage.getMessage()));
        }

        @Override
        public void onInitBOSCallback(int i, String s) {
            LogUtils.d("onInitBOSCallback", String.format("errorNo:%d, message:%s ", i, s));
        }
    };

    public void startTrace() {
        if (client != null && trace != null) {
            client.startTrace(trace, traceListener);
        }
    }

    public void stopTrace() {
        if (client != null && trace != null) {
            client.stopTrace(trace, traceListener);
        }
    }


    /**
     * @param no        司机工号
     * @param startTime 开始时间，单位s
     * @param endTime   结束时间，单位s
     * @param mapView   地图
     */
    public void queryHistoryTrack(String no, long startTime, long endTime, MapView mapView) {
        //@param isProcessed    是否返回纠偏后轨迹（0 : 否，1 : 是）
        //@param processOption
        //@param supplementMode 里程补充 传driving  使用最短骑行路线距离补充
        //降噪
        MapUtil.getInstance().init(mapView);
        final HistoryTrackRequest request = new HistoryTrackRequest();
        request.setEntityName(no);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setServiceId(serviceId);
        request.setCoordTypeOutput(CoordType.gcj02);

        ProcessOption processOption = new ProcessOption();
        processOption.setNeedDenoise(true);
        processOption.setNeedMapMatch(true);
        processOption.setNeedVacuate(true);
        processOption.setTransportMode(TransportMode.driving);
        request.setProcessOption(processOption);
        request.setProcessed(true);
        request.setSupplementMode(SupplementMode.driving);
        client.queryHistoryTrack(request, new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                super.onHistoryTrackCallback(historyTrackResponse);
                LogUtils.d("historyTrackResponse", historyTrackResponse.toString());
                int total = historyTrackResponse.getTotal();
                List<LatLng> trackPoints = new ArrayList<>();
                if (StatusCodes.SUCCESS == historyTrackResponse.getStatus() && 0 != total) {
                    List<TrackPoint> points = historyTrackResponse.getTrackPoints();
                    if (null != points) {

                        for (TrackPoint trackPoint : points) {
                            if (!CommonUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                    trackPoint.getLocation().getLongitude())) {
                                trackPoints.add(MapUtil.convertTrace2Map(trackPoint.getLocation()));
                            }
                        }
                    }
                }
                MapUtil.getInstance().drawHistoryTrack(trackPoints, SortType.asc);
            }
        });
    }


    public Trace getTrace() {
        return trace;
    }

    public LBSTraceClient getClient() {
        return client;
    }

    public int getServiceId() {
        return serviceId;
    }
}
