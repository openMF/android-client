package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.services.data.CenterPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayankjindal on 04/07/17.
 */

public class SyncCenterPayloadAdapter extends
        RecyclerView.Adapter<SyncCenterPayloadAdapter.ViewHolder> {

    private List<CenterPayload> mCenterPayloads;

    @Inject
    public SyncCenterPayloadAdapter() {
        mCenterPayloads = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sync_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CenterPayload centerPayload = mCenterPayloads.get(position);

        holder.tvName.setText(centerPayload.getName());
        holder.tvOfficeId.setText(String.valueOf(centerPayload.getOfficeId()));
        holder.tvActivationDate.setText(centerPayload.getActivationDate());
        if (centerPayload.isActive()) {
            holder.tvActiveStatus.setText(String.valueOf(true));
        } else {
            holder.tvActiveStatus.setText(String.valueOf(false));
        }
        if (mCenterPayloads.get(position).getErrorMessage() != null) {
            holder.tvErrorMessage.setText(centerPayload.getErrorMessage());
            holder.tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCenterPayloads.size();
    }

    public void setCenterPayload(List<CenterPayload> centerPayload) {
        mCenterPayloads = centerPayload;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_db_name)
        TextView tvName;

        @BindView(R.id.tv_db_office_id)
        TextView tvOfficeId;

        @BindView(R.id.tv_db_activation_date)
        TextView tvActivationDate;

        @BindView(R.id.tv_db_active_status)
        TextView tvActiveStatus;

        @BindView(R.id.tv_error_message)
        TextView tvErrorMessage;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
