package com.projects.company.homes_lock.ui.notification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            switch (mNotificationList.get(i).getContentTitle()) {
                case "Device Connection":
                    notificationsAdapterViewHolder.imgNotificationType.setImageResource(R.drawable.ic_notification_internet_connection);
                    break;
                case "Manually Open":
                    notificationsAdapterViewHolder.imgNotificationType.setImageResource(R.drawable.ic_notification_manual_opening);
                    break;
                case "Abnormal Behaviour":
                    notificationsAdapterViewHolder.imgNotificationType.setImageResource(R.drawable.ic_notification_danger);
                    break;
                case "Battery Charge":
                    notificationsAdapterViewHolder.imgNotificationType.setImageResource(R.drawable.ic_notification_battery);
                    break;
            }
            notificationsAdapterViewHolder.txvNotificationTitle.setText(mNotificationList.get(i).getContentTitle());
            notificationsAdapterViewHolder.txvNotificationTicker.setText(mNotificationList.get(i).getTicker());
            notificationsAdapterViewHolder.txvNotificationMessage.setText(mNotificationList.get(i).getMessage());
            notificationsAdapterViewHolder.txvNotificationTime.setText(mNotificationList.get(i).getCustomSentTime());

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
        ImageView imgNotificationType;
        TextView txvNotificationTitle;
        TextView txvNotificationTicker;
        TextView txvNotificationMessage;
        TextView txvNotificationTime;

        private NotificationAdapterViewHolder(View itemView) {
            super(itemView);

            imgNotificationType = itemView.findViewById(R.id.img_notification_type);
            txvNotificationTitle = itemView.findViewById(R.id.txv_notification_title);
            txvNotificationTicker = itemView.findViewById(R.id.txv_notification_ticker);
            txvNotificationMessage = itemView.findViewById(R.id.txv_notification_message);
            txvNotificationTime = itemView.findViewById(R.id.txv_notification_time);
        }
    }
    //endregion Declare Classes & Interfaces
}
