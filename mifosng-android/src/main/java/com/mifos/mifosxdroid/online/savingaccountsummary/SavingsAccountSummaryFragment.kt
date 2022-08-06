/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingaccountsummary

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SavingsAccountTransactionsListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.online.datatable.DataTableFragment
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment
import com.mifos.mifosxdroid.online.savingsaccountactivate.SavingsAccountActivateFragment
import com.mifos.mifosxdroid.online.savingsaccountapproval.SavingsAccountApprovalFragment
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.accounts.savings.DepositType.ServerTypes
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.accounts.savings.Status
import com.mifos.objects.accounts.savings.Transaction
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class SavingsAccountSummaryFragment : ProgressableFragment(), SavingsAccountSummaryMvpView {
    var savingsAccountNumber = 0
    var savingsAccountType: DepositType? = null

    @JvmField
    @BindView(R.id.tv_clientName)
    var tv_clientName: TextView? = null

    @JvmField
    @BindView(R.id.quickContactBadge_client)
    var quickContactBadge: QuickContactBadge? = null

    @JvmField
    @BindView(R.id.tv_savings_product_short_name)
    var tv_savingsProductName: TextView? = null

    @JvmField
    @BindView(R.id.tv_savingsAccountNumber)
    var tv_savingsAccountNumber: TextView? = null

    @JvmField
    @BindView(R.id.tv_savings_account_balance)
    var tv_savingsAccountBalance: TextView? = null

    @JvmField
    @BindView(R.id.tv_total_deposits)
    var tv_totalDeposits: TextView? = null

    @JvmField
    @BindView(R.id.tv_total_withdrawals)
    var tv_totalWithdrawals: TextView? = null

    @JvmField
    @BindView(R.id.lv_savings_transactions)
    var lv_Transactions: ListView? = null

    @JvmField
    @BindView(R.id.tv_interest_earned)
    var tv_interestEarned: TextView? = null

    @JvmField
    @BindView(R.id.bt_deposit)
    var bt_deposit: Button? = null

    @JvmField
    @BindView(R.id.bt_withdrawal)
    var bt_withdrawal: Button? = null

    @JvmField
    @BindView(R.id.bt_approve_saving)
    var bt_approve_saving: Button? = null

    @JvmField
    @Inject
    var mSavingAccountSummaryPresenter: SavingsAccountSummaryPresenter? = null

    // Cached List of all savings account transactions
    // that are used for inflation of rows in
    // Infinite Scroll View
    var listOfAllTransactions: MutableList<Transaction> = ArrayList()
    var savingsAccountTransactionsListAdapter: SavingsAccountTransactionsListAdapter? = null
    private lateinit var rootView: View
    private var processSavingTransactionAction = -1
    private var savingsAccountWithAssociations: SavingsAccountWithAssociations? = null
    private var parentFragment = true
    private var loadmore by Delegates.notNull<Boolean>() // variable to enable and disable loading of data into listview = false

    // variables to capture position of first visible items
    // so that while loading the listview does not scroll automatically
    private var index = 0
    private var top = 0

    // variables to control amount of data loading on each load
    private val initial = 0
    private var last = 5
    private var mListener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            savingsAccountNumber = requireArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER)
            savingsAccountType = requireArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE)
            parentFragment = requireArguments().getBoolean(Constants.IS_A_PARENT_FRAGMENT)
        }
        inflateSavingsAccountSummary()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_savings_account_summary, container, false)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        ButterKnife.bind(this, rootView)
        mSavingAccountSummaryPresenter!!.attachView(this)
        mSavingAccountSummaryPresenter!!
                .loadSavingAccount(savingsAccountType!!.endpoint, savingsAccountNumber)
        return rootView
    }

    /**
     * This Method setting the ToolBar Title and Requesting the API for Saving Account
     */
    fun inflateSavingsAccountSummary() {
        showProgress(true)
        when (savingsAccountType!!.serverType) {
            ServerTypes.RECURRING -> setToolbarTitle(resources.getString(R.string.recurringAccountSummary))
            else -> setToolbarTitle(resources.getString(R.string.savingsAccountSummary))
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = try {
            activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        if (!parentFragment) {
            requireActivity().finish()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        menu.add(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_SAVINGS_ACCOUNTS_NAME)
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE,
                resources.getString(R.string.documents))
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == MENU_ITEM_DOCUMENTS) {
            loadDocuments()
        } else if (id == MENU_ITEM_DATA_TABLES) {
            loadSavingsDataTables()
        }
        return super.onOptionsItemSelected(item)
    }

    @OnClick(R.id.bt_deposit)
    fun onDepositButtonClicked() {
        mListener!!.doTransaction(savingsAccountWithAssociations,
                Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT, savingsAccountType)
    }

    @OnClick(R.id.bt_withdrawal)
    fun onWithdrawalButtonClicked() {
        mListener!!.doTransaction(savingsAccountWithAssociations,
                Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL, savingsAccountType)
    }

    @OnClick(R.id.bt_approve_saving)
    fun onProcessTransactionClicked() {
        if (processSavingTransactionAction == ACTION_APPROVE_SAVINGS) {
            approveSavings()
        } else if (processSavingTransactionAction == ACTION_ACTIVATE_SAVINGS) {
            activateSavings()
        } else {
            Log.i(requireActivity().localClassName,
                    resources.getString(R.string.transaction_action_not_set))
        }
    }

    fun enableInfiniteScrollOfTransactions() {
        lv_Transactions!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {
                loadmore = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
            }

            override fun onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val lastItem = firstVisibleItem + visibleItemCount
                if (firstVisibleItem == 0) return
                if (lastItem == totalItemCount && loadmore) {
                    loadmore = false
                    loadNextFiveTransactions()
                }
            }
        })
    }

    fun loadNextFiveTransactions() {
        index = lv_Transactions!!.firstVisiblePosition
        val v = lv_Transactions!!.getChildAt(0)
        top = v?.top ?: 0
        last += 5
        if (last > listOfAllTransactions.size) {
            last = listOfAllTransactions.size
            savingsAccountTransactionsListAdapter = SavingsAccountTransactionsListAdapter(activity,
                    listOfAllTransactions.subList(initial, last))
            savingsAccountTransactionsListAdapter!!.notifyDataSetChanged()
            lv_Transactions!!.adapter = savingsAccountTransactionsListAdapter
            lv_Transactions!!.setSelectionFromTop(index, top)
            return
        }
        savingsAccountTransactionsListAdapter = SavingsAccountTransactionsListAdapter(activity,
                listOfAllTransactions.subList(initial, last))
        savingsAccountTransactionsListAdapter!!.notifyDataSetChanged()
        lv_Transactions!!.adapter = savingsAccountTransactionsListAdapter
        lv_Transactions!!.setSelectionFromTop(index, top)
    }

    fun loadDocuments() {
        val documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_SAVINGS, savingsAccountNumber)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, documentListFragment)
        fragmentTransaction.commit()
    }

    fun approveSavings() {
        val savingsAccountApproval = SavingsAccountApprovalFragment
                .newInstance(savingsAccountNumber, savingsAccountType)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, savingsAccountApproval)
        fragmentTransaction.commit()
    }

    fun activateSavings() {
        val savingsAccountApproval = SavingsAccountActivateFragment
                .newInstance(savingsAccountNumber, savingsAccountType)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, savingsAccountApproval)
        fragmentTransaction.commit()
    }

    fun loadSavingsDataTables() {
        val loanAccountFragment = DataTableFragment.newInstance(Constants.DATA_TABLE_NAME_SAVINGS, savingsAccountNumber)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SAVINGS_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, loanAccountFragment)
        fragmentTransaction.commit()
    }

    fun toggleTransactionCapabilityOfAccount(status: Status) {
        if (!status.active) {
            bt_deposit!!.visibility = View.GONE
            bt_withdrawal!!.visibility = View.GONE
        }
    }

    override fun showSavingAccount(savingsAccountWithAssociations: SavingsAccountWithAssociations?) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        if (savingsAccountWithAssociations != null) {
            this.savingsAccountWithAssociations = savingsAccountWithAssociations
            tv_clientName!!.text = savingsAccountWithAssociations.clientName
            tv_savingsProductName!!.text = savingsAccountWithAssociations.savingsProductName
            tv_savingsAccountNumber!!.text = savingsAccountWithAssociations.accountNo
            if (savingsAccountWithAssociations.summary.totalInterestEarned != null) {
                tv_interestEarned!!.text = savingsAccountWithAssociations
                        .summary.totalInterestEarned.toString()
            } else {
                tv_interestEarned!!.text = "0.0"
            }
            tv_savingsAccountBalance!!.text = savingsAccountWithAssociations
                    .summary.accountBalance.toString()
            if (savingsAccountWithAssociations.summary.totalDeposits != null) {
                tv_totalDeposits!!.text = savingsAccountWithAssociations
                        .summary.totalDeposits.toString()
            } else {
                tv_totalDeposits!!.text = "0.0"
            }
            if (savingsAccountWithAssociations.summary.totalWithdrawals != null) {
                tv_totalWithdrawals!!.text = savingsAccountWithAssociations
                        .summary.totalWithdrawals.toString()
            } else {
                tv_totalWithdrawals!!.text = "0.0"
            }
            savingsAccountTransactionsListAdapter = SavingsAccountTransactionsListAdapter(activity,
                    if (savingsAccountWithAssociations.transactions.size < last) savingsAccountWithAssociations.transactions else savingsAccountWithAssociations.transactions.subList(initial, last)
            )
            lv_Transactions!!.adapter = savingsAccountTransactionsListAdapter

            // Cache transactions here
            listOfAllTransactions.addAll(savingsAccountWithAssociations.transactions)
            lv_Transactions!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                showTransaction(i)
            }
            if (savingsAccountWithAssociations.status.submittedAndPendingApproval) {
                bt_approve_saving!!.isEnabled = true
                bt_deposit!!.visibility = View.GONE
                bt_withdrawal!!.visibility = View.GONE
                bt_approve_saving!!.text = resources.getString(R.string.approve_savings)
                processSavingTransactionAction = ACTION_APPROVE_SAVINGS
            } else if (!savingsAccountWithAssociations.status.active) {
                bt_approve_saving!!.isEnabled = true
                bt_deposit!!.visibility = View.GONE
                bt_withdrawal!!.visibility = View.GONE
                bt_approve_saving!!.text = resources.getString(R.string.activate_savings)
                processSavingTransactionAction = ACTION_ACTIVATE_SAVINGS
            } else if (savingsAccountWithAssociations.status.closed) {
                bt_approve_saving!!.isEnabled = false
                bt_deposit!!.visibility = View.GONE
                bt_withdrawal!!.visibility = View.GONE
                bt_approve_saving!!
                        .setText(resources.getString(R.string.savings_account_closed))
            } else {
                inflateSavingsAccountSummary()
                bt_approve_saving!!.visibility = View.GONE
            }
            enableInfiniteScrollOfTransactions()
        }
    }

    fun showTransaction(i: Int) {
        val transaction = listOfAllTransactions[i]
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.transaction_detail_dialog_layout)
        val transactionId = dialog.findViewById<TextView>(R.id.tv_transactionId)
        val date = dialog.findViewById<TextView>(R.id.tv_date)
        val transactionType = dialog.findViewById<TextView>(R.id.tv_transactionType)
        val runningBalance = dialog.findViewById<TextView>(R.id.tv_runningBalance)
        val savingAccountId = dialog.findViewById<TextView>(R.id.tv_savingAccountId)
        val accountNumber = dialog.findViewById<TextView>(R.id.tv_accountNumber)
        val currency = dialog.findViewById<TextView>(R.id.tv_currency)
        val dateList = transaction.date
        transactionId.text = transaction.id.toString()
        date.text = String.format("%s-%s-%s", dateList[0], dateList[1], dateList[2])
        transactionType.text = transaction.transactionType.value
        runningBalance.text = transaction.runningBalance.toString()
        savingAccountId.text = transaction.accountId.toString()
        accountNumber.text = transaction.accountNo
        currency.text = transaction.currency.name
        dialog.show()
    }

    override fun showSavingsActivatedSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, resources.getString(R.string.savings_account_activated),
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showFetchingError(s: Int) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showFetchingError(errorMessage: String?) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSavingAccountSummaryPresenter!!.detachView()
    }

    interface OnFragmentInteractionListener {
        fun doTransaction(savingsAccountWithAssociations: SavingsAccountWithAssociations?, transactionType: String?, accountType: DepositType?)
    }

    companion object {
        const val MENU_ITEM_DATA_TABLES = 1001
        const val MENU_ITEM_DOCUMENTS = 1004
        private const val ACTION_APPROVE_SAVINGS = 4
        private const val ACTION_ACTIVATE_SAVINGS = 5

        @JvmStatic
        fun newInstance(savingsAccountNumber: Int,
                        type: DepositType?,
                        parentFragment: Boolean): SavingsAccountSummaryFragment {
            val fragment = SavingsAccountSummaryFragment()
            val args = Bundle()
            args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber)
            args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type)
            args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, parentFragment)
            fragment.arguments = args
            return fragment
        }
    }
}