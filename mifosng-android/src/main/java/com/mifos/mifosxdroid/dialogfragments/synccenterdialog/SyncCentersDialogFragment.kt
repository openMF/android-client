package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

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
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncCentersBinding
import com.mifos.objects.group.Center
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import javax.inject.Inject

class SyncCentersDialogFragment : DialogFragment(), SyncCenterDialogMvpView {

    private lateinit var binding: DialogFragmentSyncCentersBinding
    
    @Inject
    lateinit var syncCentersDialogPresenter: SyncCenterDialogPresenter
    private var mCenterList: List<Center>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        if (arguments != null) {
            mCenterList = requireArguments().getParcelableArrayList(Constants.CENTER)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSyncCentersBinding.inflate(inflater, container, false)
        syncCentersDialogPresenter.attachView(this)
        showUI()

        //Start Syncing Centers
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            syncCentersDialogPresenter.startSyncingCenters(mCenterList!!)
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
        binding.pbTotalSyncCenter.max = mCenterList!!.size
        val totalCenters = mCenterList?.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.centers)
        binding.tvTotalCenters.text = totalCenters
        binding.tvSyncFailed.text = 0.toString()
    }

    override fun showSyncingCenter(centerName: String) {
        binding.tvSyncingCenter.text = centerName
        binding.tvSyncingCenter.text = centerName
    }

    override fun showSyncedFailedCenters(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    override fun updateSingleSyncCenterProgressBar(count: Int) {
        binding.pbSyncCenter.progress = count
    }

    override fun updateTotalSyncCenterProgressBarAndCount(count: Int) {
        binding.pbTotalSyncCenter.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + mCenterList!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    override var maxSingleSyncCenterProgressBar: Int
        get() = binding.pbSyncCenter.max
        set(value) {
            binding.pbSyncCenter.max = value
        }

    override fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity,
            resources.getString(R.string.error_network_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showCentersSyncSuccessfully() {
        binding.btnCancel.visibility = View.INVISIBLE
        binding.btnHide.text = resources.getString(R.string.dialog_action_ok)
    }

    override val isOnline: Boolean
        get() = isOnline(requireContext())

    override fun setGroupSyncProgressBarMax(count: Int) {
        binding.pbSyncGroup.max = count
    }

    override fun updateGroupSyncProgressBar(i: Int) {
        binding.pbSyncGroup.progress = i
    }

    override fun setClientSyncProgressBarMax(count: Int) {
        binding.pbSyncClient.max = count
    }

    override fun updateClientSyncProgressBar(i: Int) {
        binding.pbSyncClient.progress = i
    }

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
        syncCentersDialogPresenter.detachView()
    }

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