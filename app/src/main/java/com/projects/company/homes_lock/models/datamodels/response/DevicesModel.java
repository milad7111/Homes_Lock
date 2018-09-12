package com.projects.company.homes_lock.models.datamodels.response;

import java.util.List;

public class DevicesModel extends BaseModel{
    private List<DeviceModel> devices;

    public List<DeviceModel> getAllDevices() {
        return devices;
    }

    public DeviceModel getDevicesAtPosition(int index) {
        return devices.get(index);
    }

//    // public constructor is necessary for collections
//    public DevicesModel() {
//        devices = new ArrayList<DeviceModel>();
//    }
//
//    public static DevicesModel parseJSON(String response) {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
////        Type collectionType = new TypeToken<List<DeviceModel>>(){}.getType();
//        Gson gson = gsonBuilder.create();
//        DevicesModel devicesModel = gson.fromJson(response, DevicesModel.class);
//        return devicesModel;
//    }
}
