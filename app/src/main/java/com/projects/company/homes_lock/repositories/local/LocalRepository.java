package com.projects.company.homes_lock.repositories.local;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.projects.company.homes_lock.database.base.LockDatabase;
import com.projects.company.homes_lock.database.daos.DeviceDao;
import com.projects.company.homes_lock.database.daos.DeviceErrorDao;
import com.projects.company.homes_lock.database.daos.ErrorDao;
import com.projects.company.homes_lock.database.daos.LockUserDao;
import com.projects.company.homes_lock.database.daos.NotificationDao;
import com.projects.company.homes_lock.database.daos.UserDao;
import com.projects.company.homes_lock.database.daos.UserLockDao;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.Notification;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;

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
    private NotificationDao mNotificationDao;
    private LockUserDao mLockUserDao;
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
        mLockUserDao = LockDatabase.getDatabase(application).lockUserDao();
        mNotificationDao = LockDatabase.getDatabase(application).notificationDao();
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

    public LiveData<UserLock> getUserLockInfo(String objectId) {
        return mLockUserDao.getUserLockInfo(objectId);
    }

    public List<Device> getAllUserLocks() {
        return mLockUserDao.getAllUserLocks();
    }

    public void insertDevice(ILocalRepository iLocalRepository, Device device) {
        mILocalRepository = iLocalRepository;
        new insertDeviceAsyncTask(mDeviceDao).execute(device);
    }

    public void deleteDevice(Device device) {
        new deleteDeviceAsyncTask(mUserDao).execute(device);
    }

    public LiveData<Device> getADevice(String mDeviceObjectId) {
        return mDeviceDao.getDevice(mDeviceObjectId);
    }

    public void updateDeviceIsLocked(String mDeviceObjectId, boolean mIsLocked) {
        mDeviceDao.setIsLocked(mDeviceObjectId, mIsLocked);
    }

    public void updateDeviceIsDoorClosed(String mDeviceObjectId, boolean mIsDoorClosed) {
        mDeviceDao.setIsDoorClosed(mDeviceObjectId, mIsDoorClosed);
    }

    public void updateDeviceBatteryPercentage(String mDeviceObjectId, int mBatteryStatus) {
        mDeviceDao.setBatteryStatus(mDeviceObjectId, mBatteryStatus);
    }

    public void updateDeviceWifiStatus(String mDeviceObjectId, boolean mWifiStatus) {
        mDeviceDao.setWifiStatus(mDeviceObjectId, mWifiStatus);
    }

    public void updateConnectedWifiStrength(String mDeviceObjectId, int mWifiStrength) {
        mDeviceDao.setWifiStrength(mDeviceObjectId, mWifiStrength);
    }

    public void updateDeviceInternetStatus(String mDeviceObjectId, boolean mInternetStatus) {
        mDeviceDao.setInternetStatus(mDeviceObjectId, mInternetStatus);
    }

    public void updateDeviceMQTTServerStatus(String mDeviceObjectId, boolean mMQTTServerStatus) {
        mDeviceDao.setMQTTServerStatus(mDeviceObjectId, mMQTTServerStatus);
    }

    public void updateDeviceRestApiServerStatus(String mDeviceObjectId, boolean mRestApiServerStatus) {
        mDeviceDao.setRestApiServerStatus(mDeviceObjectId, mRestApiServerStatus);
    }

    public void updateDeviceType(String mDeviceObjectId, String deviceType) {
        mDeviceDao.setDeviceType(mDeviceObjectId, deviceType);
    }

    public void updateFirmwareVersion(String mDeviceObjectId, String firmwareVersion) {
        mDeviceDao.setFirmwareVersion(mDeviceObjectId, firmwareVersion);
    }

    public void updateHardwareVersion(String mDeviceObjectId, String hardwareVersion) {
        mDeviceDao.setHardwareVersion(mDeviceObjectId, hardwareVersion);
    }

    public void updateProductionDate(String mDeviceObjectId, String productionDate) {
        mDeviceDao.setProductionDate(mDeviceObjectId, productionDate);
    }

    public void updateSerialNumber(String mDeviceObjectId, String serialNumber) {
        mDeviceDao.setSerialNumber(mDeviceObjectId, serialNumber);
    }

    public void updateDynamicId(String mDeviceObjectId, String dynamicId) {
        mDeviceDao.setDynamicId(mDeviceObjectId, dynamicId);
    }

    public void updateConnectedDevicesCount(String mDeviceObjectId, String count) {
        mDeviceDao.setConnectedDevicesCount(mDeviceObjectId, count);
    }

    public void updateConnectedClientsCount(String mDeviceObjectId, int connectedClientsCount) {
        mDeviceDao.setConnectedClientsCount(mDeviceObjectId, connectedClientsCount);
    }

    public void updateConnectedServersCount(String mDeviceObjectId, int connectedServersCount) {
        mDeviceDao.setConnectedServersCount(mDeviceObjectId, connectedServersCount);
    }

    public void updateDoorInstallation(String mDeviceObjectId, Boolean doorInstallation) {
        mDeviceDao.setDoorInstallation(mDeviceObjectId, doorInstallation);
    }

    public void updateDeviceConfigStatus(String mDeviceObjectId, boolean configStatus) {
        mDeviceDao.setConfigStatus(mDeviceObjectId, configStatus);
    }

    public void updateDevice(Device device) {
        new updateDeviceAsyncTask(mDeviceDao).execute(device);
    }
    //endregion Device table

    //region User and UserLock table
    public void insertUser(ILocalRepository iLocalRepository, User user) {
        mILocalRepository = iLocalRepository;
        new clearAllDataAndInsertUserAsyncTask(mUserDao, mUserLockDao, mDeviceDao, mDeviceErrorDao, mErrorDao, user).execute();
    }
    //endregion User and UserLock table

    //region Notification table
    public void insertNotification(ILocalRepository iLocalRepository, Notification notification) {
        mILocalRepository = iLocalRepository;
        new insertNotificationAsyncTask(mNotificationDao).execute(notification);
    }

    public LiveData<List<Notification>> getAllNotifications() {
        return mNotificationDao.getAllNotifications();
    }
    //endregion Notification table

    //region Declare Classes & Interfaces
    private static class insertDeviceAsyncTask extends AsyncTask<Device, Void, Device> {

        private DeviceDao mDeviceDao;

        insertDeviceAsyncTask(DeviceDao mDeviceDao) {
            this.mDeviceDao = mDeviceDao;
        }

        @Override
        protected final Device doInBackground(Device... device) {
            this.mDeviceDao.insert(device[0]);
            return device[0];
        }

        @Override
        protected void onPostExecute(Device device) {
            super.onPostExecute(device);
            if (mILocalRepository != null)
                mILocalRepository.onDataInsert(device);
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

        private UserDao mUSerDao;

        deleteDeviceAsyncTask(UserDao mUserDao) {
            this.mUSerDao = mUserDao;
        }

        @Override
        protected Void doInBackground(final Device... params) {
            this.mUSerDao.deleteDevice(params[0].getObjectId());
            return null;
        }
    }

    private static class insertUserAsyncTask extends AsyncTask<User, Void, User> {

        private UserDao mUserDao;

        insertUserAsyncTask(UserDao mUserDao) {
            this.mUserDao = mUserDao;
        }

        @Override
        protected User doInBackground(User... users) {
            this.mUserDao.insert(users[0]);

            return users[0];
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (mILocalRepository != null)
                mILocalRepository.onDataInsert(user);
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

    private static class insertNotificationAsyncTask extends AsyncTask<Notification, Void, Notification> {

        private NotificationDao mNotificationDao;

        insertNotificationAsyncTask(NotificationDao mNotificationDao) {
            this.mNotificationDao = mNotificationDao;
        }

        @Override
        protected Notification doInBackground(Notification... notifications) {
            this.mNotificationDao.insert(notifications[0]);

            return notifications[0];
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            if (mILocalRepository != null)
                mILocalRepository.onDataInsert(notification);
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