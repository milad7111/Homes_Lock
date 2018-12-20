package com.projects.company.homes_lock.ui.device.fragment.managemembers;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.MemberModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.ManageMembersViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.projects.company.homes_lock.utils.helper.DataHelper.LOCK_MEMBERS_SYNCING_MODE;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_SECONDARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.NOT_DEFINED_INTEGER_NUMBER;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

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
    private Fragment mFragment;
    private Device mDevice;
    private UserViewModel mUserViewModel;
    private LockUserAdapter mLockUserAdapter;
    private Dialog mRemoveLockMemberDialog;
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
        mFragment = this;

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

        //region init
        syncLockMembersWithServer();
        //endregion init
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
    public void onGetUserLockDataSuccessful(List<User> response) {
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

            mMemberList.add(new MemberModel(
                    R.drawable.ic_default_not_admin_user,
                    user.getName(),
                    adminStatus,
                    user.getRelatedUserLocks().get(0).getObjectId()));
        }

        if (getActivity() != null){
            mLockUserAdapter = new LockUserAdapter(this, mMemberList);
            rcvManageMembersFragment.setAdapter(mLockUserAdapter);
        }

        btnSyncManageMembersFragment.setClickable(true);
    }

    @Override
    public void onGetUserLockDataFailed(Object response) {
        Log.i(this.getClass().getSimpleName(), ((FailureModel) response).getFailureMessage());
        btnSyncManageMembersFragment.setClickable(true);
    }

    @Override
    public void onActionUserClick(MemberModel member) {
        handleDeleteLockMember(member);
    }

    @Override
    public void onRemoveMemberSuccessful(Long deletionTime) {
        handleProgressDialog(null, null, null, false);
        if (mRemoveLockMemberDialog != null) {
            mRemoveLockMemberDialog.dismiss();
            mRemoveLockMemberDialog = null;
        }
        syncLockMembersWithServer();
    }

    @Override
    public void onRemoveMemberFailed(ResponseBodyFailureModel response) {
        handleProgressDialog(null, null, null, false);
        if (mRemoveLockMemberDialog != null) {
            mRemoveLockMemberDialog.dismiss();
            mRemoveLockMemberDialog = null;
        }
        syncLockMembersWithServer();

        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }
    //endregion IManageMembersFragment Callbacks

    //region Declare Methods
    private void syncLockMembersWithServer() {
        btnSyncManageMembersFragment.setClickable(false);

        mLockUserAdapter = new LockUserAdapter(this,
                Collections.singletonList(new MemberModel(NOT_DEFINED_INTEGER_NUMBER, "", LOCK_MEMBERS_SYNCING_MODE, "")));
        rcvManageMembersFragment.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvManageMembersFragment.setItemAnimator(new DefaultItemAnimator());
        rcvManageMembersFragment.setAdapter(mLockUserAdapter);

        this.mUserViewModel.getLockUsersByLockObjectId(mDevice.getObjectId());
    }

    private void handleDeleteLockMember(MemberModel member) {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            mRemoveLockMemberDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
            mRemoveLockMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRemoveLockMemberDialog.setContentView(R.layout.dialog_remove_member);

            CheckBox chbConfirmRemoveMemberDialogRemoveMember =
                    mRemoveLockMemberDialog.findViewById(R.id.chb_confirm_remove_member_dialog_remove_member);

            Button btnCancelDialogRemoveMember =
                    mRemoveLockMemberDialog.findViewById(R.id.btn_cancel_dialog_remove_member);
            Button btnRemoveDialogRemoveMember =
                    mRemoveLockMemberDialog.findViewById(R.id.btn_remove_dialog_remove_member);

            btnCancelDialogRemoveMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRemoveLockMemberDialog.dismiss();
                    mRemoveLockMemberDialog = null;
                }
            });

            btnRemoveDialogRemoveMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chbConfirmRemoveMemberDialogRemoveMember.isChecked()) {
                        handleProgressDialog(mFragment.getContext(), null, "Remove Member ...", true);
                        mUserViewModel.removeLockMember(member.getMemberUserLockObjectId());
                    }
                }
            });
        }

        mRemoveLockMemberDialog.show();
        mRemoveLockMemberDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(mRemoveLockMemberDialog));
    }
    //endregion Declare Methods
}
