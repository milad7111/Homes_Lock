package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.Notification;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.ui.notification.INotificationService;

import java.util.List;

import timber.log.Timber;

public class NotificationViewModel extends AndroidViewModel implements ILocalRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private INotificationService mINotificationService;
    private LocalRepository mLocalRepository;
    //endregion Declare Objects

    //region Constructor
    NotificationViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mLocalRepository = new LocalRepository(application);
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region ILocalRepository Callbacks
    @Override
    public void onDataInsert(Object object) {
        Timber.d("Data inserted");

        if (object instanceof Notification) {
            if (mINotificationService != null)
                mINotificationService.onDataInsert((Notification) object);
        }
    }

    @Override
    public void onClearAllData() {
    }
    //endregion ILocalRepository Callbacks

    //region Declare Local Methods
    public void insertNotification(INotificationService mINotificationService, Notification notification) {
        this.mINotificationService = mINotificationService;
        mLocalRepository.insertNotification(this, notification);
    }

    public LiveData<List<Notification>> getAllNotifications() {
        return mLocalRepository.getAllNotifications();
    }
    //endregion Declare Local Methods

    //region Declare Methods
    //endregion Declare Methods
}