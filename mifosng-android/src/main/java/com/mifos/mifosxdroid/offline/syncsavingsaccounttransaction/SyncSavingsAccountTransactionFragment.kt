package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SyncSavingsAccountTransactionAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding
import com.mifos.utils.Constants
import com.mifos.utils.Network.isOnline
import com.mifos.utils.PrefManager.userStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 19/08/16.
 */
@AndroidEntryPoint
class SyncSavingsAccountTransactionFragment : MifosBaseFragment(), OnRefreshListener,
    DialogInterface.OnClickListener {

    private lateinit var binding: FragmentSyncpayloadBinding

    val LOG_TAG = javaClass.simpleName

    private lateinit var viewModel: SyncSavingsAccountTransactionViewModel

    @Inject
    lateinit var mSyncSavingsAccountTransactionAdapter: SyncSavingsAccountTransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SyncSavingsAccountTransactionViewModel::class.java]
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSyncPayload.layoutManager = mLayoutManager
        binding.rvSyncPayload.setHasFixedSize(true)
        binding.rvSyncPayload.adapter = mSyncSavingsAccountTransactionAdapter
        binding.swipeContainer.setColorSchemeColors(
            *requireActivity()
                .resources.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)

        //Loading LoanRepayment Transactions  and PaymentTypeOptions From Database
        viewModel.loadDatabaseSavingsAccountTransactions()
        viewModel.loadPaymentTypeOption()

        viewModel.syncSavingsAccountTransactionUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions -> {
                    showProgressbar(false)
                    showEmptySavingsAccountTransactions(it.message)
                }

                is SyncSavingsAccountTransactionUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SyncSavingsAccountTransactionUiState.ShowPaymentTypeOptions -> {
                    showProgressbar(false)
                    showPaymentTypeOptions(it.paymentTypeOptions)
                }

                is SyncSavingsAccountTransactionUiState.ShowProgressbar -> showProgressbar(true)

                is SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions -> {
                    showProgressbar(false)
                    showSavingsAccountTransactions(it.savingsList)
                }
            }
        }

        return binding.root
    }

    /**
     * Loading All SavingsAccount Transactions from Database On SwipeRefresh
     */
    override fun onRefresh() {
        //Loading LoanRepayment Transactions and PaymentTypeOptions From Database
        viewModel.loadDatabaseSavingsAccountTransactions()
        viewModel.loadPaymentTypeOption()
        if (binding.swipeContainer.isRefreshing) {
            binding.swipeContainer.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noPayloadIcon.setOnClickListener {
            reloadOnError()
        }
    }

    /**
     * Show when Database response is null or failed to fetch the
     * List<SavingsAccountTransactionRequest>
     * Onclick Send Fresh Request for List<SavingsAccountTransactionRequest>.
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */

    private fun reloadOnError() {
        binding.llError.visibility = View.GONE
        viewModel.loadDatabaseSavingsAccountTransactions()
        viewModel.loadPaymentTypeOption()
    }

    private fun showOfflineModeDialog() {
        MaterialDialog.Builder().init(activity)
            .setTitle(R.string.offline_mode)
            .setMessage(R.string.dialog_message_offline_sync_alert)
            .setPositiveButton(R.string.dialog_action_go_online, this)
            .setNegativeButton(R.string.dialog_action_cancel, this)
            .createMaterialDialog()
            .show()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            DialogInterface.BUTTON_POSITIVE -> {
                userStatus = Constants.USER_ONLINE
                checkNetworkConnectionAndSync()
            }

            else -> {}
        }
    }

    private fun showSavingsAccountTransactions(
        transactions: List<SavingsAccountTransactionRequest>
    ) {
        mSyncSavingsAccountTransactionAdapter.setSavingsAccountTransactions(transactions)
    }

    private fun showEmptySavingsAccountTransactions(message: Int) {
        binding.llError.visibility = View.VISIBLE
        binding.noPayloadText.text = requireActivity().resources.getString(message)
        binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
    }

    private fun showPaymentTypeOptions(paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>) {
        mSyncSavingsAccountTransactionAdapter.setPaymentTypeOptions(paymentTypeOptions)
    }

    private fun checkNetworkConnectionAndSync() {
        if (isOnline(requireActivity())) {
            viewModel.syncSavingsAccountTransactions()
        } else {
            show(binding.root, resources.getString(R.string.error_network_not_available))
        }
    }

    private fun showError(message: Int) {
        show(binding.root, resources.getString(message))
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && mSyncSavingsAccountTransactionAdapter.itemCount == 0) {
            showMifosProgressBar()
            binding.swipeContainer.isRefreshing = false
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sync, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sync) {
            when (userStatus) {
                false -> checkNetworkConnectionAndSync()
                true -> showOfflineModeDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncSavingsAccountTransactionFragment {
            val arguments = Bundle()
            val sync = SyncSavingsAccountTransactionFragment()
            sync.arguments = arguments
            return sync
        }
    }
}