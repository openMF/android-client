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

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.Loan;

import java.util.List;


public class LoanListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Loan> listLoans;

    public LoanListAdapter(Context context, List<Loan> listLoans){

        layoutInflater = LayoutInflater.from(context);
        this.listLoans = listLoans;
    }

    @Override
    public int getCount() {
        return this.listLoans.size();
    }

    @Override
    public Loan getItem(int i) {
        return this.listLoans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if(view==null){
            view = layoutInflater.inflate(R.layout.row_loan_list_item,viewGroup,false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Loan loan = listLoans.get(i);
        viewHolder.tv_loan_id = (TextView) view.findViewById(R.id.tv_loan_id);
        viewHolder.tv_loan_account_id = (TextView) view.findViewById(R.id.tv_loan_account_id);
        viewHolder.tv_loan_account_status_id = (TextView) view.findViewById(R.id.tv_loan_account_status_id);
        viewHolder.tv_loan_product_short_name = (TextView) view.findViewById(R.id.tv_loan_product_short_name);
        viewHolder.tv_loan_disbursement_amount = (TextView) view.findViewById(R.id.tv_loan_disbursement_amount);
        viewHolder.tv_loan_principal_due = (TextView) view.findViewById(R.id.tv_loan_principal_due);
        viewHolder.tv_loan_principal_paid = (TextView) view.findViewById(R.id.tv_loan_principal_paid);
        viewHolder.tv_loan_interest_due = (TextView) view.findViewById(R.id.tv_loan_interest_due);
        viewHolder.tv_loan_interest_paid = (TextView) view.findViewById(R.id.tv_loan_interest_paid);
        viewHolder.tv_loan_charges_due = (TextView) view.findViewById(R.id.tv_loan_charges_due);
        viewHolder.tv_loan_total_due = (TextView) view.findViewById(R.id.tv_amount_disbursed);

        viewHolder.tv_loan_id.setText(String.valueOf(loan.getLoanId()));

        return view;
    }

    public static class ViewHolder{

        TextView tv_loan_id;
        TextView tv_loan_account_id;
        TextView tv_loan_account_status_id;
        TextView tv_loan_product_short_name;
        TextView tv_loan_disbursement_amount;
        TextView tv_loan_principal_due;
        TextView tv_loan_principal_paid;
        TextView tv_loan_interest_due;
        TextView tv_loan_interest_paid;
        TextView tv_loan_charges_due;
        TextView tv_loan_total_due;

    }
}
