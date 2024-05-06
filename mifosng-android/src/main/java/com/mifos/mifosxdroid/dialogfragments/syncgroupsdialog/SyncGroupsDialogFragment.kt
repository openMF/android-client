package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.objects.group.Group
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncGroupsBinding
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 11/09/16.
 */
@AndroidEntryPoint
class SyncGroupsDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentSyncGroupsBinding

    private lateinit var viewModel: SyncGroupsDialogViewModel

    private var groups: List<Group>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            groups = requireArguments().getParcelableArrayList(Constants.GROUPS)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSyncGroupsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncGroupsDialogViewModel::class.java]
        showUI()

        //Start Syncing Groups
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            viewModel.startSyncingGroups(groups!!)
        } else {
            showNetworkIsNotAvailable()
            requireActivity().supportFragmentManager.popBackStack()
        }

        viewModel.syncGroupsDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncGroupsDialogUiState.DismissDialog -> dismissDialog()
                is SyncGroupsDialogUiState.SetClientSyncProgressBarMax -> {
                    showProgressbar(false)
                    setClientSyncProgressBarMax(it.size)
                }

                is SyncGroupsDialogUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncGroupsDialogUiState.ShowGroupsSyncSuccessfully -> {
                    showProgressbar(false)
                    showGroupsSyncSuccessfully()
                }

                is SyncGroupsDialogUiState.ShowNetworkIsNotAvailable -> {
                    showProgressbar(false)
                    showNetworkIsNotAvailable()
                }

                is SyncGroupsDialogUiState.ShowProgressbar -> showProgressbar(true)
                is SyncGroupsDialogUiState.ShowSyncedFailedGroups -> {
                    showProgressbar(false)
                    showSyncedFailedGroups(it.size)
                }

                is SyncGroupsDialogUiState.ShowSyncingGroup -> {
                    showProgressbar(false)
                    showSyncingGroup(it.groupName)
                }

                is SyncGroupsDialogUiState.UpdateClientSyncProgressBar -> {
                    showProgressbar(false)
                    updateClientSyncProgressBar(it.index)
                }

                is SyncGroupsDialogUiState.UpdateSingleSyncGroupProgressBar -> {
                    showProgressbar(false)
                    updateSingleSyncGroupProgressBar(it.index)
                }

                is SyncGroupsDialogUiState.UpdateTotalSyncGroupProgressBarAndCount -> {
                    showProgressbar(false)
                    updateTotalSyncGroupProgressBarAndCount(it.total)
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
        binding.pbSyncGroup.max = groups!!.size
        val totalGroups = groups?.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.groups)
        binding.tvTotalGroups.text = totalGroups
        binding.tvSyncFailed.text = 0.toString()
    }

    private fun showSyncingGroup(groupName: String?) {
        binding.tvSyncingGroup.text = groupName
        binding.tvGroupName.text = groupName
    }

    private fun showSyncedFailedGroups(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    private fun setClientSyncProgressBarMax(count: Int) {
        binding.pbSyncClient.max = count
    }

    private fun updateClientSyncProgressBar(i: Int) {
        binding.pbSyncClient.progress = i
    }

    private fun updateSingleSyncGroupProgressBar(count: Int) {
        binding.pbSyncGroup.progress = count
    }

    private fun updateTotalSyncGroupProgressBarAndCount(count: Int) {
        binding.pbTotalSyncGroup.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + groups!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    private var maxSingleSyncGroupProgressBar: Int
        get() = binding.pbSyncGroup.max
        set(total) {
            binding.pbSyncGroup.max = total
        }

    private fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity, resources
                .getString(R.string.error_network_not_available), Toast.LENGTH_SHORT
        ).show()
    }

    private fun showGroupsSyncSuccessfully() {
        binding.btnCancel.visibility = View.INVISIBLE
        binding.btnHide.text = resources.getString(R.string.dialog_action_ok)
    }

    private val isOnline: Boolean
        get() = isOnline(requireActivity())

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
        fun newInstance(groups: List<Group>?): SyncGroupsDialogFragment {
            val syncGroupsDialogFragment = SyncGroupsDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.GROUPS, groups as ArrayList<out Parcelable?>?)
            syncGroupsDialogFragment.arguments = args
            return syncGroupsDialogFragment
        }
    }
}