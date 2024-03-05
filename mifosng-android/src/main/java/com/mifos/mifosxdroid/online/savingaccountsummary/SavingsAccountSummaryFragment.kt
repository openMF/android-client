/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingaccountsummary

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.accounts.savings.Status
import com.mifos.core.objects.accounts.savings.Transaction
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SavingsAccountTransactionsListAdapter
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.databinding.FragmentSavingsAccountSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class SavingsAccountSummaryFragment : ProgressableFragment() {

    private lateinit var binding: FragmentSavingsAccountSummaryBinding

    var savingsAccountNumber = 0
    var savingsAccountType: DepositType? = null
    private val arg: SavingsAccountSummaryFragmentArgs by navArgs()

    private lateinit var viewModel: SavingsAccountSummaryViewModel

    // Cached List of all savings account transactions
    // that are used for inflation of rows in
    // Infinite Scroll View
    private var listOfAllTransactions: MutableList<Transaction> = ArrayList()
    private var savingsAccountTransactionsListAdapter: SavingsAccountTransactionsListAdapter? = null
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savingsAccountNumber = arg.savingsAccountNumber
        savingsAccountType = arg.accountType
        parentFragment = arg.parentFragment
        inflateSavingsAccountSummary()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavingsAccountSummaryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SavingsAccountSummaryViewModel::class.java]
        viewModel.loadSavingAccount(savingsAccountType?.endpoint, savingsAccountNumber)

        viewModel.savingsAccountSummaryUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SavingsAccountSummaryUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is SavingsAccountSummaryUiState.ShowProgressbar -> showProgressbar(true)
                is SavingsAccountSummaryUiState.ShowSavingAccount -> {
                    showProgressbar(false)
                    showSavingAccount(it.savingsAccountWithAssociations)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btDeposit.setOnClickListener {
            onDepositButtonClicked()
        }

        binding.btApproveSaving.setOnClickListener {
            onProcessTransactionClicked()
        }

        binding.btWithdrawal.setOnClickListener {
            onWithdrawalButtonClicked()
        }
    }

    /**
     * This Method setting the ToolBar Title and Requesting the API for Saving Account
     */
    private fun inflateSavingsAccountSummary() {
        showProgress(true)
        when (savingsAccountType?.serverType) {
            DepositType.ServerTypes.RECURRING -> setToolbarTitle(resources.getString(R.string.recurringAccountSummary))
            else -> setToolbarTitle(resources.getString(R.string.savingsAccountSummary))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        menu.add(
            Menu.NONE,
            MENU_ITEM_DATA_TABLES,
            Menu.NONE,
            Constants.DATA_TABLE_SAVINGS_ACCOUNTS_NAME
        )
        menu.add(
            Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE,
            resources.getString(R.string.documents)
        )
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

    private fun onDepositButtonClicked() {
        doTransaction(
            savingsAccountWithAssociations,
            Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT, savingsAccountType
        )
    }

    private fun onWithdrawalButtonClicked() {
        doTransaction(
            savingsAccountWithAssociations,
            Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL, savingsAccountType
        )
    }

    private fun onProcessTransactionClicked() {
        when (processSavingTransactionAction) {
            ACTION_APPROVE_SAVINGS -> {
                approveSavings()
            }

            ACTION_ACTIVATE_SAVINGS -> {
                activateSavings()
            }

            else -> {
                Log.i(
                    requireActivity().localClassName,
                    resources.getString(R.string.transaction_action_not_set)
                )
            }
        }
    }

    private fun enableInfiniteScrollOfTransactions() {
        binding.lvSavingsTransactions.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {
                loadmore = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
            }

            override fun onScroll(
                absListView: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
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
        index = binding.lvSavingsTransactions.firstVisiblePosition
        val v = binding.lvSavingsTransactions.getChildAt(0)
        top = v?.top ?: 0
        last += 5
        if (last > listOfAllTransactions.size) {
            last = listOfAllTransactions.size
            savingsAccountTransactionsListAdapter = SavingsAccountTransactionsListAdapter(
                requireActivity(),
                listOfAllTransactions.subList(initial, last)
            )
            savingsAccountTransactionsListAdapter?.notifyDataSetChanged()
            binding.lvSavingsTransactions.adapter = savingsAccountTransactionsListAdapter
            binding.lvSavingsTransactions.setSelectionFromTop(index, top)
            return
        }
        savingsAccountTransactionsListAdapter = SavingsAccountTransactionsListAdapter(
            requireActivity(),
            listOfAllTransactions.subList(initial, last)
        )
        savingsAccountTransactionsListAdapter?.notifyDataSetChanged()
        binding.lvSavingsTransactions.adapter = savingsAccountTransactionsListAdapter
        binding.lvSavingsTransactions.setSelectionFromTop(index, top)
    }

    private fun loadDocuments() {
        val action =
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToDocumentListFragment(
                savingsAccountNumber,
                Constants.ENTITY_TYPE_SAVINGS
            )
        findNavController().navigate(action)
    }

    private fun approveSavings() {
        val action = savingsAccountType?.let {
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToSavingsAccountApprovalFragment(
                savingsAccountNumber,
                it
            )
        }
        action?.let { findNavController().navigate(it) }
    }

    private fun activateSavings() {
        val action = savingsAccountType?.let {
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToSavingsAccountActivateFragment(
                savingsAccountNumber,
                it
            )
        }
        action?.let { findNavController().navigate(it) }
    }

    private fun loadSavingsDataTables() {
        val action =
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToDataTableFragment(
                Constants.DATA_TABLE_NAME_SAVINGS,
                savingsAccountNumber
            )
        findNavController().navigate(action)
    }

    fun toggleTransactionCapabilityOfAccount(status: Status) {
        if (!status.active!!) {
            binding.btDeposit.visibility = View.GONE
            binding.btWithdrawal.visibility = View.GONE
        }
    }

    private fun showSavingAccount(savingsAccountWithAssociations: SavingsAccountWithAssociations?) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        if (savingsAccountWithAssociations != null) {
            this.savingsAccountWithAssociations = savingsAccountWithAssociations
            binding.tvClientName.text = savingsAccountWithAssociations.clientName
            binding.tvSavingsProductShortName.text =
                savingsAccountWithAssociations.savingsProductName
            binding.tvSavingsAccountNumber.text =
                savingsAccountWithAssociations.accountNo.toString()
            if (savingsAccountWithAssociations.summary?.totalInterestEarned != null) {
                binding.tvInterestEarned.text = savingsAccountWithAssociations
                    .summary?.totalInterestEarned.toString()
            } else {
                binding.tvInterestEarned.text = "0.0"
            }
            binding.tvSavingsAccountBalance.text = savingsAccountWithAssociations
                .summary?.accountBalance.toString()
            if (savingsAccountWithAssociations.summary?.totalDeposits != null) {
                binding.tvTotalDeposits.text = savingsAccountWithAssociations
                    .summary?.totalDeposits.toString()
            } else {
                binding.tvTotalDeposits.text = "0.0"
            }
            if (savingsAccountWithAssociations.summary?.totalWithdrawals != null) {
                binding.tvTotalWithdrawals.text = savingsAccountWithAssociations
                    .summary?.totalWithdrawals.toString()
            } else {
                binding.tvTotalWithdrawals.text = "0.0"
            }
            savingsAccountTransactionsListAdapter = SavingsAccountTransactionsListAdapter(
                requireActivity(),
                if (savingsAccountWithAssociations.transactions.size < last) savingsAccountWithAssociations.transactions else savingsAccountWithAssociations.transactions.subList(
                    initial,
                    last
                )
            )
            binding.lvSavingsTransactions.adapter = savingsAccountTransactionsListAdapter

            // Cache transactions here
            listOfAllTransactions.addAll(savingsAccountWithAssociations.transactions)
            binding.lvSavingsTransactions.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, i, l ->
                    showTransaction(i)
                }
            if (savingsAccountWithAssociations.status?.submittedAndPendingApproval == true) {
                binding.btApproveSaving.isEnabled = true
                binding.btDeposit.visibility = View.GONE
                binding.btWithdrawal.visibility = View.GONE
                binding.btApproveSaving.text = resources.getString(R.string.approve_savings)
                processSavingTransactionAction = ACTION_APPROVE_SAVINGS
            } else if (!savingsAccountWithAssociations.status?.active!!) {
                binding.btApproveSaving.isEnabled = true
                binding.btDeposit.visibility = View.GONE
                binding.btWithdrawal.visibility = View.GONE
                binding.btApproveSaving.text = resources.getString(R.string.activate_savings)
                processSavingTransactionAction = ACTION_ACTIVATE_SAVINGS
            } else if (savingsAccountWithAssociations.status?.closed == true) {
                binding.btApproveSaving.isEnabled = false
                binding.btDeposit.visibility = View.GONE
                binding.btWithdrawal.visibility = View.GONE
                binding.btApproveSaving.text =
                    resources.getString(R.string.savings_account_closed)
            } else {
                inflateSavingsAccountSummary()
                binding.btApproveSaving.visibility = View.GONE
            }
            if (listOfAllTransactions.isEmpty()) {
                binding.savingsTransactions.visibility = View.INVISIBLE
                binding.noSavingTransactionsView.visibility = View.VISIBLE
            }
            enableInfiniteScrollOfTransactions()
        }
    }

    private fun showTransaction(i: Int) {
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
        transactionType.text = transaction.transactionType?.value
        runningBalance.text = transaction.runningBalance.toString()
        savingAccountId.text = transaction.accountId.toString()
        accountNumber.text = transaction.accountNo
        currency.text = transaction.currency?.name
        dialog.show()
    }

    private fun showSavingsActivatedSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(
            activity, resources.getString(R.string.savings_account_activated),
            Toast.LENGTH_LONG
        ).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showFetchingError(s: Int) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    private fun showFetchingError(errorMessage: String?) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    private fun doTransaction(
        savingsAccountWithAssociations: SavingsAccountWithAssociations?,
        transactionType: String?,
        accountType: DepositType?
    ) {
        val action = savingsAccountWithAssociations?.let {
            transactionType?.let { it1 ->
                accountType?.let { it2 ->
                    SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToSavingsAccountTransactionFragment(
                        it, it1, it2
                    )
                }
            }
        }
        action?.let { findNavController().navigate(it) }
    }

    companion object {
        const val MENU_ITEM_DATA_TABLES = 1001
        const val MENU_ITEM_DOCUMENTS = 1004
        private const val ACTION_APPROVE_SAVINGS = 4
        private const val ACTION_ACTIVATE_SAVINGS = 5
    }
}