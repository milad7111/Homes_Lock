package com.projects.company.homes_lock.utils;

public interface MQTTListener<T> {
    void messageArrived(T message);

    void deliveryComplete(T token);

    void connectionLost(T cause);
}
