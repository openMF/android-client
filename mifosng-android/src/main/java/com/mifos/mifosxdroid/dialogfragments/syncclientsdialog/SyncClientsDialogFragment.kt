package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.objects.client.Client
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentSyncClientsBinding
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 08/08/16.
 */
@AndroidEntryPoint
class SyncClientsDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentSyncClientsBinding

    private lateinit var viewModel: SyncClientsDialogViewModel

    private var mClientList: List<Client>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
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
        viewModel = ViewModelProvider(this)[SyncClientsDialogViewModel::class.java]
        showUI()

        //Start Syncing Clients
        if (isOnline && userStatus == Constants.USER_ONLINE) {
            viewModel.startSyncingClients(mClientList!!)
        } else {
            showNetworkIsNotAvailable()
            requireActivity().supportFragmentManager.popBackStack()
        }

        viewModel.syncClientsDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncClientsDialogUiState.DismissDialog -> dismissDialog()

                is SyncClientsDialogUiState.ShowClientsSyncSuccessfully -> showClientsSyncSuccessfully()

                is SyncClientsDialogUiState.ShowError -> showError(it.message)

                is SyncClientsDialogUiState.ShowNetworkIsNotAvailable -> showNetworkIsNotAvailable()

                is SyncClientsDialogUiState.ShowSyncedFailedClients -> showSyncedFailedClients(it.total)

                is SyncClientsDialogUiState.ShowSyncingClient -> showSyncingClient(it.clientName)

                is SyncClientsDialogUiState.UpdateSingleSyncClientProgressBar -> updateSingleSyncClientProgressBar(
                    it.total
                )

                is SyncClientsDialogUiState.UpdateTotalSyncClientProgressBarAndCount -> updateTotalSyncClientProgressBarAndCount(
                    it.total
                )
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
        binding.pbTotalSyncClient.max = mClientList!!.size
        val totalClients = mClientList?.size.toString() + resources.getString(R.string.space) +
                resources.getString(R.string.clients)
        binding.tvTotalClients.text = totalClients
        binding.tvSyncFailed.text = 0.toString()
    }

    private fun showSyncingClient(clientName: String?) {
        binding.tvSyncingClient.text = clientName
        binding.tvClientName.text = clientName
    }

    private fun showSyncedFailedClients(failedCount: Int) {
        binding.tvSyncFailed.text = failedCount.toString()
    }

    private fun updateSingleSyncClientProgressBar(count: Int) {
        binding.pbSyncClient.progress = count
    }

    private fun updateTotalSyncClientProgressBarAndCount(count: Int) {
        binding.pbTotalSyncClient.progress = count
        val totalSyncCount = resources
            .getString(R.string.space) + count + resources
            .getString(R.string.slash) + mClientList!!.size
        binding.tvTotalProgress.text = totalSyncCount
    }

    private var maxSingleSyncClientProgressBar: Int
        get() = binding.pbSyncClient.max
        set(total) {
            binding.pbSyncClient.max = total
        }

    private fun showNetworkIsNotAvailable() {
        Toast.makeText(
            activity,
            resources.getString(R.string.error_network_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showClientsSyncSuccessfully() {
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
        val LOG_TAG = SyncClientsDialogFragment::class.java.simpleName
        fun newInstance(client: ArrayList<Client>): SyncClientsDialogFragment {
            val syncClientsDialogFragment = SyncClientsDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.CLIENT, client)
            syncClientsDialogFragment.arguments = args
            return syncClientsDialogFragment
        }
    }
}