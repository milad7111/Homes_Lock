package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.utils.ble.BleScanner;
import com.projects.company.homes_lock.utils.ble.BlinkyManager;
import com.projects.company.homes_lock.utils.ble.BlinkyManagerCallbacks;
import com.projects.company.homes_lock.utils.ble.ExtendedBluetoothDevice;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;
import com.projects.company.homes_lock.utils.helper.DataHelper;

import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.widget.Toast;

//import no.nordicsemi.android.blinky.adapter.ExtendedBluetoothDevice;
//import no.nordicsemi.android.blinky.profile.BlinkyManager;
//import no.nordicsemi.android.blinky.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.log.ILogSession;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class DeviceViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        BlinkyManagerCallbacks {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    private BleScanner mBleScanner;

    private final BlinkyManager mBlinkyManager;
    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();
    private final MutableLiveData<Void> mOnDeviceReady = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mLEDState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mButtonState = new MutableLiveData<>();
    //endregion Declare Objects

    public DeviceViewModel(Application application) {
        super(application);

        // Initialize the manager
        mBlinkyManager = new BlinkyManager(getApplication());
        mBlinkyManager.setGattCallbacks(this);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLocalRepository = new LocalRepository(application);
        mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    //region Device table
    public LiveData<Integer> getAllDevicesCount(){
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

    //region BLE
    public void getAllAccessibleBLEDevices(Context context, IBleScanListener mIBleScanListener){
        mBleScanner = new BleScanner(context, mIBleScanListener);
    }

    public LiveData<Void> isDeviceReady() {
        return mOnDeviceReady;
    }

    public LiveData<String> getConnectionState() {
        return mConnectionState;
    }

    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    ////////////////////////////
    public LiveData<Boolean> getButtonState() {
        return mButtonState;
    }

    ////////////////////////////
    public LiveData<Boolean> getLEDState() {
        return mLEDState;
    }

    ////////////////////////////
    public LiveData<Boolean> isSupported() {
        return mIsSupported;
    }

    public void connect(final ScannedDeviceModel device) {
        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBlinkyManager.setLogger(logSession);
        mBlinkyManager.connect(device.getDevice());
    }

    private void disconnect() {
        mBlinkyManager.disconnect();
    }

    ////////////////////////////
    public void toggleLED(final boolean onOff) {
        mBlinkyManager.send(onOff);
        mLEDState.setValue(onOff);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mBlinkyManager.isConnected())
            disconnect();
    }

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