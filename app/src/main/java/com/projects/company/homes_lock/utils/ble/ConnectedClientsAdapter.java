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

public class ConnectedClientsAdapter extends RecyclerView.Adapter<ConnectedClientsAdapter.ConnectedClientsViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ConnectedDeviceModel> mConnectedClients;
    private IBleScanListener mILockPageFragment;
    //endregion Declare Objects

    //region Constructor
    public ConnectedClientsAdapter(Fragment fragment, List<ConnectedDeviceModel> mConnectedClients) {
        //region Initialize Objects
        this.mActivity = fragment.getActivity();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
        this.mConnectedClients = mConnectedClients;
        this.mILockPageFragment = (IBleScanListener) fragment;
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Adapter CallBacks
    @NonNull
    @Override
    public ConnectedClientsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ConnectedClientsViewHolder(mInflater.inflate(R.layout.item_connected_devices, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedClientsViewHolder connectedClientsViewHolder, final int i) {
        if (mConnectedClients != null && !mConnectedClients.isEmpty()) {
            ConnectedDeviceModel mConnectedDeviceModel = mConnectedClients.get(i);

            String macAddress;
            String connectionType;
            Drawable connectionStatusDrawable = null;

            if (mConnectedDeviceModel.getIndex() == SEARCHING_SCAN_MODE || mConnectedDeviceModel.getIndex() == SEARCHING_TIMEOUT_MODE) {
                macAddress = mConnectedDeviceModel.getIndex() == SEARCHING_SCAN_MODE ? "Scanning ..." : "Try again ...";
                connectionType = "";

                connectedClientsViewHolder.txvItemDeviceMacAddress.setTypeface(null, Typeface.ITALIC);
            } else {
                macAddress = mConnectedDeviceModel.getMacAddress();
                connectionType = mConnectedDeviceModel.isClient() ? "CLIENT" : "SERVER";
                connectionStatusDrawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_action_disconnect_device);

                connectedClientsViewHolder.txvItemDeviceMacAddress.setTypeface(null, Typeface.NORMAL);

                connectedClientsViewHolder.itemView.setOnClickListener(v -> {
                    if (mConnectedDeviceModel.getIndex() != SEARCHING_SCAN_MODE &&
                            mConnectedDeviceModel.getIndex() != SEARCHING_TIMEOUT_MODE)
                        mILockPageFragment.onAdapterItemClick(mConnectedDeviceModel);
                });
            }

            connectedClientsViewHolder.txvItemDeviceMacAddress.setText(macAddress);
            connectedClientsViewHolder.txvItemDeviceConnectionType.setText(connectionType);
            connectedClientsViewHolder.imgItemActionDisconnectDevice.setImageDrawable(connectionStatusDrawable);
        }
    }

    @Override
    public int getItemCount() {
        if (mConnectedClients != null)
            return mConnectedClients.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Methods
    public void setConnectedClients(List<ConnectedDeviceModel> mConnectedClients) {
        this.mConnectedClients = mConnectedClients;
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    public void addConnectedDevice(ConnectedDeviceModel mConnectedDeviceModel) {
        if (!this.mConnectedClients.isEmpty()) {
            for (ConnectedDeviceModel mDevice : this.mConnectedClients)
                if (mDevice.getMacAddress().equals(mConnectedDeviceModel.getMacAddress()))
                    return;

            if (this.mConnectedClients.get(0).isInvalidData() && mConnectedDeviceModel.isValidData())
                this.mConnectedClients = new ArrayList<>();
        }

        this.mConnectedClients.add(mConnectedDeviceModel);
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    class ConnectedClientsViewHolder extends RecyclerView.ViewHolder {
        TextView txvItemDeviceMacAddress;
        TextView txvItemDeviceConnectionType;
        ImageView imgItemActionDisconnectDevice;

        private ConnectedClientsViewHolder(View itemView) {
            super(itemView);
            txvItemDeviceMacAddress = itemView.findViewById(R.id.txv_item_device_mac_address);
            txvItemDeviceConnectionType = itemView.findViewById(R.id.txv_item_device_connection_type);
            imgItemActionDisconnectDevice = itemView.findViewById(R.id.img_item_action_disconnect_device);
        }
    }
    //endregion Declare Methods
}
