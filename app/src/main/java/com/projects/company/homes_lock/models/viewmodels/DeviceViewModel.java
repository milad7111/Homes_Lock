package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;

import java.util.List;

public class DeviceViewModel extends AndroidViewModel {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    //endregion Declare Objects

    public DeviceViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLocalRepository = new LocalRepository(application);
        mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    //region Device table
    public LiveData<List<Device>> getAllLocalDevices() {
        return mLocalRepository.getAllDevices();
    }

    public LiveData<List<Device>> getAllServerDevices() {
//        mNetworkRepository.getAllDevices(this);
        return mLocalRepository.getAllDevices();
    }

    public void insertLocalDevice(Device device) {
        mLocalRepository.insertDevice(device);
    }

    public void insertLocalDevices(List<Device> devices) {
        for (Device device : devices)
            mLocalRepository.insertDevice(device);
    }

    public void deleteLocalDevice(Device device) {
        mLocalRepository.deleteDevice(device);
    }
    //endregion Device table
}