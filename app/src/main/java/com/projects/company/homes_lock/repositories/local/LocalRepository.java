package com.projects.company.homes_lock.repositories.local;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.projects.company.homes_lock.database.base.LockDatabase;
import com.projects.company.homes_lock.database.daos.DeviceDao;
import com.projects.company.homes_lock.database.daos.DeviceErrorDao;
import com.projects.company.homes_lock.database.daos.ErrorDao;
import com.projects.company.homes_lock.database.daos.LockUserViewDao;
import com.projects.company.homes_lock.database.daos.UserDao;
import com.projects.company.homes_lock.database.daos.UserLockDao;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LocalRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private UserDao mUserDao;
    private UserLockDao mUserLockDao;
    private DeviceDao mDeviceDao;
    private DeviceErrorDao mDeviceErrorDao;
    private ErrorDao mErrorDao;
    private LockUserViewDao mLockUserViewDao;
    private SharedPreferences mSharedPreferences = null;
    private static ILocalRepository mILocalRepository = null;
    //endregion Declare Objects

    public LocalRepository(Application application) {

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mUserDao = LockDatabase.getDatabase(application).userDao();
        mUserLockDao = LockDatabase.getDatabase(application).userLockDao();
        mDeviceDao = LockDatabase.getDatabase(application).deviceDao();
        mDeviceErrorDao = LockDatabase.getDatabase(application).deviceErrorDao();
        mErrorDao = LockDatabase.getDatabase(application).errorDao();
        mLockUserViewDao = LockDatabase.getDatabase(application).lockUserViewDao();
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

    public List<Device> getAllUserLocks() {
        return mLockUserViewDao.getAllUserLocks();
    }

    public void insertDevice(Device device) {
        new insertDeviceAsyncTask(mDeviceDao).execute(device);
    }

    public void deleteDevice(Device device) {
        new deleteDeviceAsyncTask(mDeviceDao).execute(device);
    }

    public LiveData<Device> getADevice(String mDeviceObjectId) {
        return mDeviceDao.getDevice(mDeviceObjectId);
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

    public void updateDevice(Device device) {
        new updateDeviceAsyncTask(mDeviceDao).execute(device);
    }
    //endregion Device table

    //region User and UserLock table
    public void insertUser(User user, ILocalRepository mILocalRepository) {
        this.mILocalRepository = mILocalRepository;
        new clearAllDataAndInsertUserAsyncTask(mUserDao, mUserLockDao, mDeviceDao, mDeviceErrorDao, mErrorDao, user).execute();
    }

    public Device getUserLockInfo(String objectId) {
        return mLockUserViewDao.getUserLockInfo(objectId);
    }
    //endregion User and UserLock table

    //region Declare Classes & Interfaces
    private static class insertDeviceAsyncTask extends AsyncTask<Device, Void, Void> {

        private DeviceDao mDeviceDao;

        insertDeviceAsyncTask(DeviceDao mDeviceDao) {
            this.mDeviceDao = mDeviceDao;
        }

        @Override
        protected final Void doInBackground(Device... device) {
            this.mDeviceDao.insert(device);
            return null;
        }
    }

    private static class updateDeviceAsyncTask extends AsyncTask<Device, Void, Void> {

        private DeviceDao mDeviceDao;

        updateDeviceAsyncTask(DeviceDao mDeviceDao) {
            this.mDeviceDao = mDeviceDao;
        }

        @Override
        protected final Void doInBackground(Device... device) {
            this.mDeviceDao.update(device);
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

    private static class insertUserAsyncTask extends AsyncTask<User, Void, Long> {

        private UserDao mUserDao;

        insertUserAsyncTask(UserDao mUserDao) {
            this.mUserDao = mUserDao;
        }

        @Override
        protected Long doInBackground(User... users) {
            return this.mUserDao.insert(users[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            if (mILocalRepository != null)
                mILocalRepository.onDataInsert(id);
        }
    }

    private static class clearAllDataAndInsertUserAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserDao mUserDao;
        private UserLockDao mUserLockDao;
        private DeviceDao mDeviceDao;
        private DeviceErrorDao mDeviceErrorDao;
        private ErrorDao mErrorDao;
        private User mUser;

        clearAllDataAndInsertUserAsyncTask(
                UserDao mUserDao, UserLockDao mUserLockDao, DeviceDao mDeviceDao,
                DeviceErrorDao mDeviceErrorDao, ErrorDao mErrorDao, User mUser) {
            this.mUserDao = mUserDao;
            this.mUserLockDao = mUserLockDao;
            this.mDeviceDao = mDeviceDao;
            this.mDeviceErrorDao = mDeviceErrorDao;
            this.mErrorDao = mErrorDao;
            this.mUser = mUser;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.mUserDao.clearAllData();
            this.mUserLockDao.clearAllData();
            this.mDeviceDao.clearAllData();
            this.mDeviceErrorDao.clearAllData();
            this.mErrorDao.clearAllData();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mILocalRepository != null)
                mILocalRepository.onClearAllData();
            new insertUserAsyncTask(mUserDao).execute(mUser);
        }
    }
    //endregion Declare Classes & Interfaces

    //region SharePreferences
    public boolean isFirstTimeLaunchApp() {
        if (mSharedPreferences.getBoolean("firstRun", true)) {
            mSharedPreferences.edit().putBoolean("firstRun", false).apply();
            return true;
        }

        return false;
    }
    //endregion SharePreferences
}