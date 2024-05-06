package com.mifos.mifosxdroid.offline.synccenterpayloads

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
import com.mifos.core.data.CenterPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SyncCenterPayloadAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SyncCenterPayloadsFragment : MifosBaseFragment(),
    DialogInterface.OnClickListener {

    private lateinit var binding: FragmentSyncpayloadBinding

    val LOG_TAG = javaClass.simpleName

    private lateinit var viewModel: SyncCenterPayloadsViewModel

    @Inject
    lateinit var mSyncCenterPayloadAdapter: SyncCenterPayloadAdapter
    var centerPayloads: MutableList<CenterPayload>? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null
    private var mCenterSyncIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        centerPayloads = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncCenterPayloadsViewModel::class.java]
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSyncPayload.layoutManager = mLayoutManager
        binding.rvSyncPayload.setHasFixedSize(true)
        binding.rvSyncPayload.adapter = mSyncCenterPayloadAdapter
        /**
         * Loading All Center Payloads from Database
         */
        binding.swipeContainer.setColorSchemeColors(
            *activity?.resources?.getIntArray(R.array.swipeRefreshColors) ?: intArrayOf()
        )
        binding.swipeContainer.setOnRefreshListener {
            viewModel.loadDatabaseCenterPayload()
            if (binding.swipeContainer.isRefreshing) binding.swipeContainer.isRefreshing = false
        }
        viewModel.loadDatabaseCenterPayload()

        viewModel.syncCenterPayloadsUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncCenterPayloadsUiState.ShowCenterPayloadUpdated -> {
                    showProgressbar(false)
                    showCenterPayloadUpdated(it.centerPayload)
                }

                is SyncCenterPayloadsUiState.ShowCenterSyncFailed -> {
                    showProgressbar(false)
                    showCenterSyncFailed(it.message)
                }

                is SyncCenterPayloadsUiState.ShowCenterSyncResponse -> {
                    showProgressbar(false)
                    showCenterSyncResponse()
                }

                is SyncCenterPayloadsUiState.ShowCenters -> {
                    showProgressbar(false)
                    showCenters(it.centerPayloads)
                }

                is SyncCenterPayloadsUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncCenterPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads -> {
                    showProgressbar(false)
                    showPayloadDeletedAndUpdatePayloads(it.centerPayloads)
                }

                is SyncCenterPayloadsUiState.ShowProgressbar -> showProgressbar(true)
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
     * Show when Database response is null or failed to fetch the center payload
     * Onclick Send Fresh Request for Center Payload.
     */

    private fun reloadOnError() {
        binding.llError.visibility = View.GONE
        viewModel.loadDatabaseCenterPayload()
    }

    private fun showCenterSyncResponse() {
        viewModel.deleteAndUpdateCenterPayload(
            centerPayloads
            !![mCenterSyncIndex].id
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
                if (centerPayloads?.size != 0) {
                    mCenterSyncIndex = 0
                    syncCenterPayload()
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

    private fun showError(stringId: String) {
        binding.llError.visibility = View.VISIBLE
        val message = stringId + resources.getString(R.string.click_to_refresh)
        binding.noPayloadText.text = message
        show(binding.root, stringId)
    }

    /**
     * This method is showing the center payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param centerPayload
     */
    private fun showCenters(centerPayload: List<CenterPayload>) {
        centerPayloads = centerPayload as MutableList<CenterPayload>
        if (centerPayload.isEmpty()) {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = activity
                ?.resources?.getString(R.string.no_center_payload_to_sync)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        } else {
            mSyncCenterPayloadAdapter.setCenterPayload(centerPayloads!!)
        }
    }

    private fun showCenterSyncFailed(errorMessage: String) {
        val centerPayload = centerPayloads!![mCenterSyncIndex]
        centerPayload.errorMessage = errorMessage
        viewModel.updateCenterPayload(centerPayload)
    }

    private fun showPayloadDeletedAndUpdatePayloads(centers: List<CenterPayload>) {
        mCenterSyncIndex = 0
        centerPayloads = centers as MutableList<CenterPayload>
        mSyncCenterPayloadAdapter.setCenterPayload(centerPayloads!!)
        if (centerPayloads?.size != 0) {
            syncCenterPayload()
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = activity
                ?.resources?.getString(R.string.all_centers_synced)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    private fun showCenterPayloadUpdated(centerPayload: CenterPayload) {
        centerPayloads!![mCenterSyncIndex] = centerPayload
        mSyncCenterPayloadAdapter.notifyDataSetChanged()
        mCenterSyncIndex += 1
        if (centerPayloads?.size != mCenterSyncIndex) {
            syncCenterPayload()
        }
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && mSyncCenterPayloadAdapter.itemCount == 0) {
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
                false -> if (centerPayloads?.size != 0) {
                    mCenterSyncIndex = 0
                    syncCenterPayload()
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

    private fun syncCenterPayload() {
        for (i in centerPayloads!!.indices) {
            if (centerPayloads!![i].errorMessage == null) {
                viewModel.syncCenterPayload(centerPayloads!![i])
                mCenterSyncIndex = i
                break
            } else {
                Log.d(
                    LOG_TAG,
                    requireActivity().resources.getString(R.string.error_fix_before_sync) +
                            centerPayloads!![i].errorMessage
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncCenterPayloadsFragment {
            val arguments = Bundle()
            val fragment = SyncCenterPayloadsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}