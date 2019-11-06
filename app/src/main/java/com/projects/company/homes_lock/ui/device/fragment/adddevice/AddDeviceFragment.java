package com.projects.company.homes_lock.ui.device.fragment.adddevice;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.TempDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.AddLockViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.device.activity.CustomDeviceAdapter;
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.findDevices;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getRandomPercentNumber;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setTypeface;

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

    //region Declare Arrays & Lists
    private List<Device> mAllDevices;
    //endregion Declare Arrays & Lists

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
    private UserViewModel mUserViewModel;

    private Dialog mAddDeviceOfflineDialog;
    private Dialog mListOfAvailableBleDevicesDialog;
//    private Dialog mAddDeviceOnlineDialog;

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
        this.mAllDevices = null;
        this.mDeviceViewModel.getAllLocalDevices().observe(this, devices -> AddDeviceFragment.this.mAllDevices = devices);
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
//    @Override
//    public void onFindLockInOnlineDataBaseSuccessful(String lockObjectId) {
//        openProgressDialog(
//                getContext(),
//                null,
//                String.format("Adding Lock ... %d %%", getRandomPercentNumber(2, 8)));
//
//        this.lockObjectId = lockObjectId;
//        handleDialogAddDeviceOnline(true);
//    }

//    @Override
//    public void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response) {
//    }

//    @Override
//    public void onInsertUserLockSuccessful(UserLock userLock) {
//        openProgressDialog(
//                getContext(),
//                null,
//                String.format("Adding Lock ... %d %%", getRandomPercentNumber(3, 8)));
//
//        userLockObjectId = userLock.getObjectId();
//        mDeviceViewModel.addLockToUserLock(this, userLockObjectId, this.lockObjectId);
//    }

//    @Override
//    public void onInsertUserLockFailed(FailureModel response) {
//    }

//    @Override
//    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
//        if (addLockToUserLockSuccessful) {
//            openProgressDialog(
//                    getContext(),
//                    null,
//                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(4, 8)));
//            mDeviceViewModel.addUserLockToUser(this, activeUserObjectId, userLockObjectId);
//        } else
//            onAddLockToUserLockFailed(new ResponseBodyFailureModel("add lock to user lock failed."));
//    }

//    @Override
//    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {
//    }

//    @Override
//    public void onAddUserLockToUserSuccessful(Boolean addUserLockToUserSuccessful) {
//        if (addUserLockToUserSuccessful) {
//            openProgressDialog(
//                    getContext(),
//                    null,
//                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(5, 8)));
//
//            if (mAddDeviceOnlineDialog != null) {
//                mAddDeviceOnlineDialog.dismiss();
//                mAddDeviceOnlineDialog = null;
//            }
//
////            mDeviceViewModel.enablePushNotification(this.lockObjectId);
//            mUserViewModel.getUserWithObjectId(activeUserObjectId);
//        } else
//            onAddUserLockToUserFailed(new ResponseBodyFailureModel("add user lock to user failed."));
//    }

//    @Override
//    public void onAddUserLockToUserFailed(ResponseBodyFailureModel response) {
//    }

//    @Override
//    public void onGetUserSuccessful(User response) {
//        openProgressDialog(
//                getContext(),
//                null,
//                String.format("Adding Lock ... %d %%", getRandomPercentNumber(6, 8)));
//
//        activeUserObjectId = response.getObjectId();
//        BaseApplication.activeUserToken = response.getUserToken();
//        DeviceActivity.PERMISSION_READ_ALL_LOCAL_DEVICES = true;
//        mUserViewModel.insertUser(response);
//    }

//    @Override
//    public void onGetUserFailed(FailureModel response) {
//        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
//    }

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
            if (mAddDeviceOfflineDialog != null) {
                mAddDeviceOfflineDialog.dismiss();
                mAddDeviceOfflineDialog = null;
            }
        }
    }

//    @Override
//    public void onDeviceRegistrationPushNotificationSuccessful(String registrationId) {
//        Log.i(getTag(), "Device registered for push notification with registrationId: " + registrationId);
//    }

//    @Override
//    public void onDeviceRegistrationPushNotificationFailed(ResponseBodyFailureModel response) {
//        Toast.makeText(getContext(), response.getFailureMessage(), Toast.LENGTH_LONG).show();
//    }
    //endregion IAddDeviceFragment CallBacks

    //region Ble Callbacks
    @Override
    public void onFindBleSuccess(List<ScannedDeviceModel> devices) {
        handleDialogListOfAvailableBleDevices(
                devices.size() == 0 ? Collections.singletonList(new ScannedDeviceModel(SEARCHING_TIMEOUT_MODE))
                        : removeSavedDevicesFromDevicesList(devices));
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
        if (mListOfAvailableBleDevicesDialog != null) {
            mListOfAvailableBleDevicesDialog.dismiss();
            mListOfAvailableBleDevicesDialog = null;
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(this::handleDialogAddDeviceOffline);
    }
    //endregion Ble Callbacks

    //region Declare Methods
    private void addNewDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity(), null);
        mTempDevice = new TempDeviceModel();
        if (getScanPermission(this))
//            handleDialogListOfAvailableBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE))); // Means user clicked Direct Connect
            handleDialogListOfAvailableBleDevices(new ArrayList<>()); // Means user clicked Direct Connect
    }

    private void handleDialogListOfAvailableBleDevices(List<ScannedDeviceModel> devices) {
        if (mListOfAvailableBleDevicesDialog == null) {
            mListOfAvailableBleDevicesDialog = new Dialog(Objects.requireNonNull(getContext()));
            mListOfAvailableBleDevicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mListOfAvailableBleDevicesDialog.setContentView(R.layout.dialog_available_ble_devices);
            mListOfAvailableBleDevicesDialog.setCancelable(false);

            setTypeface((TextView) mListOfAvailableBleDevicesDialog.findViewById(R.id.txv_title_dialog_available_ble_devices), "roboto_medium");

            if (mBleDeviceAdapter == null)
                mBleDeviceAdapter = new BleDeviceAdapter(this, devices);

            ProgressBar prgTopDialogAvailableBleDevices = mListOfAvailableBleDevicesDialog.findViewById(R.id.prg_top_dialog_available_ble_devices);
            RecyclerView rcvDialogAvailableDevices = mListOfAvailableBleDevicesDialog.findViewById(R.id.rcv_dialog_available_ble_devices);
            Button btnCancelDialogAvailableDevices = mListOfAvailableBleDevicesDialog.findViewById(R.id.btn_cancel_dialog_available_ble_devices);
            Button btnScanDialogAvailableDevices = mListOfAvailableBleDevicesDialog.findViewById(R.id.btn_scan_dialog_available_ble_devices);

            rcvDialogAvailableDevices.setLayoutManager(new LinearLayoutManager(getContext()));
            rcvDialogAvailableDevices.setItemAnimator(new DefaultItemAnimator());
            rcvDialogAvailableDevices.setAdapter(mBleDeviceAdapter);

            btnCancelDialogAvailableDevices.setOnClickListener(v -> {
                prgTopDialogAvailableBleDevices.setVisibility(VISIBLE);
//                mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE)));
                mBleDeviceAdapter.setBleDevices(new ArrayList<>());
                mListOfAvailableBleDevicesDialog.dismiss();
                mListOfAvailableBleDevicesDialog = null;
            });

            btnScanDialogAvailableDevices.setOnClickListener(v -> {
                prgTopDialogAvailableBleDevices.setVisibility(VISIBLE);
//                mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE)));
                mBleDeviceAdapter.setBleDevices(new ArrayList<>());
                findDevices(this, mBluetoothLEHelper);
            });

            findDevices(this, mBluetoothLEHelper);
        } else {
            mListOfAvailableBleDevicesDialog.findViewById(R.id.prg_top_dialog_available_ble_devices).setVisibility(INVISIBLE);

            if (!devices.isEmpty())
                mBleDeviceAdapter.setBleDevices(devices);
            else
                mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(SEARCHING_TIMEOUT_MODE)));
        }

        if (!mListOfAvailableBleDevicesDialog.isShowing())
            mListOfAvailableBleDevicesDialog.show();

        Objects.requireNonNull(mListOfAvailableBleDevicesDialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(mListOfAvailableBleDevicesDialog));
    }

    private void handleDialogAddDeviceOffline() {
        saveDeviceAfterPaired = false;

        mAddDeviceOfflineDialog = new Dialog(Objects.requireNonNull(getContext()));
        mAddDeviceOfflineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAddDeviceOfflineDialog.setContentView(R.layout.dialog_add_new_device);
        mAddDeviceOfflineDialog.setCancelable(false);

        setTypeface((TextView) mAddDeviceOfflineDialog.findViewById(R.id.txv_title_dialog_add_new_device), "roboto_medium");

        mAddDeviceOfflineDialog.setOnDismissListener(mDialog -> {
            if (!saveDeviceAfterPaired)
                mBluetoothLEHelper.unPairDevice(mDevice.getDevice().getAddress());
        });

        Button btnCancelDialogAddNewDevice = mAddDeviceOfflineDialog.findViewById(R.id.btn_cancel_dialog_add_new_device);
        Button btnAddDialogAddNewDevice = mAddDeviceOfflineDialog.findViewById(R.id.btn_add_dialog_add_new_device);

        TextInputEditText tietDeviceNameDialogAddNewDevice = mAddDeviceOfflineDialog.findViewById(R.id.tiet_device_name_dialog_add_new_device);
        TextInputEditText tietDeviceSerialNumberDialogAddNewDevice = mAddDeviceOfflineDialog.findViewById(R.id.tiet_device_serial_number_dialog_add_new_device);

        CheckBox chbDeviceFavoriteStatusDialogAddNewDevice = mAddDeviceOfflineDialog.findViewById(R.id.chb_device_favorite_status_dialog_add_new_device);

        btnCancelDialogAddNewDevice.setOnClickListener(v -> mAddDeviceOfflineDialog.dismiss());

        btnAddDialogAddNewDevice.setOnClickListener(v -> {
            if (tietDeviceNameDialogAddNewDevice.getText() == null || tietDeviceNameDialogAddNewDevice.getText().toString().equals(""))
                Toast.makeText(getActivity(), "device name is empty", Toast.LENGTH_SHORT).show();
            else if (tietDeviceSerialNumberDialogAddNewDevice.getText() == null || tietDeviceSerialNumberDialogAddNewDevice.getText().toString().equals(""))
                Toast.makeText(getActivity(), "serial number is empty", Toast.LENGTH_SHORT).show();
            else {
                this.mDeviceViewModel.getADeviceBySerialNumber(tietDeviceSerialNumberDialogAddNewDevice.getText().toString())
                        .observe(this, new Observer<Device>() {
                            @Override
                            public void onChanged(@Nullable Device device) {
                                if (device != null)
                                    Toast.makeText(getActivity(), "serial number is redundant", Toast.LENGTH_SHORT).show();
                                else {
                                    openProgressDialog(getContext(), null, "Saving ...");

                                    mTempDevice.setDeviceName(Objects.requireNonNull(tietDeviceNameDialogAddNewDevice.getText()).toString());
                                    mTempDevice.setDeviceSerialNumber(Objects.requireNonNull(tietDeviceSerialNumberDialogAddNewDevice.getText()).toString());
                                    mTempDevice.setFavoriteStatus(chbDeviceFavoriteStatusDialogAddNewDevice.isChecked());
                                    mTempDevice.setDeviceMacAddress(mDevice.getMacAddress().toLowerCase());

                                    DeviceActivity.PERMISSION_READ_ALL_LOCAL_DEVICES = true;
                                    AddDeviceFragment.this.mDeviceViewModel.insertLocalDevice(
                                            AddDeviceFragment.this, new Device(mTempDevice));

                                    saveDeviceAfterPaired = true;

                                    AddDeviceFragment.this.mDeviceViewModel
                                            .getDeviceInfo(Objects.requireNonNull(tietDeviceSerialNumberDialogAddNewDevice.getText()).toString())
                                            .removeObserver(this);
                                }
                            }
                        });
            }
        });

        mAddDeviceOfflineDialog.show();
        Objects.requireNonNull(mAddDeviceOfflineDialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(mAddDeviceOfflineDialog));
    }

//    private void handleDialogAddDeviceOnline(boolean deviceExistenceStatus) {
//        if (mAddDeviceOnlineDialog == null) {
//            mAddDeviceOnlineDialog = new Dialog(Objects.requireNonNull(getContext()));
//
//            mAddDeviceOnlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            mAddDeviceOnlineDialog.setContentView(R.layout.dialog_add_new_device);
//            mAddDeviceOnlineDialog.setCancelable(false);

//            setTypeface((TextView) mAddDeviceOnlineDialog.findViewById(R.id.txv_title_dialog_add_new_device), "roboto_medium");
//
//            mAddDeviceOnlineDialog.setOnDismissListener(dialog -> mAddDeviceOnlineDialog = null);
//
//            Button btnCancelDialogAddNewDevice = mAddDeviceOnlineDialog.findViewById(R.id.btn_cancel_dialog_add_new_device);
//            Button btnAddDialogAddNewDevice = mAddDeviceOnlineDialog.findViewById(R.id.btn_add_dialog_add_new_device);
//
//            tietDeviceNameDialogAddNewDevice = mAddDeviceOnlineDialog.findViewById(R.id.tiet_device_name_dialog_add_new_device);
//            tietDeviceSerialNumberDialogAddNewDevice = mAddDeviceOnlineDialog.findViewById(R.id.tiet_device_serial_number_dialog_add_new_device);
//            chbDeviceFavoriteStatusDialogAddNewDevice = mAddDeviceOnlineDialog.findViewById(R.id.chb_device_favorite_status_dialog_add_new_device);
//
//            btnCancelDialogAddNewDevice.setOnClickListener(v -> mAddDeviceOnlineDialog.dismiss());
//
//            btnAddDialogAddNewDevice.setOnClickListener(v -> {
//                openProgressDialog(
//                        getContext(),
//                        null,
//                        String.format("Adding Lock ... %d %%", getRandomPercentNumber(1, 8)));
//
//                mDeviceViewModel.validateLockInOnlineDatabase(this,
//                        Objects.requireNonNull(tietDeviceSerialNumberDialogAddNewDevice.getText()).toString());
//            });
//        } else {
//            if (deviceExistenceStatus)
//                mDeviceViewModel.insertOnlineUserDevice(this,
//                        new UserDeviceModel(
//                                Objects.requireNonNull(tietDeviceNameDialogAddNewDevice.getText()).toString(),
//                                true,
//                                chbDeviceFavoriteStatusDialogAddNewDevice.isChecked()));
//        }
//
//
//        if (!mAddDeviceOnlineDialog.isShowing())
//            mAddDeviceOnlineDialog.show();
//
//        mAddDeviceOnlineDialog.show();
//        Objects.requireNonNull(mAddDeviceOnlineDialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(mAddDeviceOnlineDialog));
//    }

    private void connectToDevice(ScannedDeviceModel device) {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity(), null);
        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(device.getMacAddress());

        if (tempDevice != null) {
            openProgressDialog(getActivity(), null, "Pairing ...");
            onBonded(tempDevice);
        } else if (getScanPermission(this))
            this.mDeviceViewModel.connect(this, mDevice);
    }

    private List<ScannedDeviceModel> removeSavedDevicesFromDevicesList(List<ScannedDeviceModel> devices) {
        List<ScannedDeviceModel> devicesToRemove = new ArrayList<>();

        for (ScannedDeviceModel device : devices) {
            String[] tempList = device.getMacAddress().toLowerCase().split(":");
            if (tempList.length == 0 || !tempList[0].equals("6e")) {
                devicesToRemove.add(device);
            } else {
                for (Device localDevice : mAllDevices)
                    if (device.getMacAddress().toLowerCase().equals(localDevice.getBleDeviceMacAddress().toLowerCase())) {
                        devicesToRemove.add(device);
                        break;
                    }
            }
        }

        devices.removeAll(devicesToRemove);

        return devices;
    }
    //endregion Declare Methods
}
