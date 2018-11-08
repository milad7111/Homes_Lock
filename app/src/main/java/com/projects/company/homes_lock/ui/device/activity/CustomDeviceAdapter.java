package com.projects.company.homes_lock.ui.device.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.response.DeviceModel;
import com.projects.company.homes_lock.ui.device.fragment.addlock.AddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomDeviceAdapter extends SmartFragmentStatePagerAdapter {
    private List<Device> mDeviceList = new ArrayList<>();

    public CustomDeviceAdapter(FragmentManager fragmentManager, List<Device> mDeviceList) {
        super(fragmentManager);

        this.mDeviceList = mDeviceList;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mDeviceList.size() + 1;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if (position != getCount() - 1)
            return LockPageFragment.newInstance(mDeviceList.get(position));
        else
            return AddLockFragment.newInstance();
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

//    public void setDeviceList(List<Device> mDeviceList){
//        this.mDeviceList = mDeviceList;
//        this.notifyDataSetChanged();
//    }
}