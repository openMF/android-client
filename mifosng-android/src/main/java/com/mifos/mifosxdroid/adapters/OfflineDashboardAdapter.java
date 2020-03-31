package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;

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

    private List<Integer> payloadNames;
    private List<String> payloadCounts;


    @Inject
    public OfflineDashboardAdapter() {
        payloadNames = new ArrayList<>();
        payloadCounts = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offline_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_payload_name.setText(payloadNames.get(position));
        holder.tv_item_count.setText(payloadCounts.get(position));
    }

    @Override
    public int getItemCount() {
        return payloadNames.size();
    }

    public void showCard(String clientPayloadCount, int cardName) {
        payloadCounts.add(clientPayloadCount);
        payloadNames.add(cardName);
        notifyDataSetChanged();
    }

    public void removeAllCards() {
        payloadNames.clear();
        payloadCounts.clear();
        notifyDataSetChanged();
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
