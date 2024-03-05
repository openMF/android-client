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
import com.joanzapata.iconify.fonts.MaterialIcons
import com.mifos.core.objects.accounts.loan.Transaction
import com.mifos.core.objects.accounts.loan.Type
import com.mifos.mifosxdroid.databinding.RowLoanTransactionItemBinding
import com.mifos.mifosxdroid.databinding.RowLoanTransactionItemDetailBinding
import com.mifos.utils.DateHelper.getDateAsString

/**
 * Created by ishankhanna on 21/06/14.
 */
class LoanTransactionAdapter(
    private val context: Context,
    private val transactionList: List<Transaction>
) : BaseExpandableListAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val parents: MutableList<Parent> = ArrayList()
    private val children: MutableList<Child> = ArrayList()

    init {
        for (transaction in transactionList) {
            val parent = transaction.type?.let {
                transaction.amount?.let { it1 ->
                    Parent(
                        transaction.date,
                        it,
                        it1
                    )
                }
            }
            val child = transaction.id?.let {
                transaction.officeName?.let { it1 ->
                    transaction.principalPortion?.let { it2 ->
                        transaction.interestPortion?.let { it3 ->
                            transaction.feeChargesPortion?.let { it4 ->
                                transaction.penaltyChargesPortion?.let { it5 ->
                                    Child(
                                        it,
                                        it1,
                                        it2,
                                        it3,
                                        it4,
                                        it5
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (parent != null) {
                parents.add(parent)
            }
            if (child != null) {
                children.add(child)
            }
        }
    }

    override fun getGroupCount(): Int {
        return transactionList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroup(groupPosition: Int): Parent {
        return parents[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Child {
        return children[childPosition]
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
        val binding: RowLoanTransactionItemBinding
        val view: View

        if (convertView == null) {
            binding = RowLoanTransactionItemBinding.inflate(layoutInflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as RowLoanTransactionItemBinding
            view = convertView
        }

        val contractedIconValue = MaterialIcons.md_add_circle_outline
        val expandedIconValue = MaterialIcons.md_remove_circle_outline

        if (!isExpanded) {
            binding.tvArrow.text = contractedIconValue.character().toString()
        } else {
            binding.tvArrow.text = expandedIconValue.character().toString()
        }

        binding.tvTransactionDate.text = getDateAsString(parents[groupPosition].date)
        binding.tvTransactionType.text = parents[groupPosition].type.value
        binding.tvTransactionAmount.text = parents[groupPosition].amount.toString()

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding: RowLoanTransactionItemDetailBinding
        val view: View

        if (convertView == null) {
            binding = RowLoanTransactionItemDetailBinding.inflate(layoutInflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as RowLoanTransactionItemDetailBinding
            view = convertView
        }

        binding.tvTransactionId.text = children[groupPosition].id.toString()
        binding.tvOfficeName.text = children[groupPosition].officeName
        binding.tvPrincipal.text = children[groupPosition].principalPortion.toString()
        binding.tvInterest.text = children[groupPosition].interestPortion.toString()
        binding.tvFees.text = children[groupPosition].feeChargesPortion.toString()
        binding.tvPenalties.text = children[groupPosition].penaltyChargesPortion.toString()

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    data class Parent(
        val date: List<Int>,
        val type: Type,
        val amount: Double
    )

    data class Child(
        val id: Int,
        val officeName: String,
        val principalPortion: Double,
        val interestPortion: Double,
        val feeChargesPortion: Double,
        val penaltyChargesPortion: Double
    )
}