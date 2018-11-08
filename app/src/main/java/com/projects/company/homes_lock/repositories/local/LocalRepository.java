package com.projects.company.homes_lock.repositories.local;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.projects.company.homes_lock.database.base.LockDatabase;
import com.projects.company.homes_lock.database.daos.DeviceDao;
import com.projects.company.homes_lock.database.tables.Device;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LocalRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private DeviceDao mDeviceDao;
    private SharedPreferences mSharedPreferences = null;
    //endregion Declare Objects

    public LocalRepository(Application application) {

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mDeviceDao = LockDatabase.getDatabase(application).deviceDao();
        mSharedPreferences = application.getSharedPreferences(application.getPackageName(), MODE_PRIVATE);
        //endregion Initialize Objects
    }

    //region Device table
    public LiveData<Integer> getAllDevicesCount() {
        return mDeviceDao.getAllDevicesCount();
    }

    public LiveData<List<Device>> getAllDevices() {
        return mDeviceDao.getAllDevices();
    }

    public void insertDevice(Device device) {
        new insertDeviceAsyncTask(mDeviceDao).execute(device);
    }

    public void deleteDevice(Device device) {
        new deleteDeviceAsyncTask(mDeviceDao).execute(device);
    }

    public LiveData<Device> getADevice(String mDeviceObjectId) {
        return mDeviceDao.getADevice(mDeviceObjectId);
    }

    private static class insertDeviceAsyncTask extends AsyncTask<Device, Void, Void> {

        private DeviceDao mDeviceDao;

        insertDeviceAsyncTask(DeviceDao mDeviceDao) {
            this.mDeviceDao = mDeviceDao;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(Device... device) {
            this.mDeviceDao.insert(device);
            return null;
        }
    }

    private static class deleteDeviceAsyncTask extends AsyncTask<Device, Void, Void> {

        private DeviceDao mDeviceDao;

        deleteDeviceAsyncTask(DeviceDao mDeviceDao) {
            this.mDeviceDao = mDeviceDao;
        }

        @Override
        protected Void doInBackground(final Device... params) {
            this.mDeviceDao.delete(params[0]);
            return null;
        }
    }

    public void updateDeviceLockStatus(String mDeviceObjectId, boolean mLockStatus) {
        mDeviceDao.setLockStatus(mDeviceObjectId, mLockStatus);
    }

    public void updateDeviceDoorStatus(String mDeviceObjectId, int mDoorStatus) {
        mDeviceDao.setDoorStatus(mDeviceObjectId, mDoorStatus);
    }

    public void updateDeviceBatteryStatus(String mDeviceObjectId, int mBatteryStatus) {
        mDeviceDao.setBatteryStatus(mDeviceObjectId, mBatteryStatus);
    }

    public void updateDeviceConnectionStatus(String mDeviceObjectId, byte[] connectionSetting) {
        mDeviceDao.setConnectionStatus(mDeviceObjectId,
                connectionSetting[3] >> 4 == 1, connectionSetting[3] << 4 == 1, connectionSetting[4]);
    }

    public void updateDeviceTemperature(String mDeviceObjectId, byte temperature) {
        mDeviceDao.setTemperature(mDeviceObjectId, temperature);
    }

    public void updateDeviceHumidity(String mDeviceObjectId, byte humidity) {
        mDeviceDao.setHumidity(mDeviceObjectId, humidity);
    }

    public void updateDeviceCoLevel(String mDeviceObjectId, byte coLevel) {
        mDeviceDao.setCoLevel(mDeviceObjectId, coLevel);
    }
    //endregion Device table

    //region SharePreferences
    public boolean isFirstTimeLaunchApp() {
        if (mSharedPreferences.getBoolean("firstRun", true)) {
            mSharedPreferences.edit().putBoolean("firstRun", false).commit();
            return true;
        }

        return false;
    }
    //endregion SharePreferences
}