package com.projects.company.homes_lock.utils.ble;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
    private IBleScanListener mBleScanListener;
    //endregion Declare Objects

    public BleDeviceAdapter(AppCompatActivity activity, IBleScanListener mBleScanListener, final ScannerLiveData scannerLiveData) {
        this.mInflater = LayoutInflater.from(activity);
        this.mBleScanListener = mBleScanListener;

        mScannedDeviceModelList = scannerLiveData.getDevices();
        scannerLiveData.observe(activity, devices -> {
            notifyDataSetChanged();
//            final Integer i = devices.getUpdatedDeviceIndex();
//            if (i != null)
//                notifyItemChanged(i);
//            else
//                notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public BleDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.list_item_ble_devices, viewGroup, false);
        return new BleDeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceViewHolder bleDeviceViewHolder, final int i) {
        if (mScannedDeviceModelList != null) {
            ScannedDeviceModel mScannedDeviceModel = mScannedDeviceModelList.get(i);

            final int mRSSIPercent = (int) (100.0f * (127.0f + mScannedDeviceModel.getRSSI()) / (127.0f + 20.0f));

            bleDeviceViewHolder.txvBleDevicesName.setText(mScannedDeviceModel.getName());
            bleDeviceViewHolder.txvBleDevicesMacAddress.setText(mScannedDeviceModel.getMacAddress());
//            bleDeviceViewHolder.imgBleDevicesRSSI.setImageLevel(mRSSIPercent);
        } else
            bleDeviceViewHolder.txvBleDevicesName.setText("No Device Found.");

        bleDeviceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleScanListener.onClickBleDevice(mScannedDeviceModelList.get(i));
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
//        AppCompatImageView imgBleDevicesRSSI;

        private BleDeviceViewHolder(View itemView) {
            super(itemView);
            txvBleDevicesName = itemView.findViewById(R.id.txv_ble_devices_name);
            txvBleDevicesMacAddress = itemView.findViewById(R.id.txv_ble_devices_mac_address);
//            imgBleDevicesRSSI = itemView.findViewById(R.id.img_ble_devices_rssi);
        }
    }
}
