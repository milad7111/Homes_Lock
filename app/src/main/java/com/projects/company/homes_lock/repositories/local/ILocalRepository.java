package com.projects.company.homes_lock.repositories.local;

/**
 * This is LoginFragment Interface
 */

public interface ILocalRepository<T> {
    void onDataInsert(Long id);

    void onClearAllData();
}
