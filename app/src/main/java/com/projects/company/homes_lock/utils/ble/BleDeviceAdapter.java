package com.projects.company.homes_lock.utils.ble;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.List;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder> {

    //region Declare Objects
    private LayoutInflater mInflater;
    private List<ScannedDeviceModel> mScannedDeviceModelList;
    //endregion Declare Objects

    public BleDeviceAdapter(AppCompatActivity activity, List<ScannedDeviceModel> mScannedDeviceModelList) {
        //region Initialize Objects
        this.mInflater = LayoutInflater.from(activity);
        this.mScannedDeviceModelList = mScannedDeviceModelList;
        //endregion Initialize Objects
    }

    //region Adapter CallBacks
    @Override
    public BleDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new BleDeviceViewHolder(mInflater.inflate(R.layout.item_ble_devices, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceViewHolder bleDeviceViewHolder, final int i) {
        if (mScannedDeviceModelList != null) {
            ScannedDeviceModel mScannedDeviceModel = mScannedDeviceModelList.get(i);

            String name;
            int rssi;

            if (mScannedDeviceModelList.get(i).getDevice() == null) {
                name = "Scanning ...";
                rssi = 1000;

                bleDeviceViewHolder.txvBleDeviceName.setTypeface(null, Typeface.ITALIC);
            } else {
                name = mScannedDeviceModel.getName();
                rssi = DataHelper.calculateRSSI(mScannedDeviceModel.getRSSI());

                bleDeviceViewHolder.txvBleDeviceName.setTypeface(null, Typeface.NORMAL);
            }

            bleDeviceViewHolder.txvBleDeviceName.setText(name);
            ViewHelper.setRSSIImage(bleDeviceViewHolder.imgBleDeviceRSSI, rssi);
        }
    }

    @Override
    public int getItemCount() {
        if (mScannedDeviceModelList != null)
            return mScannedDeviceModelList.size();
        else return 0;
    }

    //region Declare Methods
    public void setBleDevices(List<ScannedDeviceModel> mScannedDeviceModelList) {
        this.mScannedDeviceModelList = mScannedDeviceModelList;
        notifyDataSetChanged();
    }
    //endregion Adapter CallBacks

    class BleDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView txvBleDeviceName;
        ImageView imgBleDeviceRSSI;

        private BleDeviceViewHolder(View itemView) {
            super(itemView);
            txvBleDeviceName = itemView.findViewById(R.id.txv_item_ble_device_name);
            imgBleDeviceRSSI = itemView.findViewById(R.id.img_item_ble_device_rssi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
    //endregion Declare Methods
}
