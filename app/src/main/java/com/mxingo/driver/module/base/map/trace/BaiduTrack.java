package com.mxingo.driver.module.base.map.trace;


import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.AddEntityResponse;
import com.baidu.trace.api.entity.AroundSearchResponse;
import com.baidu.trace.api.entity.BoundSearchResponse;
import com.baidu.trace.api.entity.DeleteEntityResponse;
import com.baidu.trace.api.entity.DistrictSearchResponse;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.entity.PolygonSearchResponse;
import com.baidu.trace.api.entity.SearchResponse;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.AddPointsResponse;
import com.baidu.trace.api.track.ClearCacheTrackResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.QueryCacheTrackResponse;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.R;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.CurrentLocation;
import com.mxingo.driver.module.order.MapActivity;
import com.mxingo.driver.module.order.OrderInfoActivity;
import com.mxingo.driver.module.order.TrackQueryActivity;
import com.mxingo.driver.utils.BitmapUtil;
import com.mxingo.driver.utils.CommonUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.MapUtil;
import com.mxingo.driver.utils.NetUtil;

import java.util.concurrent.atomic.AtomicInteger;


public class BaiduTrack {

    private static BaiduTrack baiduTrack;

    public LBSTraceClient mClient = null;

    public Trace mTrace = null;

    public long serviceId = 145188;

    private int gatherInterval = 5;

    private int packInterval = 15;

    public boolean isTraceStarted = false;
    public boolean isServiceBind = false;
    public boolean isGatherStarted = false;
    public static int screenWidth = 0;

    public static int screenHeight = 0;

    private Notification notification = null;
    private Context context;
    private OnTraceListener traceListener;
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    /**
     * 实时定位任务
     */
    private RealTimeHandler realTimeHandler = new RealTimeHandler();

    private RealTimeLocRunnable realTimeLocRunnable = null;

    private boolean isRealTimeRunning = true;
    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    private OnTrackListener trackListener = null;

    /**
     * Entity监听器(用于接收实时定位回调)
     */
    private OnEntityListener entityListener = null;
    private LocRequest locRequest = null;

    private MapUtil mapUtil = null;


    public static BaiduTrack getInstance() {
        if (baiduTrack == null) {
            baiduTrack = new BaiduTrack();
            baiduTrack.initValue(MyApplication.application);
        }
        return baiduTrack;
    }

    private void initValue(Application application) {
        this.context = application.getApplicationContext();
        LBSTraceClient.setAgreePrivacy(context,true);
        // 若为创建独立进程，则不初始化成员变量
        if ("com.baidu.track:remote".equals(CommonUtil.getCurProcessName(context))) {
            return;
        }
        SDKInitializer.setAgreePrivacy(context, true);
        SDKInitializer.initialize(context);

        initNotification();
        try {
            mClient = new LBSTraceClient(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isTraceStarted = UserInfoPreferences.getInstance().getTraceStart();
        mTrace = new Trace(serviceId,UserInfoPreferences.getInstance().getMobile());

        mTrace.setNotification(notification);


        if (mClient!=null){
            // 设置定位模式
            mClient.setLocationMode(LocationMode.High_Accuracy);
            mClient.setInterval(gatherInterval, packInterval);
            mClient.setProtocolType(ProtocolType.HTTPS);
        }
        BitmapUtil.init();
        mapUtil= MapUtil.getInstance();
        locRequest = new LocRequest(serviceId);
        //startRealTimeLoc(Constants.LOC_INTERVAL);
        initListener();
    }

    private void startRealTimeLoc(int interval) {
        isRealTimeRunning = true;
        realTimeLocRunnable = new RealTimeLocRunnable(interval);
        realTimeHandler.post(realTimeLocRunnable);
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        if (mClient == null) {
            return;
        }
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(context)
                && UserInfoPreferences.getInstance().contains("is_trace_started")
                && UserInfoPreferences.getInstance().contains("is_gather_started")
                && UserInfoPreferences.getInstance().getTraceStart()
                && UserInfoPreferences.getInstance().getGatherStart()) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, UserInfoPreferences.getInstance().getMobile());
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
        }
    }

    /**
     * 实时定位任务
     *
     * @author baidu
     */
    class RealTimeLocRunnable implements Runnable {

        private int interval = 0;

        public RealTimeLocRunnable(int interval) {
            this.interval = interval;
        }

        @Override
        public void run() {
            if (isRealTimeRunning) {
                getCurrentLocation(entityListener, trackListener);
                realTimeHandler.postDelayed(this, interval * 1000);
            }
        }
    }

    static class RealTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private void initListener() {
        traceListener =new OnTraceListener() {
            /**
             * 绑定服务回调接口
             * @param i  状态码
             * @param s 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>1：失败</pre>
             */
            @Override
            public void onBindServiceCallback(int i, String s) {
                if (StatusCodes.SUCCESS==i){
                    LogUtils.d("onBindServiceSUCCESS", String.format("errorNo:%d, message:%s ", i, s));
                    isServiceBind =true;
                }else {
                    LogUtils.d("onBindServiceFAILED", String.format("errorNo:%d, message:%s ", i, s));
                }

            }

            /**
             * 开启服务回调接口
             * @param i 状态码
             * @param s 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>10000：请求发送失败</pre>
             *                <pre>10001：服务开启失败</pre>
             *                <pre>10002：参数错误</pre>
             *                <pre>10003：网络连接失败</pre>
             *                <pre>10004：网络未开启</pre>
             *                <pre>10005：服务正在开启</pre>
             *                <pre>10006：服务已开启</pre>
             */
            @Override
            public void onStartTraceCallback(int i, String s) {
                if (StatusCodes.SUCCESS == i || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= i) {
                    isTraceStarted = true;
                    UserInfoPreferences.getInstance().putTraceStart();
                    LogUtils.d("onStartTraceSUCCESS", String.format("errorNo:%d, message:%s ", i, s));
                    mClient.startGather(traceListener);
                }else {
                    LogUtils.d("onStartTraceFAILED", String.format("errorNo:%d, message:%s ", i, s));
                }
            }

            /**
             * 停止服务回调接口
             * @param i 状态码
             * @param s 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>11000：请求发送失败</pre>
             *                <pre>11001：服务停止失败</pre>
             *                <pre>11002：服务未开启</pre>
             *                <pre>11003：服务正在停止</pre>
             */
            @Override
            public void onStopTraceCallback(int i, String s) {
                if (StatusCodes.SUCCESS == i || StatusCodes.CACHE_TRACK_NOT_UPLOAD == i){
                    isTraceStarted =false;
                    isServiceBind =false;
                    UserInfoPreferences.getInstance().remove("is_trace_started");
                    UserInfoPreferences.getInstance().remove("is_gather_started");
                    LogUtils.d("onStopTraceSUCCESS", String.format("errorNo:%d, message:%s ", i, s));
                    mClient.stopGather(traceListener);
                }else {
                    LogUtils.d("onStopTraceFAILED", String.format("errorNo:%d, message:%s ", i, s));
                }
            }

            /**
             * 开启采集回调接口
             * @param i 状态码
             * @param s 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>12000：请求发送失败</pre>
             *                <pre>12001：采集开启失败</pre>
             *                <pre>12002：服务未开启</pre>
             */
            @Override
            public void onStartGatherCallback(int i, String s) {
                if (StatusCodes.SUCCESS == i || StatusCodes.GATHER_STARTED == i) {
                    isGatherStarted = true;
                    UserInfoPreferences.getInstance().putGatherStart();
                    LogUtils.d("onStartGatherSUCCESS", String.format("errorNo:%d, message:%s ", i, s));
                }else {
                    LogUtils.d("onStartGatherFAILED", String.format("errorNo:%d, message:%s ", i, s));
                }
            }

            /**
             * 停止采集回调接口
             * @param i 状态码
             * @param s 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>13000：请求发送失败</pre>
             *                <pre>13001：采集停止失败</pre>
             *                <pre>13002：服务未开启</pre>
             */
            @Override
            public void onStopGatherCallback(int i, String s) {
                if (StatusCodes.SUCCESS == i || StatusCodes.GATHER_STOPPED == i) {
                    isGatherStarted = false;
                    UserInfoPreferences.getInstance().remove("is_gather_started");
                    LogUtils.d("onStopGatherSUCCESS", String.format("errorNo:%d, message:%s ", i, s));
                }else {
                    LogUtils.d("onStopGatherFAILED", String.format("errorNo:%d, message:%s ", i, s));
                }
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }

            @Override
            public void onTraceDataUploadCallBack(int i, String s, int i1, int i2) {

            }
        };


        trackListener = new OnTrackListener() {

            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    return;
                }

                LatestPoint point = response.getLatestPoint();
                if (null == point || CommonUtil.isZeroPoint(point.getLocation().getLatitude(), point.getLocation()
                        .getLongitude())) {
                    return;
                }

                LatLng currentLatLng = mapUtil.convertTrace2Map(point.getLocation());

                if (null == currentLatLng) {
                    return;
                }
//                realTrackList.add(currentLatLng);
//                if (realTrackList != null && realTrackList.size() > 1) {
//                    mapUtil.drawPolyline(realTrackList);
//                }
                CurrentLocation.locTime = point.getLocTime();
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;

                if (null != mapUtil) {
                    mapUtil.updateStatus(currentLatLng, true);
                }
            }
        };

        entityListener= new OnEntityListener() {
            @Override
            public void onReceiveLocation(TraceLocation location) {
                if (StatusCodes.SUCCESS != location.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                        location.getLongitude())) {
                    return;
                }
                LatLng currentLatLng = mapUtil.convertTraceLocation2Map(location);
                if (null == currentLatLng) {
                    return;
                }
                CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime());
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;

                if (null != mapUtil) {
                    mapUtil.updateStatus(currentLatLng, true);
                }
                //LogUtils.d("onReceiveLocation",String.format("%s ", location.toString()));

            }
        };
    }

    @TargetApi(16)
    private void initNotification() {
        Notification.Builder builder = new Notification.Builder(context);
        Intent notificationIntent = new Intent(context, MapActivity.class);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0))
                .setContentTitle("百度鹰眼") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.push) // 设置状态栏内的小图标
                .setContentText("服务正在运行...") // 设置上下文内容
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null != notificationManager) {
            NotificationChannel notificationChannel = new NotificationChannel("trace", "trace_channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);

            builder.setChannelId("trace"); // Android O版本之后需要设置该通知的channelId
        }

        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }

    public void clear(){
        mClient.clear();
    }

    /**
     * 清除Trace状态：初始化app时，判断上次是正常停止服务还是强制杀死进程，根据trackConf中是否有is_trace_started字段进行判断。
     * <p>
     * 停止服务成功后，会将该字段清除；若未清除，表明为非正常停止服务。
     */
    private void clearTraceStatus() {
        if (UserInfoPreferences.getInstance().contains("is_trace_started") || UserInfoPreferences.getInstance().contains("is_gather_started")) {
            UserInfoPreferences.getInstance().remove("is_trace_started");
            UserInfoPreferences.getInstance().remove("is_gather_started");
        }
    }

    public void startTrace() {
        if (mClient != null && mTrace != null ) {
                mClient.startTrace(mTrace, traceListener);
                LogUtils.d("startTrace","---------------------");
        }
    }

    public void stopTrace() {
        if (mClient != null && mTrace != null) {
            mClient.stopTrace(mTrace,traceListener);
        }else {
            LogUtils.d("stopTrace","----------失败------------");
        }
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

}
