/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.noncore.DataTable
import com.mifos.feature.loan.loanAccount.LoanAccountScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this  Fragment to Create and/or Update loan
 */
@AndroidEntryPoint
class LoanAccountFragment : Fragment() {

//    private val arg: LoanAccountFragmentArgs by navArgs()
    private var clientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        clientId = arg.clientId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setContent {
                LoanAccountScreen(
//                    clientId = clientId,
                    onBackPressed = {
                        findNavController().popBackStack()
                    }, dataTable = { dataTables, loansPayload ->
                        dataTables(dataTables, loansPayload)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    private fun dataTables(dataTables: List<DataTable>, loansPayload: LoansPayload) {
//        val fragment = DataTableListFragment.newInstance(
//            dataTables,
//            loansPayload, Constants.CLIENT_LOAN
//        )
//        val fragmentTransaction = requireActivity().supportFragmentManager
//            .beginTransaction()
//        fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
//        fragmentTransaction.replace(R.id.container, fragment).commit()
    }

//    private fun submit() {
//        val loansPayload = LoansPayload()
//        loansPayload.allowPartialPeriodInterestCalcualtion = binding.cbCalculateinterest
//            .isChecked
//        loansPayload.amortizationType = amortizationTypeId
//        loansPayload.clientId = clientId
//        loansPayload.dateFormat = "dd MMMM yyyy"
//        loansPayload.expectedDisbursementDate = disbursementDate
//        loansPayload.interestCalculationPeriodType = interestCalculationPeriodTypeId
//        loansPayload.loanType = "individual"
//        loansPayload.locale = "en"
//        loansPayload.numberOfRepayments =
//            binding.etNumberofrepayments.editableText.toString().toInt()
//        loansPayload.principal = binding.etPrincipal.editableText.toString().toDouble()
//        loansPayload.productId = productId
//        loansPayload.repaymentEvery = binding.etRepaidevery.editableText.toString().toInt()
//        loansPayload.submittedOnDate = submissionDate
//        loansPayload.loanPurposeId = loanPurposeId
//        loansPayload.loanTermFrequency = binding.etLoanterm.editableText.toString().toInt()
//        loansPayload.loanTermFrequencyType = loanTermFrequency
//
//        //loanTermFrequencyType and repaymentFrequencyType should be the same.
//        loansPayload.repaymentFrequencyType = loanTermFrequency
//        loansPayload.repaymentFrequencyDayOfWeekType =
//            if (repaymentFrequencyDayOfWeek != null) repaymentFrequencyDayOfWeek else null
//        loansPayload.repaymentFrequencyNthDayType =
//            if (repaymentFrequencyNthDayType != null) repaymentFrequencyNthDayType else null
//        loansPayload.transactionProcessingStrategyId = transactionProcessingStrategyId
//        loansPayload.fundId = fundId
//        loansPayload.interestType = interestTypeId
//        loansPayload.loanOfficerId = loanOfficerId
//        loansPayload.linkAccountId = linkAccountId
//        interestRatePerPeriod =
//            binding.etNominalInterestRate.editableText.toString().toDouble()
//        loansPayload.interestRatePerPeriod = interestRatePerPeriod
//        if (hasDataTables) {
//            val fragment = DataTableListFragment.newInstance(
//                mLoanTemplate.dataTables,
//                loansPayload, Constants.CLIENT_LOAN
//            )
//            val fragmentTransaction = requireActivity().supportFragmentManager
//                .beginTransaction()
//            fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
//            fragmentTransaction.replace(R.id.container, fragment).commit()
//        } else {
//            initiateLoanCreation(loansPayload)
//        }
//    }


//    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//        when (parent.id) {
//            R.id.sp_lproduct -> {
//                productId = mLoanProducts[position].id
//                inflateLoanPurposeSpinner()
//            }
//
//            R.id.sp_loan_purpose -> loanPurposeId = mLoanTemplate.loanPurposeOptions[position].id
//            R.id.sp_amortization -> amortizationTypeId =
//                mLoanTemplate.amortizationTypeOptions[position].id
//
//            R.id.sp_interestcalculationperiod -> interestCalculationPeriodTypeId = mLoanTemplate
//                .interestCalculationPeriodTypeOptions[position].id
//
//            R.id.sp_repaymentstrategy -> transactionProcessingStrategyId = mLoanTemplate
//                .transactionProcessingStrategyOptions[position].id
//
//            R.id.sp_payment_periods -> {
//                loanTermFrequency = mLoanTemplate.termFrequencyTypeOptions[position]
//                    .id
//                loanTermFrequency?.let { binding.spLoanTermPeriods.setSelection(it) }
//                if (loanTermFrequency == 2) {
//                    // Show and inflate Nth day and week spinners
//                    showHideRepaidMonthSpinners(View.VISIBLE)
//                    inflateRepaidMonthSpinners()
//                } else {
//                    showHideRepaidMonthSpinners(View.GONE)
//                }
//            }
//
//            R.id.sp_loan_term_periods -> {
//                loanTermFrequency = mLoanTemplate.termFrequencyTypeOptions[position]
//                    .id
//                loanTermFrequency?.let { binding.spPaymentPeriods.setSelection(it) }
//                if (loanTermFrequency == 2) {
//                    // Show and inflate Nth day and week spinners
//                    showHideRepaidMonthSpinners(View.VISIBLE)
//                    inflateRepaidMonthSpinners()
//                } else {
//                    showHideRepaidMonthSpinners(View.GONE)
//                }
//            }
//
//            R.id.sp_repayment_freq_nth_day -> repaymentFrequencyNthDayType =
//                if (mListRepaymentFrequencyNthDayTypeOptions[position]
//                    == resources.getString(R.string.select_week_hint)
//                ) {
//                    null
//                } else {
//                    mLoanTemplate
//                        .repaymentFrequencyNthDayTypeOptions[position].id
//                }
//
//            R.id.sp_repayment_freq_day_of_week -> repaymentFrequencyDayOfWeek =
//                if (mListRepaymentFrequencyDayOfWeekTypeOptions[position]
//                    == resources.getString(R.string.select_day_hint)
//                ) {
//                    null
//                } else {
//                    mLoanTemplate
//                        .repaymentFrequencyDaysOfWeekTypeOptions[position].id
//                }
//
//            R.id.sp_fund -> fundId = mLoanTemplate.fundOptions[position].id
//            R.id.sp_loan_officer -> loanOfficerId = mLoanTemplate.loanOfficerOptions[position].id
//            R.id.sp_interest_type -> interestTypeId = mLoanTemplate.interestTypeOptions[position].id
//            R.id.sp_linking_options -> linkAccountId = if (mListAccountLinkingOptions[position]
//                == resources.getString(R.string.select_linkage_account_hint)
//            ) {
//                null
//            } else {
//                mLoanTemplate.accountLinkingOptions[position].id
//            }
//        }
//    }
}