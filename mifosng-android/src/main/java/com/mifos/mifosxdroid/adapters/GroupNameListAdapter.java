/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.group.Group;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupNameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LayoutInflater layoutInflater;
    List<Group> pageItems;

    public GroupNameListAdapter(Context context, List<Group> pageItems) {

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_group_name, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).tv_groupsName.setText(pageItems.get(position).getName());
            ((ViewHolder) holder).tv_groupsId.setText(pageItems.get(position).getId().toString());
        }
    }


    @Override
    public int getItemCount() {
        return pageItems.size();
    }

    public Group getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_grouplistName)
        TextView tv_groupsName;

        @BindView(R.id.tv_groupsId)
        TextView tv_groupsId;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
