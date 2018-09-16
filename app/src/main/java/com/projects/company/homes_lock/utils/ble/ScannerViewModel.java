package com.projects.company.homes_lock.utils.ble;

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
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class ScannerViewModel extends AndroidViewModel implements BlinkyManagerCallbacks {

    private final BlinkyManager mBlinkyManager;
    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();
    private final MutableLiveData<Void> mOnDeviceReady = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mLEDState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mButtonState = new MutableLiveData<>();

    private final ScannerLiveData mScannerLiveData;

    public ScannerLiveData getScannerState() {
        return mScannerLiveData;
    }

    public ScannerViewModel(final Application application) {
        super(application);

        // Initialize the manager
        mBlinkyManager = new BlinkyManager(getApplication());
        mBlinkyManager.setGattCallbacks(this);

        mScannerLiveData = new ScannerLiveData(Utils.isBleEnabled(), Utils.isLocationEnabled(application));
        registerBroadcastReceivers(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getApplication().unregisterReceiver(mBluetoothStateBroadcastReceiver);

        if (Utils.isMarshmallowOrAbove()) {
            getApplication().unregisterReceiver(mLocationProviderChangedReceiver);
        }
    }

    public void refresh() {
        mScannerLiveData.refresh();
    }

    public void startScan() {
        if (mScannerLiveData.isScanning()) {
            return;
        }

//        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
//        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
//        scanSettings = scanSettingsBuilder.build();

//        ScanSettings mScanSettings = new ScanSettings.Builder()
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                .setReportDelay(0)
//                .setUseHardwareFilteringIfSupported(false)
                // Samsung S6 and S6 Edge report equal value of RSSI for all devices. In this app we ignore the RSSI.
                /*.setUseHardwareBatchingIfSupported(false)*/
//                .build();

//        final ParcelUuid uuid = new ParcelUuid(BlinkyManager.LBS_UUID_SERVICE);
//        List<ScanFilter> filters = new ArrayList<>();
//        filters.add(new ScanFilter.Builder().setServiceUuid(uuid).build());

        BluetoothLeScannerCompat mBluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner();
//        mBluetoothLeScannerCompat.startScan(filters, mScanSettings, scanCallback);
        mBluetoothLeScannerCompat.startScan(scanCallback);
        mScannerLiveData.scanningStarted();
    }

    public void stopScan() {
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
        mScannerLiveData.scanningStopped();
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
                Utils.markLocationNotRequired(getApplication());

            int i = 0;
            if (result.getDevice().getAddress().contains("9"))
                i = 9;

            mScannerLiveData.deviceDiscovered(result);
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            // Batch scan is disabled (report delay = 0)
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // TODO This should be handled
            mScannerLiveData.scanningStopped();
        }
    };

    private void registerBroadcastReceivers(final Application application) {
        application.registerReceiver(mBluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (Utils.isMarshmallowOrAbove()) {
            application.registerReceiver(mLocationProviderChangedReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        }
    }

    private final BroadcastReceiver mLocationProviderChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final boolean enabled = Utils.isLocationEnabled(context);
            mScannerLiveData.setLocationEnabled(enabled);
        }
    };

    //region BroadcastReceiver
    private final BroadcastReceiver mBluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    mScannerLiveData.bluetoothEnabled();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_OFF:
                    if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                        stopScan();
                        mScannerLiveData.bluetoothDisabled();
                    }
                    break;
            }
        }
    };
    //endregion BroadcastReceiver

    //region BLE
//    public void getAllAccessibleBLEDevices(Context context, IBleScanListener mIBleScanListener){
//        mBleScanner = new BleScanner(context, mIBleScanListener);
//    }

//    public LiveData<Void> isDeviceReady() {
//        return mOnDeviceReady;
//    }

//    public LiveData<String> getConnectionState() {
//        return mConnectionState;
//    }

    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    ////////////////////////////
//    public LiveData<Boolean> getButtonState() {
//        return mButtonState;
//    }

    ////////////////////////////
//    public LiveData<Boolean> getLEDState() {
//        return mLEDState;
//    }

    ////////////////////////////
//    public LiveData<Boolean> isSupported() {
//        return mIsSupported;
//    }

    public void connect(final ScannedDeviceModel device) {
        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBlinkyManager.setLogger(logSession);
        mBlinkyManager.connect(device.getDevice());
    }

//    private void disconnect() {
//        mBlinkyManager.disconnect();
//    }

    ////////////////////////////
//    public void toggleLED(final boolean onOff) {
//        mBlinkyManager.send(onOff);
//        mLEDState.setValue(onOff);
//    }

//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        if (mBlinkyManager.isConnected())
//            disconnect();
//    }

    ////////////////////////////
    @Override
    public void onDataReceived(final boolean state) {
        mButtonState.postValue(state);
    }

    ////////////////////////////
    @Override
    public void onDataSent(final boolean state) {
        mLEDState.postValue(state);
    }

    @Override
    public void onDeviceConnecting(final BluetoothDevice device) {
        mConnectionState.postValue(getApplication().getString(R.string.state_connecting));
    }

    @Override
    public void onDeviceConnected(final BluetoothDevice device) {
        mIsConnected.postValue(true);
        mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services));
    }

    @Override
    public void onDeviceDisconnecting(final BluetoothDevice device) {
        mIsConnected.postValue(false);
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        mIsConnected.postValue(false);
    }

    @Override
    public void onLinklossOccur(final BluetoothDevice device) {
        mIsConnected.postValue(false);
    }

    @Override
    public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
        mConnectionState.postValue(getApplication().getString(R.string.state_initializing));
    }

    @Override
    public void onDeviceReady(final BluetoothDevice device) {
        mIsSupported.postValue(true);
        mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services_completed, device.getName()));
        mOnDeviceReady.postValue(null);
    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(final BluetoothDevice device) {
        // Blinky doesn't have Battery Service
        return false;
    }

    @Override
    public void onBatteryValueReceived(final BluetoothDevice device, final int value) {
        // Blinky doesn't have Battery Service
    }

    @Override
    public void onBondingRequired(final BluetoothDevice device) {
        // Blinky does not require bonding
    }

    @Override
    public void onBonded(final BluetoothDevice device) {
        // Blinky does not require bonding
    }

    @Override
    public void onError(final BluetoothDevice device, final String message, final int errorCode) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceNotSupported(final BluetoothDevice device) {
        mIsSupported.postValue(false);
    }
    //endregion BLE
}
