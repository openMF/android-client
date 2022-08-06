package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.R
import com.mifos.objects.client.Charges


class ChargeNameListAdapter(
    var pageItems: List<Charges>,
    var clientId: Int
) : RecyclerView.Adapter<ChargeNameListAdapter.ViewHolder>() {

    fun getItem(position: Int) = pageItems[position]


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_charge_name, parent, false)
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_charger_id.text = pageItems[position].chargeId.toString()
        holder.tv_charge_Name.text = pageItems[position].name
        holder.tv_charge_amount.text = pageItems[position].amount.toString()
        holder.tv_charge_duedate.text = pageItems[position].formattedDueDate
    }

    override fun getItemId(i: Int) = 0L

    override fun getItemCount() = pageItems.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_charger_id: TextView = v.findViewById(R.id.tv_charger_id)
        val tv_charge_Name: TextView = v.findViewById(R.id.tv_chargeName)
        val tv_charge_amount: TextView = v.findViewById(R.id.tv_charge_amount)
        val tv_charge_duedate: TextView = v.findViewById(R.id.tv_charge_duedate)
    }
}
