package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.databinding.ItemSyncCenterBinding
import com.mifos.core.data.CenterPayload
import javax.inject.Inject

/**
 * Created by mayankjindal on 04/07/17.
 */
class SyncCenterPayloadAdapter @Inject constructor() :
    RecyclerView.Adapter<SyncCenterPayloadAdapter.ViewHolder>() {
    private var mCenterPayloads: List<CenterPayload>

    init {
        mCenterPayloads = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSyncCenterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val centerPayload = mCenterPayloads[position]
        holder.binding.tvName.text = centerPayload.name
        holder.binding.tvOfficeId.text = centerPayload.officeId.toString()
        holder.binding.tvActivationDate.text = centerPayload.activationDate
        if (centerPayload.active) {
            holder.binding.tvActiveStatus.text = true.toString()
        } else {
            holder.binding.tvActiveStatus.text = false.toString()
        }
        if (mCenterPayloads[position].errorMessage != null) {
            holder.binding.tvErrorMessage.text = centerPayload.errorMessage
            holder.binding.tvErrorMessage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mCenterPayloads.size
    }

    fun setCenterPayload(centerPayload: List<CenterPayload>) {
        mCenterPayloads = centerPayload
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemSyncCenterBinding) : RecyclerView.ViewHolder(binding.root)
}