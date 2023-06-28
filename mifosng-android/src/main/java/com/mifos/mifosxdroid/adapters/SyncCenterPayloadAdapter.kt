package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.services.data.CenterPayload
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sync_center, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val centerPayload = mCenterPayloads[position]
        holder.tvName!!.text = centerPayload.name
        holder.tvOfficeId!!.text = centerPayload.officeId.toString()
        holder.tvActivationDate!!.text = centerPayload.activationDate
        if (centerPayload.isActive) {
            holder.tvActiveStatus!!.text = true.toString()
        } else {
            holder.tvActiveStatus!!.text = false.toString()
        }
        if (mCenterPayloads[position].errorMessage != null) {
            holder.tvErrorMessage!!.text = centerPayload.errorMessage
            holder.tvErrorMessage!!.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mCenterPayloads.size
    }

    fun setCenterPayload(centerPayload: List<CenterPayload>) {
        mCenterPayloads = centerPayload
        notifyDataSetChanged()
    }

    class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.tv_db_name)
        var tvName: TextView? = null

        @JvmField
        @BindView(R.id.tv_db_office_id)
        var tvOfficeId: TextView? = null

        @JvmField
        @BindView(R.id.tv_db_activation_date)
        var tvActivationDate: TextView? = null

        @JvmField
        @BindView(R.id.tv_db_active_status)
        var tvActiveStatus: TextView? = null

        @JvmField
        @BindView(R.id.tv_error_message)
        var tvErrorMessage: TextView? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }
}