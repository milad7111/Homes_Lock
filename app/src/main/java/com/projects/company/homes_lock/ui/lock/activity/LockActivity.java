package com.projects.company.homes_lock.ui.lock.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.R;

public class LockActivity extends BaseActivity
        implements
        LockActivityContract.mMvpView,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private Toolbar _appBarLock_toolbar;
    private DrawerLayout _activityLock_drawer_layout;
    private NavigationView _activityLock_navigation_view;
    private FloatingActionButton _appBarLock_fab_addLock;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LockActivityPresenter mLockActivityPresenter;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    //endregion Declare Objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        //region Initialize Views
        _appBarLock_toolbar = findViewById(R.id.appBarLock_toolbar);
        _appBarLock_fab_addLock = findViewById(R.id.appBarLock_fab_addLock);
        _activityLock_drawer_layout = findViewById(R.id.activityLock_drawer_layout);
        _activityLock_navigation_view = findViewById(R.id.activityLock_navigation_view);
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLockActivityPresenter = new LockActivityPresenter(LockActivity.this);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                _activityLock_drawer_layout,
                _appBarLock_toolbar,
                R.string.content_description_navigation_drawer_open,
                R.string.content_description_navigation_drawer_close);
        //endregion Initialize Objects

        //region Setup Views
        setSupportActionBar(_appBarLock_toolbar);

        _appBarLock_fab_addLock.setOnClickListener(this);

        _activityLock_drawer_layout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        _activityLock_navigation_view.setNavigationItemSelectedListener(this);
        //endregion Setup Views
    }

    @Override
    public void onBackPressed() {
        if (_activityLock_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            _activityLock_drawer_layout.closeDrawer(GravityCompat.START);
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
                _activityLock_drawer_layout.openDrawer(GravityCompat.START);
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

        _activityLock_drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.appBarLock_fab_addLock:
                Snackbar.make(
                        view,
                        "Replace with your own action",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }

    //region Declare Methods
    //endregion Declare Methods
}
