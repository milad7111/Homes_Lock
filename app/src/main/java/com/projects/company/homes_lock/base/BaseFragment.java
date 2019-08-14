package com.projects.company.homes_lock.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;

import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;

public class BaseFragment extends Fragment implements BaseInterface {

    @Override
    public void onPause() {
        super.onPause();
        closeProgressDialog();
    }

    public void showToast(String message) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }
}
