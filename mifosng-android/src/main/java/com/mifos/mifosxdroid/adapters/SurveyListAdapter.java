/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.survey.Survey;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class SurveyListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Survey> listSurvey;
    private Resources resources;

    public SurveyListAdapter(Context context, List<Survey> listSurvey) {

        layoutInflater = LayoutInflater.from(context);
        this.listSurvey = listSurvey;
        resources = context.getResources();
    }

    @Override
    public int getCount() {
        return this.listSurvey.size();
    }

    @Override
    public Survey getItem(int i) {
        return this.listSurvey.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_surveys_list_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Survey survey = listSurvey.get(i);
        viewHolder.tv_survey_name.setText(survey.getName());
        viewHolder.tv_description.setText(survey.getDescription());
        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.tv_survey_name)
        TextView tv_survey_name;
        @InjectView(R.id.tv_description)
        TextView tv_description;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}

