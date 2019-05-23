package com.projects.company.homes_lock.ui.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.backendless.push.BackendlessFCMService;
import com.google.gson.Gson;
import com.projects.company.homes_lock.BuildConfig;
import com.projects.company.homes_lock.database.tables.Notification;
import com.projects.company.homes_lock.models.datamodels.notification.AndroidImmediatePush;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;

public class NotificationService extends BackendlessFCMService implements ILocalRepository {

    //region Declare Objects
//    private NotificationViewModel mNotificationViewModel;
    //endregion Declare Objects

    @Override
    public void onCreate() {
        super.onCreate();

//        this.mNotificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
    }

    @Override
    public boolean onMessage(Context appContext, Intent msgIntent) {
//        if (BuildConfig.DEBUG)
//            android.os.Debug.waitForDebugger();

        Log.i("NotificationService", "Notification Received");

        try {
            AndroidImmediatePush mAndroidImmediatePush = new Gson()
                    .fromJson(msgIntent.getStringExtra("android_immediate_push"), AndroidImmediatePush.class);

            LocalRepository mLocalRepository = new LocalRepository(getApplication());
            mLocalRepository.insertNotification(
                    this, new Notification(
                            msgIntent.getStringExtra("messageId"),
                            msgIntent.getStringExtra("message"),
                            msgIntent.getStringExtra("google.delivered_priority"),
                            msgIntent.getStringExtra("google.original_priority"),
                            msgIntent.getLongExtra("google.sent_time", 0),
                            mAndroidImmediatePush.getContentTitle(),
                            mAndroidImmediatePush.getSummarySubText()
                    )
            );
        } catch (Exception e) {
            Log.e("NotificationService", e.getMessage() != null ? e.getMessage() : e.getCause().getMessage());
        }

        return true;
    }

    public NotificationService() {
        super();
    }

    //region INotificationService Callbacks
    @Override
    public void onDataInsert(Object id) {
        Log.i(getClass().getName(), "Notification inserted in database.");
    }

    @Override
    public void onClearAllData() {
    }
    //endregion INotificationService Callbacks
}
