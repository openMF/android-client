package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Client> pageItems;

    public ClientNameListAdapter(Context context, List<Client> pageItems){

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Client getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ReusableViewHolder reusableViewHolder;

        if(view==null)
        {
            view = layoutInflater.inflate(R.layout.row_client_name,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        }else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        reusableViewHolder.tv_clientName.setText(pageItems.get(position).getFirstname()+" "
                +pageItems.get(position).getLastname());

        reusableViewHolder.tv_clientAccountNumber.setText(pageItems.get(position).getAccountNo().toString());

        return view;
    }

     static class ReusableViewHolder{

         @InjectView(R.id.tv_clientName) TextView tv_clientName;
         @InjectView(R.id.tv_clientAccountNumber) TextView tv_clientAccountNumber;
         @InjectView(R.id.quickContactBadge) QuickContactBadge quickContactBadge;

         public ReusableViewHolder(View view) {
             ButterKnife.inject(this, view);
         }

    }
}
