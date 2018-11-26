package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;

import java.util.List;

import okhttp3.ResponseBody;
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

    //region Declare Methods
    public void login(final NetworkListener.SingleNetworkListener<BaseModel> listener, LoginModel parameter) {
        BaseApplication.getRetrofitAPI().login(parameter).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response != null && response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void register(final NetworkListener.SingleNetworkListener<BaseModel> listener, RegisterModel parameter) {
        BaseApplication.getRetrofitAPI().register(parameter).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response != null && response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void getADevice(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String parameter) {
        BaseApplication.getRetrofitAPI().getADevice(String.format("serialNumber=%s", parameter)).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFailure(new ResponseBodyFailureModel(t.getMessage()));
            }
        });
    }

    public void getAllDevices(final NetworkListener.ListNetworkListener<List<Device>> listener) {
        BaseApplication.getRetrofitAPI().getAllDevices().enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response != null && response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                listener.onFailure(new FailureModel(t.getMessage()));
            }
        });
    }
    //endregion Declare Methods
}