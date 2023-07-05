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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SyncCenterPayloadAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.services.data.CenterPayload
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager.userStatus
import javax.inject.Inject

class SyncCenterPayloadsFragment : MifosBaseFragment(), SyncCenterPayloadsMvpView,
    DialogInterface.OnClickListener {
    val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.rv_sync_payload)
    var rvPayloadCenter: RecyclerView? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.noPayloadText)
    var mNoPayloadText: TextView? = null

    @JvmField
    @BindView(R.id.noPayloadIcon)
    var mNoPayloadIcon: ImageView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var llError: LinearLayout? = null

    @JvmField
    @Inject
    var mSyncCenterPayloadsPresenter: SyncCenterPayloadsPresenter? = null

    @JvmField
    @Inject
    var mSyncCenterPayloadAdapter: SyncCenterPayloadAdapter? = null
    lateinit var rootView: View
    var centerPayloads: MutableList<CenterPayload>? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null
    var mCenterSyncIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent!!.inject(this)
        centerPayloads = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_syncpayload, container, false)
        ButterKnife.bind(this, rootView)
        mSyncCenterPayloadsPresenter!!.attachView(this)
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvPayloadCenter!!.layoutManager = mLayoutManager
        rvPayloadCenter!!.setHasFixedSize(true)
        rvPayloadCenter!!.adapter = mSyncCenterPayloadAdapter
        /**
         * Loading All Center Payloads from Database
         */
        swipeRefreshLayout?.setColorSchemeColors(
            *activity?.resources?.getIntArray(R.array.swipeRefreshColors) ?: intArrayOf()
        )
        swipeRefreshLayout!!.setOnRefreshListener {
            mSyncCenterPayloadsPresenter!!.loadDatabaseCenterPayload()
            if (swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout!!.isRefreshing = false
        }
        mSyncCenterPayloadsPresenter!!.loadDatabaseCenterPayload()
        return rootView
    }

    /**
     * Show when Database response is null or failed to fetch the center payload
     * Onclick Send Fresh Request for Center Payload.
     */
    @OnClick(R.id.noPayloadIcon)
    fun reloadOnError() {
        llError!!.visibility = View.GONE
        mSyncCenterPayloadsPresenter!!.loadDatabaseCenterPayload()
    }

    override fun showCenterSyncResponse() {
        mSyncCenterPayloadsPresenter!!.deleteAndUpdateCenterPayload(
            centerPayloads
            !!.get(mCenterSyncIndex).id
        )
    }

    override fun showOfflineModeDialog() {
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
                if (centerPayloads!!.size != 0) {
                    mCenterSyncIndex = 0
                    syncCenterPayload()
                } else {
                    show(
                        rootView,
                        requireActivity().resources.getString(R.string.nothing_to_sync)
                    )
                }
            }

            else -> {}
        }
    }

    override fun showError(stringId: Int) {
        llError!!.visibility = View.VISIBLE
        val message = stringId.toString() + resources.getString(R.string.click_to_refresh)
        mNoPayloadText!!.text = message
        show(rootView, stringId)
    }

    /**
     * This method is showing the center payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param centerPayload
     */
    override fun showCenters(centerPayload: MutableList<CenterPayload>) {
        centerPayloads = centerPayload
        if (centerPayload.size == 0) {
            llError!!.visibility = View.VISIBLE
            mNoPayloadText!!.text = activity
                ?.resources?.getString(R.string.no_center_payload_to_sync)
            mNoPayloadIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        } else {
            mSyncCenterPayloadAdapter!!.setCenterPayload(centerPayloads!!)
        }
    }

    override fun showCenterSyncFailed(errorMessage: String) {
        val centerPayload = centerPayloads!![mCenterSyncIndex]
        centerPayload.errorMessage = errorMessage
        mSyncCenterPayloadsPresenter!!.updateCenterPayload(centerPayload)
    }

    override fun showPayloadDeletedAndUpdatePayloads(centers: MutableList<CenterPayload>) {
        mCenterSyncIndex = 0
        centerPayloads = centers
        mSyncCenterPayloadAdapter!!.setCenterPayload(centerPayloads!!)
        if (centerPayloads!!.size != 0) {
            syncCenterPayload()
        } else {
            llError!!.visibility = View.VISIBLE
            mNoPayloadText!!.text = activity
                ?.resources?.getString(R.string.all_centers_synced)
            mNoPayloadIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showCenterPayloadUpdated(centerPayload: CenterPayload) {
        centerPayloads!![mCenterSyncIndex] = centerPayload
        mSyncCenterPayloadAdapter!!.notifyDataSetChanged()
        mCenterSyncIndex += 1
        if (centerPayloads!!.size != mCenterSyncIndex) {
            syncCenterPayload()
        }
    }

    override fun showProgressbar(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = show
        if (show && mSyncCenterPayloadAdapter!!.itemCount == 0) {
            showMifosProgressBar()
            swipeRefreshLayout!!.isRefreshing = false
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
                0 -> if (centerPayloads!!.size != 0) {
                    mCenterSyncIndex = 0
                    syncCenterPayload()
                } else {
                    show(
                        rootView,
                        requireActivity().resources.getString(R.string.nothing_to_sync)
                    )
                }

                1 -> showOfflineModeDialog()
                else -> {}
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun syncCenterPayload() {
        for (i in centerPayloads!!.indices) {
            if (centerPayloads!![i].errorMessage == null) {
                mSyncCenterPayloadsPresenter!!.syncCenterPayload(centerPayloads!![i])
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

    override fun onDestroyView() {
        super.onDestroyView()
        mSyncCenterPayloadsPresenter!!.detachView()
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