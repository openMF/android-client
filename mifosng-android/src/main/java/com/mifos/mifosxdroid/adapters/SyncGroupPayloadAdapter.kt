package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.core.objects.group.GroupPayload
import com.mifos.mifosxdroid.databinding.ItemSyncGroupBinding
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 20/07/16.
 */
class SyncGroupPayloadAdapter @Inject constructor() :
    RecyclerView.Adapter<SyncGroupPayloadAdapter.ViewHolder?>() {
    private var mGroupPayloads: List<GroupPayload>

    init {
        mGroupPayloads = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSyncGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupPayload = mGroupPayloads[position]
        holder.binding.tvName.text = groupPayload.name
        holder.binding.tvExternalId.text = groupPayload.externalId
        holder.binding.tvOfficeId.text = groupPayload.officeId.toString()
        holder.binding.tvSubmitDate.text = groupPayload.submittedOnDate
        holder.binding.tvActivationDate.text = groupPayload.activationDate
        if (groupPayload.active) {
            holder.binding.tvActiveStatus.text = true.toString()
        } else {
            holder.binding.tvActiveStatus.text = false.toString()
        }
        if (mGroupPayloads[position].errorMessage != null) {
            holder.binding.tvErrorMessage.text = groupPayload.errorMessage
            holder.binding.tvErrorMessage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mGroupPayloads.size
    }

    fun setGroupPayload(groupPayload: List<GroupPayload>) {
        mGroupPayloads = groupPayload
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemSyncGroupBinding) : RecyclerView.ViewHolder(binding.root)
}