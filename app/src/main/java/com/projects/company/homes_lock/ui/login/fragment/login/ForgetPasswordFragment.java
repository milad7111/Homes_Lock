package com.projects.company.homes_lock.ui.login.fragment.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.company.homes_lock.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment implements ILoginFragment {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    //endregion Declare Objects

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        //endregion Initialize Views

        //region Setup Views
        //endregion Setup Views
    }

    @Override
    public void onLoginSuccessful(Object response) {
    }

    @Override
    public void onLoginFailed(Object response) {
    }

    @Override
    public void onDataInsert(Long id) {

    }

    //region Declare Methods
    //endregion Declare Methods
}
