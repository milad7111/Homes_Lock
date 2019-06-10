package com.projects.company.homes_lock.ui.device.activity;

import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.aboutus.AboutUsActivity;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.DeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.ui.notification.NotificationActivity;
import com.projects.company.homes_lock.ui.proservices.activity.ProServicesActivity;
import com.projects.company.homes_lock.ui.setting.SettingActivity;
import com.projects.company.homes_lock.ui.support.SupportActivity;
import com.projects.company.homes_lock.utils.helper.ViewHelper;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

import timber.log.Timber;

import static com.projects.company.homes_lock.utils.mqtt.MQTTHandler.mqttDisconnect;

public class DeviceActivity extends BaseActivity
        implements
        ILockActivity,
        NavigationView.OnNavigationItemSelectedListener,
        LockPageFragment.OnFragmentInteractionListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private DrawerLayout activityLockDrawerLayout;
    public ViewPager mViewPager;
    public WormDotsIndicator mWormDotsIndicator;
    //endregion Declare Views

    //region Declare Variables
    public static boolean PERMISSION_READ_ALL_LOCAL_DEVICES = true;
    private String mNotSyncedDevicesMessage = "";
    //endregion Declare Variables

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
    private CustomDeviceAdapter mAdapter;
    //endregion Declare Objects

    //region Main CallBacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //region Initialize Variables
        PERMISSION_READ_ALL_LOCAL_DEVICES = true;
        //region Initialize Variables

        //region Declare Local Views
        Toolbar appBarLockToolbar = findViewById(R.id.appBarLock_toolbar);
        NavigationView activityLockNavigationView = findViewById(R.id.activityLock_navigation_view);
        //endregion Declare Local Views

        //region Initialize Views
        activityLockDrawerLayout = findViewById(R.id.activityLock_drawer_layout);
        mViewPager = findViewById(R.id.view_pager);
        mWormDotsIndicator = findViewById(R.id.worm_dots_indicator);
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Declare Local Objects
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                activityLockDrawerLayout,
                appBarLockToolbar,
                R.string.content_description_navigation_drawer_open,
                R.string.content_description_navigation_drawer_close);
        //endregion Declare Local Objects

        //region Initialize Objects
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
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mWormDotsIndicator.setViewPager(mViewPager);

        setViewPagerAdapter(mAdapter);
        readExtraData();
        getAllDevices();
        handleNotSyncedDevicesMessage();
        //endregion init
    }

    private void handleNotSyncedDevicesMessage() {
        if (!mNotSyncedDevicesMessage.isEmpty()) {
            Snackbar mSnackBar =
                    Snackbar.make(
                            mViewPager,
                            mNotSyncedDevicesMessage,
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> {
                    });

            View mSnackBarView = mSnackBar.getView();
            TextView textView = mSnackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(5);

            mSnackBar.show();
        }
    }

    private void readExtraData() {
        if (getIntent().hasExtra("notSyncedDevicesMessage"))
            mNotSyncedDevicesMessage = getIntent().getStringExtra("notSyncedDevicesMessage");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewHelper.setContext(DeviceActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (activityLockDrawerLayout.isDrawerOpen(GravityCompat.START))
            activityLockDrawerLayout.closeDrawer(GravityCompat.START);
        else {
            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.frg_device_activity);

            if (mFragment instanceof ManageMembersFragment)
                getSupportFragmentManager().popBackStackImmediate();
            else if (mFragment instanceof DeviceSettingFragment)
                getSupportFragmentManager().popBackStackImmediate();
            else if (mViewPager.getChildCount() == 0) {
                mqttDisconnect();
                finish();
            } else {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttDisconnect();
    }

    //endregion Main CallBacks

    //region Declare Methods
    private void handleNavigationItemSelected(int itemId) {
        switch (itemId) {
            case R.id.nav_locks:
                if (!getTopActivityClassName().equals(DeviceActivity.class.getName()))
                    startActivity(new Intent(DeviceActivity.this, DeviceActivity.class));
                break;
            case R.id.nav_notifications:
                if (!getTopActivityClassName().equals(NotificationActivity.class.getName()))
                    startActivity(new Intent(DeviceActivity.this, NotificationActivity.class));
                break;
            case R.id.nav_store:
                if (!getTopActivityClassName().equals(ProServicesActivity.class.getName()))
                    startActivity(new Intent(DeviceActivity.this, ProServicesActivity.class));
                break;
            case R.id.nav_support:
                if (!getTopActivityClassName().equals(SupportActivity.class.getName()))
                    startActivity(new Intent(DeviceActivity.this, SupportActivity.class));
                break;
            case R.id.nav_settings:
                if (!getTopActivityClassName().equals(SettingActivity.class.getName()))
                    startActivity(new Intent(DeviceActivity.this, SettingActivity.class));
                break;
            case R.id.nav_about_us:
                if (!getTopActivityClassName().equals(AboutUsActivity.class.getName()))
                    startActivity(new Intent(DeviceActivity.this, AboutUsActivity.class));
                break;
        }
    }

    public void getAllDevices() {
        this.mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
            if (PERMISSION_READ_ALL_LOCAL_DEVICES) { //just when initialize lockPage we need set adapter
                mAdapter = new CustomDeviceAdapter(getSupportFragmentManager(), devices);
                setViewPagerAdapter(mAdapter);
                mViewPager.setCurrentItem(0);

                PERMISSION_READ_ALL_LOCAL_DEVICES = false;
            }
        });
    }

    public void setViewPagerAdapter(PagerAdapter adapter) {
        try {
            mViewPager.setAdapter(adapter);
        } catch (Exception e) {
            Timber.e("Set ViewPager Adapter failed.");
        }
    }

    private String getTopActivityClassName() {
        return ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(1).get(0).topActivity.getClassName();
    }
    //endregion Declare Methods
}
