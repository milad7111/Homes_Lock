package com.projects.company.homes_lock.utils;

public interface IMQTTListener<T> {
    void onConnectionToBrokerLost(T response);

    void onMessageArrived(T response);

    void onDeliveryMessageComplete(T response);

    void onConnectionSuccessful(T response);

    void onConnectionFailure(T response);

    void onSubscribeSuccessful(T response);

    void onSubscribeFailure(T response);

    void onPublishSuccessful(T response);

    void onPublishFailure(T response);
}
