package com.mifos.mifosxdroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.databinding.ItemSyncGroupBinding;
import com.mifos.objects.group.GroupPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


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
        ItemSyncGroupBinding binding = ItemSyncGroupBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
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

        TextView tv_name;

        TextView tv_external_id;

        TextView tv_office_id;

        TextView tv_submit_date;

        TextView tv_activation_date;

        TextView tv_active_status;

        TextView tv_error_message;

        public ViewHolder(ItemSyncGroupBinding binding) {
            super(binding.getRoot());
            tv_name = binding.tvDbName;
            tv_external_id = binding.tvDbExternalId;
            tv_office_id = binding.tvDbOfficeId;
            tv_submit_date = binding.tvDbSubmitDate;
            tv_activation_date = binding.tvDbActivationDate;
            tv_active_status = binding.tvDbActiveStatus;
            tv_error_message = binding.tvErrorMessage;
        }
    }
}
