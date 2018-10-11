package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.utils.ble.BleDeviceManager;
import com.projects.company.homes_lock.utils.ble.IBleDeviceManagerCallbacks;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.ScannerLiveData;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.DataHelper;

import java.util.List;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

import static com.projects.company.homes_lock.utils.helper.BleHelper.LED_UUID_SERVICE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LED_UUID_SERVICE_CHARACTERISTIC_LED_BUTTON;

public class SplashViewModel extends AndroidViewModel {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    //endregion Declare Objects

    public SplashViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLocalRepository = new LocalRepository(application);
        //endregion Initialize Objects
    }

    //region SharePreferences
    public boolean isFirstTimeLaunchApp() {
        return mLocalRepository.isFirstTimeLaunchApp();
    }
    //endregion SharePreferences
}