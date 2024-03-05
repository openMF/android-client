/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.application.App.Companion.context
import com.mifos.core.objects.noncore.Identifier
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowIdentifierListBinding
import javax.inject.Inject

/**
 * Created by ishankhanna on 03/07/14.
 */
class IdentifierListAdapter @Inject constructor() :
    RecyclerView.Adapter<IdentifierListAdapter.ViewHolder>() {
    private var identifiers: List<Identifier>
    private var identifierOptionsListener: IdentifierOptionsListener? = null

    init {
        identifiers = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_identifier_list, parent, false)
        return ViewHolder(
            RowIdentifierListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val identifier = identifiers[position]
        holder.binding.tvIdentifierId.text = identifier.documentKey.toString()
        var description = identifier.description
        if (description == null) {
            description = "-"
        }
        holder.binding.tvIdentifierDescription.text = description
        holder.binding.tvIdentifierType.text = identifier.documentType?.name
        val color: Int = if (identifier.status?.contains("inactive") == true) {
            context!!.getColor(R.color.red_light)
        } else {
            context!!.getColor(R.color.green_light)
        }
        holder.binding.vStatus.setBackgroundColor(color)
    }

    fun setIdentifiers(identifiers: List<Identifier>) {
        this.identifiers = identifiers
        notifyDataSetChanged()
    }

    fun setIdentifierOptionsListener(identifierOptionsListener: IdentifierOptionsListener?) {
        this.identifierOptionsListener = identifierOptionsListener
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return identifiers.size
    }

    inner class ViewHolder(val binding: RowIdentifierListBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.ivIdentifierOptions.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            identifierOptionsListener?.onClickIdentifierOptions(adapterPosition, v)
        }
    }

    interface IdentifierOptionsListener {
        fun onClickIdentifierOptions(position: Int, view: View)
    }
}