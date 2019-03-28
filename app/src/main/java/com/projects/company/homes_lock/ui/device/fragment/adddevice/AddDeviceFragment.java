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
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
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
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;

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
    private TextInputEditText tietDeviceNameDialogAddNewDevice;
    private TextInputEditText tietDeviceSerialNumberDialogAddNewDevice;
    private CheckBox chbDeviceFavoriteStatusDialogAddNewDevice;
    //endregion Declare Views

    //region Declare Variables
    private String lockObjectId;
    private String userLockObjectId;
    private boolean saveDeviceAfterPaired;
    //endregion Declare Variables

    //region Declare Objects
//    private Context context = null;
    private DeviceViewModel mDeviceViewModel;
    private UserViewModel mUserViewModel;

    private Dialog dialogAddDeviceOffline;
    private Dialog dialogListOfAvailableBleDevices;
    private Dialog dialogAddDeviceOnline;

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
        closeProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_device:
                addNewDevice();
                break;
        }
    }
    //endregion Main CallBacks

    //region IAddDeviceFragment CallBacks
    @Override
    public void onFindLockInOnlineDataBaseSuccessful(String lockObjectId) {
        openProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(2, 8)));

        this.lockObjectId = lockObjectId;
        handleDialogAddDeviceOnline(true);
    }

    @Override
    public void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onInsertUserLockSuccessful(UserLock userLock) {
        openProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(3, 8)));

        userLockObjectId = userLock.getObjectId();
        mDeviceViewModel.addLockToUserLock(userLockObjectId, this.lockObjectId);
    }

    @Override
    public void onInsertUserLockFailed(FailureModel response) {
    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful) {
            openProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(4, 8)));
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
            openProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(5, 8)));

            if (dialogAddDeviceOnline != null) {
                dialogAddDeviceOnline.dismiss();
                dialogAddDeviceOnline = null;
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
        openProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(6, 8)));

        BaseApplication.activeUserObjectId = response.getObjectId();
        BaseApplication.activeUserToken = response.getUserToken();
        DeviceActivity.PERMISSION_READ_ALL_LOCAL_DEVICES = true;
        mUserViewModel.insertUser(response);
    }

    @Override
    public void onGetUserFailed(FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }

    @Override
    public void onDataInsert(Object object) {
        if (object instanceof User) {
            openProgressDialog(
                    getActivity(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(7, 8)));

            mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
                openProgressDialog(
                        getContext(),
                        null,
                        String.format("Adding Lock ... %d %%", getRandomPercentNumber(8, 8)));

                ((DeviceActivity) getActivity()).setViewPagerAdapter(
                        new CustomDeviceAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), devices));
            });
        } else if (object instanceof Device) {
            if (dialogAddDeviceOffline != null) {
                dialogAddDeviceOffline.dismiss();
                dialogAddDeviceOffline = null;
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
        this.mDevice = (ScannedDeviceModel) device;
        connectToDevice(mDevice);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
        if (dialogListOfAvailableBleDevices != null) {
            dialogListOfAvailableBleDevices.dismiss();
            dialogListOfAvailableBleDevices = null;
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(this::handleDialogAddDeviceOffline);
    }
    //endregion Ble Callbacks

    //region Declare Methods
    private void addNewDevice() {
        if (isUserLoggedIn())
            handleDialogAddDeviceOnline(false); // Means user Wrote username and password then clicked Login
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
            dialogListOfAvailableBleDevices.setContentView(R.layout.dialog_available_ble_devices);

            if (mBleDeviceAdapter == null)
                mBleDeviceAdapter = new BleDeviceAdapter(this, devices);

            RecyclerView rcvDialogAvailableDevices = dialogListOfAvailableBleDevices.findViewById(R.id.rcv_dialog_available_ble_devices);
            Button btnCancelDialogAvailableDevices = dialogListOfAvailableBleDevices.findViewById(R.id.btn_cancel_dialog_available_ble_devices);
            Button btnScanDialogAvailableDevices = dialogListOfAvailableBleDevices.findViewById(R.id.btn_scan_dialog_available_ble_devices);

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

    private void handleDialogAddDeviceOffline() {
        saveDeviceAfterPaired = false;

        dialogAddDeviceOffline = new Dialog(Objects.requireNonNull(getContext()));

        dialogAddDeviceOffline.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddDeviceOffline.setContentView(R.layout.dialog_add_new_device);

        dialogAddDeviceOffline.setOnDismissListener(mDialog -> {
            if (!saveDeviceAfterPaired)
                mBluetoothLEHelper.unPairDevice(mDevice.getDevice().getAddress());
        });

        Button btnCancelDialogAddNewDevice = dialogAddDeviceOffline.findViewById(R.id.btn_cancel_dialog_add_new_device);
        Button btnAddDialogAddNewDevice = dialogAddDeviceOffline.findViewById(R.id.btn_add_dialog_add_new_device);

        TextInputEditText tietDeviceNameDialogAddNewDevice = dialogAddDeviceOffline.findViewById(R.id.tiet_device_name_dialog_add_new_device);
        TextInputEditText tietDeviceSerialNumberDialogAddNewDevice = dialogAddDeviceOffline.findViewById(R.id.tiet_device_serial_number_dialog_add_new_device);

        CheckBox chbDeviceFavoriteStatusDialogAddNewDevice = dialogAddDeviceOffline.findViewById(R.id.chb_device_favorite_status_dialog_add_new_device);

        btnCancelDialogAddNewDevice.setOnClickListener(v -> dialogAddDeviceOffline.dismiss());

        btnAddDialogAddNewDevice.setOnClickListener(v -> {
            if (tietDeviceNameDialogAddNewDevice.getText() == null || tietDeviceNameDialogAddNewDevice.getText().toString().equals(""))
                Toast.makeText(getActivity(), "device name is empty", Toast.LENGTH_SHORT).show();
            else if (tietDeviceSerialNumberDialogAddNewDevice.getText() == null || tietDeviceSerialNumberDialogAddNewDevice.getText().toString().equals(""))
                Toast.makeText(getActivity(), "serial number is empty", Toast.LENGTH_SHORT).show();
            else {
                this.mDeviceViewModel.getDeviceInfo(Objects.requireNonNull(tietDeviceSerialNumberDialogAddNewDevice.getText()).toString())
                        .observe(this, device -> {
                            if (device != null)
                                Toast.makeText(getActivity(), "serial number is redundant", Toast.LENGTH_SHORT).show();
                            else {
                                openProgressDialog(getContext(), null, "Saving ...");

                                mTempDevice.setDeviceName(Objects.requireNonNull(tietDeviceNameDialogAddNewDevice.getText()).toString());
                                mTempDevice.setDeviceSerialNumber(Objects.requireNonNull(tietDeviceSerialNumberDialogAddNewDevice.getText()).toString());
                                mTempDevice.setFavoriteStatus(chbDeviceFavoriteStatusDialogAddNewDevice.isChecked());
                                mTempDevice.setDeviceMacAddress(mDevice.getMacAddress());

                                DeviceActivity.PERMISSION_READ_ALL_LOCAL_DEVICES = true;
                                this.mDeviceViewModel.insertLocalDevice(this, new Device(mTempDevice));

                                saveDeviceAfterPaired = true;
                            }
                        });
            }
        });

        dialogAddDeviceOffline.show();
        Objects.requireNonNull(dialogAddDeviceOffline.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(dialogAddDeviceOffline));
    }

    private void handleDialogAddDeviceOnline(boolean deviceExistenceStatus) {
        if (dialogAddDeviceOnline == null) {
            dialogAddDeviceOnline = new Dialog(Objects.requireNonNull(getContext()));

            dialogAddDeviceOnline.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAddDeviceOnline.setContentView(R.layout.dialog_add_new_device);

            dialogAddDeviceOnline.setOnDismissListener(dialog -> dialogAddDeviceOnline = null);

            Button btnCancelDialogAddNewDevice = dialogAddDeviceOnline.findViewById(R.id.btn_cancel_dialog_add_new_device);
            Button btnAddDialogAddNewDevice = dialogAddDeviceOnline.findViewById(R.id.btn_add_dialog_add_new_device);

            tietDeviceNameDialogAddNewDevice = dialogAddDeviceOnline.findViewById(R.id.tiet_device_name_dialog_add_new_device);
            tietDeviceSerialNumberDialogAddNewDevice = dialogAddDeviceOnline.findViewById(R.id.tiet_device_serial_number_dialog_add_new_device);
            chbDeviceFavoriteStatusDialogAddNewDevice = dialogAddDeviceOnline.findViewById(R.id.chb_device_favorite_status_dialog_add_new_device);

            btnCancelDialogAddNewDevice.setOnClickListener(v -> dialogAddDeviceOnline.dismiss());

            btnAddDialogAddNewDevice.setOnClickListener(v -> {
                openProgressDialog(
                        getContext(),
                        null,
                        String.format("Adding Lock ... %d %%", getRandomPercentNumber(1, 8)));

                mDeviceViewModel.validateLockInOnlineDatabase(this,
                        Objects.requireNonNull(tietDeviceSerialNumberDialogAddNewDevice.getText()).toString());
            });
        } else {
            if (deviceExistenceStatus)
                mDeviceViewModel.insertOnlineUserLock(
                        new UserLockModel(
                                Objects.requireNonNull(tietDeviceNameDialogAddNewDevice.getText()).toString(),
                                true,
                                chbDeviceFavoriteStatusDialogAddNewDevice.isChecked()
                        ));
        }


        if (!dialogAddDeviceOnline.isShowing())
            dialogAddDeviceOnline.show();

        dialogAddDeviceOnline.show();
        Objects.requireNonNull(dialogAddDeviceOnline.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(dialogAddDeviceOnline));
    }

    private void connectToDevice(ScannedDeviceModel device) {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(device.getMacAddress());

        if (tempDevice != null) {
            openProgressDialog(getActivity(), null, "Pairing ...");
            onBonded(tempDevice);
        } else if (getScanPermission(this))
            this.mDeviceViewModel.connect(this, mDevice);
    }
    //endregion Declare Methods
}
