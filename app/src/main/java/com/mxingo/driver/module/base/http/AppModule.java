package com.mxingo.driver.module.base.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
 * Created by zhouwei on 2017/6/22.
 */
@Module
public class AppModule {
    private final Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient okhttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
        return okhttpClient;
    }

    @Provides
    @Singleton
    public CallAdapter.Factory provideCallAdapter() {
        CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        return rxJavaCallAdapterFactory;
    }

    @Provides
    @Singleton
    public Converter.Factory provideConverter() {
        Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
        return gsonConverterFactory;
    }


    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okhttpClient, Converter.Factory gsonConverterFactory, CallAdapter.Factory rxJavaCallAdapterFactory) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(okhttpClient)
                .baseUrl(ApiConstants.ip)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Provides
    public MyManger provideMyManger(ApiService service) {
        return new MyManger(service);
    }

    @Provides
    public MyPresenter provideMyPresenter(MyManger myManger) {
        return new MyPresenter(new Bus(), myManger);
    }


    @Provides
    public Context provideContext() {
        return mContext;
    }
}
