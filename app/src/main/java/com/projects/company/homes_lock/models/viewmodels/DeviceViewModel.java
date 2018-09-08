package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.repositories.DeviceRepository;

import java.util.List;

public class DeviceViewModel extends AndroidViewModel {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private DeviceRepository mDeviceRepository;
    //endregion Declare Objects

    public DeviceViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mDeviceRepository = new DeviceRepository(application);
        //endregion Initialize Objects
    }

    //region Device table
    public LiveData<List<Device>> getAllDevices() {
        return mDeviceRepository.getAllDevices();
    }

    public void insertDevice(Device device) {
        mDeviceRepository.insertDevice(device);
    }

    public void deleteDevice(Device device) {
        mDeviceRepository.deleteDevice(device);
    }
    //endregion Device table
}