package com.mifos.mifosxdroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Loan;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ClientListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Client> listClient;
    private String tag = getClass().getSimpleName();
    private Map<Loan, Integer> listPaidAmounts;
    private EditFocusChangeListener editFocusChangeListener;
    private Context context;

    public ClientListAdapter(Context context, List<Client> listClient) {

        layoutInflater = LayoutInflater.from(context);
        this.listClient = listClient;
        this.context = context;
        this.editFocusChangeListener = new EditFocusChangeListener();
    }

    public void setPaidAmount(Map<Loan, Integer> listPaidAmounts) {
        this.listPaidAmounts = listPaidAmounts;
    }

    @Override
    public int getCount() {
        return this.listClient.size();
    }

    @Override
    public Client getItem(int i) {
        return this.listClient.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public Map<Loan, Integer> getUpdatedDueList() {
        ((Activity) context).getCurrentFocus().clearFocus();
        return listPaidAmounts;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_client_list_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Client client = listClient.get(i);

        Loan loan = Select.from(Loan.class).where(Condition.prop("client").eq(client.getId())).first();

        if (loan != null && loan.productShortName.length() > 0) {

            viewHolder.tv_product_short_name.setText(loan.productShortName);
            viewHolder.tv_client_name.setText(client.getClientName());
            viewHolder.et_amt_paid.setText(String.valueOf(loan.totalDue));
            viewHolder.et_amt_paid.setTag(loan);
            viewHolder.et_amt_paid.setOnFocusChangeListener(editFocusChangeListener);
        }

        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.tv_clientName)
        TextView tv_client_name;
        @InjectView(R.id.tv_product_short_name)
        TextView tv_product_short_name;
        @InjectView(R.id.et_amt_paid)
        EditText et_amt_paid;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    private class EditFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                Loan loan =(Loan) (v.getTag());
                int changedValue = Integer.parseInt(((EditText) v).getText().toString());
                listPaidAmounts.put(loan, changedValue);
                notifyDataSetChanged();
            }
        }
    }
}