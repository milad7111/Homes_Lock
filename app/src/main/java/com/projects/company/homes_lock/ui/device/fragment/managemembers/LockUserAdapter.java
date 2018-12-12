package com.projects.company.homes_lock.ui.device.fragment.managemembers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.MemberModel;
import com.projects.company.homes_lock.ui.device.fragment.setting.ISettingFragment;

import java.util.List;

public class LockUserAdapter extends RecyclerView.Adapter<LockUserAdapter.LockMembersAdapterViewHolder> {

    //region Declare Objects
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<MemberModel> mMemberModelList;
    private IManageMembersFragment mIManageMembersFragment;
    //endregion Declare Objects

    //region Constructor
    public LockUserAdapter(Fragment fragment, List<MemberModel> mMemberModelList) {
        //region Initialize Objects
        this.mActivity = fragment.getActivity();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
        this.mMemberModelList = mMemberModelList;
        this.mIManageMembersFragment = (IManageMembersFragment) fragment;
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Adapter CallBacks
    @Override
    public LockMembersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new LockMembersAdapterViewHolder(mInflater.inflate(R.layout.item_member_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LockMembersAdapterViewHolder lockMembersAdapterViewHolder, final int i) {
        if (mMemberModelList != null) {
            MemberModel mMemberModel = mMemberModelList.get(i);

            lockMembersAdapterViewHolder.imgMemberAvatar.setImageDrawable(mMemberModel.getMemberAvatar());
            lockMembersAdapterViewHolder.txvMemberName.setText(mMemberModel.getMemberName());
            lockMembersAdapterViewHolder.imgMemberAction.setImageDrawable(mMemberModel.getMemberActionIcon());
        }

        lockMembersAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIManageMembersFragment.onAdapterItemClick(mMemberModelList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMemberModelList != null)
            return mMemberModelList.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Methods
    public void setMembers(List<MemberModel> mMemberModelList) {
        this.mMemberModelList = mMemberModelList;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    class LockMembersAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMemberAvatar;
        TextView txvMemberName;
        ImageView imgMemberAction;

        private LockMembersAdapterViewHolder(View itemView) {
            super(itemView);

            imgMemberAvatar = itemView.findViewById(R.id.img_member_avatar);
            txvMemberName = itemView.findViewById(R.id.txv_member_name);
            imgMemberAction = itemView.findViewById(R.id.img_member_action);
        }
    }
    //endregion Declare Methods
}
