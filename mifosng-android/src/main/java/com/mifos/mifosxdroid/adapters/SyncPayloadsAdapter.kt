package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.core.objects.client.ClientPayload
import com.mifos.mifosxdroid.databinding.ItemSyncClientBinding

/**
 * Created by Rajan Maurya on 08/07/16.
 */
class SyncPayloadsAdapter(var context: Context, payloads: List<ClientPayload>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var clientPayloads: List<ClientPayload>

    init {
        clientPayloads = payloads
    }

    fun getItem(position: Int): ClientPayload {
        return clientPayloads[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemSyncClientBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val clientPayload = clientPayloads[position]
            holder.binding.tvFirstName.text =
                clientPayload.firstname
            holder.binding.tvMiddleName.text =
                clientPayload.middlename
            holder.binding.tvLastName.text =
                clientPayload.lastname
            holder.binding.tvMobileNo.text =
                clientPayload.mobileNo
            holder.binding.tvExternalId.text =
                clientPayload.externalId
            holder.binding.tvDob.text = clientPayload.dateOfBirth
            holder.binding.tvOfficeId.text =
                clientPayload.officeId.toString()
            holder.binding.tvActivationDate.text =
                clientPayload.activationDate
            when (clientPayload.genderId) {
                22 -> holder.binding.tvGender.text =
                    "Male"

                24 -> holder.binding.tvGender.text = "Female"
                91 -> holder.binding.tvGender.text =
                    "homosexual"

                else -> holder.binding.tvGender.text =
                    "Male"
            }
            if (clientPayload.active == true) {
                holder.binding.tvActiveStatus.text =
                    true.toString()
            } else {
                holder.binding.tvActiveStatus.text = false.toString()
            }
            if (clientPayloads[position].errorMessage != null) {
                holder.binding.tvErrorMessage.text =
                    clientPayload.errorMessage
                holder.binding.tvErrorMessage.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return clientPayloads.size
    }

    fun setClientPayload(clientPayload: List<ClientPayload>) {
        clientPayloads = clientPayload
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemSyncClientBinding) : RecyclerView.ViewHolder(binding.root)
}