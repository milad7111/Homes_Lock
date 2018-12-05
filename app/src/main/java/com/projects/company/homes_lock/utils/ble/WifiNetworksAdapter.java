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
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.List;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;
import static com.projects.company.homes_lock.utils.helper.DataHelper.calculateRSSI;

public class WifiNetworksAdapter extends RecyclerView.Adapter<WifiNetworksAdapter.WifiNetworksViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<WifiNetworksModel> mWifiNetworksModelList;
    private IBleScanListener mILockPageFragment;
    //endregion Declare Objects

    //region Constructor
    public WifiNetworksAdapter(Fragment fragment, List<WifiNetworksModel> mWifiNetworksModelList) {
        //region Initialize Objects
        this.mActivity = fragment.getActivity();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
        this.mWifiNetworksModelList = mWifiNetworksModelList;
        this.mILockPageFragment = (IBleScanListener) fragment;
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Adapter CallBacks
    @Override
    public WifiNetworksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new WifiNetworksViewHolder(mInflater.inflate(R.layout.item_device_wifi_networks, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WifiNetworksViewHolder wifiNetworksViewHolder, final int i) {
        if (mWifiNetworksModelList != null) {
            WifiNetworksModel mWifiNetworksModel = mWifiNetworksModelList.get(i);

            String name;
            int rssi;

            if (mWifiNetworksModelList.get(i).getRSSI() == SEARCHING_SCAN_MODE ||
                    mWifiNetworksModelList.get(i).getRSSI() == SEARCHING_TIMEOUT_MODE) {
                name = mWifiNetworksModelList.get(i).getRSSI() == SEARCHING_SCAN_MODE ? "Scanning ..." : "Try again ...";
                rssi = mWifiNetworksModelList.get(i).getRSSI();

                wifiNetworksViewHolder.txvItemWifiSsidName.setTypeface(null, Typeface.ITALIC);
            } else {
                name = mWifiNetworksModel.getSSID();
                rssi = calculateRSSI(mWifiNetworksModel.getRSSI());

                wifiNetworksViewHolder.txvItemWifiSsidName.setTypeface(null, Typeface.NORMAL);
            }

            wifiNetworksViewHolder.txvItemWifiSsidName.setText(name);
            ViewHelper.setRSSIImage(wifiNetworksViewHolder.imgItemWifiRssiName, rssi);
        }

        wifiNetworksViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mILockPageFragment.onAdapterItemClick(mWifiNetworksModelList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mWifiNetworksModelList != null)
            return mWifiNetworksModelList.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Methods
    public void setAvailableNetworks(List<WifiNetworksModel> mWifiNetworksModelList) {
        this.mWifiNetworksModelList = mWifiNetworksModelList;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    class WifiNetworksViewHolder extends RecyclerView.ViewHolder {
        TextView txvItemWifiSsidName;
        ImageView imgItemWifiRssiName;

        private WifiNetworksViewHolder(View itemView) {
            super(itemView);
            txvItemWifiSsidName = itemView.findViewById(R.id.txv_item_wifi_ssid_name);
            imgItemWifiRssiName = itemView.findViewById(R.id.img_item_wifi_rssi_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
    //endregion Declare Methods
}
