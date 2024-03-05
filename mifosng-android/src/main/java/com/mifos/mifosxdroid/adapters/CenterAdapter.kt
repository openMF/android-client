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
import com.mifos.core.objects.db.MeetingCenter
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowCenterItemBinding

class CenterAdapter(context: Context?, centers: List<MeetingCenter>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private val centers: List<MeetingCenter>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.centers = centers
    }

    override fun getCount(): Int {
        return centers.size
    }

    override fun getItem(i: Int): MeetingCenter {
        return centers[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        val binding: RowCenterItemBinding = view.tag as RowCenterItemBinding
        val center = centers[i]
        binding.tvCenterName.text = center.name

        if (center.isSynced == 1) {
            binding.ivCenterSynced.setImageResource(R.drawable.ic_content_import_export)
        }

        return view
    }
}




