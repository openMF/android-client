package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

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
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncClientsBinding
import com.mifos.objects.client.Client
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/08/16.
 */
class SyncClientsDialogFragment : DialogFragment(), SyncClientsDialogMvpView {

    private lateinit var binding: DialogFragmentSyncClientsBinding

    @Inject
    lateinit var mSyncClientsDialogPresenter: SyncClientsDialogPresenter
    private var mClientList: List<Client>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        if (arguments != null) {
            mClientList = requireArguments().getParcelableArrayList(Constants.CLIENT)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSyncClientsBinding.inflate(inflater, container, false)
        mSyncClientsDialogPresenter.attachView(this)
        showUI()

        //Start Syncing Clients
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            mSyncClientsDialogPresenter.startSyncingClients(mClientList!!)
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
        binding.pbTotalSyncClient.max = mClientList!!.size
        val totalClients = mClientList?.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.clients)
        binding.tvTotalClients.text = totalClients
        binding.tvSyncFailed.text = 0.toString()
    }

    override fun showSyncingClient(clientName: String?) {
        binding.tvSyncingClient.text = clientName
        binding.tvClientName.text = clientName
    }

    override fun showSyncedFailedClients(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    override fun updateSingleSyncClientProgressBar(count: Int) {
        binding.pbSyncClient.progress = count
    }

    override fun updateTotalSyncClientProgressBarAndCount(count: Int) {
        binding.pbTotalSyncClient.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + mClientList!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    override var maxSingleSyncClientProgressBar: Int
        get() = binding.pbSyncClient.max
        set(total) {
            binding.pbSyncClient.max = total
        }

    override fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity,
            resources.getString(R.string.error_network_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showClientsSyncSuccessfully() {
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
        mSyncClientsDialogPresenter.detachView()
    }

    companion object {
        val LOG_TAG = SyncClientsDialogFragment::class.java.simpleName
        fun newInstance(client: List<Client?>?): SyncClientsDialogFragment {
            val syncClientsDialogFragment = SyncClientsDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.CLIENT, client as ArrayList<out Parcelable?>?)
            syncClientsDialogFragment.arguments = args
            return syncClientsDialogFragment
        }
    }
}