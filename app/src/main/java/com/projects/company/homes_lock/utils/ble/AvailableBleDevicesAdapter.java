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
import com.projects.company.homes_lock.models.datamodels.ble.AvailableBleDeviceModel;

import java.util.ArrayList;
import java.util.List;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class AvailableBleDevicesAdapter extends RecyclerView.Adapter<AvailableBleDevicesAdapter.AvailableBleDevicesViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<AvailableBleDeviceModel> mAvailableBleDevices;
    private IBleScanListener mIGatewayPageFragment;
    //endregion Declare Objects

    //region Constructor
    public AvailableBleDevicesAdapter(Fragment fragment, List<AvailableBleDeviceModel> mAvailableBleDevices) {
        //region Initialize Objects
        this.mActivity = fragment.getActivity();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
        this.mAvailableBleDevices = mAvailableBleDevices;
        this.mIGatewayPageFragment = (IBleScanListener) fragment;
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Adapter CallBacks
    @NonNull
    @Override
    public AvailableBleDevicesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new AvailableBleDevicesViewHolder(mInflater.inflate(R.layout.item_available_ble_devices, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableBleDevicesViewHolder availableBleDevicesViewHolder, final int i) {
        if (mAvailableBleDevices != null && !mAvailableBleDevices.isEmpty()) {
            AvailableBleDeviceModel mAvailableBleDeviceModel = mAvailableBleDevices.get(i);

            String macAddress;
            Drawable connectionStatusDrawable = null;

            if (mAvailableBleDeviceModel.getIndex() == SEARCHING_SCAN_MODE || mAvailableBleDeviceModel.getIndex() == SEARCHING_TIMEOUT_MODE) {
                macAddress = mAvailableBleDeviceModel.getIndex() == SEARCHING_SCAN_MODE ? "Scanning ..." : "Try again ...";

                availableBleDevicesViewHolder.txvItemDeviceMacAddress.setTypeface(null, Typeface.ITALIC);
            } else {
                macAddress = mAvailableBleDeviceModel.getMacAddress();
                connectionStatusDrawable = ContextCompat.getDrawable(mActivity,
                        mAvailableBleDeviceModel.getConnectionStatus() ? R.drawable.ic_valid_connected_devices_exist :
                                R.drawable.ic_valid_connected_devices_not_exist);

                availableBleDevicesViewHolder.txvItemDeviceMacAddress.setTypeface(null, Typeface.NORMAL);

                availableBleDevicesViewHolder.itemView.setOnClickListener(v -> {
                    if (mAvailableBleDeviceModel.getIndex() != SEARCHING_SCAN_MODE &&
                            mAvailableBleDeviceModel.getIndex() != SEARCHING_TIMEOUT_MODE)
                        mIGatewayPageFragment.onAdapterItemClick(mAvailableBleDeviceModel);
                });
            }

            availableBleDevicesViewHolder.txvItemDeviceMacAddress.setText(macAddress);
            availableBleDevicesViewHolder.imgItemDeviceConnectionStatus.setImageDrawable(connectionStatusDrawable);
        }
    }

    @Override
    public int getItemCount() {
        if (mAvailableBleDevices != null)
            return mAvailableBleDevices.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Methods
    public void setAvailableBleDevices(List<AvailableBleDeviceModel> mAvailableBleDevices) {
        this.mAvailableBleDevices = mAvailableBleDevices;
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    public void addAvailableBleDevice(AvailableBleDeviceModel availableBleDeviceModel) {
        if (!this.mAvailableBleDevices.isEmpty()) {
            for (AvailableBleDeviceModel mDevice : this.mAvailableBleDevices)
                if (mDevice.getMacAddress().equals(availableBleDeviceModel.getMacAddress()))
                    return;

            if (this.mAvailableBleDevices.get(0).isInvalidData() && availableBleDeviceModel.isValidData())
                this.mAvailableBleDevices = new ArrayList<>();
        }

        this.mAvailableBleDevices.add(availableBleDeviceModel);
        mActivity.runOnUiThread(this::notifyDataSetChanged);
    }

    class AvailableBleDevicesViewHolder extends RecyclerView.ViewHolder {
        TextView txvItemDeviceMacAddress;
        ImageView imgItemDeviceConnectionStatus;

        private AvailableBleDevicesViewHolder(View itemView) {
            super(itemView);
            txvItemDeviceMacAddress = itemView.findViewById(R.id.txv_item_ble_device_mac_address);
            imgItemDeviceConnectionStatus = itemView.findViewById(R.id.img_item_ble_device_connection_status);
        }
    }
    //endregion Declare Methods
}
