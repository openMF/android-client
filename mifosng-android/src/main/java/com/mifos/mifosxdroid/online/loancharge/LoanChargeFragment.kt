/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loancharge

import android.os.Bundle
import android.view.*
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
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.chargedialog.OnChargeCreateListener
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogFragment
import com.mifos.objects.client.Charges
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */
class LoanChargeFragment : MifosBaseFragment(), LoanChargeMvpView, OnChargeCreateListener {
    @kotlin.jvm.JvmField
    @BindView(R.id.rv_charge)
    var rv_charges: RecyclerView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.noChargesText)
    var mNoChargesText: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.noChargesIcon)
    var mNoChargesIcon: ImageView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanChargePresenter: LoanChargePresenter? = null
    private var chargesList: MutableList<Charges> = ArrayList()
    private var mChargesNameListAdapter: ChargeNameListAdapter? = null
    private lateinit var rootView: View
    private var loanAccountNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_charge_list, container, false)
        setHasOptionsMenu(true)
        ButterKnife.bind(this, rootView)
        mLoanChargePresenter!!.attachView(this)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_charges!!.layoutManager = layoutManager
        rv_charges!!.setHasFixedSize(true)


        //Loading LoanChargesList
        mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
        setToolbarTitle(getString(R.string.charges))
        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        swipeRefreshLayout!!.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light)
        swipeRefreshLayout!!.setOnRefreshListener {
            mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
            if (swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout!!.isRefreshing = false
        }
        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        rv_charges!!.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {

                //Future Implementation
            }
        })
        return rootView
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Center list.
     */
    @OnClick(R.id.noChargesIcon)
    fun reloadOnError() {
        ll_error!!.visibility = View.GONE
        mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
    }

    fun setChargesList(chargesList: MutableList<Charges>) {
        this.chargesList = chargesList
    }

    override fun showLoanChargesList(charges: MutableList<Charges>) {
        chargesList = charges
        mChargesNameListAdapter = ChargeNameListAdapter(chargesList, loanAccountNumber)
        rv_charges!!.adapter = mChargesNameListAdapter
        if (charges.size == 0) {
            ll_error!!.visibility = View.VISIBLE
            mNoChargesText!!.text = getString(R.string.message_no_charges_available)
            mNoChargesIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showFetchingError(s: String) {
        ll_error!!.visibility = View.VISIBLE
        mNoChargesText!!.text = "$s\n Click to Refresh "
        mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
        Toaster.show(rootView, s)
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressBar()
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanChargePresenter!!.detachView()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val menuItemAddNewLoanCharge = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_LOAN_CHARGES,
                Menu.NONE, getString(R.string.add_new))
        menuItemAddNewLoanCharge.icon = resources.getDrawable(R.drawable.ic_add_white_24dp)
        menuItemAddNewLoanCharge.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == MENU_ITEM_ADD_NEW_LOAN_CHARGES) {
            val loanChargeDialogFragment = LoanChargeDialogFragment
                    .newInstance(loanAccountNumber)
            loanChargeDialogFragment.setOnChargeCreateListener(this)
            val fragmentTransaction = requireActivity().supportFragmentManager
                    .beginTransaction()
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CHARGE_LIST)
            loanChargeDialogFragment.show(fragmentTransaction, "Loan Charge Dialog Fragment")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onChargeCreatedSuccess(charge: Charges) {
        chargesList.add(charge)
        Toaster.show(rootView, getString(R.string.message_charge_created_success))
        mChargesNameListAdapter!!.notifyItemInserted(chargesList.size - 1)
    }

    override fun onChargeCreatedFailure(errorMessage: String) {
        Toaster.show(rootView, errorMessage)
    }

    companion object {
        const val MENU_ITEM_ADD_NEW_LOAN_CHARGES = 3000
        fun newInstance(loanAccountNumber: Int,
                        chargesList: MutableList<Charges>?): LoanChargeFragment {
            val fragment = LoanChargeFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            fragment.arguments = args
            if (chargesList != null) fragment.setChargesList(chargesList)
            return fragment
        }

        fun newInstance(loanAccountNumber: Int, chargesList: MutableList<Charges>, isParentFragmentAGroupFragment: Boolean): LoanChargeFragment {
            val fragment = LoanChargeFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            fragment.arguments = args
            fragment.setChargesList(chargesList)
            return fragment
        }
    }
}