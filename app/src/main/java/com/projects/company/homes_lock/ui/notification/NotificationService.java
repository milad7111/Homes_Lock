package com.projects.company.homes_lock.ui.notification;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.backendless.push.BackendlessFCMService;
import com.projects.company.homes_lock.BuildConfig;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Notification;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.TempDeviceModel;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.projects.company.homes_lock.utils.helper.BleHelper.findDevices;

public class NotificationService extends BackendlessFCMService implements ILocalRepository, IBleScanListener {

    //region Declare Objects
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    private TempDeviceModel mTempDevice;
    //endregion Declare Objects

    @Override
    public void onCreate() {
        super.onCreate();

//        handleAroundDeviceForProximity();
    }

    private void handleAroundDeviceForProximity() {
//        if (BuildConfig.DEBUG)
//            android.os.Debug.waitForDebugger();

        mBluetoothLEHelper = new CustomBluetoothLEHelper(null);
        mTempDevice = new TempDeviceModel();

        findDevices(this, mBluetoothLEHelper);
    }

    @Override
    public boolean onMessage(Context appContext, Intent msgIntent) {
        Timber.i("Notification Received");

        try {
            LocalRepository mLocalRepository = new LocalRepository(getApplication());
            mLocalRepository.insertNotification(this,
                    new Notification(
                            Objects.requireNonNull(msgIntent.getStringExtra("messageId") != null ? msgIntent.getStringExtra("messageId") : ""),
                            Objects.requireNonNull(msgIntent.getStringExtra("android-ticker-text") != null ? msgIntent.getStringExtra("android-ticker-text") : ""),
                            msgIntent.getStringExtra("message"),
                            msgIntent.getStringExtra("google.delivered_priority"),
                            msgIntent.getStringExtra("google.original_priority"),
                            msgIntent.getStringExtra("android-content-title"),
                            msgIntent.getLongExtra("google.sent_time", 0)
                    )
            );
        } catch (Exception e) {
            Timber.e(e.getMessage() != null ? e.getMessage() : e.getCause().getMessage());
        }

        return true;
    }

    public NotificationService() {
        super();
    }

    //region INotificationService Callbacks
    @Override
    public void onDataInsert(Object id) {
        Timber.i("Notification inserted in database.");
    }

    @Override
    public void onClearAllData() {
    }
    //endregion INotificationService Callbacks

    //region IBleScanListener
    @Override
    public void onFindBleSuccess(List<ScannedDeviceModel> response) {
        Timber.e("CustomBleHelper in background service: %s", response.toString());
    }

    @Override
    public void onFindBleFault() {
        Timber.e("CustomBleHelper in background service: onFindBleFault");
    }

    @Override
    public void onAdapterItemClick(BaseModel value) {
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }
    //endregion IBleScanListener
}
