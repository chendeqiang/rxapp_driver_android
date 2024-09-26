package com.mxingo.driver.module.base.http;

import com.mxingo.driver.module.BindAlipayActivity;
import com.mxingo.driver.module.CarRegisterActivity;
import com.mxingo.driver.module.DriverCarRegistrationActivity;
import com.mxingo.driver.module.GuideActivity;
import com.mxingo.driver.module.HesuanReportActivity;
import com.mxingo.driver.module.HybridSearchActivity;
import com.mxingo.driver.module.JkmReportActivity;
import com.mxingo.driver.module.LoginActivity;
import com.mxingo.driver.module.MyBillActivity;
import com.mxingo.driver.module.MyWalletActivity;
import com.mxingo.driver.module.NoticeActivity;
import com.mxingo.driver.module.NoticeInfoActivity;
import com.mxingo.driver.module.DriverRegisterActivity;
import com.mxingo.driver.module.RecordingService;
import com.mxingo.driver.module.SecondBindAlipayActivity;
import com.mxingo.driver.module.SettingActivity;
import com.mxingo.driver.module.WithdrawActivity;
import com.mxingo.driver.module.WithdrawRecordActivity;
import com.mxingo.driver.module.WithdrawSettingActivity;
import com.mxingo.driver.module.base.map.route.SearchRouteActivity;
import com.mxingo.driver.module.order.CarPoolInfoActivity;
import com.mxingo.driver.module.order.CarPoolOrderInfoActivity;
import com.mxingo.driver.module.order.CarpoolOrderActivity;
import com.mxingo.driver.module.order.MapActivity;
import com.mxingo.driver.module.order.MyOrderActivity;
import com.mxingo.driver.module.order.OrderInfoActivity;
import com.mxingo.driver.module.order.OrdersActivity;
import com.mxingo.driver.module.take.MainActivity;
import com.mxingo.driver.module.take.TakeOrderActivity;
import com.mxingo.driver.module.take.TakeOrderDialog;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by chendeqiang on 2017/6/22.
 * AppComponent:注入器，储存了我们要用到的全局变量对象
 */
@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {
    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(OrdersActivity activity);

    void inject(HybridSearchActivity activity);

    void inject(OrderInfoActivity activity);

    void inject(TakeOrderActivity activity);

    void inject(MyOrderActivity activity);

    void inject(CarpoolOrderActivity activity);

    void inject(CarPoolOrderInfoActivity activity);

    void inject(CarPoolInfoActivity activity);

    void inject(MapActivity activity);

    void inject(SettingActivity activity);

    void inject(TakeOrderDialog dialog);

    void inject(SearchRouteActivity activity);

    void inject(DriverCarRegistrationActivity activity);

    void inject(NoticeActivity activity);

    void inject(NoticeInfoActivity activity);

    void inject(MyBillActivity activity);

    void inject(MyWalletActivity activity);

    void inject(WithdrawSettingActivity activity);

    void inject(WithdrawActivity activity);

    void inject(WithdrawRecordActivity activity);

    void inject(BindAlipayActivity activity);

    void inject(SecondBindAlipayActivity activity);

    void inject(DriverRegisterActivity activity);

    void inject(CarRegisterActivity activity);

    void inject(RecordingService service);

    void inject(GuideActivity activity);

    void inject(HesuanReportActivity activity);

    void inject(JkmReportActivity activity);
}
