package com.projects.company.homes_lock.ui.device.fragment.managemembers;

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
import com.projects.company.homes_lock.models.datamodels.MemberModel;

import java.util.List;

import static com.projects.company.homes_lock.utils.helper.DataHelper.LOCK_MEMBERS_SYNCING_MODE;

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
            if (mMemberModelList.get(0).getMemberAdminStatus() == LOCK_MEMBERS_SYNCING_MODE) {
                lockMembersAdapterViewHolder.imgMemberAvatar.setVisibility(View.GONE);
                lockMembersAdapterViewHolder.txvMemberName.setTypeface(null, Typeface.ITALIC);
                lockMembersAdapterViewHolder.txvMemberName.setText(mActivity.getString(R.string.adapter_empty_syncing));
            } else {
                MemberModel mMemberModel = mMemberModelList.get(i);

                lockMembersAdapterViewHolder.imgMemberAvatar.setImageResource(mMemberModel.getMemberAvatarDrawableId());
                lockMembersAdapterViewHolder.txvMemberName.setText(mMemberModel.getMemberName());

                if (mMemberModel.hasMemberAction()) {
                    lockMembersAdapterViewHolder.imgMemberAction.setVisibility(View.VISIBLE);
                    lockMembersAdapterViewHolder.imgMemberAction.setImageResource(mMemberModel.getMemberActionDrawableId());
                }

                if (mMemberModel.hasMemberType()) {
                    lockMembersAdapterViewHolder.imgMemberType.setVisibility(View.VISIBLE);
                    lockMembersAdapterViewHolder.imgMemberType.setImageResource(mMemberModel.getMemberTypeDrawableId());
                }

                lockMembersAdapterViewHolder.imgMemberAction.setOnClickListener(v -> mIManageMembersFragment.onActionUserClick(mMemberModelList.get(i)));

                lockMembersAdapterViewHolder.itemView.setOnClickListener(v -> {
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMemberModelList != null)
            return mMemberModelList.size();
        else return 0;
    }
    //endregion Adapter CallBacks

    //region Declare Classes & Interfaces
    class LockMembersAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMemberAvatar;
        TextView txvMemberName;
        ImageView imgMemberAction;
        ImageView imgMemberType;

        private LockMembersAdapterViewHolder(View itemView) {
            super(itemView);

            imgMemberAvatar = itemView.findViewById(R.id.img_member_avatar);
            txvMemberName = itemView.findViewById(R.id.txv_member_name);
            imgMemberAction = itemView.findViewById(R.id.img_member_action);
            imgMemberType = itemView.findViewById(R.id.img_member_type);
        }
    }
    //endregion Declare Classes & Interfaces
}
