package com.projects.company.homes_lock.ui.device.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.ble.CustomBleCallback;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleDeviceManagerCallbacks;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NearestBleService extends Service implements
        BluetoothAdapter.LeScanCallback,
        CustomBleCallback,
        IBleDeviceManagerCallbacks,
        INearestBleService {
    private BluetoothAdapter mBluetoothAdapter;
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    private List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();
    private List<Integer> recordedRSSIs = new ArrayList<>();
    private DeviceViewModel mDeviceViewModel;
    private Observer<Device> deviceObserver;
    private Device mDevice;
    private boolean serviceIsAlive = true;

    @Override
    public void onCreate() {
        super.onCreate();
        getBTService();

        mDeviceViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DeviceViewModel.class);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NeurNet Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker("1")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Ble device finding ...")
                .setContentText("scan devices, connect to lock, then unlock if you close to door.")
                .setContentInfo("4");

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(1, notificationBuilder.build());

        serviceIsAlive = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStartNearestBleDevicesService();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mBluetoothLEHelper != null) {
            mBluetoothLEHelper.scanLeDevice(false);
            mBluetoothLEHelper = null;
        }

        super.onDestroy();

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.disable();
            mBluetoothAdapter = null;
        }

        serviceIsAlive = false;

        stopForeground(true);
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        stopForeground(true);
        stopSelf();
        return super.stopService(name);
    }

    // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
    // BluetoothAdapter through BluetoothManager.
    public void getBTService() {
        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (btManager != null) {
            mBluetoothAdapter = btManager.getAdapter();
            mBluetoothLEHelper = new CustomBluetoothLEHelper(this, mBluetoothAdapter);
        }
    }

    private void handleStartNearestBleDevicesService() {
        if (!isBluetoothSupported()) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            stopSelf();
        } else {
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                resetState(1000L);
            } else {
                enableDisableBluetooth(true);
                handleStartNearestBleDevicesService();
            }
        }
    }

    public boolean isBluetoothSupported() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static void enableDisableBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (enable) bluetoothAdapter.enable();
            else bluetoothAdapter.disable();
        }
    }

    private void scanDevices() {
        if (serviceIsAlive)
            (new Handler()).postDelayed(() -> {
                if (mBluetoothLEHelper != null && !mBluetoothLEHelper.isScanning()) {
                    mBluetoothLEHelper.scanLeDevice(true);
                    (new Handler()).postDelayed(() -> {
                        if (mBluetoothLEHelper != null) {
                            mBluetoothLEHelper.scanLeDevice(false);
                            handleFoundDevices();
                        }
                    }, 1000);
                } else {
                    if (mBluetoothLEHelper != null){
                        mBluetoothLEHelper.scanLeDevice(false);
                        scanDevices();
                    }
                }
            }, 500);
    }

    private void handleFoundDevices() {
        for (ScannedDeviceModel device : mScannedDeviceModelList) {
            recordedRSSIs.add(device.getRSSI());
            Timber.d("see711:   %s --> rssi = %d", device.getName(), device.getRSSI());

            if (recordedRSSIs.size() == 5) {
                int sum = 0;
                for (int number : recordedRSSIs)
                    sum += number;

                Timber.d("see711:   %s --> Average rssi = %d", device.getName(), sum / 5);

                if (sum / 5 >= -60) {
                    if (mDeviceViewModel.isConnected().getValue() != null && mDeviceViewModel.isConnected().getValue())
                        onDeviceReady(device.getDevice());
                    else {
                        mDeviceViewModel.connect(device.getDevice());
                        new Handler().postDelayed(() -> {
                            if (mDeviceViewModel.isConnected().getValue() != null && mDeviceViewModel.isConnected().getValue())
                                onDeviceReady(device.getDevice());
                        }, 2000);
                    }
                } else resetState(5000L);
            } else scanDevices();
        }

        if (mScannedDeviceModelList.isEmpty()) {
            Timber.d("see711:   Not found any lock.");
            scanDevices();
        }
    }

    public void sendUnLockCommand() {
        mDeviceViewModel.sendLockCommand(this, false);
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        boolean isNewItem = true;

        for (int j = 0; j < mScannedDeviceModelList.size(); j++)
            if (mScannedDeviceModelList.get(j).getMacAddress().equals(bluetoothDevice.getAddress())) {
                isNewItem = false;
                mScannedDeviceModelList.get(j).setRSSI(rssi);
                break;
            }

        if (isNewItem && bluetoothDevice.getAddress().toLowerCase().startsWith("6e:6e")
                && bluetoothDevice.getName().toLowerCase().startsWith("lock"))
            mScannedDeviceModelList.add(new ScannedDeviceModel(bluetoothDevice, rssi, scanRecord));
    }

    @Override
    public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

    }

    @Override
    public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {

    }

    @Override
    public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onDataReceived(Object value) {

    }

    @Override
    public void onDataSent(Object value) {

    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

    }

    @Override
    public void onDeviceConnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {

    }

    @Override
    public void onLinklossOccur(BluetoothDevice device) {

    }

    @Override
    public void onServicesDiscovered(BluetoothDevice device, boolean optionalServicesFound) {

    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {
        serviceIsAlive = false;

        if (deviceObserver != null)
            mDeviceViewModel.getADeviceBySerialNumber(device.getAddress()).removeObserver(deviceObserver);

        deviceObserver = dbDevice -> {
            this.mDevice = dbDevice;
            handleFoundDeviceInDB();
            mDeviceViewModel.getADeviceBySerialNumber(device.getAddress()).removeObserver(deviceObserver);
        };

        mDeviceViewModel.getADeviceBySerialNumber(device.getAddress().toLowerCase().replace(":", "")).observeForever(deviceObserver);
    }

    private void handleFoundDeviceInDB() {
        if (this.mDevice != null)
            if (this.mDevice.getMemberAdminStatus())// && this.mDevice.getIsLocked())
                sendUnLockCommand();

        resetState(5000L);
    }

    private void resetState(Long waitMillis) {
        Timber.d("see711:   reset after " + waitMillis / 1000 + " seconds.");
        (new Handler()).postDelayed(() -> {
            if (mBluetoothLEHelper != null) {
                serviceIsAlive = true;
                mBluetoothLEHelper.scanLeDevice(false);
            }

            mDeviceViewModel.disconnect();
            recordedRSSIs = new ArrayList<>();
            mScannedDeviceModelList = new ArrayList<>();
            scanDevices();
            Timber.d("see711:   reset DONE!");
        }, waitMillis);
    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(BluetoothDevice device) {
        return false;
    }

    @Override
    public void onBatteryValueReceived(BluetoothDevice device, int value) {

    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {

    }

    @Override
    public void onBonded(BluetoothDevice device) {

    }

    @Override
    public void onError(BluetoothDevice device, String message, int errorCode) {

    }

    @Override
    public void onDeviceNotSupported(BluetoothDevice device) {
    }

    public Device getDevice() {
        return this.mDevice;
    }
}
