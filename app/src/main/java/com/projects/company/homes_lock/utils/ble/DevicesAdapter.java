package com.projects.company.homes_lock.utils.ble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.List;

//import butterknife.BindView;
//import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
//    private final ScannerActivity mContext;
    private final List<ScannedDeviceModel> mScannedDeviceModelList;
//    private OnItemClickListener mOnItemClickListener;

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(final ExtendedBluetoothDevice device);
    }

    public void setOnItemClickListener(final Context context) {
//        mOnItemClickListener = (OnItemClickListener) context;
    }

    public DevicesAdapter(final ScannerLiveData scannerLiveData) {
////        mContext = activity;
        mScannedDeviceModelList = scannerLiveData.getDevices();
////        scannerLiveData.observe(activity, devices -> {
////            final Integer i = devices.getUpdatedDeviceIndex();
////            if (i != null)
////                notifyItemChanged(i);
////            else
////                notifyDataSetChanged();
////        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
//        final View layoutView = LayoutInflater.from(mContext).inflate(R.layout.device_item, parent, false);
        return new ViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ScannedDeviceModel device = mScannedDeviceModelList.get(position);
        final String deviceName = device.getName();

//        if (!TextUtils.isEmpty(deviceName))
//            holder.deviceName.setText(deviceName);
//        else
//            holder.deviceName.setText(R.string.unknown_device);
//        holder.deviceAddress.setText(device.getMacAddress());
//        final int rssiPercent = (int) (100.0f * (127.0f + device.getRSSI()) / (127.0f + 20.0f));
//        holder.rssi.setImageLevel(rssiPercent);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mScannedDeviceModelList.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
//        @BindView(R.id.device_address) TextView deviceAddress;
//        @BindView(R.id.device_name) TextView deviceName;
//        @BindView(R.id.rssi) ImageView rssi;
//
//        private ViewHolder(final View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//
//            view.findViewById(R.id.device_container).setOnClickListener(v -> {
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onItemClick(mScannedDeviceModelList.get(getAdapterPosition()));
//                }
//            });
//        }
    }
}
