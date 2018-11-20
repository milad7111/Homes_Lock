package com.projects.company.homes_lock.ui.device.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;
import com.projects.company.homes_lock.utils.mqtt.IMQTTListener;
import com.projects.company.homes_lock.utils.mqtt.MQTTHandler;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

import static com.projects.company.homes_lock.utils.helper.DataHelper.REQUEST_CODE_ACCESS_COARSE_LOCATION;

public class LockActivity extends BaseActivity
        implements
        ILockActivity,
        NavigationView.OnNavigationItemSelectedListener,
        IMQTTListener,
        LockPageFragment.OnFragmentInteractionListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private Toolbar appBarLockToolbar;
    private DrawerLayout activityLockDrawerLayout;
    private NavigationView activityLockNavigationView;
    private ViewPager mViewPager;
    WormDotsIndicator mWormDotsIndicator;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DeviceViewModel mDeviceViewModel;
    private CustomDeviceAdapter mAdapter;
    //endregion Declare Objects

    //region Main CallBacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);

        //region Initialize Views
        appBarLockToolbar = findViewById(R.id.appBarLock_toolbar);
        activityLockDrawerLayout = findViewById(R.id.activityLock_drawer_layout);
        activityLockNavigationView = findViewById(R.id.activityLock_navigation_view);
        mViewPager = findViewById(R.id.view_pager);
        mWormDotsIndicator = findViewById(R.id.worm_dots_indicator);
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

        activityLockDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        activityLockNavigationView.setNavigationItemSelectedListener(this);
        //endregion Setup Views

        //region init
        mViewPager.setOffscreenPageLimit(2);
        mAdapter = new CustomDeviceAdapter(getSupportFragmentManager(), new ArrayList<>());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mWormDotsIndicator.setViewPager(mViewPager);

        getAllDevices();
        //endregion init
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewHelper.setContext(LockActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_CODE_ACCESS_COARSE_LOCATION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        DialogHelper.handleEnableLocationDialog(this);
                    break;
            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onFragmentInteraction(Uri uri) {
    }
    //endregion Main CallBacks

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
    public void getAllDevices() {
        this.mDeviceViewModel.getAllLocalDevices().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> devices) {
                if (mAdapter.getCount() == 1) { //just when initialize lockpage we need set adapter
                    mAdapter = new CustomDeviceAdapter(getSupportFragmentManager(), devices);
                    mViewPager.setAdapter(mAdapter);
                }
            }
        });
    }

    private void initMQTT() {
        MQTTHandler.setup(this, this);
    }
    //endregion Declare Methods
}
