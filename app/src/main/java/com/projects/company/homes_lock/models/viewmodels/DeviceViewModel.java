package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import java.util.UUID;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

import static com.projects.company.homes_lock.utils.helper.BleHelper.LED_UUID_SERVICE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LED_UUID_SERVICE_CHARACTERISTIC_LED_BUTTON;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_UUID_SERVICE;

public class DeviceViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        IBleDeviceManagerCallbacks,
        IBleScanListener{

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    private IBleScanListener mIBleScanListener;
    private final BleDeviceManager mBleDeviceManager;
    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();
    private final MutableLiveData<Void> mOnDeviceReady = new MutableLiveData<>();
    private final ScannerLiveData mScannerLiveData;
    //region Location Provider Changed Receiver
    private final BroadcastReceiver mLocationProviderChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final boolean enabled = BleHelper.isLocationEnabled(context);
            mScannerLiveData.setLocationEnabled(enabled);
        }
    };
    //endregion Location Provider Changed Receiver
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (BleHelper.isLocationRequired(getApplication()) && !BleHelper.isLocationEnabled(getApplication()))
                BleHelper.markLocationNotRequired(getApplication());

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
    //region Bluetooth State Broadcast Receiver
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
    //endregion Bluetooth State Broadcast Receiver
    //endregion Declare Objects

    public DeviceViewModel(Application application, IBleScanListener mIBleScanListener) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLocalRepository = new LocalRepository(application);
        mNetworkRepository = new NetworkRepository();

        mBleDeviceManager = new BleDeviceManager(getApplication());
        mBleDeviceManager.setGattCallbacks(this);
        mScannerLiveData = new ScannerLiveData(BleHelper.isBleEnabled(), BleHelper.isLocationEnabled(application));
        this.mIBleScanListener = mIBleScanListener;
        //endregion Initialize Objects

        registerBroadcastReceivers(application);
    }

    //region Device table
    public LiveData<Integer> getAllDevicesCount() {
        return mLocalRepository.getAllDevicesCount();
    }

    public LiveData<List<Device>> getAllLocalDevices() {
        return mLocalRepository.getAllDevices();
    }

    public void getAllServerDevices() {
        mNetworkRepository.getAllDevices(this);
    }

    public void insertLocalDevice(Device device) {
        mLocalRepository.insertDevice(device);
    }

    public void insertLocalDevices(List<Device> devices) {
        for (Device device : devices)
            mLocalRepository.insertDevice(device);
    }

    public void deleteLocalDevice(Device device) {
        mLocalRepository.deleteDevice(device);
    }

    @Override
    public void onResponse(Object response) {
        if (DataHelper.isInstanceOfList(response, Device.class.getName()))
            insertLocalDevices((List<Device>) response);
    }

    @Override
    public void onFailure(com.projects.company.homes_lock.models.datamodels.response.FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }

    @Override
    public void onFailure(Object response) {
        FailureModel m = (FailureModel) response;
        Log.i(this.getClass().getSimpleName(), m.getFailureMessage());
    }
    //endregion Device table

    //region BLE CallBacks
    @Override
    protected void onCleared() {
        super.onCleared();
        getApplication().unregisterReceiver(mBluetoothStateBroadcastReceiver);

        if (BleHelper.isMarshmallowOrAbove())
            getApplication().unregisterReceiver(mLocationProviderChangedReceiver);

        if (mBleDeviceManager.isConnected())
            disconnect();
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

    @Override
    public void onDataReceived(Object response) {
        if (mIBleScanListener != null)
            mIBleScanListener.onDataReceived(response);
    }

    @Override
    public void onDataSent() {
        if (mIBleScanListener != null)
            mIBleScanListener.onDataSent();
    }

    @Override
    public void onFindBleCompleted(List response) {
    }

    @Override
    public void onFindBleFault(Object response) {
    }

    @Override
    public void setReceiver(BroadcastReceiver mBroadcastReceiver) {
    }

    @Override
    public void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel) {
    }

    @Override
    public void setBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        if (mIBleScanListener != null)
            mIBleScanListener.setBluetoothGatt(mBluetoothGatt);
    }
    //endregion BLE CallBacks

    //region BLE Methods
    private void registerBroadcastReceivers(final Application application) {
        application.registerReceiver(mBluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (BleHelper.isMarshmallowOrAbove()) {
            application.registerReceiver(mLocationProviderChangedReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
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

//        final ParcelUuid uuid = new ParcelUuid(BleDeviceManager.LBS_UUID_SERVICE);
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

    public ScannerLiveData getScannerState() {
        return mScannerLiveData;
    }

    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    public void connect(final ScannedDeviceModel device) {
        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBleDeviceManager.setLogger(logSession);
        mBleDeviceManager.connect(device.getDevice());
    }

    private void disconnect() {
        mBleDeviceManager.disconnect();
    }

    public void readCharacteristic(BluetoothGatt bluetoothGatt, UUID characteristicUUID) {
        mBleDeviceManager.readCharacteristic(bluetoothGatt, LOCK_UUID_SERVICE, characteristicUUID);
    }

    public void setNotifyForCharacteristic(BluetoothGatt bluetoothGatt, UUID serviceUUID, UUID characteristicUUID, boolean notifyStatus){
        mBleDeviceManager.setNotifyForCharacteristic(bluetoothGatt, serviceUUID, characteristicUUID, notifyStatus);
    }

    public void writeCharacteristic(String s) {
        mBleDeviceManager.writeCharacteristic(null, LED_UUID_SERVICE, LED_UUID_SERVICE_CHARACTERISTIC_LED_BUTTON, s);
    }

    public void getAllAccessibleBLEDevices(Context context, IBleScanListener mIBleScanListener) {
//        mBleScanner = new BleScanner(context, mIBleScanListener);
    }

    public LiveData<Void> isDeviceReady() {
        return mOnDeviceReady;
    }

    public LiveData<String> getConnectionState() {
        return mConnectionState;
    }

    public LiveData<Boolean> isSupported() {
        return mIsSupported;
    }
    //endregion BLE Methods

    //region SharePreferences
    //endregion SharePreferences
}