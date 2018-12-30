package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.HelperModel;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("data/Users/{userObjectId}?relationsDepth=2")
    Call<User> getUserWithObjectId(@Header("user-token") String userToken, @Path("userObjectId") String userObjectId);

    @Headers({"Content-Type: application/json"})
    @GET("data/users?")
    Call<List<User>> getUserListWithEmailAddress(@Header("user-token") String userToken, @Query("where") String whereClause);

    @Headers({"Content-Type: application/json"})
    @POST("services/deviceService/getDeviceObjectIdBySerialNumber")
    Call<ResponseBody> getDeviceObjectIdWithSerialNumber(@Header("user-token") String userToken, @Body String serialNumber);

    @Headers({"Content-Type: application/json"})
    @POST("data/UserLock")
    Call<UserLock> addUserLock(@Header("user-token") String userToken, @Body UserLockModel parameter);

    @Headers({"Content-Type: application/json"})
    @POST("data/UserLock/{userLockObjectId}/relatedDevice")
    Call<ResponseBody> addLockToUserLock(@Header("user-token") String userToken, @Path("userLockObjectId") String userLockObjectId, @Body HelperModel parameter);

    @Headers({"Content-Type: application/json"})
    @POST("data/users/{userObjectId}/relatedUserLocks")
    Call<ResponseBody> addUserLockToUser(@Header("user-token") String userToken, @Path("userObjectId") String userObjectId, @Body HelperModel parameter);

    @Headers({"Content-Type: application/json"})
    @DELETE("data/bulk/UserLock?")
    Call<ResponseBody> removeDeviceForAllMembers(@Header("user-token") String userToken, @Query("where") String whereClause);

    @Headers({"Content-Type: application/json"})
    @DELETE("data/UserLock/{userLockObjectId}")
    Call<ResponseBody> removeDeviceForOneMember(@Header("user-token") String userToken, @Path("userLockObjectId") String userLockObjectId);

    @Headers({"Content-Type: application/json"})
    @GET("data/Users?")
    Call<List<User>> getLockUsersByLockObjectId(@Header("user-token") String userToken, @Query("where") String whereClause, @Query("sortBy") String sortClause);
}
