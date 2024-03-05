/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.mifos.core.objects.client.Status
import com.mifos.core.objects.group.Group
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowGroupListBinding

/**
 * Created by ishankhanna on 28/06/14.
 */
class GroupListAdapter(context: Context, groups: List<Group>) : BaseAdapter() {
    var layoutInflater: LayoutInflater
    var context: Context
    var groups: List<Group> = ArrayList()

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.groups = groups
    }

    override fun getCount(): Int {
        return groups.size
    }

    override fun getItem(i: Int): Group {
        return groups[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(index: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: RowGroupListBinding
        val convertView: View
        if (view == null) {
            binding = RowGroupListBinding.inflate(layoutInflater, viewGroup, false)
            convertView = binding.root
            convertView.tag = binding
        } else {
            binding = view.tag as RowGroupListBinding
            convertView = view
        }
        val group = groups[index]
        /**
         * Passing the String value of Status to Helper Method of
         * Status Class that compares String Value to a Static String and returns
         * if Status is Active or not
         */
        if (group.status?.value?.let { Status.isActive(it) } == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.deposit_green)
            )
            binding.tvStatusText.text =
                context.resources.getString(R.string.active)
        } else {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.light_red)
            )
            binding.tvStatusText.text =
                context.resources.getString(R.string.inactive)
        }
        return convertView
    }
}