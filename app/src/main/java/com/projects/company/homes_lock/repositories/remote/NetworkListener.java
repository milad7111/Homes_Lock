package com.projects.company.homes_lock.repositories.remote;

public interface NetworkListener<T> {
    void onResponse(T response);

    void onFailure(T response);
}
