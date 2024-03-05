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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.survey.Survey
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SurveyListAdapter
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentSurveyListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * This Class shows the List of Surveys after fetching the surveys list from the REST API :
 * https://demo.openmf.org/fineract-provider/api/v1/surveys
 *
 *
 * Created by Nasim Banu on 27,January,2016.
 */
@AndroidEntryPoint
class SurveyListFragment : ProgressableFragment() {

    private lateinit var binding: FragmentSurveyListBinding
    private val arg: SurveyListFragmentArgs by navArgs()

    private lateinit var viewModel: SurveyListViewModel

    private var mListener: OnFragmentInteractionListener? = null
    private var clientId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientId = arg.clientId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurveyListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SurveyListViewModel::class.java]
        setToolbarTitle(getString(R.string.surveys))
        viewModel.loadSurveyList()

        viewModel.surveyListUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SurveyListUiState.ShowAllSurvey -> {
                    showProgressbar(false)
                    showAllSurvey(it.syncSurvey)
                }

                is SurveyListUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is SurveyListUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    private fun showAllSurvey(surveys: List<Survey>) {
        if (surveys.isEmpty()) {
            binding.tvSurveyName.text =
                resources.getString(R.string.no_survey_available_for_client)
        } else {
            val surveyListAdapter = SurveyListAdapter(requireActivity(), surveys)
            binding.lvSurveysList.adapter = surveyListAdapter
            binding.lvSurveysList.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, position, l ->
                    mListener?.loadSurveyQuestion(
                        surveys[position],
                        clientId
                    )
                }
        }
    }

    private fun showFetchingError(errorMessage: Int) {
        Toaster.show(binding.root, resources.getString(errorMessage))
    }

    private fun showProgressbar(b: Boolean) {
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
            throw ClassCastException(
                activity.toString() + " must implement " +
                        "OnFragmentInteractionListener"
            )
        }
    }

    interface OnFragmentInteractionListener {
        fun loadSurveyQuestion(survey: Survey?, clientId: Int)
    }
}