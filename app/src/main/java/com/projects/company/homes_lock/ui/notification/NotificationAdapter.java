package com.projects.company.homes_lock.ui.notification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHolder> {

    //region Declare Objects
    private LayoutInflater mInflater;
    private List<Notification> mNotificationList;
    //endregion Declare Objects

    //region Constructor
    NotificationAdapter(Activity activity, List<Notification> mNotificationList) {
        //region Initialize Objects
        this.mInflater = LayoutInflater.from(activity);
        this.mNotificationList = mNotificationList;
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Adapter CallBacks
    @Override
    public NotificationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new NotificationAdapterViewHolder(mInflater.inflate(R.layout.item_notification_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterViewHolder notificationsAdapterViewHolder, final int i) {
        if (mNotificationList != null) {
            notificationsAdapterViewHolder.txvNotificationTitle.setText(mNotificationList.get(i).getContentTitle());
            notificationsAdapterViewHolder.itemView.setOnClickListener(v -> {
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mNotificationList != null)
            return mNotificationList.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Classes & Interfaces
    class NotificationAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView txvNotificationTitle;

        private NotificationAdapterViewHolder(View itemView) {
            super(itemView);

            txvNotificationTitle = itemView.findViewById(R.id.txv_notification_title);
        }
    }
    //endregion Declare Classes & Interfaces
}
