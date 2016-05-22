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
import com.mifos.objects.client.Charges;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 03/07/14.
 */
public class ChargeNameListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<Charges> pageItems;
    int clientId;

    public ChargeNameListAdapter(Context context, List<Charges> pageItems, int clientId) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        this.clientId = clientId;
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Charges getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableChargeViewHolder reusableChargeViewHolder;
        if (view == null) {

            view = layoutInflater.inflate(R.layout.row_charge_name, null);
            reusableChargeViewHolder = new ReusableChargeViewHolder(view);
            view.setTag(reusableChargeViewHolder);

        } else {
            reusableChargeViewHolder = (ReusableChargeViewHolder) view.getTag();
        }
        reusableChargeViewHolder.tv_charger_id.setText(pageItems.get(i).getChargeId().toString());
        reusableChargeViewHolder.tv_charge_Name.setText(pageItems.get(i).getName());
        reusableChargeViewHolder.tv_charge_amount.setText(pageItems.get(i).getAmount().toString());
        reusableChargeViewHolder.tv_charge_duedate.setText(pageItems.get(i).getDueDate().toString());

        return view;


    }

    public static class ReusableChargeViewHolder {

        @InjectView(R.id.tv_charger_id)
        TextView tv_charger_id;
        @InjectView(R.id.tv_chargeName)
        TextView tv_charge_Name;
        @InjectView(R.id.tv_charge_amount)
        TextView tv_charge_amount;
        @InjectView(R.id.tv_charge_duedate)
        TextView tv_charge_duedate;

        public ReusableChargeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }


    }
}
