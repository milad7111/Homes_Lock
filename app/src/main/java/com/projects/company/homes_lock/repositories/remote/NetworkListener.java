package com.projects.company.homes_lock.repositories.remote;

import com.projects.company.homes_lock.models.datamodels.response.FailureModel;

import java.util.List;

public interface NetworkListener {
    interface SingleNetworkListener<T> {
        void onResponse(T response);

        void onFailure(T response);
    }

    interface ListNetworkListener<T> {
        void onResponse(T response);

        void onFailure(FailureModel response);
    }
}
