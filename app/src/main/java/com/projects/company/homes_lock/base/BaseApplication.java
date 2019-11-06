package com.projects.company.homes_lock.base;

/*
  This is Application Class of Project
 */

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.backendless.Backendless;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projects.company.homes_lock.BuildConfig;
import com.projects.company.homes_lock.repositories.remote.IRetrofit;
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.ui.device.activity.NearestBleService;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.projects.company.homes_lock.utils.helper.UrlHelper.BACKENDLESS_ANDROID_API_KEY;
import static com.projects.company.homes_lock.utils.helper.UrlHelper.BACKENDLESS_APPLICATION_ID;
import static com.projects.company.homes_lock.utils.helper.UrlHelper.BACKENDLESS_BASE_URL_HTTPS_REST;

public class BaseApplication extends Application {

    //region Declare Objects
    /**
     * true if user login with username & password in {@link com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment}
     * false if user login with direct connect option in {@link com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment}
     */
    private static boolean userLoginMode = false;

    public static String activeUserObjectId = null;
    public static String activeUserToken = null;
    public static String activeUserEmail = null;

    private static volatile IRetrofit IRETROFIT = null;
    private static Intent mNearestBleService;
    //endregion Declare Objects

    //region Main Callbacks
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Backendless.initApp(this, BACKENDLESS_APPLICATION_ID, BACKENDLESS_ANDROID_API_KEY);
        Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        logUserCrashesInFabric();
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        mNearestBleService = new Intent(getApplicationContext(), NearestBleService.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    //endregion Main Callbacks

    //region Declare Methods
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

    public static boolean isUserLoggedIn() {
        return userLoginMode;
    }

    public static void setUserLoginMode(boolean userLoginMode) {
        BaseApplication.userLoginMode = userLoginMode;
    }

    public static void logUserCrashesInFabric() {
        Crashlytics.setUserIdentifier(activeUserObjectId);
        Crashlytics.setUserEmail(activeUserEmail);
        Crashlytics.setUserName(activeUserToken);
    }

    public void startNearestService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(mNearestBleService);
        else context.startService(mNearestBleService);
    }

    public void stopNearestService(Context context) {
        context.stopService(mNearestBleService);
    }
    //endregion Declare Methods
}