package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncSurveysBinding
import com.mifos.objects.survey.Survey
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import javax.inject.Inject

class SyncSurveysDialogFragment : DialogFragment(), SyncSurveysDialogMvpView {

    private lateinit var binding: DialogFragmentSyncSurveysBinding

    @Inject
    lateinit var mSyncSurveysDialogPresenter: SyncSurveysDialogPresenter
    private var mSurveyList: List<Survey>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MifosBaseActivity).activityComponent!!.inject(this)
        mSurveyList = ArrayList()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSyncSurveysBinding.inflate(inflater, container, false)
        mSyncSurveysDialogPresenter.attachView(this)
        //Start Syncing Surveys
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            mSyncSurveysDialogPresenter.loadSurveyList()
        } else {
            showNetworkIsNotAvailable()
            fragmentManager?.popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            onClickCancelButton()
        }

        binding.btnHide.setOnClickListener {
            onClickHideButton()
        }
    }

    private fun onClickCancelButton() {
        dismissDialog()
    }

    private fun onClickHideButton() {
        if (binding.btnHide.text == resources.getString(R.string.dialog_action_ok)) {
            dismissDialog()
        } else {
            hideDialog()
        }
    }

    override fun updateSurveyList(surveyList: List<Survey>) {
        mSurveyList = surveyList
    }

    override fun showUI() {
        binding.pbTotalSyncSurvey.max = mSurveyList!!.size
        val totalSurveys = mSurveyList!!.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.surveys)
        binding.tvTotalSurveys.text = totalSurveys
        binding.tvSyncFailed.text = 0.toString()
    }

    override fun showSyncingSurvey(surveyName: String?) {
        binding.tvSyncingSurvey.text = surveyName
        binding.tvSurveyName.text = surveyName
    }

    override fun showSyncedFailedSurveys(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    override fun setQuestionSyncProgressBarMax(count: Int) {
        binding.pbSyncQuestion.max = count
    }

    override fun setResponseSyncProgressBarMax(count: Int) {
        binding.pbSyncResponse.max = count
    }

    override fun updateSingleSyncSurveyProgressBar(count: Int) {
        binding.pbSyncSurvey.progress = count
    }

    override fun updateQuestionSyncProgressBar(i: Int) {
        binding.pbSyncQuestion.progress = i
    }

    override fun updateResponseSyncProgressBar(i: Int) {
        binding.pbSyncResponse.progress = i
    }

    override fun updateTotalSyncSurveyProgressBarAndCount(count: Int) {
        binding.pbTotalSyncSurvey.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + mSurveyList!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    override var maxSingleSyncSurveyProgressBar: Int
        get() = binding.pbSyncSurvey.max
        set(total) {
            binding.pbSyncSurvey.max = total
        }

    override fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity,
            resources.getString(R.string.error_network_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showSurveysSyncSuccessfully() {
        binding.btnCancel.visibility = View.INVISIBLE
        dismissDialog()
        Toast.makeText(activity, R.string.sync_success, Toast.LENGTH_SHORT).show()
    }

    override val isOnline: Boolean
        get() = isOnline(activity)

    override fun dismissDialog() {
        dialog?.dismiss()
    }

    override fun showDialog() {
        dialog?.show()
    }

    override fun hideDialog() {
        dialog?.hide()
    }

    override fun showError(s: Int) {
        show(binding.root, s)
    }

    override fun showProgressbar(b: Boolean) {}
    override fun onDestroyView() {
        super.onDestroyView()
        mSyncSurveysDialogPresenter.detachView()
    }

    companion object {
        val LOG_TAG = SyncSurveysDialogFragment::class.java.simpleName
        fun newInstance(): SyncSurveysDialogFragment {
            val syncSurveysDialogFragment = SyncSurveysDialogFragment()
            val args = Bundle()
            syncSurveysDialogFragment.arguments = args
            return syncSurveysDialogFragment
        }
    }
}