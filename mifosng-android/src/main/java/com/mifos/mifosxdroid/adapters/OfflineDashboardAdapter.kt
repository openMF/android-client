package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.R
import javax.inject.Inject


class OfflineDashboardAdapter(
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<OfflineDashboardAdapter.ViewHolder>() {
    private val payloadNames: MutableList<Int> = ArrayList()
    private val payloadCounts: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_offline_dashboard, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            if(viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                onItemClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_payload_name.setText(payloadNames[position])
        holder.tv_item_count.text = payloadCounts[position]
    }

    override fun getItemCount() = payloadNames.size


    fun showCard(clientPayloadCount: String, cardName: Int) {
        payloadCounts.add(clientPayloadCount)
        payloadNames.add(cardName)
        notifyDataSetChanged()
    }

    fun removeAllCards() {
        payloadNames.clear()
        payloadCounts.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_item_count: TextView = v.findViewById(R.id.tv_payload_count)
        val tv_payload_name: TextView = v.findViewById(R.id.tv_payload_name)
        val iv_payload_image: ImageView = v.findViewById(R.id.iv_payload_image)
    }
}
