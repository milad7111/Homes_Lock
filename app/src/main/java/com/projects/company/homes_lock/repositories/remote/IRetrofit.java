package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.DeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.DevicesModel;
import com.projects.company.homes_lock.models.datamodels.response.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IRetrofit {

    @POST("users/login")
    Call<UserModel> login(@Body LoginModel parameter);

    @POST("users/register")
    Call<UserModel> register(@Body RegisterModel parameter);

    @GET("/data/Device")
    Call<List<DeviceModel>> getAllDevices();
}
