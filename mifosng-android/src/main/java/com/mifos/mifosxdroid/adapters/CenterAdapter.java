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
import com.mifos.mifosxdroid.databinding.RowCenterItemBinding;
import com.mifos.objects.db.MeetingCenter;

import java.util.List;



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
        RowCenterItemBinding binding;

        if (view == null) {
            binding = RowCenterItemBinding.inflate(layoutInflater, viewGroup, false);
            viewHolder = new ViewHolder(binding);
            viewHolder.view = binding.getRoot();
            viewHolder.view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.view = view;
        }
        MeetingCenter center = centers.get(i);
        viewHolder.tv_center_name.setText(center.getName());
        if (center.getIsSynced() == 1)
            viewHolder.ivCenterSynced.setImageResource(R.drawable.ic_content_import_export);
        return viewHolder.view;
    }

    public static class ViewHolder {
        private View view;
        TextView tv_center_name;
        ImageView ivCenterSynced;

        public ViewHolder(RowCenterItemBinding binding) {
            tv_center_name = binding.tvCenterName;
            ivCenterSynced = binding.ivCenterSynced;
        }
    }
}
