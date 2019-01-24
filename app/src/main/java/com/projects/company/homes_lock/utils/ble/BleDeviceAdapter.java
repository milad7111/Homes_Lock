package com.projects.company.homes_lock.utils.ble;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.List;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;
import static com.projects.company.homes_lock.utils.helper.DataHelper.calculateRSSI;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ScannedDeviceModel> mScannedDeviceModelList;
    private IBleScanListener mIAddLockFragment;
    //endregion Declare Objects

    //region Constructor
    public BleDeviceAdapter(Fragment fragment, List<ScannedDeviceModel> mScannedDeviceModelList) {
        //region Initialize Objects
        this.mActivity = fragment.getActivity();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
        this.mScannedDeviceModelList = mScannedDeviceModelList;
        this.mIAddLockFragment = (IBleScanListener) fragment;
        //endregion Initialize Objects
    }
    //endregion Constructor

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

            if (mScannedDeviceModelList.get(i).getRSSI() == SEARCHING_SCAN_MODE ||
                    mScannedDeviceModelList.get(i).getRSSI() == SEARCHING_TIMEOUT_MODE) {
                name = mScannedDeviceModelList.get(i).getRSSI() == SEARCHING_SCAN_MODE ?
                        mActivity.getString(R.string.adapter_empty_scanning) : mActivity.getString(R.string.adapter_empty_try_again);
                rssi = mScannedDeviceModelList.get(i).getRSSI();

                bleDeviceViewHolder.txvBleDeviceName.setTypeface(null, Typeface.ITALIC);
            } else {
                name = mScannedDeviceModel.getName();
                rssi = calculateRSSI(mScannedDeviceModel.getRSSI());

                bleDeviceViewHolder.txvBleDeviceName.setTypeface(null, Typeface.NORMAL);
            }

            bleDeviceViewHolder.txvBleDeviceName.setText(name);
            ViewHelper.setRSSIImage(bleDeviceViewHolder.imgBleDeviceRSSI, rssi);
        }

        bleDeviceViewHolder.itemView.setOnClickListener(v -> mIAddLockFragment.onAdapterItemClick(mScannedDeviceModelList.get(i)));
    }

    @Override
    public int getItemCount() {
        if (mScannedDeviceModelList != null)
            return mScannedDeviceModelList.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Methods
    public void setBleDevices(List<ScannedDeviceModel> mScannedDeviceModelList) {
        this.mScannedDeviceModelList = mScannedDeviceModelList;
        notifyDataSetChanged();
    }

    class BleDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView txvBleDeviceName;
        ImageView imgBleDeviceRSSI;

        private BleDeviceViewHolder(View itemView) {
            super(itemView);
            txvBleDeviceName = itemView.findViewById(R.id.txv_item_ble_device_name);
            imgBleDeviceRSSI = itemView.findViewById(R.id.img_item_ble_device_rssi);
        }
    }
    //endregion Declare Methods
}
