package com.projects.company.homes_lock.utils.ble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.List;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder> {

    //region Declare Objects
    private LayoutInflater mInflater;
    private List<ScannedDeviceModel> mScannedDeviceModelList;
    //endregion Declare Objects

    public BleDeviceAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BleDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.list_item_ble_devices, viewGroup, false);
        return new BleDeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceViewHolder bleDeviceViewHolder, int i) {
        if (mScannedDeviceModelList != null) {
            ScannedDeviceModel current = mScannedDeviceModelList.get(i);

            bleDeviceViewHolder.txvBleDevicesName.setText(current.getName());
            bleDeviceViewHolder.txvBleDevicesMacAddress.setText(current.getMacAddress());
        } else {
            // Covers the case of data not being ready yet.
            bleDeviceViewHolder.txvBleDevicesName.setText("No Word");
        }

        bleDeviceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void setBleDevices(List<ScannedDeviceModel> mScannedDeviceModelList) {
        this.mScannedDeviceModelList = mScannedDeviceModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mScannedDeviceModelList != null)
            return mScannedDeviceModelList.size();
        else return 0;
    }

    class BleDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView txvBleDevicesName;
        TextView txvBleDevicesMacAddress;

        private BleDeviceViewHolder(View itemView) {
            super(itemView);
            txvBleDevicesName = itemView.findViewById(R.id.txv_ble_devices_name);
            txvBleDevicesMacAddress = itemView.findViewById(R.id.txv_ble_devices_mac_address);
        }
    }
}
