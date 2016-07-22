package com.mifos.mifosxdroid.adapters;

import android.support.v7.widget.RecyclerView;
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

    List<GroupPayload> mGroupPayloads;

    @Inject
    public SyncGroupPayloadAdapter() {
        mGroupPayloads = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sync_payload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupPayload groupPayload = mGroupPayloads.get(position);
        holder.tv_payload.setText(groupPayload.getName());

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

        @BindView(R.id.tv_sync_payload)
        TextView tv_payload;

        @BindView(R.id.tv_payload_error_message)
        TextView tv_error_message;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
