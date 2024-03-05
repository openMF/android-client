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
import com.mifos.core.objects.db.Client
import com.mifos.core.objects.db.MifosGroup
import com.mifos.mifosxdroid.databinding.RowCollectionListGroupBinding
import com.mifos.mifosxdroid.databinding.RowCollectionListGroupClientBinding

/**
 * Created by ishankhanna on 17/07/14.
 */
class CollectionListAdapter(
    private val context: Context,
    private val mifosGroups: List<MifosGroup>
) :
    BaseExpandableListAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    init {
        for (mifosGroup in mifosGroups) {
            for (client in mifosGroup.clients) {
                for (loan in client.loans) {
                    sRepaymentTransactions[loan.loanId] = loan.totalDue
                }
            }
        }
    }

    override fun getGroupCount(): Int {
        return mifosGroups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mifosGroups[groupPosition].clients.size
    }

    override fun getGroup(groupPosition: Int): MifosGroup {
        return mifosGroups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Client {
        return mifosGroups[groupPosition].clients[childPosition]
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
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding: RowCollectionListGroupBinding = if (convertView == null) {
            RowCollectionListGroupBinding.inflate(layoutInflater, parent, false)
        } else {
            RowCollectionListGroupBinding.bind(convertView)
        }

        val groupTotalDue = calculateGroupTotalDue(groupPosition)
        binding.tvGroupName.text = mifosGroups[groupPosition].groupName
        binding.tvGroupTotal.text = groupTotalDue.toString()

        return binding.root
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding: RowCollectionListGroupClientBinding = if (convertView == null) {
            RowCollectionListGroupClientBinding.inflate(layoutInflater, parent, false)
        } else {
            RowCollectionListGroupClientBinding.bind(convertView)
        }

        val client = mifosGroups[groupPosition].clients.get(childPosition)
        val totalDue = client.let { calculateClientTotalDue(it) }
        binding.tvClientId.text = client.clientId.toString()
        binding.tvClientName.text = client.clientName
        binding.tvClientTotal.text = totalDue.toString()

        val collectionSheetLoanAccountListAdapter = client.loans?.let {
            CollectionSheetLoanAccountListAdapter(
                context, it, groupPosition, childPosition
            )
        }
        binding.lvLoans.adapter = collectionSheetLoanAccountListAdapter

        return binding.root
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private fun calculateGroupTotalDue(groupPosition: Int): Double {
        var groupTotalDue = 0.0
        for (client in mifosGroups[groupPosition].clients) {
            for (loan in client.loans) {
                groupTotalDue += sRepaymentTransactions[loan.loanId] ?: 0.0
            }
        }
        return groupTotalDue
    }

    private fun calculateClientTotalDue(client: Client): Double {
        var totalDue = 0.0
        for (loan in client.loans) {
            totalDue += loan.totalDue
        }
        return totalDue
    }

    companion object {
        // Map for RepaymentTransaction<Loan Id, Transaction Amount>
        // TODO Check about SparseArray in Android and try to convert Map into SparseArray Implementation
        val sRepaymentTransactions: MutableMap<Int, Double> = HashMap()
    }
}