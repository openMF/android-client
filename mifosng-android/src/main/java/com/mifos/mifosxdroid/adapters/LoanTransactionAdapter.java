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
import android.widget.TextView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mifos.mifosxdroid.databinding.RowLoanTransactionItemBinding;
import com.mifos.mifosxdroid.databinding.RowLoanTransactionItemDetailBinding;
import com.mifos.objects.accounts.loan.Transaction;
import com.mifos.objects.accounts.loan.Type;
import com.mifos.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ishankhanna on 21/06/14.
 */
public class LoanTransactionAdapter extends BaseExpandableListAdapter {

    List<Transaction> transactionList;
    Context context;
    LayoutInflater layoutInflater;

    List<Parent> parents;
    List<Child> children;

    public LoanTransactionAdapter(Context context, List<Transaction> transactionList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.transactionList = transactionList;
        this.context = context;

        parents = new ArrayList<Parent>();
        children = new ArrayList<Child>();
        for (Transaction transaction : transactionList) {

            Parent parent = new Parent(transaction.getDate(), transaction.getType(), transaction
                    .getAmount());
            Child child = new Child(transaction.getId(), transaction.getOfficeName(), transaction
                    .getPrincipalPortion(),
                    transaction.getInterestPortion(), transaction.getFeeChargesPortion(),
                    transaction.getPenaltyChargesPortion());

            parents.add(parent);
            children.add(child);
        }

    }

    @Override
    public int getGroupCount() {
        return transactionList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Parent getGroup(int i) {
        return parents.get(i);
    }

    @Override
    public Child getChild(int i, int i2) {
        return children.get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {

        ReusableParentViewHolder reusableParentViewHolder;
        RowLoanTransactionItemBinding binding;
        if (view == null) {
            binding = RowLoanTransactionItemBinding.inflate(layoutInflater, viewGroup, false);
            reusableParentViewHolder = new ReusableParentViewHolder(binding);
            reusableParentViewHolder.view = binding.getRoot();
            reusableParentViewHolder.view.setTag(reusableParentViewHolder);
        } else {
            reusableParentViewHolder = (ReusableParentViewHolder) view.getTag();
            reusableParentViewHolder.view = view;
        }
        MaterialIcons contractedIconValue = MaterialIcons.md_add_circle_outline;
        MaterialIcons expandedIconValue = MaterialIcons.md_remove_circle_outline;
        if (!isExpanded) {
            reusableParentViewHolder.tv_arrow.setText(String.valueOf(contractedIconValue
                    .character()));
        } else {
            reusableParentViewHolder.tv_arrow.setText(String.valueOf(expandedIconValue.character
                    ()));
        }

        Iconify.addIcons(reusableParentViewHolder.tv_arrow);
        reusableParentViewHolder.tv_transactionDate.setText(DateHelper.getDateAsString(parents
                .get(i).getDate()));

//        Iconify.addIcons(reusableParentViewHolder.tv_arrow);
        reusableParentViewHolder.tv_transactionDate.setText(
                DateHelper.getDateAsString(parents.get(i).getDate()));
        reusableParentViewHolder.tv_transactionType.setText(
                parents.get(i).getType().getValue());
        reusableParentViewHolder.tv_transactionAmount.setText(
                String.valueOf(parents.get(i).getAmount()));

        return reusableParentViewHolder.view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {

        ReusableChildViewHolder reusableChildViewHolder;
        RowLoanTransactionItemDetailBinding binding;
        if (view == null) {
            binding = RowLoanTransactionItemDetailBinding.inflate(layoutInflater, viewGroup, false);
            reusableChildViewHolder = new ReusableChildViewHolder(binding);
            reusableChildViewHolder.view = binding.getRoot();
            reusableChildViewHolder.view.setTag(reusableChildViewHolder);
        } else {
            reusableChildViewHolder = (ReusableChildViewHolder) view.getTag();
            reusableChildViewHolder.view = view;
        }

        reusableChildViewHolder.tv_transactionId.setText(children.get(i).getId().toString());
        reusableChildViewHolder.tv_officeName.setText(children.get(i).getOfficeName());
        reusableChildViewHolder.tv_principal.setText(
                String.valueOf(children.get(i).getPrincipalPortion()));
        reusableChildViewHolder.tv_interest.setText(
                String.valueOf(children.get(i).getInterestPortion()));
        reusableChildViewHolder.tv_fees.setText(
                String.valueOf(children.get(i).getFeeChargesPortion()));
        reusableChildViewHolder.tv_penalties.setText(
                String.valueOf(children.get(i).getPenaltyChargesPortion()));

        return reusableChildViewHolder.view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public static class ReusableParentViewHolder {

        private View view;
        TextView tv_arrow;
        TextView tv_transactionDate;
        TextView tv_transactionType;
        TextView tv_transactionAmount;

        public ReusableParentViewHolder(RowLoanTransactionItemBinding binding) {
            tv_arrow = binding.tvArrow;
            tv_transactionDate = binding.tvTransactionDate;
            tv_transactionType = binding.tvTransactionType;
            tv_transactionAmount = binding.tvTransactionAmount;
        }

    }

    public static class ReusableChildViewHolder {

        private View view;
        TextView tv_transactionId;
        TextView tv_officeName;
        TextView tv_principal;
        TextView tv_interest;
        TextView tv_fees;
        TextView tv_penalties;

        public ReusableChildViewHolder(RowLoanTransactionItemDetailBinding binding) {

            tv_transactionId = binding.tvTransactionId;
            tv_officeName = binding.tvOfficeName;
            tv_principal = binding.tvPrincipal;
            tv_interest = binding.tvInterest;
            tv_fees = binding.tvFees;
            tv_penalties = binding.tvPenalties;
        }

    }

    public static class Parent {

        List<Integer> date;
        Type type;
        Double amount;

        public Parent(List<Integer> date, Type type, Double amount) {
            this.date = date;
            this.type = type;
            this.amount = amount;
        }

        public List<Integer> getDate() {
            return date;
        }

        public void setDate(List<Integer> date) {
            this.date = date;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "Parent{" +
                    "date=" + date +
                    ", type=" + type +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class Child {

        Integer id;
        String officeName;
        Double principalPortion;
        Double interestPortion;
        Double feeChargesPortion;
        Double penaltyChargesPortion;

        public Child(Integer id, String officeName, Double principalPortion,
                     Double interestPortion, Double feeChargesPortion, Double
                             penaltyChargesPortion) {
            this.id = id;
            this.officeName = officeName;
            this.principalPortion = principalPortion;
            this.interestPortion = interestPortion;
            this.feeChargesPortion = feeChargesPortion;
            this.penaltyChargesPortion = penaltyChargesPortion;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getOfficeName() {
            return officeName;
        }

        public void setOfficeName(String officeName) {
            this.officeName = officeName;
        }

        public Double getPrincipalPortion() {
            return principalPortion;
        }

        public void setPrincipalPortion(Double principalPortion) {
            this.principalPortion = principalPortion;
        }

        public Double getInterestPortion() {
            return interestPortion;
        }

        public void setInterestPortion(Double interestPortion) {
            this.interestPortion = interestPortion;
        }

        public Double getFeeChargesPortion() {
            return feeChargesPortion;
        }

        public void setFeeChargesPortion(Double feeChargesPortion) {
            this.feeChargesPortion = feeChargesPortion;
        }

        public Double getPenaltyChargesPortion() {
            return penaltyChargesPortion;
        }

        public void setPenaltyChargesPortion(Double penaltyChargesPortion) {
            this.penaltyChargesPortion = penaltyChargesPortion;
        }

        @Override
        public String toString() {
            return "Child{" +
                    "id=" + id +
                    ", officeName='" + officeName + '\'' +
                    ", principalPortion=" + principalPortion +
                    ", interestPortion=" + interestPortion +
                    ", feeChargesPortion=" + feeChargesPortion +
                    ", penaltyChargesPortion=" + penaltyChargesPortion +
                    '}';
        }
    }

}
