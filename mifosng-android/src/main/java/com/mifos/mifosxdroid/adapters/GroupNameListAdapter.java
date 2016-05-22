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
import com.mifos.objects.group.Group;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GroupNameListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Group> pageItems;

    public GroupNameListAdapter(Context context, List<Group> pageItems) {

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Group getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ReusableViewHolder reusableViewHolder;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_group_name, null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        } else {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        reusableViewHolder.tv_groupsName.setText(pageItems.get(position).getName());


        reusableViewHolder.tv_groupsId.setText(pageItems.get(position).getId().toString());

        return view;
    }

    static class ReusableViewHolder {

        @InjectView(R.id.tv_grouplistName)
        TextView tv_groupsName;
        @InjectView(R.id.tv_groupsId)
        TextView tv_groupsId;

        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
