/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loancharge

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentChargeListBinding
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

    private lateinit var binding: FragmentChargeListBinding

    @JvmField
    @Inject
    var mLoanChargePresenter: LoanChargePresenter? = null
    private var chargesList: MutableList<Charges> = ArrayList()
    private var mChargesNameListAdapter: ChargeNameListAdapter? = null
    private var loanAccountNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChargeListBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        mLoanChargePresenter!!.attachView(this)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvCharge.layoutManager = layoutManager
        binding.rvCharge.setHasFixedSize(true)


        //Loading LoanChargesList
        mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
        setToolbarTitle(getString(R.string.charges))
        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        binding.swipeContainer.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light)
        binding.swipeContainer.setOnRefreshListener {
            mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
            if (binding.swipeContainer.isRefreshing) binding.swipeContainer.isRefreshing = false
        }
        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        binding.rvCharge.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {

                //Future Implementation
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noChargesIcon.setOnClickListener { reloadOnError() }
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Center list.
     */
    fun reloadOnError() {
        binding.llError.visibility = View.GONE
        mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
    }

    fun setChargesList(chargesList: MutableList<Charges>) {
        this.chargesList = chargesList
    }

    override fun showLoanChargesList(charges: MutableList<Charges>) {
        chargesList = charges
        mChargesNameListAdapter = ChargeNameListAdapter(chargesList, loanAccountNumber)
        binding.rvCharge.adapter = mChargesNameListAdapter
        if (charges.size == 0) {
            binding.llError.visibility = View.VISIBLE
            binding.noChargesText.text = getString(R.string.message_no_charges_available)
            binding.noChargesIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showFetchingError(s: String) {
        binding.llError.visibility = View.VISIBLE
        binding.noChargesText.text = "$s\n Click to Refresh "
        mLoanChargePresenter!!.loadLoanChargesList(loanAccountNumber)
        Toaster.show(binding.root, s)
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
        Toaster.show(binding.root, getString(R.string.message_charge_created_success))
        mChargesNameListAdapter!!.notifyItemInserted(chargesList.size - 1)
    }

    override fun onChargeCreatedFailure(errorMessage: String) {
        Toaster.show(binding.root, errorMessage)
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