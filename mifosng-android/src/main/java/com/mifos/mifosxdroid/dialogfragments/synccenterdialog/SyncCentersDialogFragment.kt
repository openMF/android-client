package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.objects.group.Center
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncCentersBinding
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncCentersDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentSyncCentersBinding

    private lateinit var viewModel: SyncCentersDialogViewModel

    private lateinit var mCenterList: List<Center>
    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            mCenterList = requireArguments().getParcelableArrayList(Constants.CENTER) ?: ArrayList()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSyncCentersBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncCentersDialogViewModel::class.java]
        showUI()

        //Start Syncing Centers
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            viewModel.startSyncingCenters(mCenterList)
        } else {
            showNetworkIsNotAvailable()
            requireActivity().supportFragmentManager.popBackStack()
        }

        viewModel.syncCentersDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncCentersDialogUiState.DismissDialog -> dismissDialog()
                is SyncCentersDialogUiState.SetClientSyncProgressBarMax -> {
                    showProgressbar(false)
                    setClientSyncProgressBarMax(it.total)
                }

                is SyncCentersDialogUiState.SetGroupSyncProgressBarMax -> {
                    showProgressbar(false)
                    setGroupSyncProgressBarMax(it.size)
                }

                is SyncCentersDialogUiState.ShowCentersSyncSuccessfully -> {
                    showProgressbar(false)
                    showCentersSyncSuccessfully()
                }

                is SyncCentersDialogUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncCentersDialogUiState.ShowNetworkIsNotAvailable -> {
                    showProgressbar(false)
                    showNetworkIsNotAvailable()
                }

                is SyncCentersDialogUiState.ShowProgressbar -> showProgressbar(true)
                is SyncCentersDialogUiState.ShowSyncedFailedCenters -> {
                    showProgressbar(false)
                    showSyncedFailedCenters(it.size)
                }

                is SyncCentersDialogUiState.ShowSyncingCenter -> {
                    showProgressbar(false)
                    showSyncingCenter(it.centerName)
                }

                is SyncCentersDialogUiState.UpdateClientSyncProgressBar -> {
                    showProgressbar(false)
                    updateClientSyncProgressBar(it.index)
                }

                is SyncCentersDialogUiState.UpdateGroupSyncProgressBar -> {
                    showProgressbar(false)
                    updateGroupSyncProgressBar(it.index)
                }

                is SyncCentersDialogUiState.UpdateSingleSyncCenterProgressBar -> {
                    showProgressbar(false)
                    updateSingleSyncCenterProgressBar(it.total)
                }

                is SyncCentersDialogUiState.UpdateTotalSyncCenterProgressBarAndCount -> {
                    showProgressbar(false)
                    updateTotalSyncCenterProgressBarAndCount(it.total)
                }
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

    private fun showUI() {
        binding.pbTotalSyncCenter.max = mCenterList.size
        val totalCenters = mCenterList.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.centers)
        binding.tvTotalCenters.text = totalCenters
        binding.tvSyncFailed.text = 0.toString()
    }

    private fun showSyncingCenter(centerName: String) {
        binding.tvSyncingCenter.text = centerName
        binding.tvSyncingCenter.text = centerName
    }

    private fun showSyncedFailedCenters(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    private fun updateSingleSyncCenterProgressBar(count: Int) {
        binding.pbSyncCenter.progress = count
    }

    private fun updateTotalSyncCenterProgressBarAndCount(count: Int) {
        binding.pbTotalSyncCenter.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + mCenterList.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    private var maxSingleSyncCenterProgressBar: Int
        get() = binding.pbSyncCenter.max
        set(value) {
            binding.pbSyncCenter.max = value
        }

    private fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity,
            resources.getString(R.string.error_network_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showCentersSyncSuccessfully() {
        binding.btnCancel.visibility = View.INVISIBLE
        binding.btnHide.text = resources.getString(R.string.dialog_action_ok)
    }

    private val isOnline: Boolean
        get() = isOnline(requireContext())

    private fun setGroupSyncProgressBarMax(count: Int) {
        binding.pbSyncGroup.max = count
    }

    private fun updateGroupSyncProgressBar(i: Int) {
        binding.pbSyncGroup.progress = i
    }

    private fun setClientSyncProgressBarMax(count: Int) {
        binding.pbSyncClient.max = count
    }

    private fun updateClientSyncProgressBar(i: Int) {
        binding.pbSyncClient.progress = i
    }

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
        fun newInstance(center: List<Center>?): SyncCentersDialogFragment {
            val syncCentersDialogFragment = SyncCentersDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.CENTER, center as ArrayList<out Parcelable?>?)
            syncCentersDialogFragment.arguments = args
            return syncCentersDialogFragment
        }
    }
}