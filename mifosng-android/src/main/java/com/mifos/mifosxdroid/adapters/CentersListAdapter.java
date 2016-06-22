/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.group.Center;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class CentersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Center> centers;
    private Resources resources;

    public CentersListAdapter(Context context, List<Center> centers) {

        layoutInflater = LayoutInflater.from(context);
        this.centers = centers;
        resources = context.getResources();

    }

    public Center getItem(int i) {
        return this.centers.get(i);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_center_list_item, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {

            ((ViewHolder) holder).tv_center_id.setText(resources
                    .getString(R.string.center_id) + centers.get(position).getId());

            ((ViewHolder) holder).tv_center_name.setText(centers.get(position).getName());

            ((ViewHolder) holder).tv_staff_id.setText(
                    resources.getString(R.string.staff_id) + centers.get(position).getStaffId());

            ((ViewHolder) holder).tv_staff_name.setText(centers.get(position).getStaffName());

            ((ViewHolder) holder).tv_office_id.setText(
                    resources.getString(R.string.office_id) + centers.get(position).getOfficeId());

            ((ViewHolder) holder).tv_office_name.setText(centers.get(position).getOfficeName());
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return centers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_center_name)
        TextView tv_center_name;

        @BindView(R.id.tv_center_id)
        TextView tv_center_id;

        @BindView(R.id.tv_staff_name)
        TextView tv_staff_name;

        @BindView(R.id.tv_staff_id)
        TextView tv_staff_id;

        @BindView(R.id.tv_office_name)
        TextView tv_office_name;

        @BindView(R.id.tv_office_id)
        TextView tv_office_id;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
