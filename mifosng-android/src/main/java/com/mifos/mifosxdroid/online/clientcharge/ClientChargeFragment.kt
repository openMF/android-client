/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientcharge

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogFragment
import com.mifos.mifosxdroid.dialogfragments.chargedialog.OnChargeCreateListener
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */
class ClientChargeFragment : MifosBaseFragment(), ClientChargeMvpView, OnChargeCreateListener {
    @JvmField
    @BindView(R.id.rv_charge)
    var rv_charges: RecyclerView? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.noChargesText)
    var mNoChargesText: TextView? = null

    @JvmField
    @BindView(R.id.noChargesIcon)
    var mNoChargesIcon: ImageView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null
    private lateinit var chargesList: List<Charges>

    @JvmField
    @Inject
    var mClientChargePresenter: ClientChargePresenter? = null
    var mChargesNameListAdapter: ChargeNameListAdapter? = null
    private lateinit var rootView: View
    private var clientId = 0
    private var mApiRestCounter = 0
    private val limit = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) clientId = requireArguments().getInt(Constants.CLIENT_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_charge_list, container, false)
        setHasOptionsMenu(true)
        ButterKnife.bind(this, rootView)
        mClientChargePresenter!!.attachView(this)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_charges!!.layoutManager = layoutManager
        rv_charges!!.setHasFixedSize(true)
        setToolbarTitle(getString(R.string.charges))
        mApiRestCounter = 1
        mClientChargePresenter!!.loadCharges(clientId, 0, limit)
        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        swipeRefreshLayout!!.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light)
        swipeRefreshLayout!!.setOnRefreshListener {
            mApiRestCounter = 1
            mClientChargePresenter!!.loadCharges(clientId, 0, limit)
            if (swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout!!.isRefreshing = false
        }
        loadMore(layoutManager)
        return rootView
    }

    /**
     * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
     * is shown on the Screen.
     * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
     * and offset(mCenterList.size()) and limit(100).
     */
    private fun loadMore(layoutManager: LinearLayoutManager) {
        rv_charges!!.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                mApiRestCounter = mApiRestCounter + 1
                mClientChargePresenter!!.loadCharges(clientId, chargesList.size, limit)
            }
        })
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Center list.
     */
    @OnClick(R.id.noChargesIcon)
    fun reloadOnError() {
        ll_error!!.visibility = View.GONE
        mClientChargePresenter!!.loadCharges(clientId, 0, limit)
    }

    fun setChargesList(chargesList: MutableList<Charges>) {
        this.chargesList = chargesList as ArrayList<Charges>
    }

    override fun showChargesList(chargesPage: Page<Charges?>?) {
        /**
         * if mApiRestCounter is 1, So this is the first Api Request.
         * else if mApiRestCounter is greater than 1, SO this is for loadmore request.
         */
        if (mApiRestCounter == 1) {
            chargesList = chargesPage!!.pageItems as ArrayList<Charges>
            mChargesNameListAdapter = ChargeNameListAdapter(chargesList, clientId)
            rv_charges!!.adapter = mChargesNameListAdapter
            ll_error!!.visibility = View.GONE
        } else {
            chargesList.addAll()
            mChargesNameListAdapter!!.notifyDataSetChanged()

            //checking the response size if size is zero then show toast No More
            // Clients Available for fetch
            if (chargesPage!!.pageItems.size == 0 &&
                    chargesPage.totalFilteredRecords == chargesList.size) Toaster.show(rootView, getString(R.string.message_no_more_charge))
        }
    }

    override fun onChargeCreatedSuccess(charge: Charges) {
        chargesList.add()
        Toaster.show(rootView, getString(R.string.message_charge_created_success))
        if (ll_error!!.visibility == View.VISIBLE) {
            ll_error!!.visibility = View.GONE
        }
        //If the adapter has not been initialized, there were 0 charge items earlier. Initialize it.
        if (mChargesNameListAdapter == null) {
            mChargesNameListAdapter = ChargeNameListAdapter(chargesList, clientId)
            rv_charges!!.adapter = mChargesNameListAdapter
        }
        mChargesNameListAdapter!!.notifyItemInserted(chargesList.size - 1)
    }

    override fun onChargeCreatedFailure(errorMessage: String) {
        Toaster.show(rootView, errorMessage)
    }

    override fun showEmptyCharges() {
        if (mChargesNameListAdapter == null || mChargesNameListAdapter!!.itemCount == 0) {
            ll_error!!.visibility = View.VISIBLE
            mNoChargesText!!.text = resources.getString(R.string.message_no_charges_available)
            mNoChargesIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showFetchingErrorCharges(s: String) {
        if (mApiRestCounter == 1) {
            ll_error!!.visibility = View.VISIBLE
            mNoChargesText!!.text = "$s\n Click to Refresh "
        }
        Toaster.show(rootView, s)
    }

    override fun showProgressbar(b: Boolean) {
        if (mApiRestCounter == 1) {
            if (b) {
                showMifosProgressBar()
            } else {
                hideMifosProgressBar()
            }
        } else {
            swipeRefreshLayout!!.isRefreshing = b
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mClientChargePresenter!!.detachView()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_CHARGES, Menu.NONE, getString(R.string.add_new))
        menuItemAddNewDocument.icon = ResourcesCompat.getDrawable(resources,
                R.drawable.ic_add_white_24dp, null)
        menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == MENU_ITEM_ADD_NEW_CHARGES) {
            val chargeDialogFragment = ChargeDialogFragment.newInstance(clientId)
            chargeDialogFragment.setOnChargeCreatedListener(this)
            val fragmentTransaction = requireActivity().supportFragmentManager
                    .beginTransaction()
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CHARGE_LIST)
            chargeDialogFragment.show(fragmentTransaction, "Charge Dialog Fragment")
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val MENU_ITEM_ADD_NEW_CHARGES = 2000
        fun newInstance(clientId: Int, chargesList: MutableList<Charges>?): ClientChargeFragment {
            val fragment = ClientChargeFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            fragment.arguments = args
            if (chargesList != null) fragment.setChargesList(chargesList)
            return fragment
        }

        fun newInstance(clientId: Int, chargesList: MutableList<Charges>,
                        isParentFragmentAGroupFragment: Boolean): ClientChargeFragment {
            val fragment = ClientChargeFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            fragment.arguments = args
            fragment.setChargesList(chargesList)
            return fragment
        }
    }
}

private fun <E> List<E>.add() {

}

private fun <E> List<E>.addAll() {

}
