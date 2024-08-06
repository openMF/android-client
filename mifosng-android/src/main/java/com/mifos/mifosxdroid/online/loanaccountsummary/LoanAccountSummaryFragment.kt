/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.client.Charges
import com.mifos.feature.loan.loan_account_summary.LoanAccountSummaryScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ishankhanna on 09/05/14.
 */
@AndroidEntryPoint
class LoanAccountSummaryFragment : MifosBaseFragment() {

    var loanAccountNumber = 0
    private val arg: LoanAccountSummaryFragmentArgs by navArgs()
    var chargesList: MutableList<Charges> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanAccountNumber = arg.loanAccountNumber
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoanAccountSummaryScreen(
                    loanAccountNumber = loanAccountNumber,
                    navigateBack = { findNavController().popBackStack() },
                    onTransactionsClicked = { loadLoanTransactions(it) },
                    onDocumentsClicked = { loadDocuments() },
                    onChargesClicked = { loadLoanCharges() },
                    onRepaymentScheduleClicked = { loadRepaymentSchedule(it) },
                    onMoreInfoClicked = { loadLoanDataTables() },
                    approveLoan = { approveLoan(it) },
                    disburseLoan = { disburseLoan() },
                    onRepaymentClick = { makeRepayment(it) }
                )
            }
        }
    }


    private fun loadRepaymentSchedule(loanId: Int) {
        val action =
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToLoanRepaymentScheduleFragment(
                loanId
            )
        findNavController().navigate(action)
    }

    private fun loadLoanDataTables() {
        val action =
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToDataTableFragment(
                Constants.DATA_TABLE_NAME_LOANS,
                loanAccountNumber
            )
        findNavController().navigate(action)
    }

    private fun loadDocuments() {
        val action =
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToDocumentListFragment(
                loanAccountNumber, Constants.ENTITY_TYPE_LOANS
            )
        findNavController().navigate(action)
    }

    private fun loadLoanCharges() {
        val action =
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToLoanChargeFragment(
                loanAccountNumber, chargesList.toTypedArray()
            )
        findNavController().navigate(action)
    }

    private fun makeRepayment(loan: LoanWithAssociations) {
        val action = loan.let {
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToLoanRepaymentFragment(
                it
            )
        }
        action.let { findNavController().navigate(it) }
    }

    private fun loadLoanTransactions(loanId: Int) {
        val action =
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToLoanTransactionsFragment(
                loanId
            )
        findNavController().navigate(action)
    }

    private fun approveLoan(loanWithAssociations: LoanWithAssociations) {
        val action = loanWithAssociations.let {
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToLoanAccountApproval(
                loanAccountNumber,
                it
            )
        }
        action.let { findNavController().navigate(it) }
    }

    private fun disburseLoan() {
        val action =
            LoanAccountSummaryFragmentDirections.actionLoanAccountSummaryFragmentToLoanAccountDisbursementFragment(
                loanAccountNumber
            )
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        toolbar?.visibility = View.VISIBLE
    }
}