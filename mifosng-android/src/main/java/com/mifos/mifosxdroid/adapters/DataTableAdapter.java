package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.noncore.DataTable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 12/02/17.
 */
public class DataTableAdapter extends RecyclerView.Adapter<DataTableAdapter.ViewHolder> {

    List<DataTable> dataTables;

    @Inject
    public DataTableAdapter() {
        dataTables = new ArrayList<>();
    }

    @Override
    public DataTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataTableAdapter.ViewHolder holder, int position) {
        DataTable dataTable = dataTables.get(position);
        holder.tvDataTableName.setText(dataTable.getRegisteredTableName());
    }


    public void setDataTables(List<DataTable> dataTables) {
        this.dataTables = dataTables;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataTables.size();
    }

    public DataTable getItem(int position) {
        return dataTables.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_data_table_name)
        TextView tvDataTableName;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
