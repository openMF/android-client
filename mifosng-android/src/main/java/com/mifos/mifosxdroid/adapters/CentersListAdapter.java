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
import com.mifos.objects.group.Center;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class CentersListAdapter extends RecyclerView.Adapter<CentersListAdapter.ViewHolder> {

    private List<Center> centers;
    private Context context;

    @Inject
    public CentersListAdapter() {
        centers = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_center_list_item, parent, false);
        return new CentersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            String centerId = context.getResources()
                    .getString(R.string.center_id) + centers.get(position).getId();

            String staffId = context.getResources()
                    .getString(R.string.staff_id) + centers.get(position).getStaffId();

            String officeId = context.getResources()
                    .getString(R.string.office_id) + centers.get(position).getOfficeId();

            holder.tv_center_id.setText(centerId);
            holder.tv_center_name.setText(centers.get(position).getName());
            holder.tv_staff_id.setText(staffId);
            holder.tv_staff_name.setText(centers.get(position).getStaffName());
            holder.tv_office_id.setText(officeId);
            holder.tv_office_name.setText(centers.get(position).getOfficeName());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCenters(List<Center> centers) {
        this.centers = centers;
        notifyDataSetChanged();
    }

    public Center getItem(int position) {
        return this.centers.get(position);
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
