package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import com.mifos.mifosxdroid.R


class ClientReportAdapter(
    val onClientReportClick: (Int) -> Unit
) : RecyclerView.Adapter<ClientReportAdapter.ViewHolder>() {
    private var items: List<ClientReportTypeItem> = emptyList()


    fun setReportItems(items: List<ClientReportTypeItem>) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_client_runreport, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                onClientReportClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvReportCategory.text = items[position].report_category
        holder.tvReportName.text = items[position].report_name
        holder.tvReportType.text = items[position].report_type
    }

    override fun getItemCount() = items.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvReportName: TextView = v.findViewById(R.id.tv_report_name)
        val tvReportType: TextView = v.findViewById(R.id.tv_report_type)
        val tvReportCategory: TextView = v.findViewById(R.id.tv_report_category)
    }
}
