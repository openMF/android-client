package com.mifos.mifosxdroid.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mifos.core.objects.group.Center
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.SelectableAdapter
import com.mifos.mifosxdroid.views.CircularImageView
import com.mifos.utils.Utils


class CentersListAdapter(
    val onCenterClick: (Int) -> Unit,
    val onCenterLongClick: (Int) -> Unit
) : SelectableAdapter<CentersListAdapter.ViewHolder>() {

    private var centers: List<Center> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_center_list_item, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                onCenterClick(viewHolder.adapterPosition)
        }
        viewHolder.itemView.setOnLongClickListener {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                onCenterLongClick(viewHolder.adapterPosition)
            return@setOnLongClickListener true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val center = centers[position]
        holder.apply {
            tvAccountNumber.text = String.format(
                itemView.context.getString(R.string.centerList_account_prefix), center.accountNo
            )
            tvCenterId.text = center.id.toString()
            tvCenterName.text = center.name
            if (center.staffId != null) {
                tvStaffId.text = center.staffId.toString()
                tvStaffName.text = center.staffName
            } else {
                tvStaffId.text = ""
                tvStaffName.setText(R.string.no_staff)
            }
            tvOfficeId.text = center.officeId.toString()
            tvOfficeName.text = center.officeName
            ivStatusIndicator.setImageDrawable(
                Utils.setCircularBackground(
                    if (center.active == true) R.color.light_green else R.color.light_red,
                    itemView.context
                )
            )

            //Changing the Color of Selected Centers
            viewSelectedOverlay.setBackgroundColor(
                if (isSelected(position)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        ContextCompat.getColor(itemView.context, R.color.primary)
                    } else {
                        itemView.context.resources.getColor(R.color.primary)
                    }
                } else Color.WHITE
            )
            ivSyncStatus.visibility = if (center.sync) View.VISIBLE else View.INVISIBLE
        }
    }


    fun setCenters(centers: List<Center>) {
        this.centers = centers
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = centers[position]


    override fun getItemId(i: Int) = 0L

    override fun getItemCount() = centers.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ivStatusIndicator: CircularImageView = v.findViewById(R.id.iv_status_indicator)
        val tvAccountNumber: TextView = v.findViewById(R.id.tv_account_number)
        val tvCenterName: TextView = v.findViewById(R.id.tv_center_name)
        val tvCenterId: TextView = v.findViewById(R.id.tv_center_id)
        val tvStaffName: TextView = v.findViewById(R.id.tv_staff_name)
        val tvStaffId: TextView = v.findViewById(R.id.tv_staff_id)
        val tvOfficeName: TextView = v.findViewById(R.id.tv_office_name)
        val tvOfficeId: TextView = v.findViewById(R.id.tv_office_id)
        val viewSelectedOverlay: View = v.findViewById(R.id.card_view)
        val ivSyncStatus: ImageView = v.findViewById(R.id.iv_sync_status)
    }

}