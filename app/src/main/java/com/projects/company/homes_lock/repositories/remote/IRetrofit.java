package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.request.TempListModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IRetrofit {

    @POST("users/login")
    Call<User> login(@Body LoginModel parameter);

    @POST("users/register")
    Call<User> register(@Body RegisterModel parameter);

    @Headers({"Content-Type: application/json"})
    @GET("data/Device/count?")
    Call<ResponseBody> getADevice(@Header("user-token") String userToken, @Query("where") String whereClause);

    @GET("data/Device")
    Call<List<Device>> getAllDevices();

    @Headers({"Content-Type: application/json"})
    @POST("data/UserLock")
    Call<UserLock> addUserLock(@Header("user-token") String userToken, @Body UserLockModel parameter);

    @Headers({"Content-Type: application/json"})
    @POST("data/UserLock/{userLockObjectId}/relatedDevice")
    Call<ResponseBody> addLockToUserLock(
            @Header("user-token") String userToken,
            @Path("userLockObjectId") String userLockObjectId,
            @Body TempListModel parameter);

    @Headers({"Content-Type: application/json"})
    @POST("data/users/{userObjectId}/relatedUserLocks")
    Call<ResponseBody> addUserLockToUser(
            @Header("user-token") String userToken,
            @Path("userObjectId") String userObjectId,
            @Body TempListModel parameter);
}
