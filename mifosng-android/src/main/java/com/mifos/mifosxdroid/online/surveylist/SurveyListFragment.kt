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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.survey.Survey
import com.mifos.feature.client.clientSurveyList.SurveyListScreen
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * This Class shows the List of Surveys after fetching the surveys list from the REST API :
 * https://demo.openmf.org/fineract-provider/api/v1/surveys
 *
 *
 * Created by Nasim Banu on 27,January,2016.
 */
@AndroidEntryPoint
class SurveyListFragment : MifosBaseFragment() {

    private val arg: SurveyListFragmentArgs by navArgs()

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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SurveyListScreen(
                    navigateBack = { findNavController().popBackStack() },
                    onCardClicked = { position, surveys ->
                        openSurvey(position, surveys)
                    }
                )
            }
        }
    }

    fun openSurvey(position: Int, surveys: List<Survey>) {
        mListener?.loadSurveyQuestion(
            surveys[position],
            clientId
        )
    }

    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        toolbar?.visibility = View.VISIBLE
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