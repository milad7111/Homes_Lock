package com.projects.company.homes_lock.ui.device.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.ui.device.fragment.adddevice.AddDeviceFragment;
import com.projects.company.homes_lock.ui.device.fragment.gatewaypage.GatewayPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;

import java.util.InputMismatchException;
import java.util.List;

public class CustomDeviceAdapter extends SmartFragmentStatePagerAdapter {

    //region Declare List & Arrays
    private List<Device> mDeviceList;
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
        if (position != getCount() - 1) {
            if (mDeviceList.get(position).getDeviceType().equals("LOCK"))
                return LockPageFragment.newInstance(mDeviceList.get(position));
            else if (mDeviceList.get(position).getDeviceType().equals("GTWY"))
                return GatewayPageFragment.newInstance(mDeviceList.get(position));

            throw new InputMismatchException();
        } else
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