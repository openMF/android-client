/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.databinding.RowGroupListBinding;
import com.mifos.objects.client.Status;
import com.mifos.objects.group.Group;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by ishankhanna on 28/06/14.
 */
public class GroupListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    Context context;
    List<Group> groups = new ArrayList<Group>();

    public GroupListAdapter(Context context, List<Group> groups) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.groups = groups;
    }


    @Override
    public int getCount() {
        return this.groups.size();
    }

    @Override
    public Group getItem(int i) {
        return this.groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableGroupViewHolder reusableGroupViewHolder;
        RowGroupListBinding binding;

        if (view == null) {
            binding = RowGroupListBinding.inflate(layoutInflater , viewGroup,  false);
            reusableGroupViewHolder = new ReusableGroupViewHolder(binding);
            reusableGroupViewHolder.view = binding.getRoot();
            reusableGroupViewHolder.view.setTag(reusableGroupViewHolder);
        } else {
            reusableGroupViewHolder = (ReusableGroupViewHolder) view.getTag();
            reusableGroupViewHolder.view = view;
        }

        Group group = groups.get(i);

        reusableGroupViewHolder.tv_groupName.setText(group.getName());
        reusableGroupViewHolder.tv_officeName.setText(group.getOfficeName());

        /**
         * Passing the String value of Status to Helper Method of
         * Status Class that compares String Value to a Static String and returns
         * if Status is Active or not
         */
        if (Status.isActive(group.getStatus().getValue())) {
            reusableGroupViewHolder.view_statusIndicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.deposit_green));
            reusableGroupViewHolder.tv_statusText.setText(context.getResources().getString(R
                    .string.active));
        } else {
            reusableGroupViewHolder.view_statusIndicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_red));
            reusableGroupViewHolder.tv_statusText.setText(context.getResources().getString(R
                    .string.inactive));
        }

        return reusableGroupViewHolder.view;
    }

    public static class ReusableGroupViewHolder {

        private View view;
        TextView tv_groupName;
        TextView tv_officeName;
        View view_statusIndicator;
        TextView tv_statusText;

        public ReusableGroupViewHolder(RowGroupListBinding binding) {
            tv_groupName = binding.tvGroupName;
            tv_officeName = binding.tvOfficeName;
            view_statusIndicator = binding.viewStatusIndicator;
            tv_statusText = binding.tvStatusText;
        }


    }
}
