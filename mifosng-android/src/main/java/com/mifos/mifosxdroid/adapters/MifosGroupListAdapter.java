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
import com.mifos.objects.db.MifosGroup;

import java.util.List;


public class MifosGroupListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<MifosGroup> groups;

    public MifosGroupListAdapter(Context context, List<MifosGroup> groups){
        layoutInflater = LayoutInflater.from(context);
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return this.groups.size();
    }

    @Override
    public MifosGroup getItem(int i) {
        return this.groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if(view==null){
            view = layoutInflater.inflate(R.layout.row_group_list_item,viewGroup,false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
        viewHolder.tv_staff_name = (TextView) view.findViewById(R.id.tv_staff_name);
        viewHolder.tv_level_name = (TextView) view.findViewById(R.id.tv_level_name);


        MifosGroup mifosGroup = groups.get(i);
        viewHolder.tv_group_name.setText(mifosGroup.getGroupName());
        viewHolder.tv_staff_name.setText( mifosGroup.getStaffName());
        viewHolder.tv_level_name.setText(mifosGroup.getLevelName());

        return view;
    }

    public static class ViewHolder
    {
        TextView tv_group_name;
        TextView tv_staff_name;
        TextView tv_level_name;
    }
}
