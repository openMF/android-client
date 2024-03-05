/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.grouploanaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import com.mifos.core.objects.templates.loans.RepaymentFrequencyDaysOfWeekTypeOptions
import com.mifos.core.objects.templates.loans.RepaymentFrequencyNthDayTypeOptions
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentAddLoanBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this  Fragment to Create and/or Update loan
 */
@AndroidEntryPoint
class GroupLoanAccountFragment : ProgressableDialogFragment(), OnDatePickListener,
    OnItemSelectedListener {

    private lateinit var binding: FragmentAddLoanBinding
    private val arg: GroupLoanAccountFragmentArgs by navArgs()

    private lateinit var viewModel: GroupLoanAccountViewModel

    private var submissionDate: String? = null
    private var disbursementDate: String? = null
    private val mListener: OnDialogFragmentInteractionListener? = null
    private var mfDatePicker: DialogFragment? = null
    private var productId: Int? = 0
    private var groupId = 0
    private var loanPurposeId: Int? = null
    private var loanTermFrequency: Int? = null
    private var loanTermFrequencyType: Int? = null
    private var termFrequency: Int? = null
    private var repaymentEvery: Int? = null
    private var transactionProcessingStrategyId: Int? = null
    private var amortizationTypeId: Int? = null
    private var interestCalculationPeriodTypeId: Int? = null
    private var fundId: Int? = null
    private var loanOfficerId: Int? = null
    private var interestTypeMethodId: Int? = null
    private var repaymentFrequencyNthDayType: Int? = null
    private var repaymentFrequencyDayOfWeek: Int? = null
    private var interestRatePerPeriod: Double? = null
    private val linkAccountId: Int? = null

    // Boolean values to act as flags for date selection
    private var isdisbursementDate = false
    private var issubmittedDate = false
    private val amortizationType: MutableList<String> = ArrayList()
    private val interestCalculationPeriodType: MutableList<String> = ArrayList()
    private val transactionProcessingStrategy: MutableList<String> = ArrayList()
    private val termFrequencyType: MutableList<String> = ArrayList()
    private val loans: List<String> = ArrayList()
    private val loanPurposeType: MutableList<String> = ArrayList()
    private val interestTypeOptions: MutableList<String> = ArrayList()
    private val loanOfficerOptions: MutableList<String> = ArrayList()
    private val fundOptions: MutableList<String> = ArrayList()
    private val mListLoanProductsNames: MutableList<String> = ArrayList()
    private val mListRepaymentFrequencyNthDayTypeOptions: MutableList<String> = ArrayList()
    private val mListRepaymentFrequencyDayOfWeekTypeOptions: MutableList<String> = ArrayList()
    private var amortizationTypeAdapter: ArrayAdapter<String>? = null
    private var mRepaymentFrequencyNthDayTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var mRepaymentFrequencyDayOfWeekTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var interestCalculationPeriodTypeAdapter: ArrayAdapter<String>? = null
    private var transactionProcessingStrategyAdapter: ArrayAdapter<String>? = null
    private var termFrequencyTypeAdapter: ArrayAdapter<String>? = null
    private var loanProductAdapter: ArrayAdapter<String>? = null
    private var loanPurposeTypeAdapter: ArrayAdapter<String>? = null
    private var interestTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var loanOfficerOptionsAdapter: ArrayAdapter<String>? = null
    private var fundOptionsAdapter: ArrayAdapter<String>? = null
    private var mGroupLoanTemplate: GroupLoanTemplate? = null
    private var mLoanProducts: List<LoanProducts>? = null
    private var mRepaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions>? =
        null
    private var mRepaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = arg.groupId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding = FragmentAddLoanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[GroupLoanAccountViewModel::class.java]

        //Linking Options not yet implemented for Groups but the layout file is shared.
        //So, hiding the widgets
        binding.tvLinkingOptions.visibility = View.GONE
        binding.spLinkingOptions.visibility = View.GONE
        inflateSubmissionDate()
        inflateDisbursementDate()
        inflateLoansProductSpinner()
        inflateLoanSpinner()
        disbursementDate = binding.tvDisbursementonDate.text.toString()
        submissionDate = binding.tvSubmittedonDate.text.toString()
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submissionDate)
            .replace("-", " ")
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate)
            .replace("-", " ")

        viewModel.groupLoanAccountUiState.observe(viewLifecycleOwner) {
            when (it) {
                is GroupLoanAccountUiState.ShowAllLoans -> {
                    showProgressbar(false)
                    showAllLoans(it.productLoans)
                }

                is GroupLoanAccountUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is GroupLoanAccountUiState.ShowGroupLoanTemplate -> {
                    showProgressbar(false)
                    showGroupLoanTemplate(it.groupLoanTemplate)
                }

                is GroupLoanAccountUiState.ShowGroupLoansAccountCreatedSuccessfully -> {
                    showProgressbar(false)
                    showGroupLoansAccountCreatedSuccessfully(it.loans)
                }

                is GroupLoanAccountUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoanSubmit.setOnClickListener {
            val loansPayload = GroupLoanPayload()
            loansPayload.isAllowPartialPeriodInterestCalcualtion = binding.cbCalculateinterest
                .isChecked
            loansPayload.amortizationType = amortizationTypeId
            loansPayload.groupId = groupId
            loansPayload.dateFormat = "dd MMMM yyyy"
            loansPayload.expectedDisbursementDate = disbursementDate
            loansPayload.interestCalculationPeriodType = interestCalculationPeriodTypeId
            loansPayload.loanType = "group"
            loansPayload.locale = "en"
            loansPayload.numberOfRepayments = binding.etNumberofrepayments.editableText
                .toString()
            loansPayload.principal = binding.etPrincipal.editableText.toString()
            loansPayload.productId = productId
            loansPayload.repaymentEvery = binding.etRepaidevery.editableText.toString()
            loansPayload.submittedOnDate = submissionDate
            loansPayload.loanPurposeId = loanPurposeId
            loansPayload.loanPurposeId = loanPurposeId
            loansPayload.loanTermFrequency = binding.etLoanterm.editableText.toString().toInt()

            //loanTermFrequencyType and repaymentFrequencyType must take the same value.
            loansPayload.loanTermFrequencyType = loanTermFrequencyType
            loansPayload.repaymentFrequencyType = loanTermFrequencyType
            loansPayload.repaymentFrequencyDayOfWeekType = repaymentFrequencyDayOfWeek
            loansPayload.repaymentFrequencyNthDayType = repaymentFrequencyNthDayType
            loansPayload.transactionProcessingStrategyId = transactionProcessingStrategyId
            loansPayload.linkAccountId = linkAccountId
            interestRatePerPeriod = binding.etNominalInterestRate
                .editableText.toString().toDouble()
            loansPayload.interestRatePerPeriod = interestRatePerPeriod
            initiateLoanCreation(loansPayload)
        }

        binding.tvSubmittedonDate.setOnClickListener {
            onClickSubmittedOnDate()
        }

        binding.tvDisbursementonDate.setOnClickListener {
            onClickDisbursementOnDate()
        }
    }

    private fun inflateLoanSpinner() {
        amortizationTypeAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, amortizationType
        )
        amortizationTypeAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAmortization.adapter = amortizationTypeAdapter
        binding.spAmortization.onItemSelectedListener = this
        interestCalculationPeriodTypeAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, interestCalculationPeriodType
        )
        interestCalculationPeriodTypeAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spInterestcalculationperiod.adapter = interestCalculationPeriodTypeAdapter
        binding.spInterestcalculationperiod.onItemSelectedListener = this
        transactionProcessingStrategyAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, transactionProcessingStrategy
        )
        transactionProcessingStrategyAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentstrategy.adapter = transactionProcessingStrategyAdapter
        binding.spRepaymentstrategy.onItemSelectedListener = this
        termFrequencyTypeAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, termFrequencyType
        )
        termFrequencyTypeAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPaymentPeriods.adapter = termFrequencyTypeAdapter
        binding.spPaymentPeriods.onItemSelectedListener = this
        loanProductAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListLoanProductsNames
        )
        loanProductAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLproduct.adapter = loanProductAdapter
        binding.spLproduct.onItemSelectedListener = this
        loanPurposeTypeAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, loanPurposeType
        )
        loanPurposeTypeAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanPurpose.adapter = loanPurposeTypeAdapter
        binding.spLoanPurpose.onItemSelectedListener = this
        interestTypeOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, interestTypeOptions
        )
        interestTypeOptionsAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spInterestType.adapter = interestTypeOptionsAdapter
        binding.spInterestType.onItemSelectedListener = this
        loanOfficerOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, loanOfficerOptions
        )
        loanOfficerOptionsAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanOfficer.adapter = loanOfficerOptionsAdapter
        binding.spLoanOfficer.onItemSelectedListener = this
        fundOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, fundOptions
        )
        fundOptionsAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFund.adapter = fundOptionsAdapter
        binding.spFund.onItemSelectedListener = this
    }

    private fun inflateRepaidMonthSpinners() {
        mRepaymentFrequencyNthDayTypeOptionsAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item,
            mListRepaymentFrequencyNthDayTypeOptions
        )
        mRepaymentFrequencyNthDayTypeOptionsAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentFreqNthDay.adapter = mRepaymentFrequencyNthDayTypeOptionsAdapter
        binding.spRepaymentFreqNthDay.onItemSelectedListener = this
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item,
            mListRepaymentFrequencyDayOfWeekTypeOptions
        )
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentFreqDayOfWeek.adapter = mRepaymentFrequencyDayOfWeekTypeOptionsAdapter
        binding.spRepaymentFreqDayOfWeek.onItemSelectedListener = this
    }

    override fun onDatePicked(date: String?) {
        if (isdisbursementDate) {
            binding.tvDisbursementonDate.text = date
            disbursementDate = date
            isdisbursementDate = false
        }
        if (issubmittedDate) {
            binding.tvSubmittedonDate.text = date
            submissionDate = date
            issubmittedDate = false
        }
    }

    private fun inflateLoansProductSpinner() {
        viewModel.loadAllLoans()
    }

    private fun inflateLoanPurposeSpinner() {
        productId?.let { viewModel.loadGroupLoansAccountTemplate(groupId, it) }
    }

    private fun initiateLoanCreation(loansPayload: GroupLoanPayload) {
        viewModel.createGroupLoanAccount(loansPayload)
    }

    private fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvSubmittedonDate.text = MFDatePicker.datePickedAsString
    }


    private fun onClickSubmittedOnDate() {
        issubmittedDate = true
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvDisbursementonDate.text = MFDatePicker.datePickedAsString
    }


    private fun onClickDisbursementOnDate() {
        isdisbursementDate = true
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun showAllLoans(productLoans: List<LoanProducts>) {
        mLoanProducts = productLoans
        viewModel
            .filterLoanProducts(productLoans).let { mListLoanProductsNames.addAll(it) }
        loanProductAdapter?.notifyDataSetChanged()
    }

    private fun showGroupLoanTemplate(groupLoanTemplate: GroupLoanTemplate?) {
        mGroupLoanTemplate = groupLoanTemplate
        amortizationType.clear()
        amortizationType.addAll(viewModel.filterAmortizations(groupLoanTemplate?.amortizationTypeOptions))
        amortizationTypeAdapter?.notifyDataSetChanged()
        interestCalculationPeriodType.clear()
        if (groupLoanTemplate != null) {
            interestCalculationPeriodType.addAll(
                viewModel.filterInterestCalculationPeriods(
                    groupLoanTemplate.interestCalculationPeriodTypeOptions
                )
            )
        }
        interestCalculationPeriodTypeAdapter?.notifyDataSetChanged()
        transactionProcessingStrategy.clear()
        transactionProcessingStrategy.addAll(
            viewModel.filterTransactionProcessingStrategies(
                groupLoanTemplate?.transactionProcessingStrategyOptions
            )
        )
        transactionProcessingStrategyAdapter?.notifyDataSetChanged()
        termFrequencyType.clear()
        termFrequencyType.addAll(
            viewModel.filterTermFrequencyTypes(
                groupLoanTemplate?.termFrequencyTypeOptions
            )
        )
        termFrequencyTypeAdapter?.notifyDataSetChanged()
        loanPurposeType.clear()
        loanPurposeType.addAll(viewModel.filterLoanPurposeTypes(groupLoanTemplate?.loanPurposeOptions))
        loanPurposeTypeAdapter?.notifyDataSetChanged()
        interestTypeOptions.clear()
        interestTypeOptions.addAll(
            viewModel.filterInterestTypeOptions(
                groupLoanTemplate?.interestTypeOptions
            )
        )
        interestTypeOptionsAdapter?.notifyDataSetChanged()
        loanOfficerOptions.clear()
        loanOfficerOptions.addAll(viewModel.filterLoanOfficers(groupLoanTemplate?.loanOfficerOptions))
        loanOfficerOptionsAdapter?.notifyDataSetChanged()
        fundOptions.clear()
        fundOptions.addAll(viewModel.filterFunds(groupLoanTemplate?.fundOptions))
        fundOptionsAdapter?.notifyDataSetChanged()
        mListRepaymentFrequencyNthDayTypeOptions.clear()
        mRepaymentFrequencyNthDayTypeOptions = mGroupLoanTemplate
            ?.repaymentFrequencyNthDayTypeOptions
        for (options in mRepaymentFrequencyNthDayTypeOptions!!) {
            options.value?.let { mListRepaymentFrequencyNthDayTypeOptions.add(it) }
        }
        mListRepaymentFrequencyDayOfWeekTypeOptions.clear()
        mRepaymentFrequencyDaysOfWeekTypeOptions = mGroupLoanTemplate!!
            .repaymentFrequencyDaysOfWeekTypeOptions
        for (options in mRepaymentFrequencyDaysOfWeekTypeOptions!!) {
            options.value?.let { mListRepaymentFrequencyDayOfWeekTypeOptions.add(it) }
        }
        showDefaultValues()
    }

    private fun showGroupLoansAccountCreatedSuccessfully(loans: Loans?) {
        Toast.makeText(
            requireActivity(),
            "The Loan has been submitted for Approval",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showFetchingError(s: String?) {
        Toaster.show(binding.root, s)
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    interface OnDialogFragmentInteractionListener

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_lproduct -> {
                productId = mLoanProducts?.get(position)?.id
                inflateLoanPurposeSpinner()
            }

            R.id.sp_amortization -> amortizationTypeId = mGroupLoanTemplate
                ?.amortizationTypeOptions?.get(position)?.id

            R.id.sp_interestcalculationperiod -> interestCalculationPeriodTypeId =
                mGroupLoanTemplate?.interestCalculationPeriodTypeOptions?.get(position)?.id

            R.id.sp_repaymentstrategy -> transactionProcessingStrategyId = mGroupLoanTemplate
                ?.transactionProcessingStrategyOptions?.get(position)?.id

            R.id.sp_payment_periods -> {
                loanTermFrequency = mGroupLoanTemplate
                    ?.termFrequencyTypeOptions?.get(position)?.id
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE)
                    inflateRepaidMonthSpinners()
                } else {
                    showHideRepaidMonthSpinners(View.GONE)
                }
            }

            R.id.sp_repayment_freq_nth_day -> repaymentFrequencyNthDayType = mGroupLoanTemplate
                ?.repaymentFrequencyNthDayTypeOptions?.get(position)?.id

            R.id.sp_repayment_freq_day_of_week -> repaymentFrequencyDayOfWeek = mGroupLoanTemplate
                ?.repaymentFrequencyDaysOfWeekTypeOptions?.get(position)?.id

            R.id.sp_loan_purpose -> loanPurposeId =
                mGroupLoanTemplate?.loanPurposeOptions?.get(position)?.id

            R.id.sp_interest_type -> interestTypeMethodId =
                mGroupLoanTemplate?.interestTypeOptions?.get(position)?.id

            R.id.sp_loan_officer -> loanOfficerId =
                mGroupLoanTemplate?.loanOfficerOptions?.get(position)?.id

            R.id.sp_fund -> fundId = mGroupLoanTemplate?.fundOptions?.get(position)?.id
        }
    }

    private fun showHideRepaidMonthSpinners(visibility: Int) {
        binding.spRepaymentFreqNthDay.visibility = visibility
        binding.spRepaymentFreqDayOfWeek.visibility = visibility
        binding.tvRepaidNthfreqLabelOn.visibility = visibility
    }

    private fun showDefaultValues() {
        interestRatePerPeriod = mGroupLoanTemplate?.interestRatePerPeriod
        loanTermFrequencyType = mGroupLoanTemplate?.interestRateFrequencyType?.id
        termFrequency = mGroupLoanTemplate?.termFrequency
        binding.etPrincipal.setText(mGroupLoanTemplate?.principal.toString())
        binding.etNumberofrepayments.setText(mGroupLoanTemplate?.numberOfRepayments.toString())
        binding.tvNominalRateYearMonth.text = mGroupLoanTemplate?.interestRateFrequencyType?.value
        binding.etNominalInterestRate.setText(mGroupLoanTemplate?.interestRatePerPeriod.toString())
        binding.etLoanterm.setText(termFrequency.toString())
        if (mGroupLoanTemplate?.repaymentEvery != null) {
            repaymentEvery = mGroupLoanTemplate?.repaymentEvery
            binding.etRepaidevery.setText(repaymentEvery.toString())
        }
        if (mGroupLoanTemplate?.fundId != null) {
            fundId = mGroupLoanTemplate?.fundId
            binding.spFund.setSelection(mGroupLoanTemplate?.fundId ?: 0)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}