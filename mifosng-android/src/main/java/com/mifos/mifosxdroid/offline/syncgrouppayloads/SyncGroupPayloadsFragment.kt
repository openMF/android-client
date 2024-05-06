package com.mifos.mifosxdroid.offline.syncgrouppayloads

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
import com.mifos.core.objects.group.GroupPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SyncGroupPayloadAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 19/07/16.
 */
@AndroidEntryPoint
class SyncGroupPayloadsFragment : MifosBaseFragment(), DialogInterface.OnClickListener {

    private lateinit var binding: FragmentSyncpayloadBinding

    val LOG_TAG = javaClass.simpleName

    private lateinit var viewModel: SyncGroupPayloadsViewModel

    @JvmField
    @Inject
    var mSyncGroupPayloadAdapter: SyncGroupPayloadAdapter? = null
    var groupPayloads: MutableList<GroupPayload>? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null
    private var mClientSyncIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupPayloads = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncGroupPayloadsViewModel::class.java]
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSyncPayload.layoutManager = mLayoutManager
        binding.rvSyncPayload.setHasFixedSize(true)
        binding.rvSyncPayload.adapter = mSyncGroupPayloadAdapter
        /**
         * Loading All Client Payloads from Database
         */
        binding.swipeContainer.setColorSchemeColors(
            *requireActivity()
                .resources.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener {
            viewModel.loanDatabaseGroupPayload()
            if (binding.swipeContainer.isRefreshing) binding.swipeContainer.isRefreshing = false
        }
        viewModel.loanDatabaseGroupPayload()

        viewModel.syncGroupPayloadsUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncGroupPayloadsUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncGroupPayloadsUiState.ShowGroupPayloadUpdated -> {
                    showProgressbar(false)
                    showGroupPayloadUpdated(it.groupPayload)
                }

                is SyncGroupPayloadsUiState.ShowGroupSyncFailed -> {
                    showProgressbar(false)
                    showGroupSyncFailed(it.message)
                }

                is SyncGroupPayloadsUiState.ShowGroupSyncResponse -> {
                    showProgressbar(false)
                    showGroupSyncResponse()
                }

                is SyncGroupPayloadsUiState.ShowGroups -> {
                    showProgressbar(false)
                    showGroups(it.groupPayloads)
                }

                is SyncGroupPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads -> {
                    showProgressbar(false)
                    showPayloadDeletedAndUpdatePayloads(it.groupPayloads)
                }

                is SyncGroupPayloadsUiState.ShowProgressbar -> showProgressbar(true)
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
        viewModel.loanDatabaseGroupPayload()
    }

    private fun showGroupSyncResponse() {
        viewModel.deleteAndUpdateGroupPayload(
            groupPayloads
            !![mClientSyncIndex].id
        )
    }

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
                if (groupPayloads?.size != 0) {
                    mClientSyncIndex = 0
                    syncGroupPayload()
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

    private fun showError(stringId: Int) {
        binding.llError.visibility = View.VISIBLE
        val message = stringId.toString() + resources.getString(R.string.click_to_refresh)
        binding.noPayloadText.text = message
        show(binding.root, stringId)
    }

    /**
     * This method is showing the group payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param groupPayload
     */
    private fun showGroups(groupPayload: List<GroupPayload>) {
        groupPayloads = groupPayload as MutableList<GroupPayload>
        if (groupPayload.isEmpty()) {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = requireActivity()
                .resources.getString(R.string.no_group_payload_to_sync)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        } else {
            mSyncGroupPayloadAdapter?.setGroupPayload(groupPayloads!!)
        }
    }

    private fun showGroupSyncFailed(errorMessage: String) {
        val groupPayload = groupPayloads!![mClientSyncIndex]
        groupPayload.errorMessage = errorMessage
        viewModel.updateGroupPayload(groupPayload)
    }

    private fun showPayloadDeletedAndUpdatePayloads(groups: List<GroupPayload>) {
        mClientSyncIndex = 0
        groupPayloads = groups as MutableList<GroupPayload>
        mSyncGroupPayloadAdapter?.setGroupPayload(groupPayloads!!)
        if (groupPayloads?.size != 0) {
            syncGroupPayload()
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = requireActivity()
                .resources.getString(R.string.all_groups_synced)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    private fun showGroupPayloadUpdated(groupPayload: GroupPayload) {
        groupPayloads!![mClientSyncIndex] = groupPayload
        mSyncGroupPayloadAdapter?.notifyDataSetChanged()
        mClientSyncIndex += 1
        if (groupPayloads?.size != mClientSyncIndex) {
            syncGroupPayload()
        }
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && mSyncGroupPayloadAdapter?.itemCount == 0) {
            showMifosProgressBar()
            binding.swipeContainer.isRefreshing = false
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
                false -> if (groupPayloads?.size != 0) {
                    mClientSyncIndex = 0
                    syncGroupPayload()
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

    private fun syncGroupPayload() {
        for (i in groupPayloads!!.indices) {
            if (groupPayloads!![i].errorMessage == null) {
                viewModel.syncGroupPayload(groupPayloads!![i])
                mClientSyncIndex = i
                break
            } else {
                Log.d(
                    LOG_TAG,
                    requireActivity().resources.getString(R.string.error_fix_before_sync) +
                            groupPayloads!![i].errorMessage
                )
            }
        }
    }

    companion object {
        fun newInstance(): SyncGroupPayloadsFragment {
            val arguments = Bundle()
            val fragment = SyncGroupPayloadsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}