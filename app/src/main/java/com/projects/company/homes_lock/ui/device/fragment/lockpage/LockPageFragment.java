package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockPageFragment extends Fragment
        implements ILockPageFragment, IBleScanListener, View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    ImageView imgMainLockPage;
    //endregion Declare Views

    //region Declare Variables
    private String mParam;
    //endregion Declare Variables

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
    //endregion Declare Objects

    public LockPageFragment() {
        // Required empty public constructor
    }

    public static LockPageFragment newInstance(String param) {
        LockPageFragment fragment = new LockPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        mParam = getArguments() != null ? getArguments().getString(ARG_PARAM) : null;
        //endregion Initialize Variables

        //region Initialize Objects
//        this.mDeviceViewModel = ViewModelProviders.of(
//                LockPageFragment.this,
//                new DeviceViewModelFactory(getActivity().getApplication(), LockPageFragment.this))
//                .get(DeviceViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lock_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgMainLockPage = view.findViewById(R.id.img_main_lock_page);
        //endregion Initialize Views

        //region Setup Views
        imgMainLockPage.setOnClickListener(this);

//        mDeviceViewModel.getADevice("fsafasfasfasf");
//        this.mDeviceViewModel.getADevice("fsafasfasfasf").observe(this, new Observer<Device>() {
//            @Override
//            public void onChanged(@Nullable final Device device) {
//                if (imgMainLockPage != null)
//                    ViewHelper.setLockStatusImage(imgMainLockPage, device.getLockStatus());
//            }
//        });
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_main_lock_page:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //endregion Main CallBacks

    //region BLE CallBacks
    @Override
    public void onDataReceived(Object response) {
    }

    @Override
    public void onDataSent(Object value) {
    }

    @Override
    public void onFindBleCompleted(List response) {
    }

    @Override
    public void onFindBleFault(Object response) {
    }

    @Override
    public void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel) {
    }
    //endregion BLE CallBacks

    //region Declare Methods
    //endregion Declare Methods
}
