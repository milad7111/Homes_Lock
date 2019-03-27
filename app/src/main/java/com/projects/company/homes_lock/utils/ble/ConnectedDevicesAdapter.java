package com.projects.company.homes_lock.utils.ble;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;

import java.util.ArrayList;
import java.util.List;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class ConnectedDevicesAdapter extends RecyclerView.Adapter<ConnectedDevicesAdapter.ConnectedDevicesViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ConnectedDeviceModel> mConnectedDevices;
    private IBleScanListener mILockPageFragment;
    //endregion Declare Objects

    //region Constructor
    public ConnectedDevicesAdapter(Fragment fragment, List<ConnectedDeviceModel> mConnectedDevices) {
        //region Initialize Objects
        this.mActivity = fragment.getActivity();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
        this.mConnectedDevices = mConnectedDevices;
        this.mILockPageFragment = (IBleScanListener) fragment;
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Adapter CallBacks
    @NonNull
    @Override
    public ConnectedDevicesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ConnectedDevicesViewHolder(mInflater.inflate(R.layout.item_connected_devices, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedDevicesViewHolder connectedDevicesViewHolder, final int i) {
        if (mConnectedDevices != null && !mConnectedDevices.isEmpty()) {
            ConnectedDeviceModel mConnectedDeviceModel = mConnectedDevices.get(i);

            String macAddress;
            String connectionType;
            Drawable connectionStatusDrawable = null;

            if (mConnectedDeviceModel.getIndex() == SEARCHING_SCAN_MODE || mConnectedDeviceModel.getIndex() == SEARCHING_TIMEOUT_MODE) {
                macAddress = mConnectedDeviceModel.getIndex() == SEARCHING_SCAN_MODE ? "Scanning ..." : "Try again ...";
                connectionType = "";

                connectedDevicesViewHolder.txvItemDeviceMacAddress.setTypeface(null, Typeface.ITALIC);
            } else {
                macAddress = mConnectedDeviceModel.getMacAddress();
                connectionType = mConnectedDeviceModel.isClient() ? "CLIENT" : "SERVER";
                connectionStatusDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_action_disconnect_device);

                connectedDevicesViewHolder.txvItemDeviceMacAddress.setTypeface(null, Typeface.NORMAL);

                connectedDevicesViewHolder.itemView.setOnClickListener(v -> {
                    if (mConnectedDeviceModel.getIndex() != SEARCHING_SCAN_MODE &&
                            mConnectedDeviceModel.getIndex() != SEARCHING_TIMEOUT_MODE)
                        mILockPageFragment.onAdapterItemClick(mConnectedDeviceModel);
                });
            }

            connectedDevicesViewHolder.txvItemDeviceMacAddress.setText(macAddress);
            connectedDevicesViewHolder.txvItemDeviceConnectionType.setText(connectionType);
            connectedDevicesViewHolder.imgItemActionDisconnectDevice.setImageDrawable(connectionStatusDrawable);
        }
    }

    @Override
    public int getItemCount() {
        if (mConnectedDevices != null)
            return mConnectedDevices.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Methods
    public void setConnectedDevices(List<ConnectedDeviceModel> mConnectedDevices) {
        this.mConnectedDevices = mConnectedDevices;
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    public void addConnectedDevice(ConnectedDeviceModel mConnectedDeviceModel) {
        if (!this.mConnectedDevices.isEmpty()) {
            for (ConnectedDeviceModel mDevice : this.mConnectedDevices)
                if (mDevice.getMacAddress().equals(mConnectedDeviceModel.getMacAddress()))
                    return;

            if (this.mConnectedDevices.get(0).isInvalidData() && mConnectedDeviceModel.isValidData())
                this.mConnectedDevices = new ArrayList<>();
        }

        this.mConnectedDevices.add(mConnectedDeviceModel);
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    class ConnectedDevicesViewHolder extends RecyclerView.ViewHolder {
        TextView txvItemDeviceMacAddress;
        TextView txvItemDeviceConnectionType;
        ImageView imgItemActionDisconnectDevice;

        private ConnectedDevicesViewHolder(View itemView) {
            super(itemView);
            txvItemDeviceMacAddress = itemView.findViewById(R.id.txv_item_device_mac_address);
            txvItemDeviceConnectionType = itemView.findViewById(R.id.txv_item_device_connection_type);
            imgItemActionDisconnectDevice = itemView.findViewById(R.id.img_item_action_disconnect_device);
        }
    }
    //endregion Declare Methods
}
