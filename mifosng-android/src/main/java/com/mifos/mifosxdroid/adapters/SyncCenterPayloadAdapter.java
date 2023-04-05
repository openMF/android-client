package com.mifos.mifosxdroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.databinding.ItemSyncCenterBinding;
import com.mifos.services.data.CenterPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;



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
        ItemSyncCenterBinding binding = ItemSyncCenterBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
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

        TextView tvName;

        TextView tvOfficeId;

        TextView tvActivationDate;

        TextView tvActiveStatus;

        TextView tvErrorMessage;

        public ViewHolder(ItemSyncCenterBinding binding) {
            super(binding.getRoot());

            tvName  = binding.tvDbName;
            tvOfficeId = binding.tvDbOfficeId;
            tvActivationDate = binding.tvDbActivationDate;
            tvActiveStatus = binding.tvDbActiveStatus;
            tvErrorMessage = binding.tvErrorMessage;
        }
    }
}
