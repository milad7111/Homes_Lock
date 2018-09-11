package com.projects.company.homes_lock.base;

/**
 * This is Application Class of Project
 */

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projects.company.homes_lock.BuildConfig;
import com.projects.company.homes_lock.repositories.remote.IRetrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.projects.company.homes_lock.utils.UrlHelper.BACKENDLESS_BASE_URL_HTTPS_REST;

public class BaseApplication extends Application {

    private static volatile IRetrofit IRETROFIT = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static synchronized IRetrofit getRetrofitAPI() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        IRETROFIT = new Retrofit.Builder().baseUrl(BACKENDLESS_BASE_URL_HTTPS_REST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build()
                .create(IRetrofit.class);

        return IRETROFIT;
    }
}
