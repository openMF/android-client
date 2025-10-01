package com.mifos.androidclient.features.shares.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mifos.androidclient.databinding.ItemChargeBinding
import com.mifos.androidclient.features.shares.data.models.Charge

class ChargesAdapter : ListAdapter<Charge, ChargesAdapter.ChargeViewHolder>(ChargeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeViewHolder {
        val binding = ItemChargeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChargeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChargeViewHolder, position: Int) {
        val charge = getItem(position)
        holder.bind(charge)
    }

    class ChargeViewHolder(private val binding: ItemChargeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(charge: Charge) {
            binding.chargeTitleTv.text = charge.title
            binding.chargeAmountTv.text = charge.amount
        }
    }
}

class ChargeDiffCallback : DiffUtil.ItemCallback<Charge>() {
    override fun areItemsTheSame(oldItem: Charge, newItem: Charge): Boolean {
        return oldItem.title == newItem.title && oldItem.amount == newItem.amount
    }

    override fun areContentsTheSame(oldItem: Charge, newItem: Charge): Boolean {
        return oldItem == newItem
    }
}