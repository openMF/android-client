/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingaccountsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.feature.savings.account_summary.SavingsAccountSummaryScreen
import com.mifos.feature.savings.account_summary.SavingsAccountSummaryViewModel
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavingsAccountSummaryFragment : MifosBaseFragment() {

    private val viewModel: SavingsAccountSummaryViewModel by viewModels()
    private val arg: SavingsAccountSummaryFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.accountId = arg.savingsAccountNumber
        viewModel.savingsAccountType = arg.accountType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavingsAccountSummaryScreen(
                    navigateBack = { findNavController().popBackStack() },
                    loadMoreSavingsAccountInfo = { loadSavingsDataTables(it) },
                    loadDocuments = { loadDocuments(it) },
                    onDepositClick = { savingsAccountWithAssociations, type ->
                        onDepositButtonClicked(savingsAccountWithAssociations, type)
                    },
                    onWithdrawButtonClicked = { savingsAccountWithAssociations, type ->
                        onWithdrawalButtonClicked(savingsAccountWithAssociations, type)
                    },
                    activateSavings = { type, accountNumber ->
                        activateSavings(type, accountNumber)
                    },
                    approveSavings = { type, accountNumber ->
                        approveSavings(type, accountNumber)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        toolbar?.visibility = View.VISIBLE
    }

    private fun loadDocuments(savingsAccountNumber: Int) {
        val action =
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToDocumentListFragment(
                savingsAccountNumber,
                Constants.ENTITY_TYPE_SAVINGS
            )
        findNavController().navigate(action)
    }

    private fun onDepositButtonClicked(
        savingsAccountWithAssociations: SavingsAccountWithAssociations,
        savingsAccountType: DepositType?
    ) {
        doTransaction(
            savingsAccountWithAssociations,
            Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT, savingsAccountType
        )
    }

    private fun onWithdrawalButtonClicked(
        savingsAccountWithAssociations: SavingsAccountWithAssociations,
        savingsAccountType: DepositType?
    ) {
        doTransaction(
            savingsAccountWithAssociations,
            Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL, savingsAccountType
        )
    }

    private fun approveSavings(savingsAccountType: DepositType?, savingsAccountNumber: Int) {
        val action = savingsAccountType?.let {
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToSavingsAccountApprovalFragment(
                savingsAccountNumber,
                it
            )
        }
        action?.let { findNavController().navigate(it) }
    }

    private fun activateSavings(savingsAccountType: DepositType?, savingsAccountNumber: Int) {
        val action = savingsAccountType?.let {
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToSavingsAccountActivateFragment(
                savingsAccountNumber,
                it
            )
        }
        action?.let { findNavController().navigate(it) }
    }

    private fun loadSavingsDataTables(savingsAccountNumber: Int) {
        val action =
            SavingsAccountSummaryFragmentDirections.actionSavingsAccountSummaryFragmentToDataTableFragment(
                Constants.DATA_TABLE_NAME_SAVINGS,
                savingsAccountNumber
            )
        findNavController().navigate(action)
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
}