package com.projects.company.homes_lock.ui.device.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.projects.company.homes_lock.ui.device.fragment.addlock.AddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomDeviceAdapter extends SmartFragmentStatePagerAdapter {
    private List<String> jlist = new ArrayList<String>();

    public CustomDeviceAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        jlist.add("hey 1");
        jlist.add("hey 2");
        jlist.add("hey 3");
        jlist.add("hey 4");
        jlist.add("hey 5");
        jlist.add("hey 6");
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return jlist.size() + 1;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if (position != getCount() - 1)
            return LockPageFragment.newInstance(jlist.get(position));
        else
            return AddLockFragment.newInstance();
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}