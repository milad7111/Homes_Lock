package com.projects.company.homes_lock.base;

import android.support.v4.app.Fragment;

import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

public class BaseFragment extends Fragment implements BaseInterface {

    @Override
    public void onPause() {
        super.onPause();
        handleProgressDialog(null, null, null, false);
    }
}
