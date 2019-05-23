package com.projects.company.homes_lock.ui.notification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Notification;

import java.util.Date;
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
            notificationsAdapterViewHolder.txvNotificationSubtitle.setText(mNotificationList.get(i).getSummarySubText());
            notificationsAdapterViewHolder.txvNotificationMessage.setText(mNotificationList.get(i).getMessage());
            notificationsAdapterViewHolder.txvNotificationTime.setText(new Date(mNotificationList.get(i).getSentTime()).toString());

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
        ImageView imgNotificationPriority;
        CheckBox imgNotificationStatus;
        TextView txvNotificationTitle;
        TextView txvNotificationSubtitle;
        TextView txvNotificationMessage;
        TextView txvNotificationTime;

        private NotificationAdapterViewHolder(View itemView) {
            super(itemView);

            imgNotificationPriority = itemView.findViewById(R.id.img_notification_priority);
            imgNotificationStatus = itemView.findViewById(R.id.chb_notification_status);
            txvNotificationTitle = itemView.findViewById(R.id.txv_notification_title);
            txvNotificationSubtitle = itemView.findViewById(R.id.txv_notification_subtitle);
            txvNotificationMessage = itemView.findViewById(R.id.txv_notification_message);
            txvNotificationTime = itemView.findViewById(R.id.txv_notification_time);
        }
    }
    //endregion Declare Classes & Interfaces
}
