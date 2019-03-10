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
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedClientsModel;

import java.util.ArrayList;
import java.util.List;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class ConnectedClientsAdapter extends RecyclerView.Adapter<ConnectedClientsAdapter.ConnectedDevicesViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ConnectedClientsModel> mConnectedClients;
    private IBleScanListener mILockPageFragment;
    //endregion Declare Objects

    //region Constructor
    public ConnectedClientsAdapter(Fragment fragment, List<ConnectedClientsModel> mConnectedClients) {
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
    public ConnectedDevicesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ConnectedDevicesViewHolder(mInflater.inflate(R.layout.item_connected_clients_to_device, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedDevicesViewHolder connectedDevicesViewHolder, final int i) {
        if (mConnectedClients != null && !mConnectedClients.isEmpty()) {
            ConnectedClientsModel mConnectedClientModel = mConnectedClients.get(i);
            connectedDevicesViewHolder.imgItemClientConnectionStatus.setImageDrawable(null);

            String macAddress;
            Drawable connectionStatusDrawable = null;

            if (mConnectedClientModel.getIndex() == SEARCHING_SCAN_MODE || mConnectedClientModel.getIndex() == SEARCHING_TIMEOUT_MODE) {
                macAddress = mConnectedClientModel.getIndex() == SEARCHING_SCAN_MODE ? "Scanning ..." : "Try again ...";

                connectedDevicesViewHolder.txvItemClientMacAddress.setTypeface(null, Typeface.ITALIC);
            } else {
                macAddress = mConnectedClientModel.getMacAddress();
                connectionStatusDrawable = ContextCompat.getDrawable(mActivity,
                        mConnectedClientModel.getConnectionStatus() ? R.drawable.ic_valid_gateway_on : R.drawable.ic_valid_gateway_off);

                connectedDevicesViewHolder.txvItemClientMacAddress.setTypeface(null, Typeface.NORMAL);

                connectedDevicesViewHolder.itemView.setOnClickListener(v -> {
                    if (mConnectedClientModel.getIndex() != SEARCHING_SCAN_MODE &&
                            mConnectedClientModel.getIndex() != SEARCHING_TIMEOUT_MODE)
                        mILockPageFragment.onAdapterItemClick(mConnectedClientModel);
                });
            }

            connectedDevicesViewHolder.txvItemClientMacAddress.setText(macAddress);
            connectedDevicesViewHolder.imgItemClientConnectionStatus.setImageDrawable(connectionStatusDrawable);
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
    public void setConnectedDevices(List<ConnectedClientsModel> mConnectedClients) {
        this.mConnectedClients = mConnectedClients;
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    public void addConnectedDevice(ConnectedClientsModel mConnectedClientsModel) {
        if (!this.mConnectedClients.isEmpty()) {
            for (ConnectedClientsModel mClient : this.mConnectedClients)
                if (mClient.getMacAddress().equals(mConnectedClientsModel.getMacAddress()))
                    return;

            if (this.mConnectedClients.get(0).isInvalidData() && mConnectedClientsModel.isValidData())
                this.mConnectedClients = new ArrayList<>();
        }

        this.mConnectedClients.add(mConnectedClientsModel);
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    class ConnectedDevicesViewHolder extends RecyclerView.ViewHolder {
        TextView txvItemClientMacAddress;
        ImageView imgItemClientConnectionStatus;

        private ConnectedDevicesViewHolder(View itemView) {
            super(itemView);
            txvItemClientMacAddress = itemView.findViewById(R.id.txv_item_client_mac_address);
            imgItemClientConnectionStatus = itemView.findViewById(R.id.img_item_client_connection_status);
        }
    }
    //endregion Declare Methods
}
