package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MifosGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 17/07/14.
 */
public class CollectionListAdapter extends BaseExpandableListAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<MifosGroup> mifosGroups = new ArrayList<MifosGroup>();



    public CollectionListAdapter(Context context, List<MifosGroup> mifosGroups) {
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
        this.mifosGroups = mifosGroups;
    }


    @Override
    public int getGroupCount() {
        return mifosGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mifosGroups.get(groupPosition).getClients().size();
    }

    @Override
    public MifosGroup getGroup(int groupPosition) {
        return mifosGroups.get(groupPosition);
    }

    @Override
    public Client getChild(int groupPosition, int childPosition) {
        return mifosGroups.get(groupPosition).getClients().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        MifosGroupReusableViewHolder mifosGroupReusableViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group, null);
            mifosGroupReusableViewHolder = new MifosGroupReusableViewHolder(convertView);
            convertView.setTag(mifosGroupReusableViewHolder);
        } else {
            mifosGroupReusableViewHolder = (MifosGroupReusableViewHolder) convertView.getTag();
        }

        mifosGroupReusableViewHolder.tv_groupName.setText(mifosGroups.get(groupPosition).getGroupName());
        mifosGroupReusableViewHolder.tv_groupTotal.setText("-");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ClientReusableViewHolder clientReusableViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group_client, null);
            clientReusableViewHolder = new ClientReusableViewHolder(convertView);
            convertView.setTag(clientReusableViewHolder);
        } else {
            clientReusableViewHolder = (ClientReusableViewHolder) convertView.getTag();
        }

        Client client = mifosGroups.get(groupPosition).getClients().get(childPosition);
        double totalDue = 0;
        List<Loan> loans = client.getLoans();

        for (Loan loan : loans) {
            totalDue += loan.getTotalDue();
        }

        //clientReusableViewHolder.tv_clientId.setText(client.getClientId());
        clientReusableViewHolder.tv_clientName.setText(client.getClientName());
        clientReusableViewHolder.et_amount.setText(String.valueOf(totalDue));


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class MifosGroupReusableViewHolder {

        @InjectView(R.id.tv_groupName)
        TextView tv_groupName;
        @InjectView(R.id.tv_groupTotal)
        TextView tv_groupTotal;

        public MifosGroupReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public static class ClientReusableViewHolder {

        @InjectView(R.id.tv_clientId)
        TextView tv_clientId;
        @InjectView(R.id.tv_clientName)
        TextView tv_clientName;
        @InjectView(R.id.et_amount)
        EditText et_amount;
        @InjectView(R.id.sp_attendance)
        Spinner sp_attendance;

        public ClientReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}
