package com.projects.company.homes_lock.ui.device.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.ederdoski.simpleble.utils.BluetoothLEHelper;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModelFactory;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.ScannerLiveData;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;
import com.projects.company.homes_lock.utils.mqtt.IMQTTListener;
import com.projects.company.homes_lock.utils.mqtt.MQTTHandler;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

import static com.projects.company.homes_lock.utils.helper.DataHelper.ERROR_CODE_BLE_NOT_ENABLED;
import static com.projects.company.homes_lock.utils.helper.DataHelper.REQUEST_CODE_ACCESS_COARSE_LOCATION;
import static com.projects.company.homes_lock.utils.helper.DataHelper.REQUEST_CODE_ENABLE_BLUETOOTH;

public class LockActivity extends BaseActivity
        implements
        ILockActivity,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        IMQTTListener,
        IBleScanListener,
        LockPageFragment.OnFragmentInteractionListener{

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private Toolbar appBarLockToolbar;
    private DrawerLayout activityLockDrawerLayout;
    private NavigationView activityLockNavigationView;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DeviceViewModel mDeviceViewModel;
    private BleDeviceAdapter mBleDeviceAdapter;
    private static BluetoothGatt mBluetoothGatt;
    private BluetoothLEHelper mBluetoothLEHelper;
    //endregion Declare Objects

    //region Main CallBacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);

        WormDotsIndicator wormDotsIndicator = findViewById(R.id.worm_dots_indicator);

        ViewPager viewPager = findViewById(R.id.view_pager);
        CustomDeviceAdapter mAdapter = new CustomDeviceAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        wormDotsIndicator.setViewPager(viewPager);

//        initMQTT();

        //region Initialize Views
        appBarLockToolbar = findViewById(R.id.appBarLock_toolbar);
        activityLockDrawerLayout = findViewById(R.id.activityLock_drawer_layout);
        activityLockNavigationView = findViewById(R.id.activityLock_navigation_view);
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mBleDeviceAdapter = new BleDeviceAdapter(this, this, new ArrayList<>());
        mBluetoothLEHelper = new BluetoothLEHelper(this);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                activityLockDrawerLayout,
                appBarLockToolbar,
                R.string.content_description_navigation_drawer_open,
                R.string.content_description_navigation_drawer_close);

        this.mDeviceViewModel = ViewModelProviders.of(
                this,
                new DeviceViewModelFactory(this.getApplication(), this))
                .get(DeviceViewModel.class);
        //endregion Initialize Objects

        //region Setup Views
        setSupportActionBar(appBarLockToolbar);

        activityLockDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        activityLockNavigationView.setNavigationItemSelectedListener(this);
        //endregion Setup Views
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewHelper.setContext(LockActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:
                mDeviceViewModel.refresh();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (activityLockDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityLockDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Handle the action_settings
                break;
            case android.R.id.home:
                activityLockDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_locks:
                // Handle the nav_camera action
                break;
            case R.id.nav_notifications:
                // Handle the nav_gallery action
                break;
            case R.id.nav_store:
                // Handle the nav_slideshow action
                break;
            case R.id.nav_support:
                // Handle the nav_manage action
                break;
            case R.id.nav_settings:
                // Handle the nav_share action
                break;
            case R.id.nav_about_us:
                // Handle the nav_send action
                break;
        }

        activityLockDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.appBarLock_fab_addLock:
//                //mDeviceViewModel.getAllServerDevices();
//                getAccessibleBleDevices();
//                break;
//            case R.id.btn1:
//                mDeviceViewModel.writeCharacteristic(
//                        CHARACTERISTIC_UUID_LOCK_COMMAND,
//                        "{\"command\":\"unlock\"}");
//                break;
//            case R.id.btn2:
//                mDeviceViewModel.readCharacteristic(CHARACTERISTIC_UUID_WIFI_LIST);
//                break;
//            case R.id.btn3:
//                mDeviceViewModel.changeNotifyForCharacteristic(CHARACTERISTIC_UUID_LOCK_STATUS, true);
//                break;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
    //endregion Main CallBacks

    //region ViewModel CallBacks
    @Override
    public void getAllDevices() {
//        this.mDeviceViewModel.getAllDevices().observe(this, new Observer<List<Device>>() {
//            @Override
//            public void onChanged(@Nullable final List<Device> words) {
//                mMvpView.showAllWords(words);
//            }
//        });
    }

    @Override
    public void insertDevice(Device device) {
    }

    @Override
    public void deleteDevice(Device device) {

    }
    //endregion ViewModel CallBacks

    //region BLE CallBacks
    @Override
    public void onFindBleCompleted(List response) {
        mBleDeviceAdapter.setBleDevices(response);
    }

    @Override
    public void onFindBleFault(Object response) {
        switch (((FailureModel) response).getFailureCode()) {
            case ERROR_CODE_BLE_NOT_ENABLED: {
                Intent mEnableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(mEnableBluetoothIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
                break;
            }
            default:
                Snackbar.make(appBarLockToolbar, ((FailureModel) response).getFailureMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override//02:80:E1:00:34:12
    public void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel) {
        mDeviceViewModel.connect(mScannedDeviceModel);
        mDeviceViewModel.isConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isConnected) {
//                Toast.makeText(LockActivity.this, String.valueOf(isConnected), Toast.LENGTH_LONG).show();
                if (isConnected) {
                    Toast.makeText(LockActivity.this, String.valueOf(isConnected), Toast.LENGTH_SHORT).show();
//                    mDeviceViewModel.readCharacteristic(CHARACTERISTIC_UUID_LOCK_STATUS);
//                    mDeviceViewModel.changeNotifyForCharacteristic(CHARACTERISTIC_UUID_LOCK_STATUS, true);
//                    ViewHelper.setFragment(LockActivity.this, R.id.frg_lock_activity, new LockPageFragment());
                }
            }
        });
    }

    @Override
    public void onDataReceived(Object value) {
        Log.e("Read done : ", new String(((BluetoothGattCharacteristic) value).getValue()));
    }

    @Override
    public void onDataSent(Object value) {
        Log.e("Write done : ", new String(((BluetoothGattCharacteristic) value).getValue()));
    }
    //endregion BLE CallBacks

    //region MQTT CallBacks
    @Override
    public void onConnectionToBrokerLost(Object response) {

    }

    @Override
    public void onMessageArrived(Object response) {
        MessageModel mMessageModel;
        if (response instanceof MessageModel) {
            mMessageModel = (MessageModel) response;
            String payload = new String(mMessageModel.getMqttMessagePayload());
        }
    }

    @Override
    public void onDeliveryMessageComplete(Object response) {

    }

    @Override
    public void onConnectionSuccessful(Object response) {
        MQTTHandler.subscribe(this);
    }

    @Override
    public void onConnectionFailure(Object response) {

    }

    @Override
    public void onSubscribeSuccessful(Object response) {

    }

    @Override
    public void onSubscribeFailure(Object response) {

    }

    @Override
    public void onPublishSuccessful(Object response) {

    }

    @Override
    public void onPublishFailure(Object response) {

    }
    //endregion MQTT CallBacks

    //region Declare Methods
    private void getAccessibleBleDevices() {
//        if (mBluetoothLEHelper.isReadyForScan())
        scanDevices();
//        this.mDeviceViewModel.getScannerState().observe(this, this::startScan);
//        mBleDeviceAdapter = new BleDeviceAdapter(this, this, null);
    }

    private void startScan(final ScannerLiveData state) {
        // First, check the Location permission. This is required on Marshmallow onwards in order to scan for Bluetooth LE devices.
        if (BleHelper.isLocationPermissionsGranted(this)) {


            // Bluetooth must be enabled
            if (state.isBluetoothEnabled()) {
//                mNoBluetoothView.setVisibility(View.GONE);

                // We are now OK to start scanning
                mDeviceViewModel.startScan();
//                mScanningView.setVisibility(View.VISIBLE);

                if (state.isEmpty()) {
//                    mEmptyView.setVisibility(View.VISIBLE);

                    if (!BleHelper.isLocationRequired(this) || BleHelper.isLocationEnabled(this)) {
//                        mNoLocationView.setVisibility(View.INVISIBLE);
                    } else {
                        onEnableLocationClicked();
//                        mNoLocationView.setVisibility(View.VISIBLE);
                    }
                } else {
//                    mEmptyView.setVisibility(View.GONE);
                }
            } else {
                onEnableBluetoothClicked();
//                mNoBluetoothView.setVisibility(View.VISIBLE);
//                mScanningView.setVisibility(View.INVISIBLE);
//                mEmptyView.setVisibility(View.GONE);
            }
        } else {
            onGrantLocationPermissionClicked();
//            mNoLocationPermissionView.setVisibility(View.VISIBLE);
//            mNoBluetoothView.setVisibility(View.GONE);
//            mScanningView.setVisibility(View.INVISIBLE);
//            mEmptyView.setVisibility(View.GONE);

            final boolean deniedForever = BleHelper.isLocationPermissionDeniedForever(this);
//            mGrantPermissionButton.setVisibility(deniedForever ? View.GONE : View.VISIBLE);
            if (!deniedForever)
                onGrantLocationPermissionClicked();

            if (deniedForever)
                onPermissionSettingsClicked();
//            mPermissionSettingsButton.setVisibility(deniedForever ? View.VISIBLE : View.GONE);
        }
    }

    public void onEnableLocationClicked() {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void onEnableBluetoothClicked() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableIntent);
    }

    public void onGrantLocationPermissionClicked() {
        BleHelper.markLocationPermissionRequested(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
    }

    public void onPermissionSettingsClicked() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    private void stopScan() {
        this.mDeviceViewModel.stopScan();
    }

    private void initMQTT() {
        MQTTHandler.setup(this, this);
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void scanDevices() {
        if (!mBluetoothLEHelper.isScanning()) {
            mBluetoothLEHelper.setScanPeriod(1000);
            Handler mHandler = new Handler();
            mBluetoothLEHelper.scanLeDevice(true);

            mHandler.postDelayed(() -> {
                mBleDeviceAdapter.setBleDevices(getListOfScannedDevices());
            }, mBluetoothLEHelper.getScanPeriod());
        }
    }

    public List<ScannedDeviceModel> getListOfScannedDevices() {

        List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();

        if (mBluetoothLEHelper.getListDevices().size() > 0)
            for (BluetoothLE device : mBluetoothLEHelper.getListDevices())
                mScannedDeviceModelList.add(new ScannedDeviceModel(device));

        return mScannedDeviceModelList;
    }
    //endregion Declare Methods
}
