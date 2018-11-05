package com.projects.company.homes_lock.ui.login.fragment.login;

/**
 * This is LoginFragment Interface
 */

public interface ILoginFragment<T> {
    void onLoginSuccessful(T response);

    void onLoginFailed(T response);
}
