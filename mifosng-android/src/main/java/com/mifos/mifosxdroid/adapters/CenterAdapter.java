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
import android.widget.ImageView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.MeetingCenter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CenterAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<MeetingCenter> centers;

    public CenterAdapter(Context context, List<MeetingCenter> centers) {
        layoutInflater = LayoutInflater.from(context);
        this.centers = centers;
    }

    @Override
    public int getCount() {
        return this.centers.size();
    }

    @Override
    public MeetingCenter getItem(int i) {
        return this.centers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_center_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MeetingCenter center = centers.get(i);
        viewHolder.tv_center_name.setText(center.getName());
        if (center.getIsSynced() == 1)
            viewHolder.ivCenterSynced.setImageResource(R.drawable.ic_content_import_export);
        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.tv_center_name)
        TextView tv_center_name;
        @InjectView(R.id.iv_center_synced)
        ImageView ivCenterSynced;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
