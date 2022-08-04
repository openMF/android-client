/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientlist

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
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerViewScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogFragment
import com.mifos.mifosxdroid.online.ClientActivity
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment
import com.mifos.objects.client.Client
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by ishankhanna on 09/02/14.
 *
 *
 * This class loading client, Here is two way to load the clients. First one to load clients
 * from Rest API
 *
 *
 * >demo.openmf.org/fineract-provider/api/v1/clients?paged=true&offset=offset_value&limit
 * =limit_value>
 *
 *
 * Offset : From Where index, client will be fetch.
 * limit : Total number of client, need to fetch
 *
 *
 * and showing in the ClientList.
 *
 *
 * and Second one is showing Group Clients. Here Group load the ClientList and send the
 * Client to ClientListFragment newInstance(List<Client> clientList,
 * boolean isParentFragment) {...}
 * and unregister the ScrollListener and SwipeLayout.
</Client> */
class ClientListFragment : MifosBaseFragment(), ClientListMvpView, OnRefreshListener {
    @JvmField
    @BindView(R.id.rv_clients)
    var rv_clients: RecyclerView? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.layout_error)
    var errorView: View? = null

    @JvmField
    @BindView(R.id.pb_client)
    var pb_client: ProgressBar? = null

    val mClientNameListAdapter by lazy {
        ClientNameListAdapter(
            onClientNameClick = { position ->
                if (actionMode != null) {
                    toggleSelection(position)
                } else {
                    val clientActivityIntent = Intent(activity, ClientActivity::class.java)
                    clientActivityIntent.putExtra(Constants.CLIENT_ID, clientList!![position].id)
                    startActivity(clientActivityIntent)
                    clickedPosition = position
                }
            },
            onClientNameLongClick = { position ->
                if (actionMode == null) {
                    actionMode = (activity as MifosBaseActivity?)!!.startSupportActionMode(actionModeCallback!!)
                }
                toggleSelection(position)
            }
        )
    }

    @JvmField
    @Inject
    var mClientListPresenter: ClientListPresenter? = null
    private lateinit var rootView: View
    private var clientList: List<Client>? = null
    private var selectedClients: MutableList<Client>? = null
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var isParentFragment = false
    private var mLayoutManager: LinearLayoutManager? = null
    private var clickedPosition = -1
    private var sweetUIErrorHandler: SweetUIErrorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientList = ArrayList()
        selectedClients = ArrayList()
        actionModeCallback = ActionModeCallback()
        if (arguments != null) {
            clientList = requireArguments().getParcelableArrayList(Constants.CLIENTS)
            isParentFragment = requireArguments()
                    .getBoolean(Constants.IS_A_PARENT_FRAGMENT)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_client, container, false)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        setToolbarTitle(resources.getString(R.string.clients))
        ButterKnife.bind(this, rootView)
        mClientListPresenter!!.attachView(this)

        //setting all the UI content to the view
        showUserInterface()
        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         */
        rv_clients!!.addOnScrollListener(object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int, totalItemCount: Int) {
                mClientListPresenter!!.loadClients(true, totalItemCount)
            }
        })
        /**
         * First Check the Parent Fragment is true or false. If parent fragment is true then no
         * need to fetch clientList from Rest API, just need to showing parent fragment ClientList
         * and is Parent Fragment is false then Presenter make the call to Rest API and fetch the
         * Client Lis to show. and Presenter make transaction to Database to load saved clients.
         */
        if (isParentFragment) {
            mClientListPresenter!!.showParentClients(clientList)
        } else {
            mClientListPresenter!!.loadClients(false, 0)
        }
        mClientListPresenter!!.loadDatabaseClients()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        if (clickedPosition != -1) {
            mClientNameListAdapter!!.updateItem(clickedPosition)
        }
    }

    /**
     * This method initializes the all Views.
     */
    override fun showUserInterface() {
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        rv_clients!!.layoutManager = mLayoutManager
        rv_clients!!.setHasFixedSize(true)
        rv_clients!!.adapter = mClientNameListAdapter
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
        sweetUIErrorHandler = SweetUIErrorHandler(activity, rootView)
    }

    @OnClick(R.id.fab_create_client)
    fun onClickCreateNewClient() {
        (activity as MifosBaseActivity?)!!.replaceFragment(CreateNewClientFragment.newInstance(),
                true, R.id.container_a)
    }

    /**
     * This method will be called when user will swipe down to Refresh the ClientList then
     * Presenter make the Fresh call to Rest API to load ClientList from offset = 0 and fetch the
     * first 100 clients and update the client list.
     */
    override fun onRefresh() {
        showUserInterface()
        mClientListPresenter!!.loadClients(false, 0)
        mClientListPresenter!!.loadDatabaseClients()
        if (actionMode != null) actionMode!!.finish()
    }

    /**
     * This Method unregister the RecyclerView OnScrollListener and SwipeRefreshLayout
     * and NoClientIcon click event.
     */
    override fun unregisterSwipeAndScrollListener() {
        rv_clients!!.clearOnScrollListeners()
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
     * Onclick Send Fresh Request for Client list.
     */
    @OnClick(R.id.btn_try_again)
    fun reloadOnError() {
        sweetUIErrorHandler!!.hideSweetErrorLayoutUI(rv_clients, errorView)
        mClientListPresenter!!.loadClients(false, 0)
        mClientListPresenter!!.loadDatabaseClients()
    }

    /**
     * Setting ClientList to the Adapter and updating the Adapter.
     */
    override fun showClientList(clients: List<Client>?) {
        clientList = clients
        mClientNameListAdapter!!.setClients(clients ?: emptyList())
        mClientNameListAdapter!!.notifyDataSetChanged()
    }

    /**
     * Updating Adapter Attached ClientList
     *
     * @param clients List<Client></Client>>
     */
    override fun showLoadMoreClients(clients: List<Client>?) {
        clientList.addAll()
        mClientNameListAdapter!!.notifyDataSetChanged()
    }

    /**
     * Showing Fetched ClientList size is 0 and show there is no client to show.
     *
     * @param message String Message to show user.
     */
    override fun showEmptyClientList(message: Int) {
        sweetUIErrorHandler!!.showSweetEmptyUI(getString(R.string.client),
                getString(message), R.drawable.ic_error_black_24dp, rv_clients, errorView)
    }

    /**
     * This Method Will be called. When Presenter failed to First page of ClientList from Rest API.
     * Then user look the Message that failed to fetch clientList.
     */
    override fun showError() {
        val errorMessage = getStringMessage(R.string.failed_to_load_client)
        sweetUIErrorHandler!!.showSweetErrorUI(errorMessage, R.drawable.ic_error_black_24dp,
                rv_clients, errorView)
    }

    /**
     * show MifosBaseActivity ProgressBar, if mClientNameListAdapter.getItemCount() == 0
     * otherwise show SwipeRefreshLayout.
     */
    override fun showProgressbar(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = show
        if (show && mClientNameListAdapter!!.itemCount == 0) {
            pb_client!!.visibility = View.VISIBLE
            swipeRefreshLayout!!.isRefreshing = false
        } else {
            pb_client!!.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideMifosProgressBar()
        mClientListPresenter!!.detachView()
        //As the Fragment Detach Finish the ActionMode
        if (actionMode != null) actionMode!!.finish()
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
        mClientNameListAdapter!!.toggleSelection(position)
        val count = mClientNameListAdapter!!.selectedItemCount
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
                    selectedClients!!.clear()
                    for (position in mClientNameListAdapter!!.selectedItems) {
                        selectedClients!!.add(clientList!![position!!])
                    }
                    val syncClientsDialogFragment = SyncClientsDialogFragment.newInstance(selectedClients)
                    val fragmentTransaction = activity
                            ?.getSupportFragmentManager()!!.beginTransaction()
                    fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_SYNC)
                    syncClientsDialogFragment.isCancelable = false
                    syncClientsDialogFragment.show(fragmentTransaction,
                            resources.getString(R.string.sync_clients))
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mClientNameListAdapter!!.clearSelection()
            actionMode = null
        }
    }

    companion object {
        val LOG_TAG = ClientListFragment::class.java.simpleName

        /**
         * This method will be called, whenever ClientListFragment will not have Parent Fragment.
         * So, Presenter make the call to Rest API and fetch the Client List and show in UI
         *
         * @return ClientListFragment
         */

        @JvmStatic
        fun newInstance(): ClientListFragment {
            val arguments = Bundle()
            val clientListFragment = ClientListFragment()
            clientListFragment.arguments = arguments
            return clientListFragment
        }

        /**
         * This Method will be called, whenever Parent (Fragment or Activity) will be true and Presenter
         * do not need to make Rest API call to server. Parent (Fragment or Activity) already fetched
         * the clients and for showing, they call ClientListFragment.
         *
         *
         * Example : Showing Group Clients.
         *
         * @param clientList       List<Client>
         * @param isParentFragment true
         * @return ClientListFragment
        </Client> */
        @JvmStatic
        fun newInstance(clientList: List<Client?>?,
                        isParentFragment: Boolean): ClientListFragment {
            val clientListFragment = ClientListFragment()
            val args = Bundle()
            if (isParentFragment && clientList != null) {
                args.putParcelableArrayList(Constants.CLIENTS,
                        clientList as ArrayList<out Parcelable?>?)
                args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, true)
                clientListFragment.arguments = args
            }
            return clientListFragment
        }

    }
}

private fun <E> List<E>?.addAll() {

}
