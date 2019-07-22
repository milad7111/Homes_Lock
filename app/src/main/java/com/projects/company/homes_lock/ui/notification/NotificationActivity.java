package com.projects.company.homes_lock.ui.notification;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.models.viewmodels.NotificationViewModel;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity {

    //region Declare Objects
    private NotificationViewModel mNotificationViewModel;
    private NotificationAdapter mAdapter;
    //endregion Declare Objects

    //region Declare Views
    private RecyclerView rcvNotificationActivity;
    //endregion Declare Views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //region Initialize Objects
        mAdapter = new NotificationAdapter(this, new ArrayList<>());
        this.mNotificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        //endregion Initialize Objects

        //region Initialize Views
        rcvNotificationActivity = findViewById(R.id.rcv_notification_activity);
        rcvNotificationActivity.setItemAnimator(new DefaultItemAnimator());
        rcvNotificationActivity.setAdapter(mAdapter);

        DividerItemDecoration verticalDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        verticalDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_view_vertical_divider));
        rcvNotificationActivity.addItemDecoration(verticalDecoration);
        //endregion Initialize Views

        //region init
        getAllNotifications();
        //endregion init
    }

    public void getAllNotifications() {
        this.mNotificationViewModel.getAllNotifications().observe(this, notifications -> {
            mAdapter = new NotificationAdapter(this, notifications);
            rcvNotificationActivity.setAdapter(mAdapter);
        });
    }
}
