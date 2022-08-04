package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.R
import com.mifos.objects.noncore.DataTable

class DataTableAdapter(
    val onDateTableClick: (DataTable) -> Unit
) : RecyclerView.Adapter<DataTableAdapter.ViewHolder>() {
    var dataTables: List<DataTable> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_data_table, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            if(viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                onDateTableClick(dataTables[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataTable = dataTables[position]
        holder.tvDataTableName.text = dataTable.registeredTableName
    }

    override fun getItemCount() = dataTables.size


    fun getItem(position: Int): DataTable {
        return dataTables[position]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvDataTableName: TextView = v.findViewById(R.id.tv_data_table_name)
    }
}
