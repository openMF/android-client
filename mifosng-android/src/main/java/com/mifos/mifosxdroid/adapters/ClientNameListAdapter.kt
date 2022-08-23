package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.SelectableAdapter
import com.mifos.objects.client.Client
import com.mifos.utils.ImageLoaderUtils
import org.mifos.mobile.ui.getThemeAttributeColor


class ClientNameListAdapter(
    val onClientNameClick: (Int) -> Unit,
    val onClientNameLongClick: (Int) -> Unit
) : SelectableAdapter<ClientNameListAdapter.ViewHolder>() {
    private var pageItems: List<Client> = ArrayList()

    fun getItem(position: Int) = pageItems[position]


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_client_name, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
                onClientNameClick(viewHolder.bindingAdapterPosition)
        }
        viewHolder.itemView.setOnLongClickListener {
                onClientNameLongClick(viewHolder.bindingAdapterPosition)
            return@setOnLongClickListener true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val client = getItem(position)
        val clientName = client.fullname ?: (client.firstname + " " + client.lastname)

        holder.tv_clientName.text = clientName
        holder.tv_clientAccountNumber.text = client.accountNo

        // lazy the  load profile picture
        if (client.isImagePresent) {
            // make the image url
            ImageLoaderUtils.loadImage(holder.itemView.context, client.id, holder.iv_userPicture)
        } else {
            holder.iv_userPicture.setImageResource(R.drawable.ic_dp_placeholder)
        }

        //Changing the Color of Selected Clients
        holder.view_selectedOverlay.setBackgroundColor(
            holder.itemView.context.getThemeAttributeColor(
                if (isSelected(position)) R.attr.colorSurfaceVariant
                else R.attr.colorSurface
            )
        )
        holder.iv_sync_status.visibility = if (client.isSync) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemId(i: Int) = 0L

    override fun getItemCount() = pageItems.size

    fun setClients(clients: List<Client>) {
        pageItems = clients
    }

    fun updateItem(position: Int) {
        notifyItemChanged(position)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_clientName: TextView = v.findViewById(R.id.tv_clientName)
        val tv_clientAccountNumber: TextView = v.findViewById(R.id.tv_clientAccountNumber)
        val iv_userPicture: ImageView = v.findViewById(R.id.iv_user_picture)
        val view_selectedOverlay: View = v.findViewById(R.id.cv_client)
        val iv_sync_status: ImageView = v.findViewById(R.id.iv_sync_status)

    }
}
