package com.projects.company.homes_lock.ui.device.activity;

import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.aboutus.AboutUsActivity;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.ui.device.fragment.setting.SettingFragment;
import com.projects.company.homes_lock.ui.notification.NotificationActivity;
import com.projects.company.homes_lock.ui.proservices.activity.ProServicesActivity;
import com.projects.company.homes_lock.ui.setting.SettingActivity;
import com.projects.company.homes_lock.ui.support.SupportActivity;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;
import com.projects.company.homes_lock.utils.mqtt.IMQTTListener;
import com.projects.company.homes_lock.utils.mqtt.MQTTHandler;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

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
    private static ViewPager mViewPager;
    WormDotsIndicator mWormDotsIndicator;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DeviceViewModel mDeviceViewModel;
    private CustomDeviceAdapter mAdapter;
    public static CustomBluetoothLEHelper mBluetoothLEHelper;
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
        setViewPagerAdapter(mAdapter);
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
    public void onBackPressed() {
//        super.onBackPressed();

        if (activityLockDrawerLayout.isDrawerOpen(GravityCompat.START))
            activityLockDrawerLayout.closeDrawer(GravityCompat.START);
        else {
            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.frg_lock_activity);

            if (mFragment instanceof ManageMembersFragment)
                getSupportFragmentManager().popBackStackImmediate();
            else if (mFragment instanceof SettingFragment)
                getSupportFragmentManager().popBackStackImmediate();
            else if (mViewPager.getChildCount() == 0)
                finish();
            else {
                if (mViewPager.getCurrentItem() == 0)
                    finish();
                else if (mViewPager.getCurrentItem() == mViewPager.getChildCount() - 1)
                    mViewPager.setCurrentItem(0);
                else
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_locks:
                handleNavigationItemSelected(R.id.nav_locks);
                break;
            case R.id.nav_notifications:
                handleNavigationItemSelected(R.id.nav_notifications);
                break;
            case R.id.nav_store:
                handleNavigationItemSelected(R.id.nav_store);
                break;
            case R.id.nav_support:
                handleNavigationItemSelected(R.id.nav_support);
                break;
            case R.id.nav_settings:
                handleNavigationItemSelected(R.id.nav_settings);
                break;
            case R.id.nav_about_us:
                handleNavigationItemSelected(R.id.nav_about_us);
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
    private void initMQTT() {
        MQTTHandler.setup(this, this);
    }

    private void handleNavigationItemSelected(int itemId) {
        switch (itemId) {
            case R.id.nav_locks:
                if (!getTopActivityClassName().equals(LockActivity.class.getName()))
                    startActivity(new Intent(LockActivity.this, LockActivity.class));
                break;
            case R.id.nav_notifications:
                if (!getTopActivityClassName().equals(NotificationActivity.class.getName()))
                    startActivity(new Intent(LockActivity.this, NotificationActivity.class));
                break;
            case R.id.nav_store:
                if (!getTopActivityClassName().equals(ProServicesActivity.class.getName()))
                    startActivity(new Intent(LockActivity.this, ProServicesActivity.class));
                break;
            case R.id.nav_support:
                if (!getTopActivityClassName().equals(SupportActivity.class.getName()))
                    startActivity(new Intent(LockActivity.this, SupportActivity.class));
                break;
            case R.id.nav_settings:
                if (!getTopActivityClassName().equals(SettingActivity.class.getName()))
                    startActivity(new Intent(LockActivity.this, SettingActivity.class));
                break;
            case R.id.nav_about_us:
                if (!getTopActivityClassName().equals(AboutUsActivity.class.getName()))
                    startActivity(new Intent(LockActivity.this, AboutUsActivity.class));
                break;
        }
    }

    public void getAllDevices() {
        this.mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
            if (mAdapter.getCount() == 1) { //just when initialize lockPage we need set adapter
                mAdapter = new CustomDeviceAdapter(getSupportFragmentManager(), devices);
                setViewPagerAdapter(mAdapter);
                mViewPager.setCurrentItem(0);
            }
        });
    }

    public static void setViewPagerAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    private String getTopActivityClassName() {
        return ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(1).get(0).topActivity.getClassName();
    }
    //endregion Declare Methods
}
