package com.mifos.mifosxdroid.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.SelectableAdapter
import com.mifos.objects.group.Group
import org.mifos.mobile.ui.getThemeAttributeColor
import javax.inject.Inject

class GroupNameListAdapter(
    val onGroupClick: (Int) -> Unit,
    val onGroupLongClick: (Int) -> Unit
) : SelectableAdapter<GroupNameListAdapter.ViewHolder>() {
    private var groups: List<Group> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_group_name, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
                onGroupClick(viewHolder.bindingAdapterPosition)
        }
        viewHolder.itemView.setOnLongClickListener {
                onGroupLongClick(viewHolder.bindingAdapterPosition)
            return@setOnLongClickListener true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.tv_groupsName.text = group.name
        holder.tv_groupsId.text = group.id.toString()

        //Changing the Color of Selected Groups
        holder.view_selectedOverlay.setBackgroundColor(
            holder.itemView.context.getThemeAttributeColor(
                if (isSelected(position)) R.attr.colorSurfaceVariant
                else R.attr.colorSurface
            )
        )
        holder.iv_sync_status.visibility = if (group.isSync) View.VISIBLE else View.INVISIBLE
    }

    fun setGroups(groups: List<Group>) {
        this.groups = groups
        notifyDataSetChanged()
    }


    override fun getItemCount() = groups.size


    fun getItem(position: Int) = groups[position]


    override fun getItemId(i: Int) = 0L

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_groupsName: TextView = v.findViewById(R.id.tv_group_name)
        val tv_groupsId: TextView = v.findViewById(R.id.tv_group_id)
        var view_selectedOverlay: View = v.findViewById(R.id.card_view)
        val iv_sync_status: ImageView = v.findViewById(R.id.iv_sync_status)
    }
}
