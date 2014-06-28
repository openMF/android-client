package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.Group;
import com.mifos.objects.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 28/06/14.
 */
public class GroupListAdapter extends BaseAdapter{

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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableGroupViewHolder reusableGroupViewHolder;

        if(view == null) {
            view = layoutInflater.inflate(R.layout.row_group_list, null);
            reusableGroupViewHolder = new ReusableGroupViewHolder(view);
            view.setTag(reusableGroupViewHolder);
        } else {
            reusableGroupViewHolder = (ReusableGroupViewHolder) view.getTag();
        }

        Group group = groups.get(i);

        reusableGroupViewHolder.tv_groupName.setText(group.getName());
        reusableGroupViewHolder.tv_officeName.setText(group.getOfficeName());

        /**
         * Passing the String value of Status to Helper Method of
         * Status Class that compares String Value to a Static String and returns
         * if Status is Active or not
         */
        if(Status.isActive(group.getStatus().getValue())) {
            reusableGroupViewHolder.view_statusIndicator.setBackgroundColor(context.getResources().getColor(R.color.deposit_green));
            reusableGroupViewHolder.tv_statusText.setText(context.getResources().getString(R.string.active));
        } else {
            reusableGroupViewHolder.view_statusIndicator.setBackgroundColor(context.getResources().getColor(R.color.light_red));
            reusableGroupViewHolder.tv_statusText.setText(context.getResources().getString(R.string.inactive));
        }

        return view;
    }

    public static class ReusableGroupViewHolder {

        @InjectView(R.id.tv_group_name) TextView tv_groupName;
        @InjectView(R.id.tv_office_name) TextView tv_officeName;
        @InjectView(R.id.view_status_indicator) View view_statusIndicator;
        @InjectView(R.id.tv_status_text) TextView tv_statusText;

        public ReusableGroupViewHolder(View view) { ButterKnife.inject(this, view); }


    }
}
