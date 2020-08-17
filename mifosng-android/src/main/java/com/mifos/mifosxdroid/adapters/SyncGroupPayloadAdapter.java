package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.group.GroupPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public class SyncGroupPayloadAdapter extends
        RecyclerView.Adapter<SyncGroupPayloadAdapter.ViewHolder> {

    private List<GroupPayload> mGroupPayloads;

    @Inject
    public SyncGroupPayloadAdapter() {
        mGroupPayloads = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sync_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GroupPayload groupPayload = mGroupPayloads.get(position);

        holder.tv_name.setText(groupPayload.getName());
        holder.tv_external_id.setText(groupPayload.getExternalId());
        holder.tv_office_id.setText(String.valueOf(groupPayload.getOfficeId()));
        holder.tv_submit_date.setText(groupPayload.getSubmittedOnDate());
        holder.tv_activation_date.setText(groupPayload.getActivationDate());
        if (groupPayload.isActive()) {
            holder.tv_active_status.setText(String.valueOf(true));
        } else {
            holder.tv_active_status.setText(String.valueOf(false));
        }

        if (mGroupPayloads.get(position).getErrorMessage() != null) {
            holder.tv_error_message.setText(groupPayload.getErrorMessage());
            holder.tv_error_message.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mGroupPayloads.size();
    }

    public void setGroupPayload(List<GroupPayload> groupPayload) {
        mGroupPayloads = groupPayload;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_db_name)
        TextView tv_name;

        @BindView(R.id.tv_db_externalId)
        TextView tv_external_id;

        @BindView(R.id.tv_db_office_id)
        TextView tv_office_id;

        @BindView(R.id.tv_db_submit_date)
        TextView tv_submit_date;

        @BindView(R.id.tv_db_activation_date)
        TextView tv_activation_date;

        @BindView(R.id.tv_db_active_status)
        TextView tv_active_status;

        @BindView(R.id.tv_error_message)
        TextView tv_error_message;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
