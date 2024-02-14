/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.mifos.feature.client.clientList.ClientListScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentClientBinding
import dagger.hilt.android.AndroidEntryPoint

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
@AndroidEntryPoint
class ClientListFragment : MifosBaseFragment()
//    , OnRefreshListener
{

    private lateinit var binding: FragmentClientBinding
//    private val arg: ClientListFragmentArgs by navArgs()
//
//    private lateinit var viewModel: ClientListViewModel
//
//    val mClientNameListAdapter by lazy {
//        ClientNameListAdapter(
//            onClientNameClick = { position ->
//                if (actionMode != null) {
//                    toggleSelection(position)
//                } else {
//                    if (!isParentFragment) {
//                        val action =
//                            ClientListFragmentDirections.actionClientListFragmentToClientActivity(
//                                ClientArgs(clientId = clientList[position].id)
//                            )
//                        findNavController().navigate(action)
//                        clickedPosition = position
//                    } else {
//                        val action =
//                            ClientListFragmentDirections.actionClientListFragmentToClientActivity(
//                                ClientArgs(clientId = clientList[position].id)
//                            )
//                        findNavController().navigate(action)
//                        clickedPosition = position
//                    }
//                }
//            },
//            onClientNameLongClick = { position ->
//                if (actionMode == null) {
//                    actionMode = actionModeCallback?.let {
//                        (activity as? MifosBaseActivity)?.startSupportActionMode(
//                            it
//                        )
//                    }
//                }
//                toggleSelection(position)
//            }
//        )
//    }
//
//    private lateinit var clientList: List<Client>
//    private var selectedClients: MutableList<Client>? = null
//    private var actionModeCallback: ActionModeCallback? = null
//    private var actionMode: ActionMode? = null
//    private var isParentFragment = false
//    private var mLayoutManager: LinearLayoutManager? = null
//    private var clickedPosition = -1
//    private var sweetUIErrorHandler: SweetUIErrorHandler? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        clientList = ArrayList()
//        selectedClients = ArrayList()
//        actionModeCallback = ActionModeCallback()
//        if (arguments != null) {
//            clientList = arg.clientListArgs.clientsList
//            isParentFragment = arg.clientListArgs.isParentFragment
//        }
//        setHasOptionsMenu(true)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ClientListScreen()
            }
        }
    }
}
//        binding = FragmentClientBinding.inflate(inflater, container, false)
//        if (!isParentFragment) (activity as HomeActivity).supportActionBar?.title =
//            getString(R.string.clients)
//        viewModel = ViewModelProvider(this)[ClientListViewModel::class.java]
//
//        //setting all the UI content to the view
//        showUserInterface()
//        /**
//         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
//         * is shown on the Screen.
//         */
//        mLayoutManager?.let {
//            binding.rvClients.addOnScrollListener(object :
//                EndlessRecyclerViewScrollListener(it) {
//                override fun onLoadMore(page: Int, totalItemCount: Int) {
//                    viewModel.loadClients(true, totalItemCount)
//                }
//            })
//        }
//        /**
//         * First Check the Parent Fragment is true or false. If parent fragment is true then no
//         * need to fetch clientList from Rest API, just need to showing parent fragment ClientList
//         * and is Parent Fragment is false then Presenter make the call to Rest API and fetch the
//         * Client Lis to show. and Presenter make transaction to Database to load saved clients.
//         */
//        if (isParentFragment) {
//            viewModel.showParentClients(clientList)
//            binding.pbClient.visibility = View.GONE
//        } else {
//            viewModel.loadClients(false, 0)
//        }
//        viewModel.loadDatabaseClients()
//
//        viewModel.clientListUiState.observe(viewLifecycleOwner) {
//            when (it) {
//                is ClientListUiState.ShowClientList -> {
//                    showProgressbar(false)
//                    showClientList(it.clients)
//                }
//
//                is ClientListUiState.ShowEmptyClientList -> {
//                    showProgressbar(false)
//                    showEmptyClientList(it.message)
//                }
//
//                is ClientListUiState.ShowError -> {
//                    showProgressbar(false)
//                    showError()
//                }
//
//                is ClientListUiState.ShowLoadMoreClients -> {
//                    showProgressbar(false)
//                    showLoadMoreClients(it.clients)
//                }
//
//                is ClientListUiState.ShowMessage -> {
//                    showProgressbar(false)
//                    showMessage(it.message)
//                }
//
//                is ClientListUiState.ShowProgressbar -> showProgressbar(it.state)
//                is ClientListUiState.UnregisterSwipeAndScrollListener -> {
//                    showProgressbar(false)
//                    unregisterSwipeAndScrollListener()
//                }
//            }
//        }
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.fabCreateClient.setOnClickListener {
//            if (!isParentFragment) {
//                findNavController().navigate(R.id.action_navigation_client_list_to_createNewClientFragment)
//            } else {
//                findNavController().navigate(R.id.action_clientListFragment_to_createNewClientFragment)
//            }
//        }
//
//        binding.layoutError.findViewById<Button>(com.github.therajanmaurya.sweeterror.R.id.btnTryAgain).setOnClickListener {
//            reloadOnError()
//        }
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (clickedPosition != -1) {
//            mClientNameListAdapter.updateItem(clickedPosition)
//        }
//    }
//
//    /**
//     * This method initializes the all Views.
//     */
//    private fun showUserInterface() {
//        mLayoutManager = LinearLayoutManager(activity)
//        mLayoutManager?.orientation = LinearLayoutManager.VERTICAL
//        binding.rvClients.layoutManager = mLayoutManager
//        binding.rvClients.setHasFixedSize(true)
//        binding.rvClients.adapter = mClientNameListAdapter
//        binding.swipeContainer.setColorSchemeColors(
//            *activity?.resources?.getIntArray(R.array.swipeRefreshColors) ?: IntArray(0)
//        )
//        binding.swipeContainer.setOnRefreshListener(this)
//        sweetUIErrorHandler = SweetUIErrorHandler(requireActivity(), binding.root)
//    }
//
//    /**
//     * This method will be called when user will swipe down to Refresh the ClientList then
//     * Presenter make the Fresh call to Rest API to load ClientList from offset = 0 and fetch the
//     * first 100 clients and update the client list.
//     */
//    override fun onRefresh() {
//        showUserInterface()
//        viewModel.loadClients(false, 0)
//        viewModel.loadDatabaseClients()
//        if (actionMode != null) actionMode?.finish()
//    }
//
//    /**
//     * This Method unregister the RecyclerView OnScrollListener and SwipeRefreshLayout
//     * and NoClientIcon click event.
//     */
//    private fun unregisterSwipeAndScrollListener() {
//        binding.rvClients.clearOnScrollListeners()
//        binding.swipeContainer.isEnabled = false
//    }
//
//    /**
//     * This Method showing the Simple Taster Message to user.
//     *
//     * @param message String Message to show.
//     */
//    private fun showMessage(message: Int) {
//        Toaster.show(binding.root, getStringMessage(message))
//    }
//
//    /**
//     * Onclick Send Fresh Request for Client list.
//     */
//    fun reloadOnError() {
//        sweetUIErrorHandler?.hideSweetErrorLayoutUI(binding.rvClients, binding.layoutError)
//        viewModel.loadClients(false, 0)
//        viewModel.loadDatabaseClients()
//    }
//
//    /**
//     * Setting ClientList to the Adapter and updating the Adapter.
//     */
//    private fun showClientList(clients: List<Client>) {
//        clientList = clients
//        mClientNameListAdapter.setClients(clients)
//        mClientNameListAdapter.notifyDataSetChanged()
//    }
//
//    /**
//     * Updating Adapter Attached ClientList
//     *
//     * @param clients List<Client></Client>>
//     */
//    private fun showLoadMoreClients(clients: List<Client>?) {
//        clientList.addAll()
//        mClientNameListAdapter.notifyDataSetChanged()
//    }
//
//    /**
//     * Showing Fetched ClientList size is 0 and show there is no client to show.
//     *
//     * @param message String Message to show user.
//     */
//    private fun showEmptyClientList(message: Int) {
//        sweetUIErrorHandler?.showSweetEmptyUI(
//            getString(R.string.client),
//            getString(message),
//            R.drawable.ic_error_black_24dp,
//            binding.rvClients,
//            binding.layoutError
//        )
//    }
//
//    /**
//     * This Method Will be called. When Presenter failed to First page of ClientList from Rest API.
//     * Then user look the Message that failed to fetch clientList.
//     */
//    private fun showError() {
//        val errorMessage = getStringMessage(R.string.failed_to_load_client)
//        sweetUIErrorHandler?.showSweetErrorUI(
//            errorMessage, R.drawable.ic_error_black_24dp,
//            binding.rvClients, binding.layoutError
//        )
//    }
//
//    /**
//     * show MifosBaseActivity ProgressBar, if mClientNameListAdapter.getItemCount() == 0
//     * otherwise show SwipeRefreshLayout.
//     */
//    private fun showProgressbar(show: Boolean) {
//        binding.swipeContainer.isRefreshing = show
//        if (show && mClientNameListAdapter.itemCount == 0) {
//            binding.pbClient.visibility = View.VISIBLE
//            binding.swipeContainer.isRefreshing = false
//        } else {
//            binding.pbClient.visibility = View.GONE
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        hideMifosProgressBar()
//        //As the Fragment Detach Finish the ActionMode
//        if (actionMode != null) actionMode?.finish()
//    }
//
//    /**
//     * Toggle the selection state of an item.
//     *
//     *
//     * If the item was the last one in the selection and is unselected, the selection is stopped.
//     * Note that the selection must already be started (actionMode must not be null).
//     *
//     * @param position Position of the item to toggle the selection state
//     */
//    private fun toggleSelection(position: Int) {
//        mClientNameListAdapter.toggleSelection(position)
//        val count = mClientNameListAdapter.selectedItemCount
//        if (count == 0) {
//            actionMode?.finish()
//        } else {
//            actionMode?.title = count.toString()
//            actionMode?.invalidate()
//        }
//    }
//
//    /**
//     * This ActionModeCallBack Class handling the User Event after the Selection of Clients. Like
//     * Click of Menu Sync Button and finish the ActionMode
//     */
//    private inner class ActionModeCallback : ActionMode.Callback {
//        private val LOG_TAG = ActionModeCallback::class.java.simpleName
//        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
//            mode.menuInflater.inflate(R.menu.menu_sync, menu)
//            return true
//        }
//
//        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
//            return false
//        }
//
//        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
//            return when (item.itemId) {
//                R.id.action_sync -> {
//                    selectedClients?.clear()
//                    for (position in mClientNameListAdapter.getSelectedItems()) {
//                        selectedClients?.let { list ->
//                            clientList[position].let { client ->
//                                list.add(client)
//                            }
//                        }
//                    }
//                    val syncClientsDialogFragment =
//                        SyncClientsDialogFragment.newInstance(selectedClients)
//                    val fragmentTransaction = activity
//                        ?.supportFragmentManager?.beginTransaction()
//                    fragmentTransaction?.addToBackStack(FragmentConstants.FRAG_CLIENT_SYNC)
//                    syncClientsDialogFragment.isCancelable = false
//                    fragmentTransaction?.let {
//                        syncClientsDialogFragment.show(
//                            it,
//                            resources.getString(R.string.sync_clients)
//                        )
//                    }
//                    mode.finish()
//                    true
//                }
//
//                else -> false
//            }
//        }
//
//        override fun onDestroyActionMode(mode: ActionMode) {
//            mClientNameListAdapter.clearSelection()
//            actionMode = null
//        }
//    }
//
//    companion object {
//        val LOG_TAG = ClientListFragment::class.java.simpleName
//    }
//}
//
//private fun <E> List<E>?.addAll() {
//
//}
