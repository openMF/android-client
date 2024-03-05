/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.centerlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.therajanmaurya.sweeterror.SweetUIErrorHandler
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.adapters.CentersListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerViewScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentCentersListBinding
import com.mifos.mifosxdroid.dialogfragments.synccenterdialog.SyncCentersDialogFragment
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetFragment
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ishankhanna on 11/03/14.
 *
 *
 * CenterListFragment Fetching and Showing CenterList in RecyclerView from
 * >demo.openmf.org/fineract-provider/api/v1/centers?paged=true&offset=0&limit=100>
 */
@AndroidEntryPoint
class CenterListFragment : MifosBaseFragment(), OnRefreshListener {

    private lateinit var binding: FragmentCentersListBinding

    private lateinit var viewModel: CenterListViewModel

    lateinit var centersListAdapter: CentersListAdapter
    private lateinit var centers: List<Center>
    private var selectedCenters: MutableList<Center>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var actionModeCallback: ActionModeCallback
    private var actionMode: ActionMode? = null
    private lateinit var sweetUIErrorHandler: SweetUIErrorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        centers = ArrayList()
        selectedCenters = ArrayList()
        actionModeCallback = ActionModeCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCentersListBinding.inflate(inflater, container, false)
//        mCenterListPresenter.attachView(this)
        viewModel = ViewModelProvider(this)[CenterListViewModel::class.java]

        //Showing User Interface.
        showUserInterface()

        //Fetching Centers
        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * will shown on the Screen.
         */
        binding.rvCenterList.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                viewModel.loadCenters(true, totalItemsCount)
            }
        })
        viewModel.loadCenters(false, 0)
        viewModel.loadDatabaseCenters()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.centerListUiState.observe(viewLifecycleOwner) {
            when (it) {
                is CenterListUiState.ShowCenters -> {
                    showProgressbar(false)
                    showCenters(it.centers)
                }

                is CenterListUiState.ShowCentersGroupAndMeeting -> {
                    showProgressbar(false)
                    showCentersGroupAndMeeting(it.centerWithAssociations, it.id)
                }

                is CenterListUiState.ShowEmptyCenters -> {
                    showProgressbar(false)
                    showEmptyCenters(it.message)
                }

                CenterListUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError()
                }

                is CenterListUiState.ShowMessage -> {
                    showProgressbar(false)
                    showMessage(it.message)
                }

                is CenterListUiState.ShowMoreCenters -> {
                    showProgressbar(false)
                    showMoreCenters(it.centers)
                }

                is CenterListUiState.ShowProgressbar -> showProgressbar(true)
                CenterListUiState.UnregisterSwipeAndScrollListener -> {
                    showProgressbar(false)
                    unregisterSwipeAndScrollListener()
                }
            }
        }

        binding.fabCreateCenter.setOnClickListener {
            onClickCreateNewCenter()
        }
        binding.layoutError.findViewById<Button>(com.github.therajanmaurya.sweeterror.R.id.btnTryAgain)
            .setOnClickListener {
                reloadOnError()
            }
    }

    /**
     * This Method is setting the UI
     */
    private fun showUserInterface() {
        (activity as HomeActivity).supportActionBar?.title = getString(R.string.centers)
        layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvCenterList.layoutManager = layoutManager
        centersListAdapter = CentersListAdapter(
            onCenterClick = { position ->
                if (actionMode != null) {
                    toggleSelection(position)
                } else {
                    val action = centers[position].id?.let {
                        CenterListFragmentDirections.actionNavigationCenterListToCentersActivity(
                            it
                        )
                    }
                    action?.let { findNavController().navigate(it) }
                }
            },
            onCenterLongClick = { position ->
                if (actionMode == null)
                    actionMode =
                        (activity as MifosBaseActivity?)?.startSupportActionMode(actionModeCallback)
                toggleSelection(position)
            }
        )
        binding.rvCenterList.setHasFixedSize(true)
        binding.rvCenterList.adapter = centersListAdapter
        binding.swipeContainer.setColorSchemeColors(
            *activity?.resources?.getIntArray(R.array.swipeRefreshColors) ?: IntArray(0)
        )
        binding.swipeContainer.setOnRefreshListener(this)
        sweetUIErrorHandler = SweetUIErrorHandler(requireActivity(), binding.root)
    }

    private fun onClickCreateNewCenter() {
        findNavController().navigate(R.id.action_navigation_center_list_to_createNewCenterFragment)
    }

    /**
     * This Method will be called, whenever user will pull down to RefreshLayout.
     */
    override fun onRefresh() {
        viewModel.loadCenters(false, 0)
        viewModel.loadDatabaseCenters()
        if (actionMode != null) actionMode?.finish()
    }

    /**
     * OnClick Error Image icon, reload the centers
     */
    private fun reloadOnError() {
        sweetUIErrorHandler.hideSweetErrorLayoutUI(binding.rvCenterList, binding.layoutError)
        viewModel.loadCenters(false, 0)
        viewModel.loadDatabaseCenters()
    }

    /**
     * Attaching the this.centers to the CentersListAdapter
     *
     * @param centers List<Center>
    </Center> */
    private fun showCenters(centers: List<Center>) {
        this.centers = centers
        centersListAdapter.setCenters(this.centers)
        centersListAdapter.notifyDataSetChanged()
    }

    /**
     * Updating the CenterListAdapter
     *
     * @param centers List<Center>
    </Center> */
    private fun showMoreCenters(centers: List<Center>) {
        this.centers.addAll()
        centersListAdapter.notifyDataSetChanged()
    }

    /**
     * Showing that Server response is Empty
     *
     * @param message
     */
    private fun showEmptyCenters(message: Int) {
        sweetUIErrorHandler.showSweetEmptyUI(
            getString(R.string.center), getString(message),
            R.drawable.ic_error_black_24dp, binding.rvCenterList, binding.layoutError
        )
    }

    /**
     * This Method for showing simple SeekBar
     *
     * @param message
     */
    private fun showMessage(message: Int) {
        Toaster.show(binding.root, getStringMessage(message))
    }

    /**
     * This Method for showing the CollectionSheet of Center
     *
     * @param centerWithAssociations
     * @param id
     */
    private fun showCentersGroupAndMeeting(
        centerWithAssociations: CenterWithAssociations?,
        id: Int
    ) {
        Toaster.show(binding.root, "Hii")
        val mfDatePicker = MFDatePicker()
        mfDatePicker.setCustomOnDatePickListener(object : MFDatePicker.OnDatePickListener {
            override fun onDatePicked(date: String?) {
                if (centerWithAssociations?.collectionMeetingCalendar?.id != null) {
                    (activity as MifosBaseActivity).replaceFragment(
                        CollectionSheetFragment.newInstance(
                            id, date,
                            centerWithAssociations.collectionMeetingCalendar.id
                        ),
                        true, R.id.container
                    )
                } else {
                    showMessage(R.string.no_meeting_found)
                }
            }
        })
        mfDatePicker.show(requireActivity().supportFragmentManager, MFDatePicker.TAG)
    }

    /**
     * If Loading Centers is failed on first request then show to user a message that center failed
     * to load.
     */
    private fun showFetchingError() {
        val errorMessage = getStringMessage(R.string.failed_to_fetch_centers)
        sweetUIErrorHandler.showSweetErrorUI(
            errorMessage,
            R.drawable.ic_error_black_24dp, binding.rvCenterList, binding.layoutError
        )
    }

    /**
     * This Method for showing Progress bar if the Center count is zero otherwise
     * shows swipeRefreshLayout
     *
     * @param show Boolean
     */
    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && centersListAdapter.itemCount == 0) {
            binding.progressbarCenter.visibility = View.VISIBLE
            binding.swipeContainer.isRefreshing = false
        } else {
            binding.progressbarCenter.visibility = View.GONE
        }
    }

    /**
     * This Method unregister the RecyclerView OnScrollListener and SwipeRefreshLayout
     * and NoClientIcon click event.
     */
    private fun unregisterSwipeAndScrollListener() {
        binding.rvCenterList.clearOnScrollListeners()
        binding.swipeContainer.isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mItem_search) requireActivity().finish()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Toggle the selection state of an item.
     *
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private fun toggleSelection(position: Int) {
        centersListAdapter.toggleSelection(position)
        val count = centersListAdapter.selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    /**
     * This ActionModeCallBack Class handling the User Event after the Selection of Clients. Like
     * Click of Menu Sync Button and finish the ActionMode
     */
    private inner class ActionModeCallback : ActionMode.Callback {
        private val LOG_TAG = ActionModeCallback::class.java.simpleName
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_sync, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_sync -> {
                    selectedCenters?.clear()
                    for (position in centersListAdapter.getSelectedItems()) {
                        val center = centers.get(position)
                        if (center != null) {
                            selectedCenters?.add(center)
                        }
                    }
                    val syncCentersDialogFragment =
                        SyncCentersDialogFragment.newInstance(selectedCenters)
                    val fragmentTransaction = activity
                        ?.supportFragmentManager?.beginTransaction()
                    fragmentTransaction?.addToBackStack(FragmentConstants.FRAG_CLIENT_SYNC)
                    syncCentersDialogFragment.isCancelable = false
                    if (fragmentTransaction != null) {
                        syncCentersDialogFragment.show(
                            fragmentTransaction,
                            resources.getString(R.string.sync_centers)
                        )
                    }
                    mode.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            centersListAdapter.clearSelection()
            actionMode = null
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): CenterListFragment {
            val centerListFragment = CenterListFragment()
            val args = Bundle()
            centerListFragment.arguments = args
            return centerListFragment
        }
    }
}

private fun <E> List<E>?.addAll() {

}
