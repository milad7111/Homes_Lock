package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRetrofit {

    @POST("users/login")
    Call<User> login(@Body LoginModel parameter);

    @POST("users/register")
    Call<User> register(@Body RegisterModel parameter);

    @GET("data/Device/count?")
    Call<ResponseBody> getADevice(@Query("where") String whereClause);

    @GET("data/Device")
    Call<List<Device>> getAllDevices();
}
