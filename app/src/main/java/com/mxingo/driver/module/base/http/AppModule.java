package com.mxingo.driver.module.base.http;

import android.content.Context;
import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
import androidx.preference.PreferenceManager;
import com.squareup.otto.Bus;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by chendeqiang on 2017/6/22.
 */
@Module
public class AppModule {
    private final Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    CallAdapter.Factory provideCallAdapter() {
        return RxJavaCallAdapterFactory.create();
    }

    @Provides
    @Singleton
    Converter.Factory provideConverter() {
        return GsonConverterFactory.create();
    }


    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okhttpClient, Converter.Factory gsonConverterFactory, CallAdapter.Factory rxJavaCallAdapterFactory) {
        return new Retrofit.Builder()
                .client(okhttpClient)
                .baseUrl(ApiConstants.ip)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Provides
    MyManger provideMyManger(ApiService service) {
        return new MyManger(service);
    }

    @Provides
    MyPresenter provideMyPresenter(MyManger myManger) {
        return new MyPresenter(new Bus(), myManger);
    }


    @Provides
    Context provideContext() {
        return mContext;
    }
}
