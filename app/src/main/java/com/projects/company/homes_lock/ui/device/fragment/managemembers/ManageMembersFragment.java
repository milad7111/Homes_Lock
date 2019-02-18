package com.projects.company.homes_lock.ui.device.fragment.managemembers;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
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
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.MemberModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.ManageMembersViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.projects.company.homes_lock.utils.helper.DataHelper.LOCK_MEMBERS_SYNCING_MODE;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_SECONDARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.NOT_DEFINED_INTEGER_NUMBER;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getRandomPercentNumber;
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
    private static final int CHECK_EXIST_USER_WITH_EMAIL = 1000;
    private static final int INSERT_LOCK_MEMBER = 2000;
    private static final int ADD_RELATED_DEVICE_RELATION = 3000;
    //endregion Declare Constants

    //region Declare Views
    private RecyclerView rcvManageMembersFragment;
    private FloatingActionButton fabSyncManageMembersFragment;
    private FloatingActionButton fabAddManageMembersFragment;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private Fragment mFragment;
    private DeviceViewModel mDeviceViewModel;
    private UserViewModel mUserViewModel;
    private Device mDevice;
    private User mUser;
    private UserLock mUserLock;
    private UserLockModel mUserLockModel;
    private LockUserAdapter mLockUserAdapter;
    private Dialog mRemoveLockMemberDialog;
    private Dialog mAddLockMemberDialog;
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

        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new ManageMembersViewModelFactory(getActivity().getApplication(), this))
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
        fabSyncManageMembersFragment = view.findViewById(R.id.fab_sync_manage_members_fragment);
        fabAddManageMembersFragment = view.findViewById(R.id.fab_add_manage_members_fragment);
        //endregion Initialize Views

        //region Setup Views
        fabSyncManageMembersFragment.setOnClickListener(this);
        fabAddManageMembersFragment.setOnClickListener(this);
        //endregion Setup Views

        //region init
        syncLockMembersWithServer();
        //endregion init
    }

    @Override
    public void onPause() {
        super.onPause();
        handleProgressDialog(null, null, null, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_sync_manage_members_fragment:
                syncLockMembersWithServer();
                break;
            case R.id.fab_add_manage_members_fragment:
                handleAddLockMemberDialog(CHECK_EXIST_USER_WITH_EMAIL);
                break;
        }
    }


    //endregion Main Callbacks

    //region IManageMembersFragment Callbacks
    @Override
    public void onGetUserLockDataSuccessful(List<User> response) {
        handleProgressDialog(null, null, null, false);

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

        if (getActivity() != null) {
            mLockUserAdapter = new LockUserAdapter(this, mMemberList);
            rcvManageMembersFragment.setAdapter(mLockUserAdapter);
        }

        fabSyncManageMembersFragment.setClickable(true);
    }

    @Override
    public void onGetUserLockDataFailed(Object response) {
        Log.i(this.getClass().getSimpleName(), ((FailureModel) response).getFailureMessage());
        handleProgressDialog(null, null, null, false);
        fabSyncManageMembersFragment.setClickable(true);
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

    @Override
    public void onGetUserListWithEmailAddressSuccessful(List<User> response) {
        if (response.size() != 0) {
            this.mUser = response.get(0);
            handleAddLockMemberDialog(INSERT_LOCK_MEMBER);
        } else
            onGetUserListWithEmailAddressFailed(new FailureModel("user does not exist"));
    }

    @Override
    public void onGetUserListWithEmailAddressFailed(FailureModel response) {
        Log.e(getClass().getName(), response.getFailureMessage());
    }

    @Override
    public void onInsertUserLockSuccessful(UserLock response) {
        this.mUserLock = response;
        handleAddLockMemberDialog(ADD_RELATED_DEVICE_RELATION);
    }

    @Override
    public void onInsertUserLockFailed(FailureModel response) {
    }

    @Override
    public void onAddLockToUserLockSuccessful(boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(4, 4)),
                    true);
            mDeviceViewModel.addUserLockToUser(mUser.getObjectId(), mUserLock.getObjectId());
        } else
            onAddLockToUserLockFailed(new ResponseBodyFailureModel("add lock to user lock failed."));
    }

    @Override
    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onAddUserLockToUserSuccessful(boolean addUserLockToUserSuccessful) {
        handleProgressDialog(null, null, null, false);

        if (addUserLockToUserSuccessful) {
            if (mAddLockMemberDialog != null) {
                mAddLockMemberDialog.dismiss();
                mAddLockMemberDialog = null;
            }

            syncLockMembersWithServer();
        } else
            onAddUserLockToUserFailed(new ResponseBodyFailureModel("add user lock to user failed."));
    }

    @Override
    public void onAddUserLockToUserFailed(ResponseBodyFailureModel response) {
        handleProgressDialog(null, null, null, false);
    }
    //endregion IManageMembersFragment Callbacks

    //region Declare Methods
    private void syncLockMembersWithServer() {
        DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Sync Lock members ...", true);

        fabSyncManageMembersFragment.setClickable(false);

        mLockUserAdapter = new LockUserAdapter(this,
                Collections.singletonList(new MemberModel(NOT_DEFINED_INTEGER_NUMBER, "", LOCK_MEMBERS_SYNCING_MODE, "")));
        rcvManageMembersFragment.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvManageMembersFragment.setItemAnimator(new DefaultItemAnimator());
        rcvManageMembersFragment.setAdapter(mLockUserAdapter);

        this.mUserViewModel.getLockUsersByLockObjectId(mDevice.getObjectId());
    }

    private void handleAddLockMemberDialog(int status) {
        TextInputEditText tietLockNameDialogAddMember;
        CheckBox chbUserAdminStatusDialogAddMember;
        CheckBox chbLockFavoriteStatusDialogAddMember;

        if (status == CHECK_EXIST_USER_WITH_EMAIL) {
            mAddLockMemberDialog = new Dialog(mFragment.getContext());
            mAddLockMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mAddLockMemberDialog.setContentView(R.layout.dialog_add_member);

            TextInputEditText tietEmailDialogAddMember =
                    mAddLockMemberDialog.findViewById(R.id.tiet_email_dialog_add_member);
            tietLockNameDialogAddMember =
                    mAddLockMemberDialog.findViewById(R.id.tiet_lock_name_dialog_add_member);

            chbUserAdminStatusDialogAddMember =
                    mAddLockMemberDialog.findViewById(R.id.chb_member_admin_status_dialog_add_member);
            chbLockFavoriteStatusDialogAddMember =
                    mAddLockMemberDialog.findViewById(R.id.chb_lock_favorite_status_dialog_add_member);

            Button btnCancelDialogAddMember =
                    mAddLockMemberDialog.findViewById(R.id.btn_cancel_dialog_add_member);
            Button btnAddDialogAddMember =
                    mAddLockMemberDialog.findViewById(R.id.btn_add_dialog_add_member);

            btnCancelDialogAddMember.setOnClickListener(v -> {
                mAddLockMemberDialog.dismiss();
                mAddLockMemberDialog = null;
            });

            btnAddDialogAddMember.setOnClickListener(v -> {
                DialogHelper.handleProgressDialog(
                        getContext(),
                        null,
                        String.format("Add Lock Member ... %d %%", getRandomPercentNumber(1, 4)),
                        true);

                this.mUserLockModel = new UserLockModel(
                        tietLockNameDialogAddMember.getText().toString(),
                        chbUserAdminStatusDialogAddMember.isChecked(),
                        chbLockFavoriteStatusDialogAddMember.isChecked());

                if (tietEmailDialogAddMember.getText().toString().equals(BaseApplication.activeUserEmail))
                    onGetUserListWithEmailAddressFailed(new FailureModel("email is same as your email"));
                else
                    mUserViewModel.getUserListWithEmailAddress(tietEmailDialogAddMember.getText().toString());
            });
        } else if (mAddLockMemberDialog != null && status == INSERT_LOCK_MEMBER) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Add Lock Member ... %d %%", getRandomPercentNumber(2, 4)),
                    true);
            mDeviceViewModel.insertOnlineUserLock(this, this.mUserLockModel);
        } else if (mAddLockMemberDialog != null && status == ADD_RELATED_DEVICE_RELATION) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Add Lock Member ... %d %%", getRandomPercentNumber(3, 4)),
                    true);
            mDeviceViewModel.addLockToUserLock(mUserLock.getObjectId(), mDevice.getObjectId());
        }

        if (!mAddLockMemberDialog.isShowing())
            mAddLockMemberDialog.show();

        mAddLockMemberDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(mAddLockMemberDialog));
    }

    private void handleDeleteLockMember(MemberModel member) {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            mRemoveLockMemberDialog = new Dialog(mFragment.getContext());
            mRemoveLockMemberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRemoveLockMemberDialog.setContentView(R.layout.dialog_remove_member);

            CheckBox chbConfirmRemoveMemberDialogRemoveMember =
                    mRemoveLockMemberDialog.findViewById(R.id.chb_confirm_remove_member_dialog_remove_member);

            Button btnCancelDialogRemoveMember =
                    mRemoveLockMemberDialog.findViewById(R.id.btn_cancel_dialog_remove_member);
            Button btnRemoveDialogRemoveMember =
                    mRemoveLockMemberDialog.findViewById(R.id.btn_remove_dialog_remove_member);

            btnCancelDialogRemoveMember.setOnClickListener(v -> {
                mRemoveLockMemberDialog.dismiss();
                mRemoveLockMemberDialog = null;
            });

            btnRemoveDialogRemoveMember.setOnClickListener(v -> {
                if (chbConfirmRemoveMemberDialogRemoveMember.isChecked()) {
                    handleProgressDialog(mFragment.getContext(), null, "Remove Member ...", true);
                    mUserViewModel.removeLockMember(member.getMemberUserLockObjectId());
                }
            });
        }

        mRemoveLockMemberDialog.show();
        mRemoveLockMemberDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(mRemoveLockMemberDialog));
    }
    //endregion Declare Methods
}
