/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mifos.core.objects.survey.Survey
import com.mifos.mifosxdroid.databinding.RowSurveysListItemBinding

/**
 * Created by Nasim Banu on 27,January,2016.
 */
class SurveyListAdapter(context: Context, listSurvey: List<Survey>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private val listSurvey: List<Survey>
    private val resources: Resources

    init {
        layoutInflater = LayoutInflater.from(context)
        this.listSurvey = listSurvey
        resources = context.resources
    }

    override fun getCount(): Int {
        return listSurvey.size
    }

    override fun getItem(i: Int): Survey {
        return listSurvey[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: RowSurveysListItemBinding
        val convertView: View
        if (view == null) {
            binding = RowSurveysListItemBinding.inflate(layoutInflater, viewGroup, false)
            convertView = binding.root
            convertView.tag = binding
        } else {
            binding = view.tag as RowSurveysListItemBinding
            convertView = view
        }
        val survey = listSurvey[i]
        binding.tvSurveyName.text = survey.name
        binding.tvDescription.text = survey.description
        binding.ivSyncStatus.visibility = if (survey.isSync) View.VISIBLE else View.INVISIBLE
        return convertView
    }
}