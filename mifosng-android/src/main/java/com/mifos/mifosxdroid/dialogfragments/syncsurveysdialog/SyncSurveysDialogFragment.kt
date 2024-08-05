package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

/*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.objects.survey.Survey
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncSurveysBinding
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncSurveysDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentSyncSurveysBinding

    private lateinit var viewModel: SyncSurveysDialogViewModel

    private var mSurveyList: List<Survey>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        mSurveyList = ArrayList()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSyncSurveysBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncSurveysDialogViewModel::class.java]
        //Start Syncing Surveys
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            viewModel.loadSurveyList()
        } else {
            showNetworkIsNotAvailable()
            fragmentManager?.popBackStack()
        }

        viewModel.syncSurveysDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncSurveysDialogUiState.DismissDialog -> hideDialog()
                is SyncSurveysDialogUiState.SetQuestionSyncProgressBarMax -> {
                    showProgressbar(false)
                    setQuestionSyncProgressBarMax(it.total)
                }

                is SyncSurveysDialogUiState.SetResponseSyncProgressBarMax -> {
                    showProgressbar(false)
                    setResponseSyncProgressBarMax(it.total)
                }

                is SyncSurveysDialogUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncSurveysDialogUiState.ShowNetworkIsNotAvailable -> {
                    showProgressbar(false)
                    showNetworkIsNotAvailable()
                }

                is SyncSurveysDialogUiState.ShowProgressbar -> showProgressbar(true)
                is SyncSurveysDialogUiState.ShowSurveysSyncSuccessfully -> {
                    showProgressbar(false)
                    showSurveysSyncSuccessfully()
                }

                is SyncSurveysDialogUiState.ShowSyncedFailedSurveys -> {
                    showProgressbar(false)
                    showSyncedFailedSurveys(it.failedCount)
                }

                is SyncSurveysDialogUiState.ShowSyncingSurvey -> {
                    showProgressbar(false)
                    showSyncingSurvey(it.surveyName)
                }

                is SyncSurveysDialogUiState.ShowUI -> {
                    showProgressbar(false)
                    showUI()
                }

                is SyncSurveysDialogUiState.UpdateQuestionSyncProgressBar -> {
                    showProgressbar(false)
                    updateQuestionSyncProgressBar(it.index)
                }

                is SyncSurveysDialogUiState.UpdateResponseSyncProgressBar -> {
                    showProgressbar(false)
                    updateResponseSyncProgressBar(it.index)
                }

                is SyncSurveysDialogUiState.UpdateSingleSyncSurveyProgressBar -> {
                    showProgressbar(false)
                    updateSingleSyncSurveyProgressBar(it.index)
                }

                is SyncSurveysDialogUiState.UpdateSurveyList -> {
                    showProgressbar(false)
                    updateSurveyList(it.surveys)
                }

                is SyncSurveysDialogUiState.UpdateTotalSyncSurveyProgressBarAndCount -> {
                    showProgressbar(false)
                    updateTotalSyncSurveyProgressBarAndCount(it.total)
                }

                SyncSurveysDialogUiState.Initial -> Unit
            }
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

    private fun updateSurveyList(surveyList: List<Survey>) {
        mSurveyList = surveyList
    }

    private fun showUI() {
        binding.pbTotalSyncSurvey.max = mSurveyList!!.size
        val totalSurveys = mSurveyList!!.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.surveys)
        binding.tvTotalSurveys.text = totalSurveys
        binding.tvSyncFailed.text = 0.toString()
    }

    private fun showSyncingSurvey(surveyName: String?) {
        binding.tvSyncingSurvey.text = surveyName
        binding.tvSurveyName.text = surveyName
    }

    private fun showSyncedFailedSurveys(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    private fun setQuestionSyncProgressBarMax(count: Int) {
        binding.pbSyncQuestion.max = count
    }

    private fun setResponseSyncProgressBarMax(count: Int) {
        binding.pbSyncResponse.max = count
    }

    private fun updateSingleSyncSurveyProgressBar(count: Int) {
        binding.pbSyncSurvey.progress = count
    }

    private fun updateQuestionSyncProgressBar(i: Int) {
        binding.pbSyncQuestion.progress = i
    }

    private fun updateResponseSyncProgressBar(i: Int) {
        binding.pbSyncResponse.progress = i
    }

    private fun updateTotalSyncSurveyProgressBarAndCount(count: Int) {
        binding.pbTotalSyncSurvey.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + mSurveyList!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    private var maxSingleSyncSurveyProgressBar: Int
        get() = binding.pbSyncSurvey.max
        set(total) {
            binding.pbSyncSurvey.max = total
        }

    private fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity,
            resources.getString(R.string.error_network_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showSurveysSyncSuccessfully() {
        binding.btnCancel.visibility = View.INVISIBLE
        dismissDialog()
        Toast.makeText(activity, R.string.sync_success, Toast.LENGTH_SHORT).show()
    }

    private val isOnline: Boolean
        get() = activity?.let { isOnline(it) } == true

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun showDialog() {
        dialog?.show()
    }

    private fun hideDialog() {
        dialog?.hide()
    }

    private fun showError(s: String) {
        show(binding.root, s)
    }

    private fun showProgressbar(b: Boolean) {}

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
 */