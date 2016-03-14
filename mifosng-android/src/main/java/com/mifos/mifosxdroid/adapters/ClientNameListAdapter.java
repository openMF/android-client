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
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.mifos.api.OauthOkHttpClient;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.utils.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Client> pageItems;
	Context mContext;
	Picasso picasso;

    public ClientNameListAdapter(Context context, List<Client> pageItems){

	    this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
	    picasso = new Picasso.Builder(mContext)
			    .downloader(new OkHttp3Downloader(new OauthOkHttpClient().getOauthOkHttpClient()))
			    .build();
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

        reusableViewHolder.tv_clientName.setText(pageItems.get(position).getFirstname()+" " +pageItems.get(position).getLastname());

        reusableViewHolder.tv_clientAccountNumber.setText(pageItems.get(position).getAccountNo().toString());

	    picasso.load(PrefManager.getInstanceUrl()
			    + "/" + "clients/" + pageItems.get(position).getId() + "/images?maxHeight=120&maxWidth=120")
			    .placeholder(R.drawable.ic_dp_placeholder)
			    .resize(96, 96)
			    .centerCrop()
			    .into(reusableViewHolder.quickContactBadge);

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
