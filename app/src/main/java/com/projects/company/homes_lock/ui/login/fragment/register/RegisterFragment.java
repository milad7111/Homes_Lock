package com.projects.company.homes_lock.ui.login.fragment.register;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.viewmodels.RegisterViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment
        implements
        IRegisterFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private TextInputEditText tietEmail;
    private TextInputEditText tietUserName;
    private TextInputEditText tietMobileNumber;
    private TextInputEditText tietPassword;
    private TextInputEditText tietConfirmPassword;

    private Button btnSignUp;
    private TextView txvLogin;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private UserViewModel mUserViewModel;
    //endregion Declare Objects

    //region Declare Constructor
    public RegisterFragment() {
        // Required empty public constructor
    }
    //endregion Declare Constructor

    //region Main Callbacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new RegisterViewModelFactory(getActivity().getApplication(), this))
                .get(UserViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        tietEmail = view.findViewById(R.id.tiet_email_register_fragment);
        tietUserName = view.findViewById(R.id.tiet_user_name_register_fragment);
        tietMobileNumber = view.findViewById(R.id.tiet_mobile_number_register_fragment);
        tietPassword = view.findViewById(R.id.tiet_password_register_fragment);
        tietConfirmPassword = view.findViewById(R.id.tiet_confirm_password_register_fragment);
        btnSignUp = view.findViewById(R.id.btn_sign_up_register_fragment);
        txvLogin = view.findViewById(R.id.txv_login_register_fragment);
        //endregion Initialize Views

        //region Setup Views
        btnSignUp.setOnClickListener(this);
        txvLogin.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up_register_fragment:
                DialogHelper.handleProgressDialog(getContext(), null, "Register process ...", true);
                mUserViewModel.register(
                        new RegisterModel(
                                tietEmail.getText().toString(),
                                tietUserName.getText().toString(),
                                tietMobileNumber.getText().toString(),
                                tietPassword.getText().toString()), tietConfirmPassword.getText().toString()
                );
                break;
            case R.id.txv_login_register_fragment:
                ViewHelper.setFragment((AppCompatActivity) getActivity(), R.id.frg_login_activity, new LoginFragment());
                break;
        }
    }
    //endregion Main Callbacks

    //region Register Callbacks
    @Override
    public void onRegisterSuccessful(Object response) {
        handleProgressDialog(null, null, null, false);
        Toast.makeText(getContext(), "Register user Successful", Toast.LENGTH_SHORT).show();
        txvLogin.performClick();
    }

    @Override
    public void onRegisterFailed(Object response) {
        Log.i(this.getClass().getSimpleName(), ((FailureModel) response).getFailureMessage());
    }
    //endregion Register Callbacks

    //region Declare Methods
    private void clearViews() {
        tietEmail.setText(null);
        tietUserName.setText(null);
        tietMobileNumber.setText(null);
        tietPassword.setText(null);
        tietConfirmPassword.setText(null);
    }
    //endregion Declare Methods
}
