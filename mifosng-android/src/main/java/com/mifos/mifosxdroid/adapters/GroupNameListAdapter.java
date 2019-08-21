/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.SelectableAdapter;
import com.mifos.objects.group.Group;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupNameListAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    private List<Group> groups;
    private Context context;

    @Inject
    public GroupNameListAdapter() {
        this.groups = new ArrayList<>();
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
            Group group = groups.get(position);

            ((ViewHolder) holder).tv_groupsName.setText(group.getName());
            ((ViewHolder) holder).tv_groupsId.setText(String.valueOf(group.getId()));

            //Changing the Color of Selected Groups
            ((ViewHolder) holder).view_selectedOverlay
                    .setBackgroundColor(isSelected(position) ? ContextCompat.getColor(context,
                            R.color.gray_light) : Color.WHITE);

            ((ViewHolder) holder).iv_sync_status
                    .setVisibility(group.isSync() ? View.VISIBLE : View.INVISIBLE);
        }
    }


    public void setGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_group_name)
        TextView tv_groupsName;

        @BindView(R.id.tv_group_id)
        TextView tv_groupsId;

        @BindView(R.id.linearLayout)
        LinearLayout view_selectedOverlay;

        @BindView(R.id.iv_sync_status)
        ImageView iv_sync_status;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
