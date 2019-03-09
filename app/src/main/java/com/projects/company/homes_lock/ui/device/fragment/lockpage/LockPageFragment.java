package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedClientsModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.ui.device.fragment.setting.SettingFragment;
import com.projects.company.homes_lock.utils.ble.ConnectedClientsAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.support.v4.content.ContextCompat.getColor;
import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.TIMES_TO_SCAN_BLE_DEVICES;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getLockBriefStatusColor;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getLockBriefStatusText;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBatteryStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setFragment;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setIsLockedImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setLockConnectionStatusToGatewayImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockPageFragment extends BaseFragment
        implements
        IBleScanListener,
        ILockPageFragment,
        View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    ImageView imgIsLockedLockPage;
    ImageView imgBatteryStatusLockPage;
    ImageView imgGatewayConnectionStatusLockPage;
    ImageView imgBleLockPage;
    ImageView imgManageMembersLockPage;
    ImageView imgMoreInfoLockPage;

    TextView txvDeviceNameLockPage;
    TextView txvNewUpdateLockPage;
    TextView txvBriefStatusLockPage;
    TextView txvDeviceTypeLockPage;
    //endregion Declare Views

    //region Declare Variables
    private boolean isConnectedToBleDevice;
//    private int wifiNetworksCount = 0; //TODO gateway
    //endregion Declare Variables

    //region Declare Arrays & Lists
    private List<ConnectedClientsModel> mConnectedClients = new ArrayList<>();
    //endregion Declare Arrays & Lists

    //region Declare Objects
    private Context mContext;
    private DeviceViewModel mDeviceViewModel;
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    private Device mDevice;
    private ConnectedClientsAdapter mConnectedClientsAdapter;

    private Dialog connectedClientsToDeviceListDialog;
    private Dialog disconnectClientDialog;
    //endregion Declare Objects

    //region Constructor
    public LockPageFragment() {
    }

    public static LockPageFragment newInstance(Device device) {
        LockPageFragment fragment = new LockPageFragment();
        fragment.mDevice = device;
        return fragment;
    }
    //endregion Constructor

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        isConnectedToBleDevice = false;
        //endregion Initialize Variables

        //region Initialize Objects
        mContext = getContext();
        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        //endregion Initialize Objects

        readAllLockInfo();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgIsLockedLockPage = view.findViewById(R.id.img_lock_status_lock_page);
        imgBatteryStatusLockPage = view.findViewById(R.id.img_battery_status_lock_page);
        imgGatewayConnectionStatusLockPage = view.findViewById(R.id.img_gateway_connection_status_lock_page);
        imgBleLockPage = view.findViewById(R.id.img_ble_lock_page);
        imgManageMembersLockPage = view.findViewById(R.id.img_manage_members_lock_page);
        imgMoreInfoLockPage = view.findViewById(R.id.img_more_info_lock_page);

        txvDeviceNameLockPage = view.findViewById(R.id.txv_device_name_lock_page);
        txvNewUpdateLockPage = view.findViewById(R.id.txv_new_update_lock_page);
        txvBriefStatusLockPage = view.findViewById(R.id.txv_brief_status_lock_page);
        txvDeviceTypeLockPage = view.findViewById(R.id.txv_device_type_lock_page);
        //endregion Initialize Views

        //region Setup Views
        imgIsLockedLockPage.setOnClickListener(this);
        imgGatewayConnectionStatusLockPage.setOnClickListener(this);
        imgBleLockPage.setOnClickListener(this);
        imgManageMembersLockPage.setOnClickListener(this);
        imgMoreInfoLockPage.setOnClickListener(this);
        //endregion Setup Views

        //region init
        ViewHelper.setContext(getContext());
        handleProgressDialog(null, null, null, false);

        updateViewData(!isUserLoggedIn());

        if (isUserLoggedIn()) {
            this.mDeviceViewModel.setListenerForDevice(this, mDevice);
            this.mDeviceViewModel.initMQTT(getActivity(), mDevice.getObjectId());
        }
        //endregion init
    }

    @Override
    public void onPause() {
        super.onPause();

        if (BaseApplication.isUserLoggedIn())
            this.mDeviceViewModel.disconnectMQTT();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceViewModel.disconnect();

        if (mBluetoothLEHelper != null)
            mBluetoothLEHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_lock_status_lock_page:
                sendLockCommand(!mDevice.getIsLocked());
                break;
            case R.id.img_gateway_connection_status_lock_page:
                handleConnectedDevices();
                break;
            case R.id.img_ble_lock_page:
                handleLockBleConnection();
                break;
            case R.id.img_manage_members_lock_page:
                handleDeviceMembers();
                break;
            case R.id.img_more_info_lock_page:
                setFragment(
                        (AppCompatActivity) getActivity(),
                        R.id.frg_lock_activity,
                        SettingFragment.newInstance(mDevice.getObjectId(), mDeviceViewModel));
                break;
        }
    }
    //endregion Main CallBacks

    //region BLE CallBacks
    @Override
    public void onFindBleSuccess(List response) {
    }

    @Override
    public void onFindBleFault() {
    }

    @Override
    public void onAdapterItemClick(BaseModel client) {
        handleDialogDisconnectClientFromDevice((ConnectedClientsModel) client);
//        WifiNetworksModel tempNetwork = (WifiNetworksModel) network;//TODO gateway
//        handleDialogSetDeviceWifiNetwork(tempNetwork);//TODO gateway
    }

    @Override
    public void onBonded(BluetoothDevice device) {
//        mDeviceViewModel.getDeviceDataFromBleDevice();
//        mDeviceViewModel.getDeviceCommonSettingInfoFromBleDevice();
//        mDeviceViewModel.getLockSpecifiedSettingInfoFromBleDevice();
    }

    @Override
    public void onGetAvailableWifiNetworksCountAroundDevice(int count) {
//        wifiNetworksCount = count;//TODO gateway
//        mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this, 0);//TODO gateway
    }

    @Override
    public void onFindNewNetworkAroundDevice(WifiNetworksModel wifiNetworksModel) {
//        addFoundNetworkToList(wifiNetworksModel);//TODO gateway
//
//        if (wifiNetworksModel.getIndex() != wifiNetworksCount - 1)
//            mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this, wifiNetworksModel.getIndex() + 1);
//
//        handleDialogListOfConnectedClientsToDevice();
    }

    @Override
    public void onSetDeviceWifiNetworkSSIDSuccessful() {
        //TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkSSIDFailed() {
        //TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkPasswordSuccessful() {
        //TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkPasswordFailed() {
        //TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkAuthenticationTypeSuccessful() {
        //TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkAuthenticationTypeFailed() {
        //TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkSuccessful() {
//        closeDialogHandleDeviceWifiNetwork();//TODO gateway
    }

    @Override
    public void onSetDeviceWifiNetworkFailed() {
//        DialogHelper.handleProgressDialog(null, null, null, false);//TODO gateway
//        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onGetUpdatedDevice(Device response) {
        mDevice = response;
        updateViewData(false);
        this.mDeviceViewModel.updateDevice(response);
    }

    @Override
    public void onSendRequestGetAvailableWifiSuccessful() {
//        Log.d(getTag(), "Get Available Wifi Networks Around Device Sent Successful! Wait ...");//TODO gateway
    }

    @Override
    public void onSendLockCommandSuccessful(String command) {
        Log.d(getTag(), String.format("Command : %s , received by device successfully.", command));
    }

    @Override
    public void onSendLockCommandFailed(String command) {
        Log.d(getTag(), String.format("Command : %s , failed.", command));
    }

    @Override
    public void onGetReceiveNewConnectedClientToDevice(String connectedClientMacAddress) {
        mConnectedClientsAdapter.addConnectedDevice(
                new ConnectedClientsModel(mConnectedClients.size(), connectedClientMacAddress, true));
//        handleDialogListOfConnectedClientsToDevice();
    }
    //endregion BLE CallBacks

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        txvDeviceTypeLockPage.setText(mDevice.getDeviceType());

        setIsLockedImage(imgIsLockedLockPage,
                setDefault ? 2 : (mDevice.getIsLocked() ? 1 : 0));

        setBatteryStatusImage(setDefault, imgBatteryStatusLockPage, mDevice.getBatteryPercentage());
        setLockConnectionStatusToGatewayImage(
                imgGatewayConnectionStatusLockPage, setDefault, mDevice.getGatewayStatus(), mDevice.getWifiStrength());

        imgManageMembersLockPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
        txvDeviceNameLockPage.setTextColor(setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));
        txvDeviceNameLockPage.setText(mDevice.getBleDeviceName());

        txvBriefStatusLockPage.setText(
                setDefault ?
                        getString(R.string.fragment_text_view_data_not_synced) :
                        getLockBriefStatusText(mDevice.getIsLocked(), mDevice.getIsDoorClosed()));
        txvBriefStatusLockPage.setTextColor(
                setDefault ? getColor(mContext, R.color.md_grey_500) :
                        getColor(mContext, getLockBriefStatusColor(mDevice.getIsLocked(), mDevice.getIsDoorClosed())));

        txvNewUpdateLockPage.setText(null);

        handleProgressDialog(null, null, null, false);
    }

    private void getDeviceInfo() {
        this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, device -> {
            mDevice = device;
            updateViewData(false);
        });
    }

    private void addFoundNetworkToList(WifiNetworksModel wifiNetworksModel) {
//        if (!findNetworkInList(wifiNetworksModel.getSSID()))//TODO gateway
//            this.mWifiNetworkList.add(wifiNetworksModel);
    }

    private boolean findNetworkInList(String mSSID) {
//        for (WifiNetworksModel network : mWifiNetworkList)//TODO gateway
//            if (network.getSSID().equals(mSSID))
//                return true;

        return false;
    }

    private void closeDialogHandleDeviceWifiNetwork() {
//        if (confirmDisconnectClientDialog != null) {//TODO gateway
//            confirmDisconnectClientDialog.dismiss();
//            confirmDisconnectClientDialog = null;
//            connectedClientsToDeviceListDialog.dismiss();
//            connectedClientsToDeviceListDialog = null;
//        }
    }

    private void handleDeviceMembers() {
        if (isUserLoggedIn())
            setFragment((AppCompatActivity) getActivity(), R.id.frg_lock_activity, ManageMembersFragment.newInstance(mDevice));
        else
            Toast.makeText(getActivity(), "This is not available in Local Mode", Toast.LENGTH_LONG).show();
    }

    private void readAllLockInfo() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mDevice = LockPageFragment.this.mDeviceViewModel.getUserLockInfo(mDevice.getObjectId());
            }
        }.start();
    }
    //endregion Declare Methods

    //region Declare BLE Methods
    private void waitForBleResponse() {
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(() -> {
//            mDeviceViewModel.freeBleBuffer();
//        }, 2000);
    }

    private void handleLockBleConnection() {
        if (isUserLoggedIn())
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else {
            if (isConnectedToBleDevice)
                this.mDeviceViewModel.disconnect();
            else
                connectToDevice();
        }
    }

    private void handleConnectedDevices() {
        if (isConnectedToBleDevice) {
            handleDialogListOfConnectedClientsToDevice();
        }
//        if (mDevice.getWifiStatus()) {
//            DialogHelper.handleProgressDialog(getContext(), null, "Disconnect Internet Connection ...", true);
//            disconnectLockFromGateway(this);//TODO gateway
//        }
//            else//TODO gateway
//                handleDialogListOfConnectedClientsToDevice();
    }

    private void disconnectLockFromGateway(LockPageFragment fragment) {
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(mDevice.getBleDeviceMacAddress());

        if (tempDevice != null) {
            handleProgressDialog(getActivity(), null, "Pairing ...", true);

            this.mDeviceViewModel.connect(this, new ScannedDeviceModel(tempDevice));
            this.mDeviceViewModel.isConnected().observe(this, isConnected -> {
                isConnectedToBleDevice = isConnected;
                ViewHelper.setBleConnectionStatusImage(imgBleLockPage, isConnected);
                initBleInfo();
            });
        } else if (getScanPermission(this))
            scanDevices();
    }

    private void scanDevices() {
        handleProgressDialog(getActivity(), null, "Pairing ...", true);

        if (!mBluetoothLEHelper.isScanning()) {
            mBluetoothLEHelper.setScanPeriod(1000);
            Handler mHandler = new Handler();
            mBluetoothLEHelper.scanLeDevice(true);

            mHandler.postDelayed(() -> {
                for (int i = 0; i < TIMES_TO_SCAN_BLE_DEVICES; i++)
                    if (connectToSpecificBleDevice(getListOfScannedDevices()))
                        break;
            }, mBluetoothLEHelper.getScanPeriod());
        }
    }

    private boolean connectToSpecificBleDevice(List<ScannedDeviceModel> listOfScannedDevices) {
        for (ScannedDeviceModel device : listOfScannedDevices) {
            if (device.getMacAddress().equals(mDevice.getBleDeviceMacAddress())) {
                this.mDeviceViewModel.connect(this, device);
                this.mDeviceViewModel.isConnected().observe(this, isConnected -> {
                    isConnectedToBleDevice = isConnected;
                    ViewHelper.setBleConnectionStatusImage(imgBleLockPage, isConnected);
                    updateViewData(!isConnected);
                    initBleInfo();
                });
                return true;
            }
        }

        return false;
    }

    private void sendLockCommand(boolean lockCommand) {
        this.mDeviceViewModel.sendLockCommand(this, lockCommand);
    }

    private void initBleInfo() {
        this.mDeviceViewModel.isSupported().observe(this, isSupported -> {
            if (isSupported != null)
                if (isSupported) {
                    getDeviceInfo();
                    mDeviceViewModel.getDeviceDataFromBleDevice();
                    mDeviceViewModel.getDeviceCommonSettingInfoFromBleDevice();
                    mDeviceViewModel.getLockSpecifiedSettingInfoFromBleDevice();
                } else {
                    updateViewData(true);

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
        });

//        this.mDeviceViewModel.isBleBufferFree().observe(this, bleBufferStatus -> {
//            if (!bleBufferStatus)
//                waitForBleResponse();
//        });
    }

    private List<ScannedDeviceModel> getListOfScannedDevices() {
        List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();

        for (BluetoothLE device : mBluetoothLEHelper.getListDevices())
            mScannedDeviceModelList.add(new ScannedDeviceModel(device));

        return mScannedDeviceModelList;
    }

    public Device getDevice() {
        return mDevice;
    }

    private void handleDialogListOfConnectedClientsToDevice() {
        if (connectedClientsToDeviceListDialog != null) {
            connectedClientsToDeviceListDialog.dismiss();
            connectedClientsToDeviceListDialog = null;
        }

        connectedClientsToDeviceListDialog = new Dialog(requireContext());
        connectedClientsToDeviceListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectedClientsToDeviceListDialog.setContentView(R.layout.dialog_connected_clients);

        if (mConnectedClientsAdapter == null) {
            mConnectedClientsAdapter = new ConnectedClientsAdapter(this,
                    Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
        }

        mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));

        RecyclerView rcvDialogConnectedClients = connectedClientsToDeviceListDialog.findViewById(R.id.rcv_dialog_connected_clients);
        Button btnCancelDialogConnectedClients = connectedClientsToDeviceListDialog.findViewById(R.id.btn_cancel_dialog_connected_clients);
        Button btnScanDialogConnectedClients = connectedClientsToDeviceListDialog.findViewById(R.id.btn_scan_dialog_connected_clients);

        rcvDialogConnectedClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogConnectedClients.setItemAnimator(new DefaultItemAnimator());
        rcvDialogConnectedClients.setAdapter(mConnectedClientsAdapter);

        btnCancelDialogConnectedClients.setOnClickListener(v -> {
            mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
            connectedClientsToDeviceListDialog.dismiss();
            connectedClientsToDeviceListDialog = null;
        });

        btnScanDialogConnectedClients.setOnClickListener(v -> {
            mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
            mDeviceViewModel.getConnectedClientsToDevice(this);
        });

        connectedClientsToDeviceListDialog.setOnDismissListener(dialog -> {
            if (connectedClientsToDeviceListDialog != null) {
                connectedClientsToDeviceListDialog.dismiss();
                connectedClientsToDeviceListDialog = null;
            }
        });

        mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
        mDeviceViewModel.getConnectedClientsToDevice(this);

        if (!connectedClientsToDeviceListDialog.isShowing()) {
            connectedClientsToDeviceListDialog.show();
            Objects.requireNonNull(connectedClientsToDeviceListDialog.getWindow())
                    .setAttributes(ViewHelper.getDialogLayoutParams(connectedClientsToDeviceListDialog));
        }
    }

    private void handleDialogDisconnectClientFromDevice(ConnectedClientsModel mConnectedClientsModel) {
        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        disconnectClientDialog = new Dialog(requireContext());
        disconnectClientDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        disconnectClientDialog.setContentView(R.layout.dialog_disconnect_device);

        Button btnCancelDialogDisconnectClient =
                disconnectClientDialog.findViewById(R.id.btn_no_dialog_disconnect_device);
        Button btnDisconnectDialogDisconnectClient =
                disconnectClientDialog.findViewById(R.id.btn_disconnect_dialog_disconnect_device);

        btnCancelDialogDisconnectClient.setOnClickListener(v -> {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        });

        btnDisconnectDialogDisconnectClient.setOnClickListener(v -> {
//            DialogHelper.handleProgressDialog(getContext(), null, "Reset Lock ...", true);
            mDeviceViewModel.disconnectClientFromDevice(this, mConnectedClientsModel);
        });

        disconnectClientDialog.show();
        Objects.requireNonNull(disconnectClientDialog.getWindow())
                .setAttributes(ViewHelper.getDialogLayoutParams(disconnectClientDialog));
    }
    //endregion Declare BLE Methods

    //region Declare Classes & Interfaces
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //endregion Declare Classes & Interfaces

    //region Declare Methods
    //endregion Declare Methods
}
