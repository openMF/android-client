/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Charges;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 03/07/14.
 */
public class ChargeNameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

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

    public Charges getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_charge_name, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).tv_charger_id
                    .setText(pageItems.get(position).getChargeId().toString());
            ((ViewHolder) holder).tv_charge_Name.setText(pageItems.get(position).getName());
            ((ViewHolder) holder).tv_charge_amount
                    .setText(pageItems.get(position).getAmount().toString());
            ((ViewHolder) holder).tv_charge_duedate
                    .setText(pageItems.get(position).getDueDate().toString());
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return pageItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_charger_id)
        TextView tv_charger_id;

        @BindView(R.id.tv_chargeName)
        TextView tv_charge_Name;

        @BindView(R.id.tv_charge_amount)
        TextView tv_charge_amount;

        @BindView(R.id.tv_charge_duedate)
        TextView tv_charge_duedate;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
