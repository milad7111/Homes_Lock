package com.projects.company.homes_lock.ui.device.fragment.adddevice;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.TempDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.AddLockViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.device.activity.CustomDeviceAdapter;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.findDevices;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getRandomPercentNumber;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class AddDeviceFragment extends BaseFragment
        implements
        IBleScanListener,
        IAddDeviceFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private TextInputEditText tietLockNameDialogAddNewLock;
    private TextInputEditText tietLockSerialNumberDialogAddNewLock;
    private CheckBox chbLockFavoriteStatusDialogAddNewLock;
    //endregion Declare Views

    //region Declare Variables
    private String lockObjectId;
    private String userLockObjectId;
    private boolean saveLockAfterPaired;
    //endregion Declare Variables

    //region Declare Objects
//    private Context context = null;
    private DeviceViewModel mDeviceViewModel;
    private UserViewModel mUserViewModel;

    private Dialog dialogAddLockOffline;
    private Dialog dialogListOfAvailableBleDevices;
    private Dialog dialogAddLockOnline;

    private ScannedDeviceModel mDevice;
    private TempDeviceModel mTempDevice;

    private BleDeviceAdapter mBleDeviceAdapter;
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    //endregion Declare Objects

    //region Constructor
    public AddDeviceFragment() {
    }

    public static AddDeviceFragment newInstance() {
        return new AddDeviceFragment();
    }
    //endregion Constructor

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new AddLockViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), this))
                .get(UserViewModel.class);

        this.mTempDevice = new TempDeviceModel();
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Objects
        //endregion Initialize Objects

        //region Initialize Views
        Button btnAddNewLock = view.findViewById(R.id.btn_add_new_device);
        //endregion Initialize Views

        //region Setup Views
        btnAddNewLock.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onPause() {
        super.onPause();
        handleProgressDialog(null, null, null, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_device:
                addNewLock();
                break;
        }
    }
    //endregion Main CallBacks

    //region IAddDeviceFragment CallBacks
    @Override
    public void onFindLockInOnlineDataBaseSuccessful(String lockObjectId) {
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(2, 8)),
                true);

        this.lockObjectId = lockObjectId;
        handleDialogAddLockOnline(true);
    }

    @Override
    public void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onInsertUserLockSuccessful(UserLock userLock) {
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(3, 8)),
                true);

        userLockObjectId = userLock.getObjectId();
        mDeviceViewModel.addLockToUserLock(userLockObjectId, this.lockObjectId);
    }

    @Override
    public void onInsertUserLockFailed(FailureModel response) {
    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(4, 8)),
                    true);
            mDeviceViewModel.addUserLockToUser(BaseApplication.activeUserObjectId, userLockObjectId);
        } else
            onAddLockToUserLockFailed(new ResponseBodyFailureModel("add lock to user lock failed."));
    }

    @Override
    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onAddUserLockToUserSuccessful(Boolean addUserLockToUserSuccessful) {
        if (addUserLockToUserSuccessful) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(5, 8)),
                    true);

            if (dialogAddLockOnline != null) {
                dialogAddLockOnline.dismiss();
                dialogAddLockOnline = null;
            }

            mDeviceViewModel.enablePushNotification(this.lockObjectId);
            mUserViewModel.getUserWithObjectId(BaseApplication.activeUserObjectId);
        } else
            onAddUserLockToUserFailed(new ResponseBodyFailureModel("add user lock to user failed."));
    }

    @Override
    public void onAddUserLockToUserFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onGetUserSuccessful(User response) {
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(6, 8)),
                true);

        BaseApplication.activeUserObjectId = response.getObjectId();
        BaseApplication.activeUserToken = response.getUserToken();
        LockActivity.PERMISSION_READ_ALL_LOCAL_DEVICES = true;
        mUserViewModel.insertUser(response);
    }

    @Override
    public void onGetUserFailed(FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }

    @Override
    public void onDataInsert(Object object) {
        if (object instanceof User) {
            DialogHelper.handleProgressDialog(
                    getActivity(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(7, 8)),
                    true);

            mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
                DialogHelper.handleProgressDialog(
                        getContext(),
                        null,
                        String.format("Adding Lock ... %d %%", getRandomPercentNumber(8, 8)),
                        true);

                ((LockActivity) getActivity()).setViewPagerAdapter(
                        new CustomDeviceAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), devices));
            });
        } else if (object instanceof Device) {
            if (dialogAddLockOffline != null) {
                dialogAddLockOffline.dismiss();
                dialogAddLockOffline = null;
            }
        }
    }

    @Override
    public void onDeviceRegistrationPushNotificationSuccessful(String registrationId) {
        Log.i(getTag(), "Device registered for push notification with registrationId: " + registrationId);
    }

    @Override
    public void onDeviceRegistrationPushNotificationFailed(ResponseBodyFailureModel response) {
        Toast.makeText(getContext(), response.getFailureMessage(), Toast.LENGTH_LONG).show();
    }
    //endregion IAddDeviceFragment CallBacks

    //region Ble Callbacks
    @Override
    public void onFindBleSuccess(List devices) {
        handleDialogListOfAvailableBleDevices(devices);
    }

    @Override
    public void onFindBleFault() {
        handleDialogListOfAvailableBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_TIMEOUT_MODE)));
    }

    @Override
    public void onAdapterItemClick(BaseModel device) {
        mDevice = (ScannedDeviceModel) device;
        mDeviceViewModel.connect(this, mDevice);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
        if (dialogListOfAvailableBleDevices != null) {
            dialogListOfAvailableBleDevices.dismiss();
            dialogListOfAvailableBleDevices = null;
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(this::handleDialogAddLockOffline);
    }
    //endregion Ble Callbacks

    //region Declare Methods
    private void addNewLock() {
        if (isUserLoggedIn())
            handleDialogAddLockOnline(false); // Means user Wrote username and password then clicked Login
        else {
            mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
            mTempDevice = new TempDeviceModel();
            if (getScanPermission(this))
                handleDialogListOfAvailableBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE))); // Means user clicked Direct Connect
        }
    }

    private void handleDialogListOfAvailableBleDevices(List<ScannedDeviceModel> devices) {
        if (dialogListOfAvailableBleDevices == null) {
            dialogListOfAvailableBleDevices = new Dialog(Objects.requireNonNull(getContext()));
            dialogListOfAvailableBleDevices.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogListOfAvailableBleDevices.setContentView(R.layout.dialog_available_devices);

            if (mBleDeviceAdapter == null)
                mBleDeviceAdapter = new BleDeviceAdapter(this, devices);

            RecyclerView rcvDialogAvailableDevices = dialogListOfAvailableBleDevices.findViewById(R.id.rcv_dialog_available_devices);
            Button btnCancelDialogAvailableDevices = dialogListOfAvailableBleDevices.findViewById(R.id.btn_cancel_dialog_available_devices);
            Button btnScanDialogAvailableDevices = dialogListOfAvailableBleDevices.findViewById(R.id.btn_scan_dialog_available_devices);

            rcvDialogAvailableDevices.setLayoutManager(new LinearLayoutManager(getContext()));
            rcvDialogAvailableDevices.setItemAnimator(new DefaultItemAnimator());
            rcvDialogAvailableDevices.setAdapter(mBleDeviceAdapter);

            btnCancelDialogAvailableDevices.setOnClickListener(v -> {
                mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE)));
                dialogListOfAvailableBleDevices.dismiss();
                dialogListOfAvailableBleDevices = null;
            });

            btnScanDialogAvailableDevices.setOnClickListener(v -> {
                mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE)));
                findDevices(this, mBluetoothLEHelper);
            });

            findDevices(this, mBluetoothLEHelper);
        } else
            mBleDeviceAdapter.setBleDevices(devices);

        if (!dialogListOfAvailableBleDevices.isShowing())
            dialogListOfAvailableBleDevices.show();

        Objects.requireNonNull(dialogListOfAvailableBleDevices.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(dialogListOfAvailableBleDevices));
    }

    private void handleDialogAddLockOffline() {
        saveLockAfterPaired = false;

        dialogAddLockOffline = new Dialog(Objects.requireNonNull(getContext()));

        dialogAddLockOffline.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddLockOffline.setContentView(R.layout.dialog_add_new_lock);

        dialogAddLockOffline.setOnDismissListener(mDialog -> {
            if (!saveLockAfterPaired)
                mBluetoothLEHelper.unPairDevice(mDevice.getDevice().getAddress());
        });

        Button btnCancelDialogAddNewLock = dialogAddLockOffline.findViewById(R.id.btn_cancel_dialog_add_new_lock);
        Button btnAddDialogAddNewLock = dialogAddLockOffline.findViewById(R.id.btn_add_dialog_add_new_lock);

        TextInputEditText tietLockNameDialogAddNewLock = dialogAddLockOffline.findViewById(R.id.tiet_lock_name_dialog_add_new_lock);
        TextInputEditText tietLockSerialNumberDialogAddNewLock = dialogAddLockOffline.findViewById(R.id.tiet_lock_serial_number_dialog_add_new_lock);

        CheckBox chbLockFavoriteStatusDialogAddNewLock = dialogAddLockOffline.findViewById(R.id.chb_lock_favorite_status_dialog_add_new_lock);

        btnCancelDialogAddNewLock.setOnClickListener(v -> dialogAddLockOffline.dismiss());

        btnAddDialogAddNewLock.setOnClickListener(v -> {
            DialogHelper.handleProgressDialog(getContext(), null, "Saving ...", true);

            mTempDevice.setDeviceName(Objects.requireNonNull(tietLockNameDialogAddNewLock.getText()).toString());
            mTempDevice.setDeviceSerialNumber(Objects.requireNonNull(tietLockSerialNumberDialogAddNewLock.getText()).toString());
            mTempDevice.setFavoriteStatus(chbLockFavoriteStatusDialogAddNewLock.isChecked());
            mTempDevice.setDeviceMacAddress(mDevice.getMacAddress());

            LockActivity.PERMISSION_READ_ALL_LOCAL_DEVICES = true;
            mDeviceViewModel.insertLocalDevice(new Device(mTempDevice));

            saveLockAfterPaired = true;
        });

        dialogAddLockOffline.show();
        Objects.requireNonNull(dialogAddLockOffline.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(dialogAddLockOffline));
    }

    private void handleDialogAddLockOnline(boolean lockExistenceStatus) {
        if (dialogAddLockOnline == null) {
            dialogAddLockOnline = new Dialog(Objects.requireNonNull(getContext()));

            dialogAddLockOnline.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAddLockOnline.setContentView(R.layout.dialog_add_new_lock);

            dialogAddLockOnline.setOnDismissListener(dialog -> dialogAddLockOnline = null);

            Button btnCancelDialogAddNewLock = dialogAddLockOnline.findViewById(R.id.btn_cancel_dialog_add_new_lock);
            Button btnAddDialogAddNewLock = dialogAddLockOnline.findViewById(R.id.btn_add_dialog_add_new_lock);

            tietLockNameDialogAddNewLock = dialogAddLockOnline.findViewById(R.id.tiet_lock_name_dialog_add_new_lock);
            tietLockSerialNumberDialogAddNewLock = dialogAddLockOnline.findViewById(R.id.tiet_lock_serial_number_dialog_add_new_lock);
            chbLockFavoriteStatusDialogAddNewLock = dialogAddLockOnline.findViewById(R.id.chb_lock_favorite_status_dialog_add_new_lock);

            btnCancelDialogAddNewLock.setOnClickListener(v -> dialogAddLockOnline.dismiss());

            btnAddDialogAddNewLock.setOnClickListener(v -> {
                DialogHelper.handleProgressDialog(
                        getContext(),
                        null,
                        String.format("Adding Lock ... %d %%", getRandomPercentNumber(1, 8)),
                        true);

                mDeviceViewModel.validateLockInOnlineDatabase(this,
                        Objects.requireNonNull(tietLockSerialNumberDialogAddNewLock.getText()).toString());
            });
        } else {
            if (lockExistenceStatus)
                mDeviceViewModel.insertOnlineUserLock(
                        new UserLockModel(
                                Objects.requireNonNull(tietLockNameDialogAddNewLock.getText()).toString(),
                                true,
                                chbLockFavoriteStatusDialogAddNewLock.isChecked()
                        ));
        }


        if (!dialogAddLockOnline.isShowing())
            dialogAddLockOnline.show();

        dialogAddLockOnline.show();
        Objects.requireNonNull(dialogAddLockOnline.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(dialogAddLockOnline));
    }
    //endregion Declare Methods
}
