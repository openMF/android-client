package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.PageItem;

import java.util.List;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<PageItem> pageItems;

    public ClientNameListAdapter(Context context, List<PageItem> pageItems){

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public PageItem getItem(int position) {
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
            reusableViewHolder = new ReusableViewHolder();
            view.setTag(reusableViewHolder);

        }else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        reusableViewHolder.tv_clientName = (TextView) view.findViewById(R.id.tv_clientName);
        reusableViewHolder.quickContactBadge = (QuickContactBadge) view.findViewById(R.id.quickContactBadge);

        reusableViewHolder.tv_clientName.setText(pageItems.get(position).getFirstname()+" "
                +pageItems.get(position).getLastname());


        return view;
    }

    private static class ReusableViewHolder{

        TextView tv_clientName;
        QuickContactBadge quickContactBadge;

    }
}
