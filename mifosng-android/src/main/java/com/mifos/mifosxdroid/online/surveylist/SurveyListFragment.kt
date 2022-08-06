/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.surveylist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SurveyListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.survey.Survey
import com.mifos.utils.Constants
import javax.inject.Inject

/**
 * This Class shows the List of Surveys after fetching the surveys list from the REST API :
 * https://demo.openmf.org/fineract-provider/api/v1/surveys
 *
 *
 * Created by Nasim Banu on 27,January,2016.
 */
class SurveyListFragment : ProgressableFragment(), SurveyListMvpView {
    @JvmField
    @BindView(R.id.lv_surveys_list)
    var lv_surveys_list: ListView? = null

    @JvmField
    @BindView(R.id.tv_survey_name)
    var surveySelectText: TextView? = null

    @JvmField
    @Inject
    var mSurveyListPresenter: SurveyListPresenter? = null
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var rootView: View
    private var clientId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            clientId = requireArguments().getInt(Constants.CLIENT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_survey_list, container, false)
        ButterKnife.bind(this, rootView)
        mSurveyListPresenter!!.attachView(this)
        setToolbarTitle(getString(R.string.surveys))
        mSurveyListPresenter!!.loadSurveyList()
        return rootView
    }

    override fun showAllSurvey(surveys: List<Survey?>?) {
        if (surveys!!.size == 0) {
            surveySelectText!!.text = resources.getString(R.string.no_survey_available_for_client)
        } else {
            val surveyListAdapter = SurveyListAdapter(activity, surveys)
            lv_surveys_list!!.adapter = surveyListAdapter
            lv_surveys_list!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l -> mListener!!.loadSurveyQuestion(surveys?.get(position), clientId) }
        }
    }

    override fun showFetchingError(errorMessage: Int) {
        Toaster.show(rootView, resources.getString(errorMessage))
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mItem_search) requireActivity().finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = if (context is Activity) context else null
        mListener = try {
            activity as OnFragmentInteractionListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSurveyListPresenter!!.detachView()
    }

    interface OnFragmentInteractionListener {
        fun loadSurveyQuestion(survey: Survey?, clientId: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance(clientId: Int): SurveyListFragment {
            val fragment = SurveyListFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.CLIENT_ID, clientId)
            fragment.arguments = bundle
            return fragment
        }
    }
}