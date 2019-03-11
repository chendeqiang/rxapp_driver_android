package com.mxingo.driver.module.take;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mxingo.driver.OrderModel;
import com.mxingo.driver.R;
import com.mxingo.driver.model.CommEntity;
import com.mxingo.driver.model.OrderEntity;
import com.mxingo.driver.model.PushOrderEntity;
import com.mxingo.driver.model.QryOrderEntity;
import com.mxingo.driver.model.TakeOrderEntity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.speech.MySpeechSynthesizer;
import com.mxingo.driver.module.order.OrderInfoActivity;
import com.mxingo.driver.module.order.OrderSource;
import com.mxingo.driver.utils.TextUtil;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.ShowToast;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Stack;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhouwei on 2017/6/22.
 */

public class TakeOrderDialog extends Dialog implements TextWatcher {

    @Inject
    MyPresenter presenter;

    @BindView(R.id.tv_order_type)
    TextView tvOrderType;
    @BindView(R.id.tv_flight)
    TextView tvFlight;
    @BindView(R.id.tv_start_address)
    TextView tvStartAddress;
    @BindView(R.id.tv_end_address)
    TextView tvEndAddress;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_order_hint)
    TextView tvOrderHint;
    @BindView(R.id.et_quote)
    EditText etQuote;
    @BindView(R.id.btn_take)
    Button btnTake;
    @BindView(R.id.ll_quote)
    LinearLayout llQuote;
    @BindView(R.id.tv_count_down_time)
    TextView tvCountDownTime;
    @BindView(R.id.img_flight)
    ImageView imgFlight;
    @BindView(R.id.tv_flight_hint)
    TextView tvFlightHint;
    @BindView(R.id.ll_end_address)
    LinearLayout llEndAddress;
    @BindView(R.id.tv_book_time)
    TextView tvBookTime;
    @BindView(R.id.ll_take_order)
    LinearLayout llTakeOrder;
    @BindView(R.id.ll_flight)
    LinearLayout llFlight;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.ll_start_address)
    LinearLayout llStartAddress;
    @BindView(R.id.tv_mileage_forecast)
    TextView mTvMileageForecast;
    @BindView(R.id.tv_open_order)
    TextView mTvOpenOrder;
    @BindView(R.id.rl_mileage_forecast)
    RelativeLayout mRlMileageForecast;
    @BindView(R.id.tv_mileage)
    TextView mTvMileage;
    @BindView(R.id.tv_remark)
    TextView mTvRemark;
    @BindView(R.id.ll_remark)
    LinearLayout mLlRemark;
    @BindView(R.id.ll_order_from_dialog)
    LinearLayout mLlOrderFromDialog;
    @BindView(R.id.tv_order_from_dialog)
    TextView tvOrderFromDialog;

    private Activity activity;
    private String driverNo;
    private Stack<PushOrderEntity> pushData;
    private OrderEntity orderEntity;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM月dd号 HH:mm");
    private MySpeechSynthesizer speechSynthesizer;
    private MyProgress progress;
    private boolean isSpeak = false;
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long l) {
            tvCountDownTime.setText("" + l / 1000 + "s");
        }

        @Override
        public void onFinish() {
            getNext();
        }
    };

    private void getNext() {
        if (!pushData.isEmpty()) {
            dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isShowing()) {
                        show();
                        isSpeak = true;
                        llTakeOrder.setVisibility(View.GONE);
                        PushOrderEntity data = pushData.pop();
                        progress.show();
                        presenter.qryOrder(data.order.orderNo);
                    }
                }
            }, 1000);

//            PushOrderEntity data = pushData.pop();
//            presenter.qryOrder(data.order.orderNo);
        } else {
            dismiss();
        }
    }


    public TakeOrderDialog(@NonNull Activity activity, String driverNo) {
        super(activity, R.style.dialog);
        this.activity = activity;
        this.driverNo = driverNo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_take_order);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        progress = new MyProgress(activity);
        etQuote.addTextChangedListener(this);
        speechSynthesizer = new MySpeechSynthesizer();
    }

    @Override
    public void dismiss() {
        if (isShowing() && activity != null && !activity.isDestroyed()) {
            super.dismiss();
            presenter.unregister(this);
        }
    }

    @Override
    public void show() {
        if (!isShowing() && activity != null && !activity.isDestroyed()) {
            super.show();
            ComponentHolder.getAppComponent().inject(this);
            presenter.register(this);
        }
    }

    public void setPushData(Stack<PushOrderEntity> pushData) {
        this.pushData = pushData;
        llTakeOrder.setVisibility(View.GONE);
        PushOrderEntity data = pushData.pop();
        isSpeak = true;
        progress.show();
        presenter.qryOrder(data.order.orderNo);
    }

    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == QryOrderEntity.class) {
            qryOrder((QryOrderEntity) object);
            progress.dismiss();
        } else if (TakeOrderEntity.class == object.getClass()) {
            progress.dismiss();
            TakeOrderEntity data = (TakeOrderEntity) object;
            if (data.rspCode.equals("00")) {
                ShowToast.showCenter(activity, "抢单成功");
                dismiss();
                OrderInfoActivity.startOrderInfoActivity(activity, orderEntity.orderNo, data.flowNo, driverNo);
                pushData.clear();

            } else {
                ShowToast.showCenter(activity, data.rspDesc);
                getNext();
            }
        } else if (CommEntity.class == object.getClass()) {
            CommEntity data = (CommEntity) object;
            if (data.rspCode.equals("00")) {
                progress.dismiss();
                ShowToast.showCenter(activity, "报价成功");
                getNext();
            } else {
                ShowToast.showCenter(activity, data.rspDesc);
                presenter.qryOrder(orderEntity.orderNo);
            }

        }

    }


    private void qryOrder(QryOrderEntity qryOrderEntity) {
        if (qryOrderEntity.rspCode.equals("00")) {
            if (OrderStatus.PUBORDER_TYPE == qryOrderEntity.order.orderStatus) {
                getData(qryOrderEntity);
            } else if (OrderStatus.CANCELORDER_TYPE == qryOrderEntity.order.orderStatus) {
                ShowToast.showCenter(activity, "订单取消");
                getNext();
            } else {
                ShowToast.showCenter(activity, "抢单失败");
                getNext();
            }
        } else {
            ShowToast.showCenter(activity, qryOrderEntity.rspDesc);
        }
    }

    private void getData(QryOrderEntity qryOrderEntity) {
        OrderEntity order = qryOrderEntity.order;
        orderEntity = order;
        llTakeOrder.setVisibility(View.VISIBLE);
        tvFlight.setText(order.tripNo);
        tvOrderFromDialog.setText(OrderSource.getKey(order.source));
        tvOrderType.setText(OrderType.getKey(order.orderType) + "(" + CarLevel.getKey(order.carLevel) + ")");
        mTvMileageForecast.setText(order.planMileage / 100 / 10.0 + "公里");
        if (OrderType.SEND_PLANE_TYPE == order.orderType || OrderType.TAKE_PLANE_TYPE == order.orderType) {
            tvFlightHint.setText("航班:");
            imgFlight.setImageResource(R.drawable.ic_plane);
            llEndAddress.setVisibility(View.VISIBLE);
            llFlight.setVisibility(View.VISIBLE);
            llStartAddress.setVisibility(View.VISIBLE);
            llAddress.setVisibility(View.GONE);
            tvBookTime.setText(TextUtil.getFormatWeek(Long.valueOf(order.bookTime)));
        } else if (OrderType.SEND_TRAIN_TYPE == order.orderType || OrderType.TAKE_TRAIN_TYPE == order.orderType) {
            tvFlightHint.setText("车次:");
            imgFlight.setImageResource(R.drawable.ic_train);
            llEndAddress.setVisibility(View.VISIBLE);
            llFlight.setVisibility(View.VISIBLE);
            llStartAddress.setVisibility(View.VISIBLE);
            llAddress.setVisibility(View.GONE);
            tvBookTime.setText(TextUtil.getFormatWeek(Long.valueOf(order.bookTime)));
        } else {
            llEndAddress.setVisibility(View.GONE);
            llFlight.setVisibility(View.GONE);
            llStartAddress.setVisibility(View.GONE);
            llAddress.setVisibility(View.VISIBLE);
            tvBookTime.setText(TextUtil.getFormatString(Long.valueOf(order.bookTime), order.bookDays, "yyyy-MM-dd HH:mm"));
        }
        if (!TextUtil.isEmpty(order.startAddr)) {
            tvStartAddress.setText(order.startAddr);
            tvAddress.setText(order.startAddr);
        }
        if (!TextUtil.isEmpty(order.endAddr)) {
            tvEndAddress.setText(order.endAddr);
        }
        if (!TextUtil.isEmpty(order.remark) && order.remark.getBytes().length > 50) {
            mTvRemark.setText(order.remark.substring(0, 50) + "...");
        } else if (!TextUtil.isEmpty(order.remark)) {
            mTvRemark.setText(order.remark);
        } else {
            mTvRemark.setText("");
        }
        if (order.orderModel == OrderModel.ROB_TYPE || order.orderModel == OrderModel.POINT_TYPE) {
            tvFee.setText("¥ " + order.orderAmount / 100 + " 元");
            robOrder();
        } else if (order.orderModel == OrderModel.QUOTE_TYPE) {
            orderEntity.orderQuote = qryOrderEntity.orderQuote / 100;
            tvFee.setText("¥ " + qryOrderEntity.orderQuote / 100 + " 元");
            etQuote.setText("" + (qryOrderEntity.orderQuote / 100 - 5 < 1 ? 1 : qryOrderEntity.orderQuote / 100 - 5) + "");
            etQuote.setSelection(etQuote.getText().length());
            quoteOrder();
        }
        timer.start();
        if (isSpeak) {
            isSpeak = false;
            if (order.orderType != OrderType.DAY_RENTER_TYPE) {
                speechSynthesizer.startSpeaking(OrderModel.getKey(order.orderModel) + "从" + order.startAddr + "到" + order.endAddr);
            } else {
                speechSynthesizer.startSpeaking(OrderModel.getKey(order.orderModel) + "地址:" + order.startAddr + "时间:" + tvBookTime.getText().toString());
            }
        }
    }

    //抢单
    private void robOrder() {
        if (btnTake != null) {
            btnTake.setText("抢单");
            mRlMileageForecast.setVisibility(View.VISIBLE);
            mLlRemark.setVisibility(View.VISIBLE);
            mLlOrderFromDialog.setVisibility(View.VISIBLE);
        }
        if (tvOrderHint != null) {
            tvOrderHint.setVisibility(View.GONE);
        }
        if (llQuote != null) {
            llQuote.setVisibility(View.GONE);
        }
    }

    //报价
    private void quoteOrder() {
        if (btnTake != null) {
            btnTake.setText("报价");
        }
        if (tvOrderHint != null) {
            tvOrderHint.setVisibility(View.VISIBLE);
        }
        if (llQuote != null) {
            llQuote.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.img_cancel, R.id.tv_open_order, R.id.btn_take})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                getNext();
                break;
            case R.id.tv_open_order:
                TakeOrderActivity.startTakeOrderActivity(getContext(), orderEntity, driverNo);
                getNext();
                break;
            case R.id.btn_take:
                if (TextUtil.isFastClick()) {
                    if (OrderModel.ROB_TYPE == orderEntity.orderModel) {
                        progress.show();
                        presenter.takeOrder(orderEntity.orderNo, driverNo);
                    } else if (OrderModel.QUOTE_TYPE == orderEntity.orderModel) {
                        progress.show();
                        presenter.quoteOrder(orderEntity.orderNo, driverNo, orderEntity.orderQuote, etQuote.getText().toString());
                    }
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null) {
            if (editable.toString().length() == 2) {
                char first = editable.charAt(0);
                char last = editable.charAt(1);
                if (first == '0') {
                    etQuote.setText(String.valueOf(last));
                    etQuote.setSelection(1);
                }
            }
        }
    }
}
