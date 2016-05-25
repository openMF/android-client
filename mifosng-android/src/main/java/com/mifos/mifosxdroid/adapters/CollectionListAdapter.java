/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MifosGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 17/07/14.
 */
public class CollectionListAdapter extends BaseExpandableListAdapter {

    public static List<MifosGroup> sMifosGroups = new ArrayList<MifosGroup>();
    //Map for RepaymentTransaction<Loan Id, Transaction Amount>
    //TODO Check about SparseArray in Android and try to convert Map into SparseArray Implementation
    public static Map<Integer, Double> sRepaymentTransactions = new HashMap<Integer, Double>();
    Context context;
    LayoutInflater layoutInflater;

    public CollectionListAdapter(Context context, List<MifosGroup> mifosGroups) {
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
        sMifosGroups = mifosGroups;

        for (MifosGroup mifosGroup : sMifosGroups) {
            for (Client client : mifosGroup.getClients()) {
                for (Loan loan : client.getLoans()) {
                    sRepaymentTransactions.put(loan.getLoanId(), loan.getTotalDue());
                }

            }
        }

    }


    @Override
    public int getGroupCount() {
        return sMifosGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sMifosGroups.get(groupPosition).getClients().size();
    }

    @Override
    public MifosGroup getGroup(int groupPosition) {
        return sMifosGroups.get(groupPosition);
    }

    @Override
    public Client getChild(int groupPosition, int childPosition) {
        return sMifosGroups.get(groupPosition).getClients().get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {

        MifosGroupReusableViewHolder mifosGroupReusableViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group, null);
            mifosGroupReusableViewHolder = new MifosGroupReusableViewHolder(convertView);
            convertView.setTag(mifosGroupReusableViewHolder);
        } else {
            mifosGroupReusableViewHolder = (MifosGroupReusableViewHolder) convertView.getTag();
        }

        double groupTotalDue = 0;

        for (Client client : sMifosGroups.get(groupPosition).getClients()) {
            for (Loan loan : client.getLoans()) {
                groupTotalDue += sRepaymentTransactions.get(loan.getLoanId());
            }

        }

        mifosGroupReusableViewHolder.tv_groupName.setText(sMifosGroups.get(groupPosition)
                .getGroupName());
        mifosGroupReusableViewHolder.tv_groupTotal.setText(String.valueOf(groupTotalDue));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {

        ClientReusableViewHolder clientReusableViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group_client, null);
            clientReusableViewHolder = new ClientReusableViewHolder(convertView);
            convertView.setTag(clientReusableViewHolder);
        } else {
            clientReusableViewHolder = (ClientReusableViewHolder) convertView.getTag();
        }

        Client client = sMifosGroups.get(groupPosition).getClients().get(childPosition);
        double totalDue = 0;
        List<Loan> loans = client.getLoans();

        for (Loan loan : loans) {
            totalDue += loan.getTotalDue();
        }

        clientReusableViewHolder.tv_clientId.setText(String.valueOf(client.getClientId()));
        clientReusableViewHolder.tv_clientName.setText(client.getClientName());
        clientReusableViewHolder.tv_clientTotal.setText(String.valueOf(totalDue));

        CollectionSheetLoanAccountListAdapter collectionSheetLoanAccountListAdapter
                = new CollectionSheetLoanAccountListAdapter(context, loans, groupPosition,
                childPosition);
        clientReusableViewHolder.lv_loans.setAdapter(collectionSheetLoanAccountListAdapter);

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
        @InjectView(R.id.tv_clientTotal)
        TextView tv_clientTotal;
        @InjectView(R.id.lv_loans)
        ListView lv_loans;

        public ClientReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}
