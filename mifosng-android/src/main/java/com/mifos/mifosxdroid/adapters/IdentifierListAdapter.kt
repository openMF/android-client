/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.objects.noncore.Identifier
import com.mifos.utils.Constants
import java.util.*
import javax.inject.Inject

/**
 * Created by ishankhanna on 03/07/14.
 */
class IdentifierListAdapter @Inject constructor() : RecyclerView.Adapter<IdentifierListAdapter.ViewHolder>() {
    private var identifiers: List<Identifier>
    private var identifierOptionsListener: IdentifierOptionsListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_identifier_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val identifier = identifiers[position]
        holder.tv_identifier_id!!.text = identifier.documentKey.toString()
        holder.tv_identifier_description!!.text = identifier.description
        holder.tv_identifier_type!!.text = identifier.documentType.name
        holder.tv_identifier_status!!.text = identifier.status
        if (holder.tv_identifier_status!!.text.toString() == Constants.IDENTIFIER_STATUS) {
            holder.tv_identifier_status!!.setText(R.string.active)
        } else {
            holder.tv_identifier_status!!.setText(R.string.inactive)
        }
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!), View.OnClickListener {
        @JvmField
        @BindView(R.id.tv_identifier_id)
        var tv_identifier_id: TextView? = null

        @JvmField
        @BindView(R.id.tv_identifier_type)
        var tv_identifier_type: TextView? = null

        @JvmField
        @BindView(R.id.tv_identifier_description)
        var tv_identifier_description: TextView? = null

        @JvmField
        @BindView(R.id.tv_identifier_status)
        var tv_identifier_status: TextView? = null

        @JvmField
        @BindView(R.id.iv_identifier_options)
        var iv_identifier_options: ImageView? = null
        override fun onClick(v: View) {
            identifierOptionsListener!!.onClickIdentifierOptions(adapterPosition, v)
        }

        init {
            ButterKnife.bind(this, view!!)
            iv_identifier_options!!.setOnClickListener(this)
        }
    }

    interface IdentifierOptionsListener {
        fun onClickIdentifierOptions(position: Int, view: View?)
    }

    init {
        identifiers = ArrayList()
    }
}