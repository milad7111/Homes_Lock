package com.projects.company.homes_lock.repositories.remote;

import android.support.annotation.NonNull;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.push.DeviceRegistrationResult;
import com.backendless.rt.data.BulkEvent;
import com.backendless.rt.data.EventHandler;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.AddRelationHelperModel;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.request.TempDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.UserDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
                else
                    listener.onSingleNetworkListenerFailure(
                            new FailureModel((
                                    new ResponseBodyModel(response.errorBody().source().toString(), false)).getMessage()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onSingleNetworkListenerFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void register(final NetworkListener.SingleNetworkListener<BaseModel> listener, RegisterModel parameter) {
        BaseApplication.getRetrofitAPI().register(parameter).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response != null) {
                    if (response.body() != null)
                        listener.onResponse(response.body());
                    else if (response.errorBody() != null)
                        listener.onSingleNetworkListenerFailure(new FailureModel(response.message(), response.code()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onSingleNetworkListenerFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void getUserWithObjectId(final NetworkListener.SingleNetworkListener<BaseModel> listener, String parameter) {
        BaseApplication.getRetrofitAPI().getUserWithObjectId(BaseApplication.activeUserToken, parameter).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response != null && response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onSingleNetworkListenerFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void getDeviceWithObjectId(final NetworkListener.SingleNetworkListener<BaseModel> listener, String parameter) {
        BaseApplication.getRetrofitAPI().getDeviceWithObjectId(BaseApplication.activeUserToken, parameter).enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if (response != null && response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                listener.onSingleNetworkListenerFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void getDeviceObjectIdWithSerialNumber(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String parameter) {
        BaseApplication.getRetrofitAPI().getDeviceObjectIdWithSerialNumber(BaseApplication.activeUserToken, parameter)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null)
                            if (response.body() != null)
                                listener.onResponse(response.body());
                            else if (response.errorBody() != null)
                                listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(response.message()));
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(t.getMessage()));
                    }
                });
    }

    public void insertUserLock(final NetworkListener.SingleNetworkListener<BaseModel> listener, UserDeviceModel parameter) {
        BaseApplication.getRetrofitAPI().addUserLock(BaseApplication.activeUserToken, parameter).enqueue(new Callback<UserLock>() {
            @Override
            public void onResponse(Call<UserLock> call, Response<UserLock> response) {
                if (response.body() != null)
                    listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<UserLock> call, Throwable t) {
                listener.onSingleNetworkListenerFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public void addLockToUserLock(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String userLockObjectId, AddRelationHelperModel parameter) {
        BaseApplication.getRetrofitAPI().addLockToUserLock(
                BaseApplication.activeUserToken,
                userLockObjectId,
                parameter)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null)
                            listener.onResponse(response.body());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(t.getMessage()));
                    }
                });
    }

    public void addUserLockToUser(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String userObjectId, AddRelationHelperModel parameter) {
        BaseApplication.getRetrofitAPI().addUserLockToUser(
                BaseApplication.activeUserToken,
                userObjectId,
                parameter)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.body() != null)
                            listener.onResponse(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(t.getMessage()));
                    }
                });
    }

    public void removeDeviceForAllMembers(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String objectId) {
        BaseApplication.getRetrofitAPI().removeDeviceForAllMembers(BaseApplication.activeUserToken,
                String.format("relatedDevice.objectId='%s'", objectId)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() != null)
                    listener.onResponse(response.body());
                else if (response.errorBody() != null)
                    listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(response.message()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(t.getMessage()));
            }
        });
    }

    public void removeDeviceForOneMember(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String userLockObjectId) {
        BaseApplication.getRetrofitAPI().removeDeviceForOneMember(BaseApplication.activeUserToken, userLockObjectId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null && response.body() != null)
                            listener.onResponse(response.body());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(t.getMessage()));
                    }
                });
    }

    public void getLockUsersByLockObjectId(final NetworkListener.ListNetworkListener<List<User>> listener, String lockObjectId) {
        BaseApplication.getRetrofitAPI().getLockUsersByLockObjectId(
                BaseApplication.activeUserToken,
                String.format("relatedUserLocks.relatedDevice.objectId='%s'", lockObjectId), "relatedUserLocks.created")
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if (response != null && response.body() != null)
                            listener.onResponse(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        listener.onListNetworkListenerFailure(new FailureModel(t.getMessage()));
                    }
                });
    }

    public void getUserListWithEmailAddress(final NetworkListener.ListNetworkListener<List<User>> listener, String emailAddress) {
        BaseApplication.getRetrofitAPI().getUserListWithEmailAddress(
                BaseApplication.activeUserToken,
                String.format("email='%s'", emailAddress)).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response != null)
                    if (response.body() != null)
                        listener.onResponse(response.body());
                    else if (response.errorBody() != null)
                        listener.onListNetworkListenerFailure(new FailureModel(response.message(), response.code()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                listener.onListNetworkListenerFailure(new FailureModel(t.getMessage()));
            }
        });
    }

    public EventHandler<Map> setListenerForDevice(final NetworkListener.SingleNetworkListener<BaseModel> listener, Device mDevice) {
        final EventHandler<Map> deviceEventHandler = Backendless.Data.of("Device").rt();
        deviceEventHandler.addUpdateListener(String.format("objectId='%s'", mDevice.getObjectId()), new AsyncCallback<Map>() {
            @Override
            public void handleResponse(Map updatedLock) {
                updatedLock.put("bleDeviceName", mDevice.getBleDeviceName());
                updatedLock.put("memberAdminStatus", mDevice.getUserAdminStatus());
                listener.onResponse(new Device(updatedLock));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                listener.onSingleNetworkListenerFailure(new FailureModel(fault.getMessage()));
            }
        });

        deviceEventHandler.addBulkUpdateListener(String.format("sn='%s'", mDevice.getSerialNumber()), new AsyncCallback<BulkEvent>() {
            @Override
            public void handleResponse(BulkEvent response) {
                if (response.getCount() >= 1) {
                    listener.onResponse(new TempDeviceModel(mDevice.getObjectId()));
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                listener.onSingleNetworkListenerFailure(new FailureModel(fault.getMessage()));
            }
        });

        return deviceEventHandler;
    }

    public void removeListenerForDevice(final NetworkListener.SingleNetworkListener<BaseModel> listener, Device mDevice, EventHandler<Map> deviceEventHandler) {
        deviceEventHandler.removeBulkUpdateListener(
                String.format("sn='%s'", mDevice.getSerialNumber()), new AsyncCallback<BulkEvent>() {
                    @Override
                    public void handleResponse(BulkEvent response) {
                        Timber.d("removeListenerForDevice1 done!");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Timber.d("removeListenerForDevice1 done!");
                    }
                });

        deviceEventHandler.removeUpdateListener(
                String.format("objectId='%s'", mDevice.getObjectId()), new AsyncCallback<Map>() {
                    @Override
                    public void handleResponse(Map response) {
                        Timber.d("removeListenerForDevice2 done!");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Timber.d("removeListenerForDevice2 done!");
                    }
                });
    }

    public void setListenerForUser(final NetworkListener.SingleNetworkListener<BaseModel> listener, String mUserDeviceObjectId) {
        EventHandler<UserLock> userEventHandler = Backendless.Data.of(UserLock.class).rt();

        userEventHandler.addDeleteListener(String.format("objectId='%s'", mUserDeviceObjectId), new AsyncCallback<UserLock>() {
            @Override
            public void handleResponse(UserLock response) {
                listener.onResponse(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                listener.onSingleNetworkListenerFailure(new FailureModel(fault.getMessage()));
            }
        });
    }

    public void enablePushNotification(final NetworkListener.SingleNetworkListener<ResponseBody> listener, String serialNumber) {
        Backendless.Messaging.registerDevice(Collections.singletonList(serialNumber), new AsyncCallback<DeviceRegistrationResult>() {
            @Override
            public void handleResponse(DeviceRegistrationResult response) {
                listener.onResponse(new ResponseBodyModel(response.getDeviceToken(), true));
            }

            @Override
            public void handleFault(BackendlessFault t) {
                listener.onSingleNetworkListenerFailure(new ResponseBodyFailureModel(t.getMessage()));
            }
        });
    }
    //endregion Declare Methods
}