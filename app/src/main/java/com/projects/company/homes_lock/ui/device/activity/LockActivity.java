package com.projects.company.homes_lock.ui.device.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.mqtt.IMQTTListener;
import com.projects.company.homes_lock.utils.mqtt.MQTTHandler;

import java.util.List;

import static com.projects.company.homes_lock.utils.helper.DataHelper.ERROR_CODE_BLE_NOT_ENABLED;
import static com.projects.company.homes_lock.utils.helper.DataHelper.REQUEST_CODE_ENABLE_BLUETOOTH;

public class LockActivity extends BaseActivity
        implements
        ILockActivity,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        IMQTTListener,
        IBleScanListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private Toolbar appBarLockToolbar;
    private DrawerLayout activityLockDrawerLayout;
    private NavigationView activityLockNavigationView;
    private FloatingActionButton appBarLockFabAddLock;
    private RecyclerView rcvBleDevices;
    private BroadcastReceiver mBroadcastReceiver;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DeviceViewModel mDeviceViewModel;
    private BleDeviceAdapter mBleDeviceAdapter;
    //endregion Declare Objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

//        initMQTT();

        //region Initialize Views
        appBarLockToolbar = findViewById(R.id.appBarLock_toolbar);
        appBarLockFabAddLock = findViewById(R.id.appBarLock_fab_addLock);
        activityLockDrawerLayout = findViewById(R.id.activityLock_drawer_layout);
        activityLockNavigationView = findViewById(R.id.activityLock_navigation_view);
        rcvBleDevices = findViewById(R.id.rcv_ble_devices);
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                activityLockDrawerLayout,
                appBarLockToolbar,
                R.string.content_description_navigation_drawer_open,
                R.string.content_description_navigation_drawer_close);

        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        //endregion Initialize Objects

        //region Setup Views
        setSupportActionBar(appBarLockToolbar);

        appBarLockFabAddLock.setOnClickListener(this);

        activityLockDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        activityLockNavigationView.setNavigationItemSelectedListener(this);

//        _lsv.setAdapter(mAdapterOfAvailableBluetoothName);
        //endregion Setup Views

        mDeviceViewModel.getAllDevicesCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer count) {
                Toast.makeText(LockActivity.this, String.valueOf(count), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initMQTT() {
        MQTTHandler.setup(this, this);
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
        getMenuInflater().inflate(R.menu.lock, menu);
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
            case R.id.nav_camera:
                // Handle the nav_camera action
                break;
            case R.id.nav_gallery:
                // Handle the nav_gallery action
                break;
            case R.id.nav_slideshow:
                // Handle the nav_slideshow action
                break;
            case R.id.nav_manage:
                // Handle the nav_manage action
                break;
            case R.id.nav_share:
                // Handle the nav_share action
                break;
            case R.id.nav_send:
                // Handle the nav_send action
                break;
        }

        activityLockDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.appBarLock_fab_addLock:
                //mDeviceViewModel.getAllServerDevices();
                getAccessibleBleDevices();
                break;
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }

    //region Declare Methods
    private void getAccessibleBleDevices() {
        mBleDeviceAdapter = new BleDeviceAdapter(this);
        rcvBleDevices.setAdapter(mBleDeviceAdapter);
        rcvBleDevices.setLayoutManager(new LinearLayoutManager(this));

        mDeviceViewModel.getAllAccessibleBLEDevices(this, this);
    }

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

    @Override
    public void setReceiver(BroadcastReceiver mBroadcastReceiver) {
        this.mBroadcastReceiver = mBroadcastReceiver;
    }
    //endregion Declare Methods
}
