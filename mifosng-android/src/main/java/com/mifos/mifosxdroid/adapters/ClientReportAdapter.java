package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.runreports.client.ClientReportTypeItem;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 03-08-17.
 */

public class ClientReportAdapter extends
        RecyclerView.Adapter<ClientReportAdapter.ViewHolder> {

    private List<ClientReportTypeItem> items;

    @Inject
    public ClientReportAdapter() {}

    public void setReportItems(List<ClientReportTypeItem> items) {
        this.items = items;
    }

    @Override
    public ClientReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client_runreport, parent, false);

        return new ClientReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientReportAdapter.ViewHolder holder, int position) {
        if (holder != null) {
            holder.tvReportCategory.setText(items.get(position).getReportCategory());
            holder.tvReportName.setText(items.get(position).getReportName());
            holder.tvReportType.setText(items.get(position).getReportType());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @BindView(R.id.tv_report_name)
        TextView tvReportName;

        @BindView(R.id.tv_report_type)
        TextView tvReportType;

        @BindView(R.id.tv_report_category)
        TextView tvReportCategory;
    }

}
