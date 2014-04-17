package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.LoanAccount;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ishankhanna on 01/03/14.
 */
public class AccountsListAdapter extends BaseAdapter {

    List<LoanAccount> loanAccountList;
    LayoutInflater layoutInflater;

    public AccountsListAdapter(Context context, List<LoanAccount> loanAccountList){

        layoutInflater = LayoutInflater.from(context);
        this.loanAccountList = loanAccountList;

    }



    @Override
    public int getCount() {
        return loanAccountList.size();
    }

    @Override
    public LoanAccount getItem(int i) {
        return loanAccountList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableViewHolder reusableViewHolder;

        if(view==null)
        {
            view = layoutInflater.inflate(R.layout.row_account_item,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);
        }else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        reusableViewHolder.tv_loanAccountName.setText(loanAccountList.get(i).getProductName());
        reusableViewHolder.tv_loanAccountNumber.setText(loanAccountList.get(i).getAccountNo());

        return view;
    }

    public static class ReusableViewHolder{

        @InjectView(R.id.tv_loanAccountName) TextView tv_loanAccountName;
        @InjectView(R.id.tv_loanAccountNumber) TextView tv_loanAccountNumber;

        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
