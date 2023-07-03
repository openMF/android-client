/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.objects.db.Client
import com.mifos.objects.db.MifosGroup

/**
 * Created by ishankhanna on 17/07/14.
 */
class CollectionListAdapter(var context: Context, mifosGroups: List<MifosGroup>) :
    BaseExpandableListAdapter() {
    var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    init {
        sMifosGroups = mifosGroups
        for (mifosGroup in sMifosGroups) {
            for (client in mifosGroup.clients) {
                for (loan in client.loans) {
                    sRepaymentTransactions[loan.getLoanId()] = loan.getTotalDue()
                }
            }
        }
    }

    override fun getGroupCount(): Int {
        return sMifosGroups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return sMifosGroups[groupPosition].clients.size
    }

    override fun getGroup(groupPosition: Int): MifosGroup {
        return sMifosGroups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Client {
        return sMifosGroups[groupPosition].clients[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val mifosGroupReusableViewHolder: MifosGroupReusableViewHolder
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group, null)
            mifosGroupReusableViewHolder = MifosGroupReusableViewHolder(convertView)
            convertView.tag = mifosGroupReusableViewHolder
        } else {
            mifosGroupReusableViewHolder = convertView.tag as MifosGroupReusableViewHolder
        }
        var groupTotalDue = 0.0
        for (client in sMifosGroups[groupPosition].clients) {
            for (loan in client.loans) {
                groupTotalDue += sRepaymentTransactions[loan.getLoanId()]!!
            }
        }
        mifosGroupReusableViewHolder.tv_groupName!!.text = sMifosGroups[groupPosition]
            .groupName
        mifosGroupReusableViewHolder.tv_groupTotal!!.text = groupTotalDue.toString()
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val clientReusableViewHolder: ClientReusableViewHolder
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_list_group_client, null)
            clientReusableViewHolder = ClientReusableViewHolder(convertView)
            convertView.tag = clientReusableViewHolder
        } else {
            clientReusableViewHolder = convertView.tag as ClientReusableViewHolder
        }
        val client = sMifosGroups[groupPosition].clients[childPosition]
        var totalDue = 0.0
        val loans = client.loans
        for (loan in loans) {
            totalDue += loan.getTotalDue()
        }
        clientReusableViewHolder.tv_clientId!!.text = client.clientId.toString()
        clientReusableViewHolder.tv_clientName!!.text = client.clientName
        clientReusableViewHolder.tv_clientTotal!!.text = totalDue.toString()
        val collectionSheetLoanAccountListAdapter = CollectionSheetLoanAccountListAdapter(
            context, loans, groupPosition,
            childPosition
        )
        clientReusableViewHolder.lv_loans!!.adapter = collectionSheetLoanAccountListAdapter
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    class MifosGroupReusableViewHolder(view: View?) {
        @JvmField
        @BindView(R.id.tv_groupName)
        var tv_groupName: TextView? = null

        @JvmField
        @BindView(R.id.tv_groupTotal)
        var tv_groupTotal: TextView? = null

        init {
            ButterKnife.bind(this, view!!)
        }
    }

    class ClientReusableViewHolder(view: View?) {
        @JvmField
        @BindView(R.id.tv_clientId)
        var tv_clientId: TextView? = null

        @JvmField
        @BindView(R.id.tv_clientName)
        var tv_clientName: TextView? = null

        @JvmField
        @BindView(R.id.tv_clientTotal)
        var tv_clientTotal: TextView? = null

        @JvmField
        @BindView(R.id.lv_loans)
        var lv_loans: ListView? = null

        init {
            ButterKnife.bind(this, view!!)
        }
    }

    companion object {
        //Map for RepaymentTransaction<Loan Id, Transaction Amount>
        //TODO Check about SparseArray in Android and try to convert Map into SparseArray Implementation
        val sRepaymentTransactions: MutableMap<Int, Double> = HashMap()
        var sMifosGroups: List<MifosGroup> = ArrayList()
    }
}