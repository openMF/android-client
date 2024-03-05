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
import com.mifos.core.objects.db.MifosGroup
import com.mifos.mifosxdroid.databinding.RowGroupListItemBinding

class MifosGroupListAdapter(context: Context?, groups: List<MifosGroup>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private val groups: List<MifosGroup>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.groups = groups
    }

    override fun getCount(): Int {
        return groups.size
    }

    override fun getItem(i: Int): MifosGroup {
        return groups[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: RowGroupListItemBinding
        val convertView: View
        if (view == null) {
            binding = RowGroupListItemBinding.inflate(layoutInflater, viewGroup, false)
            convertView = binding.root
            convertView.tag = binding
        } else {
            binding = view.tag as RowGroupListItemBinding
            convertView = view
        }
        val mifosGroup = groups[i]
        binding.tvGroupName.text = mifosGroup.groupName
        binding.tvStaffName.text = mifosGroup.staffName
        binding.tvLevelName.text = mifosGroup.levelName
        return convertView
    }
}