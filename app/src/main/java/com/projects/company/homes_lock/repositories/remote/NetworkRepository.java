package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;
import com.projects.company.homes_lock.models.datamodels.response.DeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.DevicesModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    //endregion Declare Objects

    public void login(final NetworkListener<BaseModel> listener, LoginModel parameter) {
        BaseApplication.getRetrofitAPI().login(parameter).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null && response.body() != null) {
                    listener.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                listener.onFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void register(final NetworkListener<BaseModel> listener, RegisterModel parameter) {
        BaseApplication.getRetrofitAPI().register(parameter).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null && response.body() != null) {
                    listener.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                listener.onFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void getAllDevices(final NetworkListener<BaseModel> listener) {
        BaseApplication.getRetrofitAPI().getAllDevices().enqueue(new Callback<List<DeviceModel>>() {
            @Override
            public void onResponse(Call<List<DeviceModel>> call, Response<List<DeviceModel>> response) {
                if (response != null && response.body() != null) {
                    listener.onResponse(DevicesModel.parseJSON(response.body().toString()));
                }
            }

            @Override
            public void onFailure(Call<List<DeviceModel>> call, Throwable t) {
                listener.onFailure(new FailureModel(t.getMessage()));
            }
        });
    }
}