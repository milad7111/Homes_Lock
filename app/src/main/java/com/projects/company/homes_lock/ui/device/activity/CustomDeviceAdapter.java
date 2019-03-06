package com.projects.company.homes_lock.ui.device.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.ui.device.fragment.adddevice.AddDeviceFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomDeviceAdapter extends SmartFragmentStatePagerAdapter {

    //region Declare List & Arrays
    private List<Device> mDeviceList = new ArrayList<>();
    //endregion Declare List & Arrays

    //region Constructor
    public CustomDeviceAdapter(FragmentManager fragmentManager, List<Device> mDeviceList) {
        super(fragmentManager);

        this.mDeviceList = mDeviceList;
    }
    //endregion Constructor

    //region Main Callbacks
    @Override
    public int getCount() {
        return mDeviceList.size() + 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (position != getCount() - 1)
            return LockPageFragment.newInstance(mDeviceList.get(position));
        else
            return AddDeviceFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
    //endregion Main Callbacks

    //region Declare Methods
    void setDevices(List<Device> devices) {
        this.mDeviceList = devices;
    }
    //region Declare Methods
}