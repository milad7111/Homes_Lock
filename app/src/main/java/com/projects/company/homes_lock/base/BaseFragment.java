package com.projects.company.homes_lock.base;

import android.support.v4.app.Fragment;

import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;

public class BaseFragment extends Fragment implements BaseInterface {

    @Override
    public void onPause() {
        super.onPause();
        closeProgressDialog();
    }
}
