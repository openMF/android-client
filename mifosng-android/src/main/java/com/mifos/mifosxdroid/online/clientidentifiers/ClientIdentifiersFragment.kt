/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientidentifiers

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.IdentifierListAdapter
import com.mifos.mifosxdroid.adapters.IdentifierListAdapter.IdentifierOptionsListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.identifierdialog.ClientIdentifierCreationListener
import com.mifos.mifosxdroid.dialogfragments.identifierdialog.IdentifierDialogFragment
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment
import com.mifos.objects.noncore.Identifier
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

class ClientIdentifiersFragment : MifosBaseFragment(), ClientIdentifiersMvpView, IdentifierOptionsListener, OnRefreshListener, ClientIdentifierCreationListener {
    @JvmField
    @BindView(R.id.rv_client_identifier)
    var rv_client_identifier: RecyclerView? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.noIdentifierText)
    var mNoIdentifierText: TextView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null

    @JvmField
    @BindView(R.id.noIdentifierIcon)
    var mNoIdentifierIcon: ImageView? = null

    @JvmField
    @Inject
    var mClientIdentifiersPresenter: ClientIdentifiersPresenter? = null

    @JvmField
    @Inject
    var identifierListAdapter: IdentifierListAdapter? = null
    private lateinit var rootView: View
    private var clientId = 0
    var identifiers: MutableList<Identifier>? = null
    private var mLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            clientId = requireArguments().getInt(Constants.CLIENT_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_client_identifiers, container, false)
        ButterKnife.bind(this, rootView)
        mClientIdentifiersPresenter!!.attachView(this)
        showUserInterface()
        loadIdentifiers()
        return rootView
    }

    fun loadIdentifiers() {
        mClientIdentifiersPresenter!!.loadIdentifiers(clientId)
    }

    override fun onRefresh() {
        loadIdentifiers()
    }

    override fun showUserInterface() {
        setToolbarTitle(getString(R.string.identifiers))
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        identifierListAdapter!!.setIdentifierOptionsListener(this)
        rv_client_identifier!!.layoutManager = mLayoutManager
        rv_client_identifier!!.setHasFixedSize(true)
        rv_client_identifier!!.adapter = identifierListAdapter
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
    }

    override fun showClientIdentifiers(identifiers: MutableList<Identifier>) {
        this.identifiers = identifiers
        identifierListAdapter!!.setIdentifiers(identifiers)
        identifierListAdapter!!.notifyDataSetChanged()
        if (identifiers.isEmpty()) {
            showEmptyClientIdentifier()
        } else {
            if (ll_error!!.visibility == View.VISIBLE) {
                ll_error!!.visibility = View.GONE
            }
        }
    }

    private fun showEmptyClientIdentifier() {
        ll_error!!.visibility = View.VISIBLE
        mNoIdentifierText!!.text = resources.getString(R.string.no_identifier_to_show)
        mNoIdentifierIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
    }

    override fun onClientIdentifierCreationSuccess(identifier: Identifier) {
        if (identifiers!!.size == 0) {
            //The list is empty prior to adding the new identifier. Remove the empty list message.
            ll_error!!.visibility = View.GONE
        }
        identifiers!!.add(identifier)
        identifierListAdapter!!.notifyItemInserted(identifiers!!.size - 1)
    }

    override fun onClientIdentifierCreationFailure(errorMessage: String) {
        Toaster.show(rootView, errorMessage)
    }

    override fun showFetchingError(errorMessage: Int) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onClickIdentifierOptions(position: Int, view: View) {
        val popup = PopupMenu(context!!, view)
        popup.menuInflater.inflate(R.menu.menu_client_identifier, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_remove_identifier -> mClientIdentifiersPresenter!!.deleteIdentifier(clientId,
                        identifiers!![position].id, position)
                R.id.menu_identifier_documents -> {
                    val documentListFragment = DocumentListFragment.newInstance(
                            Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS,
                            identifiers!![position].id)
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_IDENTIFIER)
                    fragmentTransaction.replace(R.id.container, documentListFragment)
                    fragmentTransaction.commit()
                }
                else -> {
                }
            }
            true
        }
        popup.show()
    }

    override fun identifierDeletedSuccessfully(position: Int) {
        Toast.makeText(activity, R.string.identifier_deleted_successfully,
                Toast.LENGTH_SHORT).show()
        identifiers!!.removeAt(position)
        identifierListAdapter!!.notifyItemRemoved(position)
    }

    override fun showProgressbar(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = show
        if (show && identifierListAdapter!!.itemCount == 0) {
            showMifosProgressBar()
            swipeRefreshLayout!!.isRefreshing = false
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                val identifierDialogFragment = IdentifierDialogFragment.newInstance(clientId)
                identifierDialogFragment.setOnClientIdentifierCreationListener(this)
                val fragmentTransaction = requireActivity().supportFragmentManager
                        .beginTransaction()
                fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST)
                identifierDialogFragment.show(fragmentTransaction, "Identifier Dialog Fragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mClientIdentifiersPresenter!!.detachView()
        hideMifosProgressBar()
    }

    companion object {
        fun newInstance(clientId: Int): ClientIdentifiersFragment {
            val fragment = ClientIdentifiersFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            fragment.arguments = args
            return fragment
        }
    }
}