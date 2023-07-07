package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncGroupsBinding
import com.mifos.objects.group.Group
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 11/09/16.
 */
class SyncGroupsDialogFragment : DialogFragment(), SyncGroupsDialogMvpView {

    private lateinit var binding: DialogFragmentSyncGroupsBinding

    @Inject
    lateinit var syncGroupsDialogPresenter: SyncGroupsDialogPresenter
    private var groups: List<Group>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MifosBaseActivity).activityComponent?.inject(this)
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
        syncGroupsDialogPresenter.attachView(this)
        showUI()

        //Start Syncing Groups
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            syncGroupsDialogPresenter.startSyncingGroups(groups!!)
        } else {
            showNetworkIsNotAvailable()
            requireActivity().supportFragmentManager.popBackStack()
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

    override fun showUI() {
        binding.pbSyncGroup.max = groups!!.size
        val totalGroups = groups?.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.groups)
        binding.tvTotalGroups.text = totalGroups
        binding.tvSyncFailed.text = 0.toString()
    }

    override fun showSyncingGroup(groupName: String?) {
        binding.tvSyncingGroup.text = groupName
        binding.tvGroupName.text = groupName
    }

    override fun showSyncedFailedGroups(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    override fun setClientSyncProgressBarMax(count: Int) {
        binding.pbSyncClient.max = count
    }

    override fun updateClientSyncProgressBar(i: Int) {
        binding.pbSyncClient.progress = i
    }

    override fun updateSingleSyncGroupProgressBar(count: Int) {
        binding.pbSyncGroup.progress = count
    }

    override fun updateTotalSyncGroupProgressBarAndCount(count: Int) {
        binding.pbTotalSyncGroup.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + groups!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    override var maxSingleSyncGroupProgressBar: Int
        get() = binding.pbSyncGroup.max
        set(total) {
            binding.pbSyncGroup.max = total
        }

    override fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity, resources
                .getString(R.string.error_network_not_available), Toast.LENGTH_SHORT
        ).show()
    }

    override fun showGroupsSyncSuccessfully() {
        binding.btnCancel.visibility = View.INVISIBLE
        binding.btnHide.text = resources.getString(R.string.dialog_action_ok)
    }

    override val isOnline: Boolean
        get() = isOnline(requireActivity())

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
        syncGroupsDialogPresenter.detachView()
    }

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