package com.projects.company.homes_lock.ui.login.fragment.register;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.viewmodels.RegisterViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment;
import com.projects.company.homes_lock.utils.helper.ValidationHelper;

import java.util.Objects;

import timber.log.Timber;

import static android.support.v4.content.res.ResourcesCompat.getDrawable;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_EMPTY;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_EMAIL_FORMAT;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_MOBILE_NUMBER_FORMAT;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_REGISTER_NO_MATCH;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_REGISTER_PASS_LENGTH;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.validateMobileNumber;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.validateUserEmail;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.validateUserName;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.validateUserPassword;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.addFragment;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.hideKeyboard;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class RegisterFragment extends BaseFragment
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

    //region Constructor
    public RegisterFragment() {
    }
    //endregion Constructor

    //region Main Callbacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new RegisterViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), this))
                .get(UserViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
    public void onPause() {
        super.onPause();
        closeProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up_register_fragment:
                if (isInputsValid()) {
                    hideKeyboard(Objects.requireNonNull(getActivity()));
                    openProgressDialog(getContext(), null, "Register process ...");
                    mUserViewModel.register(
                            new RegisterModel(
                                    Objects.requireNonNull(tietEmail.getText()).toString(),
                                    Objects.requireNonNull(tietUserName.getText()).toString(),
                                    Objects.requireNonNull(tietMobileNumber.getText()).toString(),
                                    Objects.requireNonNull(tietPassword.getText()).toString())
                    );
                }
                break;
            case R.id.txv_login_register_fragment:
                addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_login_activity, new LoginFragment());
                break;
        }
    }
    //endregion Main Callbacks

    //region Register Callbacks
    @Override
    public void onRegisterSuccessful(Object response) {
        closeProgressDialog();
        Toast.makeText(getContext(), "Register user Successful", Toast.LENGTH_SHORT).show();
        txvLogin.performClick();
    }

    @Override
    public void onRegisterFailed(FailureModel response) {
        closeProgressDialog();
        Timber.i(response.getFailureMessage());
        Toast.makeText(getContext(), response.getFailureMessage(), Toast.LENGTH_SHORT).show();

        if (response.getFailureCode() == 409) {
            tietEmail.setError("Redundant email",
                    getDrawable(getResources(),
                            android.R.drawable.stat_notify_error,
                            Objects.requireNonNull(getContext()).getTheme()));
            tietEmail.requestFocus();
        }
    }
    //endregion Register Callbacks

    //region Declare Methods
    private boolean isInputsValid() {
        tietEmail.setError(null);
        tietMobileNumber.setError(null);
        tietPassword.setError(null);
        tietConfirmPassword.setError(null);

        switch (validateUserEmail(Objects.requireNonNull(tietEmail.getText()).toString())) {
            case VALIDATION_EMPTY:
                tietEmail.setError("Must not be empty",
                        getDrawable(getResources(),
                                android.R.drawable.stat_notify_error,
                                Objects.requireNonNull(getContext()).getTheme()));
                tietEmail.requestFocus();
                return false;
            case VALIDATION_EMAIL_FORMAT:
                tietEmail.setError("Wrong email format",
                        getDrawable(getResources(),
                                android.R.drawable.stat_notify_error,
                                Objects.requireNonNull(getContext()).getTheme()));
                tietEmail.requestFocus();
                return false;
        }

        switch (validateUserName(String.valueOf(tietUserName.getText()))) {
            case ValidationHelper.VALIDATION_EMPTY:
                tietUserName.setError("Must not be empty",
                        getDrawable(getResources(),
                                android.R.drawable.stat_notify_error,
                                Objects.requireNonNull(getContext()).getTheme()));
                tietUserName.requestFocus();
                return false;
        }

        switch (validateMobileNumber(Objects.requireNonNull(tietMobileNumber.getText()).toString())) {
            case VALIDATION_MOBILE_NUMBER_FORMAT:
                tietMobileNumber.setError("Wrong phone number format",
                        getDrawable(getResources(),
                                android.R.drawable.stat_notify_error,
                                Objects.requireNonNull(getContext()).getTheme()));
                tietMobileNumber.requestFocus();
                return false;
        }

        switch (validateUserPassword(
                Objects.requireNonNull(tietPassword.getText()).toString(),
                Objects.requireNonNull(tietConfirmPassword.getText()).toString())) {
            case VALIDATION_REGISTER_PASS_LENGTH:
                tietPassword.setError("At least 8 characters",
                        getDrawable(getResources(),
                                android.R.drawable.stat_notify_error,
                                Objects.requireNonNull(getContext()).getTheme()));
                tietPassword.requestFocus();
                return false;
            case VALIDATION_REGISTER_NO_MATCH:
                tietConfirmPassword.setError("Not match with original password",
                        getDrawable(getResources(),
                                android.R.drawable.stat_notify_error,
                                Objects.requireNonNull(getContext()).getTheme()));
                tietConfirmPassword.requestFocus();
                return false;
        }

        return true;
    }
    //endregion Declare Methods
}
