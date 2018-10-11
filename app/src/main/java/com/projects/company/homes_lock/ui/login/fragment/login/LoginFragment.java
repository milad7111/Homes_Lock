package com.projects.company.homes_lock.ui.login.fragment.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements ILoginFragment, View.OnClickListener {

    //region Declare Constants
    public static String TAG = LoginFragment.class.getName();
    //endregion Declare Constants

    //region Declare Views
    private Button btnLogin;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    //endregion Declare Objects

    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        btnLogin = view.findViewById(R.id.btn_login);
        //endregion Initialize Views

        //region Setup Views
        btnLogin.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(getActivity(), LockActivity.class));
                break;
        }
    }

    //region Declare Methods
    //endregion Declare Methods
}
