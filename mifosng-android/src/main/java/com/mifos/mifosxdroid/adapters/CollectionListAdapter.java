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

import com.mifos.mifosxdroid.databinding.RowCollectionListGroupBinding;
import com.mifos.mifosxdroid.databinding.RowCollectionListGroupClientBinding;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MifosGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ishankhanna on 17/07/14.
 */
public class CollectionListAdapter extends BaseExpandableListAdapter {

    //Map for RepaymentTransaction<Loan Id, Transaction Amount>
    //TODO Check about SparseArray in Android and try to convert Map into SparseArray Implementation
    public static final Map<Integer, Double> sRepaymentTransactions = new HashMap<Integer,
            Double>();
    public static List<MifosGroup> sMifosGroups = new ArrayList<MifosGroup>();
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
        RowCollectionListGroupBinding binding;

        if (convertView == null) {
            binding = RowCollectionListGroupBinding.inflate(layoutInflater, parent, false);
            mifosGroupReusableViewHolder = new MifosGroupReusableViewHolder(binding);
            mifosGroupReusableViewHolder.view = binding.getRoot();
            mifosGroupReusableViewHolder.view.setTag(mifosGroupReusableViewHolder);
        } else {
            mifosGroupReusableViewHolder = (MifosGroupReusableViewHolder) convertView.getTag();
            mifosGroupReusableViewHolder.view = convertView;
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

        return mifosGroupReusableViewHolder.view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {

        ClientReusableViewHolder clientReusableViewHolder;
        RowCollectionListGroupClientBinding binding;

        if (convertView == null) {
            binding = RowCollectionListGroupClientBinding
                    .inflate(layoutInflater, parent, false);
            clientReusableViewHolder = new ClientReusableViewHolder(binding);
            clientReusableViewHolder.view = binding.getRoot();
            clientReusableViewHolder.view.setTag(clientReusableViewHolder);
        } else {
            clientReusableViewHolder = (ClientReusableViewHolder) convertView.getTag();
            clientReusableViewHolder.view = convertView;
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

        return clientReusableViewHolder.view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class MifosGroupReusableViewHolder {

        private View view;
        TextView tv_groupName;
        TextView tv_groupTotal;

        public MifosGroupReusableViewHolder(RowCollectionListGroupBinding binding) {

            tv_groupName = binding.tvGroupName;
            tv_groupTotal = binding.tvGroupTotal;
        }
    }

    public static class ClientReusableViewHolder {

        private View view;
        TextView tv_clientId;
        TextView tv_clientName;
        TextView tv_clientTotal;
        ListView lv_loans;

        public ClientReusableViewHolder(RowCollectionListGroupClientBinding binding) {

            tv_clientId = binding.tvClientId;
            tv_clientName = binding.tvClientName;
            tv_clientTotal = binding.tvClientTotal;
            lv_loans = binding.lvLoans;
        }

    }

}
