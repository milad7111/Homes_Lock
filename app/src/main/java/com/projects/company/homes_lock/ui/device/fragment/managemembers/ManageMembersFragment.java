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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.MemberModel;
import com.projects.company.homes_lock.models.datamodels.request.UserDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.ManageMembersViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.projects.company.homes_lock.base.BaseApplication.activeUserEmail;
import static com.projects.company.homes_lock.utils.helper.DataHelper.LOCK_MEMBERS_SYNCING_MODE;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_SECONDARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.NOT_DEFINED_INTEGER_NUMBER;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getRandomPercentNumber;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_EMAIL_FORMAT;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_EMPTY;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.VALIDATION_OK;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.validateDeviceNameForUser;
import static com.projects.company.homes_lock.utils.helper.ValidationHelper.validateUserEmail;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ManageMembersFragment extends BaseFragment
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

    //region Declare Arrays & Lists
    private List<String> mDeviceMembersEmail = new ArrayList<>();
    //endregion Declare Arrays & Lists

    //region Declare Objects
    private Fragment mFragment;
    private DeviceViewModel mDeviceViewModel;
    private UserViewModel mUserViewModel;
    private Device mDevice;
    private User mUser;
    private UserLock mUserLock;
    private UserDeviceModel mUserLockModel;
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
        closeProgressDialog();
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
        closeProgressDialog();

        mDeviceMembersEmail = new ArrayList<>();

        ArrayList<MemberModel> mMemberList = new ArrayList<>();
        boolean findPrimaryAdmin = false;

        for (User user : response) {
            mDeviceMembersEmail.add(user.getEmail());

            int adminStatus;

            int userLockIndex = -1;
            for (int i = 0; i < user.getRelatedUserLocks().size(); i++) {
                if (user.getRelatedUserLocks().get(i).getRelatedDevice().getObjectId().equals(mDevice.getObjectId())) {
                    userLockIndex = i;
                    break;
                }
            }
            if (userLockIndex != -1) {
                if (user.getRelatedUserLocks().get(userLockIndex).getAdminStatus()) {
                    if (findPrimaryAdmin)
                        adminStatus = user.getEmail().equals(activeUserEmail) ? MEMBER_STATUS_PRIMARY_ADMIN : MEMBER_STATUS_SECONDARY_ADMIN;
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
        }

        if (getActivity() != null) {
            mLockUserAdapter = new LockUserAdapter(this, mMemberList);
            rcvManageMembersFragment.setAdapter(mLockUserAdapter);
        }

        fabSyncManageMembersFragment.setClickable(true);
    }

    @Override
    public void onGetUserLockDataFailed(Object response) {
        Timber.i(((FailureModel) response).getFailureMessage());
        closeProgressDialog();
        fabSyncManageMembersFragment.setClickable(true);
    }

    @Override
    public void onActionUserClick(MemberModel member) {
        handleDeleteLockMember(member);
    }

    @Override
    public void onRemoveMemberSuccessful(Long deletionTime) {
        closeProgressDialog();
        if (mRemoveLockMemberDialog != null) {
            mRemoveLockMemberDialog.dismiss();
            mRemoveLockMemberDialog = null;
        }
        syncLockMembersWithServer();
    }

    @Override
    public void onRemoveMemberFailed(ResponseBodyFailureModel response) {
        closeProgressDialog();

        if (mRemoveLockMemberDialog != null) {
            mRemoveLockMemberDialog.dismiss();
            mRemoveLockMemberDialog = null;
        }
        syncLockMembersWithServer();

        Timber.i(response.getFailureMessage());
    }

    @Override
    public void onGetUserListWithEmailAddressSuccessful(List<User> response) {
        if (response.size() != 0) {
            this.mUser = response.get(0);
            handleAddLockMemberDialog(INSERT_LOCK_MEMBER);
        }
    }

    @Override
    public void onGetUserListWithEmailAddressFailed(FailureModel response) {
        Timber.e(response.getFailureMessage());

        closeProgressDialog();
        if (mAddLockMemberDialog != null && mAddLockMemberDialog.isShowing()) {
            ((TextInputEditText) mAddLockMemberDialog.findViewById(R.id.tiet_email_dialog_add_member)).setError(response.getFailureMessage());
            mAddLockMemberDialog.findViewById(R.id.tiet_email_dialog_add_member).requestFocus();
        }
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
            openProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(4, 4)));
            mDeviceViewModel.addUserLockToUser(this, mUser.getObjectId(), mUserLock.getObjectId());
        } else
            onAddLockToUserLockFailed(new ResponseBodyFailureModel("add lock to user lock failed."));
    }

    @Override
    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onAddUserLockToUserSuccessful(boolean addUserLockToUserSuccessful) {
        closeProgressDialog();

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
        closeProgressDialog();
    }
    //endregion IManageMembersFragment Callbacks

    //region Declare Methods
    private void syncLockMembersWithServer() {
        openProgressDialog(mFragment.getContext(), null, "Sync Lock members ...");

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
            mAddLockMemberDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
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
                if (tietEmailDialogAddMember.getText().toString().equals(activeUserEmail))
                    onGetUserListWithEmailAddressFailed(new FailureModel("Email is same as yours"));
                else if (mDeviceMembersEmail.contains(tietEmailDialogAddMember.getText().toString()))
                    onGetUserListWithEmailAddressFailed(new FailureModel("Already is member of device"));
                else
                    switch (validateUserEmail(tietEmailDialogAddMember.getText().toString())) {
                        case VALIDATION_EMPTY:
                            onGetUserListWithEmailAddressFailed(new FailureModel("Must not be empty"));
                            break;
                        case VALIDATION_EMAIL_FORMAT:
                            onGetUserListWithEmailAddressFailed(new FailureModel("Wrong email format"));
                            break;
                        case VALIDATION_OK:
                            switch (validateDeviceNameForUser(tietLockNameDialogAddMember.getText().toString())) {
                                case VALIDATION_EMPTY:
                                    closeProgressDialog();
                                    tietLockNameDialogAddMember.setError("Must not be empty");
                                    tietLockNameDialogAddMember.requestFocus();
                                    break;
                                case VALIDATION_OK:
                                    openProgressDialog(
                                            getContext(),
                                            null,
                                            String.format("Add Lock Member ... %d %%", getRandomPercentNumber(1, 4)));

                                    this.mUserLockModel = new UserDeviceModel(
                                            tietLockNameDialogAddMember.getText().toString(),
                                            chbUserAdminStatusDialogAddMember.isChecked(),
                                            chbLockFavoriteStatusDialogAddMember.isChecked());
                                    mUserViewModel.getUserListWithEmailAddress(tietEmailDialogAddMember.getText().toString());
                                    break;
                            }
                            break;
                    }
            });
        } else if (mAddLockMemberDialog != null && status == INSERT_LOCK_MEMBER) {
            openProgressDialog(
                    getContext(),
                    null,
                    String.format("Add Lock Member ... %d %%", getRandomPercentNumber(2, 4)));
            mDeviceViewModel.insertOnlineUserDevice(this, this.mUserLockModel);
        } else if (mAddLockMemberDialog != null && status == ADD_RELATED_DEVICE_RELATION) {
            openProgressDialog(
                    getContext(),
                    null,
                    String.format("Add Lock Member ... %d %%", getRandomPercentNumber(3, 4)));
            mDeviceViewModel.addLockToUserLock(this, mUserLock.getObjectId(), mDevice.getObjectId());
        }

        if (mAddLockMemberDialog != null && !mAddLockMemberDialog.isShowing()) {
            mAddLockMemberDialog.show();
            Objects.requireNonNull(mAddLockMemberDialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(mAddLockMemberDialog));
        }
    }

    private void handleDeleteLockMember(MemberModel member) {
        if (mDevice.getUserAdminStatus() != MEMBER_STATUS_NOT_ADMIN) {
            mRemoveLockMemberDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
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
                    openProgressDialog(mFragment.getContext(), null, "Remove Member ...");
                    mUserViewModel.removeLockMember(member.getMemberUserLockObjectId());
                }
            });
        }

        if (mRemoveLockMemberDialog != null) {
            mRemoveLockMemberDialog.show();
            Objects.requireNonNull(mRemoveLockMemberDialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(mRemoveLockMemberDialog));
        }
    }
    //endregion Declare Methods
}
