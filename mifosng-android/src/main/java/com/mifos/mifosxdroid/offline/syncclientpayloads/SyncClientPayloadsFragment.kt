package com.mifos.mifosxdroid.offline.syncclientpayloads

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.core.objects.client.ClientPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SyncPayloadsAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint

/**
 * This Class for Syncing the clients that is created in offline mode.
 * For syncing the clients user make sure that he/she is in the online mode.
 *
 *
 * Created by Rajan Maurya on 08/07/16.
 */
@AndroidEntryPoint
class SyncClientPayloadsFragment : MifosBaseFragment(), DialogInterface.OnClickListener {
    val LOG_TAG = javaClass.simpleName

    private lateinit var binding: FragmentSyncpayloadBinding

    private lateinit var viewModel: SyncClientPayloadsViewModel

    var clientPayloads: MutableList<ClientPayload>? = null
    var mSyncPayloadsAdapter: SyncPayloadsAdapter? = null
    var mClientSyncIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientPayloads = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncClientPayloadsViewModel::class.java]
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSyncPayload.layoutManager = mLayoutManager
        binding.rvSyncPayload.setHasFixedSize(true)
        viewModel.loadDatabaseClientPayload()
        /**
         * Loading All Client Payloads from Database
         */
        binding.swipeContainer.setColorSchemeColors(
            *requireActivity().resources.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener {
            viewModel.loadDatabaseClientPayload()
            if (binding.swipeContainer.isRefreshing) binding.swipeContainer.isRefreshing = false
        }

        viewModel.syncClientPayloadsUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncClientPayloadsUiState.ShowClientPayloadUpdated -> {
                    showProgressbar(false)
                    showClientPayloadUpdated(it.clientPayload)
                }

                is SyncClientPayloadsUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncClientPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads -> {
                    showProgressbar(false)
                    showPayloadDeletedAndUpdatePayloads(it.clientPayloads)
                }

                is SyncClientPayloadsUiState.ShowPayloads -> {
                    showProgressbar(false)
                    showPayloads(it.clientPayloads)
                }

                is SyncClientPayloadsUiState.ShowProgressbar -> showProgressbar(true)

                is SyncClientPayloadsUiState.ShowSyncResponse -> {
                    showProgressbar(false)
                    showSyncResponse()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noPayloadIcon.setOnClickListener {
            reloadOnError()
        }
    }

    /**
     * Show when Database response is null or failed to fetch the client payload
     * Onclick Send Fresh Request for Client Payload.
     */

    private fun reloadOnError() {
        binding.llError.visibility = View.GONE
        viewModel.loadDatabaseClientPayload()
    }

    /**
     * This method is showing the client payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param clientPayload
     */
    private fun showPayloads(clientPayload: List<ClientPayload>) {
        clientPayloads = clientPayload as MutableList<ClientPayload>
        if (clientPayload.isEmpty()) {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = requireActivity()
                .resources.getString(R.string.no_client_payload_to_sync)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        } else {
            mSyncPayloadsAdapter = SyncPayloadsAdapter(requireActivity(), clientPayload)
            binding.rvSyncPayload.adapter = mSyncPayloadsAdapter
        }
    }

    /**
     * Showing Error when failed to fetch client payload from Database
     *
     * @param s Error String
     */
    private fun showError(stringId: String) {
        binding.llError.visibility = View.VISIBLE
        val message =
            stringId + requireActivity().resources.getString(R.string.click_to_refresh)
        binding.noPayloadText.text = message
        show(binding.root, stringId)
    }

    /**
     * Called Whenever any client payload is synced to server.
     * then first delete that client from database and sync again from Database
     * and update the recyclerView
     */
    private fun showSyncResponse() {
        clientPayloads?.get(mClientSyncIndex)?.id?.let {
            clientPayloads?.get(mClientSyncIndex)?.clientCreationTime?.let { it1 ->
                viewModel.deleteAndUpdateClientPayload(
                    it,
                    it1
                )
            }
        }
    }

    /**
     * Called when client payload synced is failed then there can be some problem with the
     * client payload data example externalId or phone number already exist.
     * If client synced failed the there is no need to delete from the Database and increase
     * the mClientSyncIndex by one and sync the next client payload
     */
    private fun showClientSyncFailed(errorMessage: String) {
        val clientPayload = clientPayloads!![mClientSyncIndex]
        clientPayload.errorMessage = errorMessage
        viewModel.updateClientPayload(clientPayload)
    }

    /**
     * This Method will called whenever user trying to sync the client payload in
     * offline mode.
     */
    private fun showOfflineModeDialog() {
        MaterialDialog.Builder().init(activity)
            .setTitle(R.string.offline_mode)
            .setMessage(R.string.dialog_message_offline_sync_alert)
            .setPositiveButton(R.string.dialog_action_go_online, this)
            .setNegativeButton(R.string.dialog_action_cancel, this)
            .createMaterialDialog()
            .show()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> {}
            DialogInterface.BUTTON_POSITIVE -> {
                userStatus = Constants.USER_ONLINE
                if (clientPayloads!!.isNotEmpty()) {
                    mClientSyncIndex = 0
                    syncClientPayload()
                } else {
                    show(
                        binding.root,
                        requireActivity().resources.getString(R.string.nothing_to_sync)
                    )
                }
            }

            else -> {}
        }
    }

    /**
     * This method will update client Payload in List<ClientPayload> after adding Error message in
     * database
     *
     * @param clientPayload
    </ClientPayload> */
    private fun showClientPayloadUpdated(clientPayload: ClientPayload) {
        clientPayloads?.set(mClientSyncIndex, clientPayload)
        mSyncPayloadsAdapter!!.notifyDataSetChanged()
        mClientSyncIndex += 1
        if (clientPayloads!!.size != mClientSyncIndex) {
            syncClientPayload()
        }
    }

    /**
     * This is called whenever a client  payload is synced and synced client payload is
     * deleted from the Database and update UI
     *
     * @param clients
     */
    private fun showPayloadDeletedAndUpdatePayloads(clients: List<ClientPayload>) {
        mClientSyncIndex = 0
        clientPayloads?.clear()
        clientPayloads = clients as MutableList<ClientPayload>
        mSyncPayloadsAdapter!!.setClientPayload(clientPayloads!!)
        if (clientPayloads!!.isNotEmpty()) {
            syncClientPayload()
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = requireActivity()
                .resources.getString(R.string.all_clients_synced)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressBar()
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sync, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sync) {
            when (userStatus) {
                false -> if (clientPayloads!!.isNotEmpty()) {
                    mClientSyncIndex = 0
                    syncClientPayload()
                } else {
                    show(
                        binding.root,
                        requireActivity().resources.getString(R.string.nothing_to_sync)
                    )
                }

                true -> showOfflineModeDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun syncClientPayload() {
        for (i in clientPayloads!!.indices) {
            if (clientPayloads!![i].errorMessage == null) {
                viewModel.syncClientPayload(clientPayloads!![i])
                mClientSyncIndex = i
                break
            } else {
                Log.d(
                    LOG_TAG,
                    requireActivity().resources.getString(R.string.error_fix_before_sync) +
                            clientPayloads!![i].errorMessage
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncClientPayloadsFragment {
            val arguments = Bundle()
            val fragment = SyncClientPayloadsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}