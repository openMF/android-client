/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientcharge

import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentChargeListBinding
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogFragment
import com.mifos.mifosxdroid.dialogfragments.chargedialog.OnChargeCreateListener
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */
class ClientChargeFragment : MifosBaseFragment(), ClientChargeMvpView, OnChargeCreateListener {
    private lateinit var binding: FragmentChargeListBinding

    private lateinit var chargesList: List<Charges>

    @JvmField
    @Inject
    var mClientChargePresenter: ClientChargePresenter? = null
    var mChargesNameListAdapter: ChargeNameListAdapter? = null
    private var clientId = 0
    private var mApiRestCounter = 0
    private val limit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) clientId = requireArguments().getInt(Constants.CLIENT_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChargeListBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        mClientChargePresenter!!.attachView(this)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvCharge.layoutManager = layoutManager
        binding.rvCharge.setHasFixedSize(true)
        setToolbarTitle(getString(R.string.charges))
        mApiRestCounter = 1
        mClientChargePresenter!!.loadCharges(clientId, 0, limit)
        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        binding.swipeContainer.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light)
        binding.swipeContainer.setOnRefreshListener {
            mApiRestCounter = 1
            mClientChargePresenter!!.loadCharges(clientId, 0, limit)
            if (binding.swipeContainer.isRefreshing) binding.swipeContainer.isRefreshing = false
        }
        loadMore(layoutManager)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noChargesIcon.setOnClickListener { reloadOnError() }
    }

    /**
     * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
     * is shown on the Screen.
     * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
     * and offset(mCenterList.size()) and limit(100).
     */
    private fun loadMore(layoutManager: LinearLayoutManager) {
        binding.rvCharge.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
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
    fun reloadOnError() {
        binding.llError.visibility = View.GONE
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
            binding.rvCharge.adapter = mChargesNameListAdapter
            binding.llError.visibility = View.GONE
        } else {
            chargesList.addAll()
            mChargesNameListAdapter!!.notifyDataSetChanged()

            //checking the response size if size is zero then show toast No More
            // Clients Available for fetch
            if (chargesPage!!.pageItems.size == 0 &&
                    chargesPage.totalFilteredRecords == chargesList.size) Toaster.show(binding.root, getString(R.string.message_no_more_charge))
        }
    }

    override fun onChargeCreatedSuccess(charge: Charges) {
        chargesList.add()
        Toaster.show(binding.root, getString(R.string.message_charge_created_success))
        if (binding.llError.visibility == View.VISIBLE) {
            binding.llError.visibility = View.GONE
        }
        //If the adapter has not been initialized, there were 0 charge items earlier. Initialize it.
        if (mChargesNameListAdapter == null) {
            mChargesNameListAdapter = ChargeNameListAdapter(chargesList, clientId)
            binding.rvCharge.adapter = mChargesNameListAdapter
        }
        mChargesNameListAdapter!!.notifyItemInserted(chargesList.size - 1)
    }

    override fun onChargeCreatedFailure(errorMessage: String) {
        Toaster.show(binding.root, errorMessage)
    }

    override fun showEmptyCharges() {
        if (mChargesNameListAdapter == null || mChargesNameListAdapter!!.itemCount == 0) {
            binding.llError.visibility = View.VISIBLE
            binding.noChargesText.text = resources.getString(R.string.message_no_charges_available)
            binding.noChargesIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showFetchingErrorCharges(s: String) {
        if (mApiRestCounter == 1) {
            binding.llError.visibility = View.VISIBLE
            binding.noChargesText.text = "$s\n Click to Refresh "
        }
        Toaster.show(binding.root, s)
    }

    override fun showProgressbar(b: Boolean) {
        if (mApiRestCounter == 1) {
            if (b) {
                showMifosProgressBar()
            } else {
                hideMifosProgressBar()
            }
        } else {
            binding.swipeContainer.isRefreshing = b
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
