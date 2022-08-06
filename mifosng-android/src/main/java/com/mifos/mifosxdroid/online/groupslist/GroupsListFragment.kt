/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.groupslist

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.therajanmaurya.sweeterror.SweetUIErrorHandler
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.GroupNameListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerViewScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog.SyncGroupsDialogFragment
import com.mifos.mifosxdroid.online.GroupsActivity
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment
import com.mifos.objects.group.Group
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 2/27/2016.
 *
 *
 * This class loading and showing groups, Here is two way to load the Groups. First one to load
 * Groups from Rest API
 *
 *
 * >demo.openmf.org/fineract-provider/api/v1/groups?paged=true&offset=offset_value&limit
 * =limit_value>
 *
 *
 * Offset : From Where index, Groups will be fetch.
 * limit : Total number of client, need to fetch
 *
 *
 * and showing in the GroupList.
 *
 *
 * and Second one is showing Groups provided by Parent(Fragment or Activity).
 * Parent(Fragment or Activity) load the GroupList and send the
 * Groups to GroupsListFragment newInstance(List<Group> groupList,
 * boolean isParentFragment) {...}
 * and unregister the ScrollListener and SwipeLayout.
</Group> */
class GroupsListFragment : MifosBaseFragment(), GroupsListMvpView, OnRefreshListener {
    @JvmField
    @BindView(R.id.rv_groups)
    var rv_groups: RecyclerView? = null

    @JvmField
    @BindView(R.id.progressbar_group)
    var pb_groups: ProgressBar? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.layout_error)
    var errorView: View? = null

    @JvmField
    @Inject
    var mGroupsListPresenter: GroupsListPresenter? = null

    val mGroupListAdapter by lazy {
        GroupNameListAdapter(
            onGroupClick = { position ->
                if (actionMode != null) {
                    toggleSelection(position)
                } else {
                    val groupActivityIntent = Intent(activity, GroupsActivity::class.java)
                    groupActivityIntent.putExtra(Constants.GROUP_ID, mGroupList!![position].id)
                    startActivity(groupActivityIntent)
                }
            },
            onGroupLongClick = { position ->
                if (actionMode == null) {
                    actionMode = (activity as MifosBaseActivity?)!!.startSupportActionMode(actionModeCallback!!)
                }
                toggleSelection(position)
            }
        )
    }
    var mLayoutManager: LinearLayoutManager? = null
    private var mGroupList: List<Group>? = null
    private var selectedGroups: MutableList<Group>? = null
    private var isParentFragment = false
    private lateinit var rootView: View
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var sweetUIErrorHandler: SweetUIErrorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        mGroupList = ArrayList()
        selectedGroups = ArrayList()
        actionModeCallback = ActionModeCallback()
        if (arguments != null) {
            mGroupList = requireArguments().getParcelableArrayList(Constants.GROUPS)
            isParentFragment = requireArguments()
                    .getBoolean(Constants.IS_A_PARENT_FRAGMENT)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false)
        ButterKnife.bind(this, rootView)
        mGroupsListPresenter!!.attachView(this)

        //setting all the UI content to the view
        showUserInterface()
        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         */
        rv_groups!!.addOnScrollListener(object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                mGroupsListPresenter!!.loadGroups(true, totalItemsCount)
            }
        })
        /**
         * First Check the Parent Fragment is true or false. If parent fragment is true then no
         * need to fetch groupList from Rest API, just need to show parent fragment groupList
         * and if Parent Fragment is false then Presenter make the call to Rest API and fetch the
         * Group List to show. and Presenter make transaction to Database to load saved clients.
         * To show user that is there already any group is synced already or not.
         */
        if (isParentFragment) {
            mGroupList?.let { mGroupsListPresenter!!.showParentClients(it) }
        } else {
            mGroupsListPresenter!!.loadGroups(false, 0)
        }
        mGroupsListPresenter!!.loadDatabaseGroups()
        return rootView
    }

    /**
     * This method Initializing the UI.
     */
    override fun showUserInterface() {
        setToolbarTitle(resources.getString(R.string.groups))
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        rv_groups!!.layoutManager = mLayoutManager
        rv_groups!!.setHasFixedSize(true)
        rv_groups!!.adapter = mGroupListAdapter
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
        sweetUIErrorHandler = SweetUIErrorHandler(activity, rootView)
    }

    @OnClick(R.id.fab_create_group)
    fun onClickCreateNewGroup() {
        (activity as MifosBaseActivity?)!!.replaceFragment(CreateNewGroupFragment.newInstance(),
                true, R.id.container_a)
    }

    /**
     * This Method will be called. Whenever user will swipe down to refresh the group list.
     */
    override fun onRefresh() {
        mGroupsListPresenter!!.loadGroups(false, 0)
        mGroupsListPresenter!!.loadDatabaseGroups()
        if (actionMode != null) actionMode!!.finish()
    }

    /**
     * This method will be called, whenever first time error occurred during the fetching group
     * list from REST API.
     * As the error will occurred. user is able to see the error message and ability to reload
     * groupList.
     */
    @OnClick(R.id.btn_try_again)
    fun reloadOnError() {
        sweetUIErrorHandler!!.hideSweetErrorLayoutUI(rv_groups, errorView)
        mGroupsListPresenter!!.loadGroups(false, 0)
        mGroupsListPresenter!!.loadDatabaseGroups()
    }

    /**
     * Setting GroupList to the Adapter and updating the Adapter.
     */
    override fun showGroups(groups: List<Group?>?) {
        mGroupList = groups as List<Group>?
        Collections.sort(mGroupList) { grp1, grp2 -> grp1.name.compareTo(grp2.name) }
        mGroupListAdapter!!.setGroups(mGroupList ?: emptyList())
    }

    /**
     * Adding the More Groups in List and Update the Adapter.
     *
     * @param groups
     */
    override fun showLoadMoreGroups(clients: List<Group?>?) {
        mGroupList.addAll()
        mGroupListAdapter!!.notifyDataSetChanged()
    }

    /**
     * This method will be called, if fetched groupList is Empty and show there is no Group to show.
     *
     * @param message String Message.
     */
    override fun showEmptyGroups(message: Int) {
        sweetUIErrorHandler!!.showSweetEmptyUI(getString(R.string.group), getString(message),
                R.drawable.ic_error_black_24dp, rv_groups, errorView)
    }

    /**
     * This Method unregistered the SwipeLayout and OnScrollListener
     */
    override fun unregisterSwipeAndScrollListener() {
        rv_groups!!.clearOnScrollListeners()
        swipeRefreshLayout!!.isEnabled = false
    }

    /**
     * This Method showing the Simple Taster Message to user.
     *
     * @param message String Message to show.
     */
    override fun showMessage(message: Int) {
        Toaster.show(rootView, getStringMessage(message))
    }

    /**
     * If Any any exception occurred during fetching the Groups. like No Internet or etc.
     * then this method show the error message to user and give the ability to refresh groups.
     */
    override fun showFetchingError() {
        val errorMessage = getStringMessage(R.string.failed_to_fetch_groups)
        sweetUIErrorHandler!!.showSweetErrorUI(errorMessage,
                R.drawable.ic_error_black_24dp, rv_groups, errorView)
    }

    /**
     * This Method showing the Progressbar during fetching the group List on first time and
     * otherwise showing swipe refresh layout
     *
     * @param show Status of Progressbar or SwipeRefreshLayout
     */
    override fun showProgressbar(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = show
        if (show && mGroupListAdapter!!.itemCount == 0) {
            pb_groups!!.visibility = View.VISIBLE
            swipeRefreshLayout!!.isRefreshing = false
        } else {
            pb_groups!!.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGroupsListPresenter!!.detachView()
        //As the Fragment Detach Finish the ActionMode
        if (actionMode != null) actionMode!!.finish()
    }

    /**
     * Toggle the selection state of an item.
     *
     *
     * If the item was the last one in the selection and is unselected, then selection will stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private fun toggleSelection(position: Int) {
        mGroupListAdapter!!.toggleSelection(position)
        val count = mGroupListAdapter!!.selectedItemCount
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
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
                    selectedGroups!!.clear()
                    for (position in mGroupListAdapter!!.selectedItems) {
                        selectedGroups!!.add(mGroupList!![position!!])
                    }
                    val syncGroupsDialogFragment = SyncGroupsDialogFragment.newInstance(selectedGroups)
                    val fragmentTransaction = activity
                            ?.getSupportFragmentManager()!!.beginTransaction()
                    fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_SYNC)
                    syncGroupsDialogFragment.isCancelable = false
                    syncGroupsDialogFragment.show(fragmentTransaction,
                            resources.getString(R.string.sync_groups))
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mGroupListAdapter!!.clearSelection()
            actionMode = null
        }
    }

    companion object {
        /**
         * This Method will be called, whenever isParentFragment will be true
         * and Presenter do not need to make Rest API call to server. Parent (Fragment or Activity)
         * already fetched the groups and for showing, they call GroupsListFragment.
         *
         *
         * Example : Showing Parent Groups.
         *
         * @param groupList        List<Group>
         * @param isParentFragment true
         * @return GroupsListFragment
        </Group> */
        @JvmStatic
        fun newInstance(groupList: List<Group?>?,
                        isParentFragment: Boolean): GroupsListFragment {
            val groupListFragment = GroupsListFragment()
            val args = Bundle()
            if (isParentFragment && groupList != null) {
                args.putParcelableArrayList(Constants.GROUPS,
                        groupList as ArrayList<out Parcelable?>?)
                args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, true)
                groupListFragment.arguments = args
            }
            return groupListFragment
        }
        /**
         * This method will be called, whenever GroupsListFragment will not have Parent Fragment.
         * So, Presenter make the call to Rest API and fetch the Client List and show in UI
         *
         * @return GroupsListFragment
         */

        @JvmStatic
        fun newInstance(): GroupsListFragment {
            val arguments = Bundle()
            val groupListFragment = GroupsListFragment()
            groupListFragment.arguments = arguments
            return groupListFragment
        }
    }
}

private fun <E> List<E>?.addAll() {

}
