package com.projects.company.homes_lock.ui.login.fragment.register;

/**
 * This is RegisterFragment Interface
 */
public interface IRegisterFragment<T> {
    void onRegisterSuccessful(T response);

    void onRegisterFailed(T response);
}
