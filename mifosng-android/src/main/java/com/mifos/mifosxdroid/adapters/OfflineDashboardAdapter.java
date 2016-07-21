package com.mifos.mifosxdroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public class OfflineDashboardAdapter  extends
        RecyclerView.Adapter<OfflineDashboardAdapter.ViewHolder> {

    private List<ClientPayload> clientPayloads = new ArrayList<>();
    private List<GroupPayload> groupPayloads = new ArrayList<>();

    private String [] payloadNames = {"Sync Clients", "Sync Groups"};
    private String [] payloadCounts = {"Total Payload : " + String.valueOf(clientPayloads.size()),
                                       "Total Payload : " + String.valueOf(groupPayloads.size())};


    @Inject
    public OfflineDashboardAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offline_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_payload_name.setText(payloadNames[position]);
        holder.tv_item_count.setText(payloadCounts[position]);
    }

    @Override
    public int getItemCount() {
        return payloadNames.length;
    }

    public void setClientPayloads(List<ClientPayload> clientPayload) {
        clientPayloads = clientPayload;
    }

    public void setGroupPayloads(List<GroupPayload> groupPayload) {
        groupPayloads = groupPayload;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_payload_count)
        TextView tv_item_count;

        @BindView(R.id.tv_payload_name)
        TextView tv_payload_name;

        @BindView(R.id.iv_payload_image)
        ImageView iv_payload_image;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
