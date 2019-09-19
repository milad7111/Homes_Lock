package com.projects.company.homes_lock.utils;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.projects.company.homes_lock.BuildConfig;
import com.projects.company.homes_lock.R;

import java.util.List;

import timber.log.Timber;

/**
 * Both RX and RSSI (Received Signal Strength Indication) are indications of the power level being received
 * by an antenna
 * The difference between RX and RSSI is that RX is measured in milliWatts (mW) or decibel-milliwatts (dBm)
 * whereas RSSI is a signal strength percentage—the higher the RSSI number, the stronger the signal
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconService extends Service implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = BeaconService.class.getSimpleName();

    private BluetoothGatt btGatt;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        writeLine("Automate service created...");
        getBTService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        writeLine("Automate service start...");
        if (!isBluetoothSupported()) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            stopSelf();
        } else {
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                startBLEscan();
            } else {
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        writeLine("Automate service destroyed...");
        stopBLEscan();
        super.onDestroy();

        if (btGatt != null) {
            btGatt.disconnect();
            btGatt.close();
            btGatt = null;
        }
    }

    @Override
    public boolean stopService(Intent name) {
        writeLine("Automate service stop...");
        stopSelf();
        return super.stopService(name);
    }

    // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
    // BluetoothAdapter through BluetoothManager.
    public BluetoothAdapter getBTService() {
        BluetoothManager btManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);

        if (btManager != null)
            mBluetoothAdapter = btManager.getAdapter();

        return mBluetoothAdapter;
    }

    public boolean isBluetoothSupported() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public void startBLEscan() {
        mBluetoothAdapter.startLeScan(this);
    }

    public void stopBLEscan() {
        mBluetoothAdapter.stopLeScan(this);
    }

    /**
     * @param enable
     */
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            startBLEscan();
        } else {
            stopBLEscan();
        }
    }

    public static void enableDisableBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (enable) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        if (BuildConfig.DEBUG)
            android.os.Debug.waitForDebugger();

        if (device != null && device.getName() != null) {
            //Log.d(TAG + " onLeScan: ", "Name: "+device.getName() + "Address: "+device.getAddress()+ "RSSI: "+rssi);
            if (rssi > -90 && rssi < -1) {
                writeLine("Automate service BLE device in range: " + device.getName() + " " + rssi);
                if (device.getName().equalsIgnoreCase("NCS_Beacon") || device.getName().equalsIgnoreCase("estimote")) {
                    //This Main looper thread is main for connect gatt, don't remove it
                    // Although you need to pass an appropriate context getApplicationContext(),
                    //Here if you use Looper.getMainLooper() it will stop getting callback and give internal exception fail to register //callback
                    new Handler(getApplicationContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            btGatt = device.connectGatt(getApplicationContext(), false, bleGattCallback);
                            Timber.e("onLeScan btGatt value returning from connectGatt %s", btGatt);
                        }
                    });
                }
                stopBLEscan();
            } else {
                //Log.v("Device Scan Activity", device.getAddress()+" "+"BT device is still too far - not connecting");
            }
        }
    }

    BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            writeLine("Automate service connection state: " + newState);
            if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                writeLine("Automate service connection state: STATE_CONNECTED");
                Timber.tag("BLEService").v("BLE Connected now discover services");
                Timber.tag("BLEService").v("BLE Connected");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeLine("Automate service go for discover services");
                        gatt.discoverServices();
                    }
                }).start();
            } else if (newState == android.bluetooth.BluetoothProfile.STATE_DISCONNECTED) {
                writeLine("Automate service connection state: STATE_DISCONNECTED");
                Timber.tag("BLEService").v("BLE Disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeLine("Automate service discover service: GATT_SUCCESS");
                Timber.tag("BLEService").v("BLE Services onServicesDiscovered");
                //Get service
                List<BluetoothGattService> services = gatt.getServices();
                writeLine("Automate service discover service imei: " + "imei");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    private void writeLine(final String message) {
        Handler h = new Handler(getApplicationContext().getMainLooper());
        // Although you need to pass an appropriate context
        h.post(() -> Timber.e(message));
    }
}
