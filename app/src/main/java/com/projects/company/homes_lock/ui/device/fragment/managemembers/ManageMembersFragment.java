package com.projects.company.homes_lock.ui.device.fragment.managemembers;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.MemberModel;
import com.projects.company.homes_lock.models.viewmodels.ManageMembersViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_SECONDARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageMembersFragment extends Fragment
        implements
        IManageMembersFragment,
        View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    private RecyclerView rcvManageMembersFragment;
    private Button btnSyncManageMembersFragment;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private Device mDevice;
    private UserViewModel mUserViewModel;
    private LockUserAdapter mLockUserAdapter;
    //endregion Declare Objects

    //region Constructor
    public ManageMembersFragment() {
    }

    public static ManageMembersFragment newInstance(Device device) {
        ManageMembersFragment fragment = new ManageMembersFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM, new Gson().toJson(device));
        fragment.setArguments(args);

        return fragment;
    }
    //endregion Constructor

    //region Main Callbacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mDevice = getArguments() != null ?
                (Device) convertJsonToObject(getArguments().getString(ARG_PARAM), Device.class.getName())
                : null;

        this.mUserViewModel = ViewModelProviders.of(
                this,
                new ManageMembersViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), this))
                .get(UserViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        rcvManageMembersFragment = view.findViewById(R.id.rcv_manage_members_fragment);
        btnSyncManageMembersFragment = view.findViewById(R.id.btn_sync_manage_members_fragment);
        //endregion Initialize Views

        //region Setup Views
        btnSyncManageMembersFragment.setOnClickListener(this);
        //endregion Setup Views

        syncLockMembersWithServer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sync_manage_members_fragment:
                syncLockMembersWithServer();
                break;
        }
    }
    //endregion Main Callbacks

    //region IManageMembersFragment Callbacks
    @Override
    public void onGetUserLockData(List<User> response) {
        ArrayList<MemberModel> mMemberList = new ArrayList<>();

        boolean findPrimaryAdmin = false;
        for (User user : response) {
            int adminStatus;
            if (user.getRelatedUserLocks().get(0).getAdminStatus()) {
                if (findPrimaryAdmin)
                    adminStatus = MEMBER_STATUS_SECONDARY_ADMIN;
                else {
                    adminStatus = MEMBER_STATUS_PRIMARY_ADMIN;
                    findPrimaryAdmin = true;
                }
            } else
                adminStatus = MEMBER_STATUS_NOT_ADMIN;

            mMemberList.add(new MemberModel(R.drawable.ic_default_not_admin_user, user.getName(), adminStatus));
        }
        mLockUserAdapter = new LockUserAdapter(this, mMemberList);
        rcvManageMembersFragment.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvManageMembersFragment.setItemAnimator(new DefaultItemAnimator());
        rcvManageMembersFragment.setAdapter(mLockUserAdapter);
    }

    @Override
    public void onAdapterItemClick(MemberModel member) {
    }

    @Override
    public void onActionUserClick(MemberModel member) {
        Toast.makeText(getContext(), "delete" + member.getMemberName(), Toast.LENGTH_SHORT).show();
    }
    //endregion IManageMembersFragment Callbacks

    //region Declare Methods
    private void syncLockMembersWithServer() {
        this.mUserViewModel.getLockUsersByLockObjectId(mDevice.getObjectId());
    }
    //endregion Declare Methods
}
