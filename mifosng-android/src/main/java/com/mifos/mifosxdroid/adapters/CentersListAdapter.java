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
import com.mifos.mifosxdroid.views.CircularImageView;
import com.mifos.objects.group.Center;
import com.mifos.utils.Utils;

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
        Center center = centers.get(position);

        holder.tvAccountNumber.setText(String.format(context.
                getString(R.string.centerList_account_prefix), center.getAccountNo()));
        holder.tvCenterId.setText(String.valueOf(center.getId()));
        holder.tvCenterName.setText(center.getName());
        if (center.getStaffId() != null) {
            holder.tvStaffId.setText(String.valueOf(center.getStaffId()));
            holder.tvStaffName.setText(center.getStaffName());
        } else {
            holder.tvStaffId.setText("");
            holder.tvStaffName.setText(R.string.no_staff);
        }
        holder.tvOfficeId.setText(String.valueOf(center.getOfficeId()));
        holder.tvOfficeName.setText(center.getOfficeName());
        if (center.getActive()) {
            holder.ivStatusIndicator.setImageDrawable(
                    Utils.setCircularBackground(R.color.light_green, context));
        } else {
            holder.ivStatusIndicator.setImageDrawable(
                    Utils.setCircularBackground(R.color.light_red, context));
        }
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

        @BindView(R.id.iv_status_indicator)
        CircularImageView ivStatusIndicator;

        @BindView(R.id.tv_account_number)
        TextView tvAccountNumber;

        @BindView(R.id.tv_center_name)
        TextView tvCenterName;

        @BindView(R.id.tv_center_id)
        TextView tvCenterId;

        @BindView(R.id.tv_staff_name)
        TextView tvStaffName;

        @BindView(R.id.tv_staff_id)
        TextView tvStaffId;

        @BindView(R.id.tv_office_name)
        TextView tvOfficeName;

        @BindView(R.id.tv_office_id)
        TextView tvOfficeId;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}