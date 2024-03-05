/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccount

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
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate
import com.mifos.core.objects.templates.loans.RepaymentFrequencyDaysOfWeekTypeOptions
import com.mifos.core.objects.templates.loans.RepaymentFrequencyNthDayTypeOptions
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentAddLoanBinding
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
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
class LoanAccountFragment : ProgressableDialogFragment(), OnDatePickListener,
    OnItemSelectedListener {

    private lateinit var binding: FragmentAddLoanBinding
    private val arg: LoanAccountFragmentArgs by navArgs()

    private lateinit var viewModel: LoanAccountViewModel

    private var submissionDate: String? = null
    private var disbursementDate: String? = null
    private var hasDataTables = false
    private var mfDatePicker: DialogFragment? = null
    private var productId: Int? = 0
    private var clientId = 0
    private var loanPurposeId: Int? = null
    private var loanTermFrequency: Int? = null
    private val loanTermFrequencyType = 0
    private var termFrequency: Int? = null
    private var repaymentEvery: Int? = null
    private var transactionProcessingStrategyId: Int? = null
    private var amortizationTypeId: Int? = null
    private var interestCalculationPeriodTypeId: Int? = null
    private var fundId: Int? = null
    private var loanOfficerId: Int? = null
    private var interestTypeId: Int? = null
    private var repaymentFrequencyNthDayType: Int? = null
    private var repaymentFrequencyDayOfWeek: Int? = null
    private var interestRatePerPeriod: Double? = null
    private var linkAccountId: Int? = null
    private var isDisbursebemntDate = false
    private var isSubmissionDate = false
    private var mLoanProducts: List<LoanProducts> = ArrayList()
    private var mRepaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> =
        ArrayList()
    private var mRepaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> =
        ArrayList()
    private var mLoanTemplate = LoanTemplate()
    private var mListLoanProducts: MutableList<String> = ArrayList()
    private var mListLoanPurposeOptions: MutableList<String> = ArrayList()
    private var mListAccountLinkingOptions: MutableList<String> = ArrayList()
    private var mListAmortizationTypeOptions: MutableList<String> = ArrayList()
    private var mListInterestCalculationPeriodTypeOptions: MutableList<String> = ArrayList()
    private var mListTransactionProcessingStrategyOptions: MutableList<String> = ArrayList()
    private var mListTermFrequencyTypeOptions: MutableList<String> = ArrayList()
    private var mListLoanTermFrequencyTypeOptions: MutableList<String> = ArrayList()
    private var mListRepaymentFrequencyNthDayTypeOptions: MutableList<String> = ArrayList()
    private var mListRepaymentFrequencyDayOfWeekTypeOptions: MutableList<String> = ArrayList()
    private var mListLoanFundOptions: MutableList<String> = ArrayList()
    private var mListLoanOfficerOptions: MutableList<String> = ArrayList()
    private var mListInterestTypeOptions: MutableList<String> = ArrayList()
    private var mLoanProductAdapter: ArrayAdapter<String>? = null
    private var mLoanPurposeOptionsAdapter: ArrayAdapter<String>? = null
    private var mAccountLinkingOptionsAdapter: ArrayAdapter<String>? = null
    private var mAmortizationTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var mInterestCalculationPeriodTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var mTransactionProcessingStrategyOptionsAdapter: ArrayAdapter<String>? = null
    private var mTermFrequencyTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var mLoanTermFrequencyTypeAdapter: ArrayAdapter<String>? = null
    private var mRepaymentFrequencyNthDayTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var mRepaymentFrequencyDayOfWeekTypeOptionsAdapter: ArrayAdapter<String>? = null
    private var mLoanFundOptionsAdapter: ArrayAdapter<String>? = null
    private var mLoanOfficerOptionsAdapter: ArrayAdapter<String>? = null
    private var mInterestTypeOptionsAdapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientId = arg.clientId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding = FragmentAddLoanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoanAccountViewModel::class.java]
        inflateSubmissionDate()
        inflateDisbursementDate()
        inflateLoansProductSpinner()
        disbursementDate = binding.tvDisbursementonDate.text.toString()
        submissionDate = binding.tvDisbursementonDate.text.toString()
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submissionDate)
            .replace("-", " ")
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate)
            .replace("-", " ")
        inflateSpinners()

        viewModel.loanAccountUiState.observe(viewLifecycleOwner) {
            when (it) {
                is LoanAccountUiState.ShowAllLoan -> {
                    showProgressbar(false)
                    showAllLoan(it.productLoans)
                }

                is LoanAccountUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is LoanAccountUiState.ShowLoanAccountCreatedSuccessfully -> {
                    showProgressbar(false)
                    showLoanAccountCreatedSuccessfully(it.loans)
                }

                is LoanAccountUiState.ShowLoanAccountTemplate -> {
                    showProgressbar(false)
                    showLoanAccountTemplate(it.loanTemplate)
                }

                is LoanAccountUiState.ShowMessage -> {
                    showProgressbar(false)
                    showMessage(it.message)
                }

                is LoanAccountUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoanSubmit.setOnClickListener {
            submit()
        }

        binding.tvSubmittedonDate.setOnClickListener {
            setTvSubmittedOnDate()
        }

        binding.tvDisbursementonDate.setOnClickListener {
            setTvDisbursementOnDate()
        }
    }


    private fun submit() {
        val loansPayload = LoansPayload()
        loansPayload.allowPartialPeriodInterestCalcualtion = binding.cbCalculateinterest
            .isChecked
        loansPayload.amortizationType = amortizationTypeId
        loansPayload.clientId = clientId
        loansPayload.dateFormat = "dd MMMM yyyy"
        loansPayload.expectedDisbursementDate = disbursementDate
        loansPayload.interestCalculationPeriodType = interestCalculationPeriodTypeId
        loansPayload.loanType = "individual"
        loansPayload.locale = "en"
        loansPayload.numberOfRepayments =
            binding.etNumberofrepayments.editableText.toString().toInt()
        loansPayload.principal = binding.etPrincipal.editableText.toString().toDouble()
        loansPayload.productId = productId
        loansPayload.repaymentEvery = binding.etRepaidevery.editableText.toString().toInt()
        loansPayload.submittedOnDate = submissionDate
        loansPayload.loanPurposeId = loanPurposeId
        loansPayload.loanTermFrequency = binding.etLoanterm.editableText.toString().toInt()
        loansPayload.loanTermFrequencyType = loanTermFrequency

        //loanTermFrequencyType and repaymentFrequencyType should be the same.
        loansPayload.repaymentFrequencyType = loanTermFrequency
        loansPayload.repaymentFrequencyDayOfWeekType =
            if (repaymentFrequencyDayOfWeek != null) repaymentFrequencyDayOfWeek else null
        loansPayload.repaymentFrequencyNthDayType =
            if (repaymentFrequencyNthDayType != null) repaymentFrequencyNthDayType else null
        loansPayload.transactionProcessingStrategyId = transactionProcessingStrategyId
        loansPayload.fundId = fundId
        loansPayload.interestType = interestTypeId
        loansPayload.loanOfficerId = loanOfficerId
        loansPayload.linkAccountId = linkAccountId
        interestRatePerPeriod =
            binding.etNominalInterestRate.editableText.toString().toDouble()
        loansPayload.interestRatePerPeriod = interestRatePerPeriod
        if (hasDataTables) {
            val fragment = DataTableListFragment.newInstance(
                mLoanTemplate.dataTables,
                loansPayload, Constants.CLIENT_LOAN
            )
            val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
            fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
            fragmentTransaction.replace(R.id.container, fragment).commit()
        } else {
            initiateLoanCreation(loansPayload)
        }
    }

    override fun onDatePicked(date: String?) {
        if (isSubmissionDate) {
            binding.tvSubmittedonDate.text = date
            submissionDate = date
            isSubmissionDate = false
        }
        if (isDisbursebemntDate) {
            binding.tvDisbursementonDate.text = date
            disbursementDate = date
            isDisbursebemntDate = false
        }
    }

    private fun inflateSpinners() {

        //Inflating the LoanProducts Spinner
        mLoanProductAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item,
            mListLoanProducts
        )
        mLoanProductAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLproduct.adapter = mLoanProductAdapter
        binding.spLproduct.onItemSelectedListener = this

        //Inflating the LoanPurposeOptions
        mLoanPurposeOptionsAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item,
            mListLoanPurposeOptions
        )
        mLoanPurposeOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanPurpose.adapter = mLoanPurposeOptionsAdapter
        binding.spLoanPurpose.onItemSelectedListener = this

        //Inflating Linking Options
        mAccountLinkingOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListAccountLinkingOptions
        )
        mAccountLinkingOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLinkingOptions.adapter = mAccountLinkingOptionsAdapter
        binding.spLinkingOptions.onItemSelectedListener = this

        //Inflating AmortizationTypeOptions Spinner
        mAmortizationTypeOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListAmortizationTypeOptions
        )
        mAmortizationTypeOptionsAdapter?.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spAmortization.adapter = mAmortizationTypeOptionsAdapter
        binding.spAmortization.onItemSelectedListener = this

        //Inflating InterestCalculationPeriodTypeOptions Spinner
        mInterestCalculationPeriodTypeOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListInterestCalculationPeriodTypeOptions
        )
        mInterestCalculationPeriodTypeOptionsAdapter?.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spInterestcalculationperiod.adapter = mInterestCalculationPeriodTypeOptionsAdapter
        binding.spInterestcalculationperiod.onItemSelectedListener = this

        //Inflate TransactionProcessingStrategyOptions Spinner
        mTransactionProcessingStrategyOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListTransactionProcessingStrategyOptions
        )
        mTransactionProcessingStrategyOptionsAdapter?.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spRepaymentstrategy.adapter = mTransactionProcessingStrategyOptionsAdapter
        binding.spRepaymentstrategy.onItemSelectedListener = this

        //Inflate TermFrequencyTypeOptionsAdapter Spinner
        mTermFrequencyTypeOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListTermFrequencyTypeOptions
        )
        mTermFrequencyTypeOptionsAdapter?.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spPaymentPeriods.adapter = mTermFrequencyTypeOptionsAdapter
        binding.spPaymentPeriods.onItemSelectedListener = this

        //Inflate LoanTerm Frequency Type adapter
        mLoanTermFrequencyTypeAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListLoanTermFrequencyTypeOptions
        )
        mLoanTermFrequencyTypeAdapter?.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spLoanTermPeriods.adapter = mLoanTermFrequencyTypeAdapter
        binding.spLoanTermPeriods.onItemSelectedListener = this

        //Inflate FondOptions Spinner
        mLoanFundOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListLoanFundOptions
        )
        mLoanFundOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFund.adapter = mLoanFundOptionsAdapter
        binding.spFund.onItemSelectedListener = this

        //Inflating LoanOfficerOptions Spinner
        mLoanOfficerOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListLoanOfficerOptions
        )
        mLoanOfficerOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanOfficer.adapter = mLoanOfficerOptionsAdapter
        binding.spLoanOfficer.onItemSelectedListener = this

        //Inflating InterestTypeOptions Spinner
        mInterestTypeOptionsAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListInterestTypeOptions
        )
        mInterestTypeOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spInterestType.adapter = mInterestTypeOptionsAdapter
        binding.spInterestType.onItemSelectedListener = this
    }

    private fun inflateRepaidMonthSpinners() {
        mRepaymentFrequencyNthDayTypeOptionsAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item,
            mListRepaymentFrequencyNthDayTypeOptions
        )
        mRepaymentFrequencyNthDayTypeOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentFreqNthDay.adapter = mRepaymentFrequencyNthDayTypeOptionsAdapter
        binding.spRepaymentFreqNthDay.onItemSelectedListener = this
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item,
            mListRepaymentFrequencyDayOfWeekTypeOptions
        )
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentFreqDayOfWeek.adapter = mRepaymentFrequencyDayOfWeekTypeOptionsAdapter
        binding.spRepaymentFreqDayOfWeek.onItemSelectedListener = this
        binding.spRepaymentFreqNthDay.setSelection(mListRepaymentFrequencyNthDayTypeOptions.size - 1)
        binding.spRepaymentFreqDayOfWeek.setSelection(
            mListRepaymentFrequencyDayOfWeekTypeOptions.size - 1
        )
    }

    private fun inflateLoansProductSpinner() {
        viewModel.loadAllLoans()
    }

    private fun inflateLoanPurposeSpinner() {
        productId?.let { viewModel.loadLoanAccountTemplate(clientId, it) }
    }

    private fun initiateLoanCreation(loansPayload: LoansPayload) {
        viewModel.createLoansAccount(loansPayload)
    }

    private fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvSubmittedonDate.text = MFDatePicker.datePickedAsString
    }

    private fun setTvSubmittedOnDate() {
        isSubmissionDate = true
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvDisbursementonDate.text = MFDatePicker.datePickedAsString
    }

    private fun setTvDisbursementOnDate() {
        isDisbursebemntDate = true
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun showAllLoan(loans: List<LoanProducts>) {
        mLoanProducts = loans
        mListLoanProducts.clear()
        for (loanProducts in mLoanProducts) {
            loanProducts.name?.let { mListLoanProducts.add(it) }
        }
        mLoanProductAdapter?.notifyDataSetChanged()
    }

    private fun showLoanAccountTemplate(loanTemplate: LoanTemplate) {
        mLoanTemplate = loanTemplate
        hasDataTables = mLoanTemplate.dataTables.size > 0
        mListRepaymentFrequencyNthDayTypeOptions.clear()
        mRepaymentFrequencyNthDayTypeOptions = mLoanTemplate
            .repaymentFrequencyNthDayTypeOptions
        for (options in mRepaymentFrequencyNthDayTypeOptions) {
            options.value?.let { mListRepaymentFrequencyNthDayTypeOptions.add(it) }
        }
        mListRepaymentFrequencyNthDayTypeOptions.add(
            resources.getString(R.string.select_week_hint)
        )
        mListRepaymentFrequencyDayOfWeekTypeOptions.clear()
        mRepaymentFrequencyDaysOfWeekTypeOptions = mLoanTemplate
            .repaymentFrequencyDaysOfWeekTypeOptions
        for (options in mRepaymentFrequencyDaysOfWeekTypeOptions) {
            options.value?.let { mListRepaymentFrequencyDayOfWeekTypeOptions.add(it) }
        }
        mListRepaymentFrequencyDayOfWeekTypeOptions.add(
            resources.getString(R.string.select_day_hint)
        )
        mListLoanPurposeOptions.clear()
        for (loanPurposeOptions in mLoanTemplate.loanPurposeOptions) {
            loanPurposeOptions.name?.let { mListLoanPurposeOptions.add(it) }
        }
        mLoanPurposeOptionsAdapter?.notifyDataSetChanged()
        mListAccountLinkingOptions.clear()
        for (options in mLoanTemplate.accountLinkingOptions) {
            options.productName?.let { mListAccountLinkingOptions.add(it) }
        }
        mListAccountLinkingOptions.add(
            resources.getString(R.string.select_linkage_account_hint)
        )
        mAccountLinkingOptionsAdapter?.notifyDataSetChanged()
        mListAmortizationTypeOptions.clear()
        for (amortizationTypeOptions in mLoanTemplate.amortizationTypeOptions) {
            amortizationTypeOptions.value?.let { mListAmortizationTypeOptions.add(it) }
        }
        mAmortizationTypeOptionsAdapter?.notifyDataSetChanged()
        mListInterestCalculationPeriodTypeOptions.clear()
        for (interestCalculationPeriodType in mLoanTemplate
            .interestCalculationPeriodTypeOptions) {
            interestCalculationPeriodType.value?.let {
                mListInterestCalculationPeriodTypeOptions.add(
                    it
                )
            }
        }
        mInterestCalculationPeriodTypeOptionsAdapter?.notifyDataSetChanged()
        mListTransactionProcessingStrategyOptions.clear()
        for (transactionProcessingStrategyOptions in mLoanTemplate.transactionProcessingStrategyOptions) {
            transactionProcessingStrategyOptions
                .name?.let {
                    mListTransactionProcessingStrategyOptions.add(
                        it
                    )
                }
        }
        mTransactionProcessingStrategyOptionsAdapter?.notifyDataSetChanged()
        mListTermFrequencyTypeOptions.clear()
        for (termFrequencyTypeOptions in mLoanTemplate.termFrequencyTypeOptions) {
            termFrequencyTypeOptions.value?.let { mListTermFrequencyTypeOptions.add(it) }
        }
        mTermFrequencyTypeOptionsAdapter?.notifyDataSetChanged()
        mListLoanTermFrequencyTypeOptions.clear()
        for (termFrequencyTypeOptions in mLoanTemplate.termFrequencyTypeOptions) {
            termFrequencyTypeOptions.value?.let { mListLoanTermFrequencyTypeOptions.add(it) }
        }
        mLoanTermFrequencyTypeAdapter?.notifyDataSetChanged()
        mListLoanFundOptions.clear()
        for (fundOptions in mLoanTemplate.fundOptions) {
            fundOptions.name?.let { mListLoanFundOptions.add(it) }
        }
        mLoanFundOptionsAdapter?.notifyDataSetChanged()
        mListLoanOfficerOptions.clear()
        for (loanOfficerOptions in mLoanTemplate.loanOfficerOptions) {
            loanOfficerOptions.displayName?.let { mListLoanOfficerOptions.add(it) }
        }
        mLoanOfficerOptionsAdapter?.notifyDataSetChanged()
        mListInterestTypeOptions.clear()
        for (interestTypeOptions in mLoanTemplate.interestTypeOptions) {
            interestTypeOptions.value?.let { mListInterestTypeOptions.add(it) }
        }
        mInterestTypeOptionsAdapter?.notifyDataSetChanged()
        showDefaultValues()
    }

    private fun showLoanAccountCreatedSuccessfully(loans: Loans?) {
        Toast.makeText(activity, R.string.loan_creation_success, Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    private fun showMessage(messageId: Int) {
        Toaster.show(binding.root, messageId)
    }

    private fun showFetchingError(s: String?) {
        Toaster.show(binding.root, s)
    }

    private fun showProgressbar(show: Boolean) {
        showProgress(show)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_lproduct -> {
                productId = mLoanProducts[position].id
                inflateLoanPurposeSpinner()
            }

            R.id.sp_loan_purpose -> loanPurposeId = mLoanTemplate.loanPurposeOptions[position].id
            R.id.sp_amortization -> amortizationTypeId =
                mLoanTemplate.amortizationTypeOptions[position].id

            R.id.sp_interestcalculationperiod -> interestCalculationPeriodTypeId = mLoanTemplate
                .interestCalculationPeriodTypeOptions[position].id

            R.id.sp_repaymentstrategy -> transactionProcessingStrategyId = mLoanTemplate
                .transactionProcessingStrategyOptions[position].id

            R.id.sp_payment_periods -> {
                loanTermFrequency = mLoanTemplate.termFrequencyTypeOptions[position]
                    .id
                loanTermFrequency?.let { binding.spLoanTermPeriods.setSelection(it) }
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE)
                    inflateRepaidMonthSpinners()
                } else {
                    showHideRepaidMonthSpinners(View.GONE)
                }
            }

            R.id.sp_loan_term_periods -> {
                loanTermFrequency = mLoanTemplate.termFrequencyTypeOptions[position]
                    .id
                loanTermFrequency?.let { binding.spPaymentPeriods.setSelection(it) }
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE)
                    inflateRepaidMonthSpinners()
                } else {
                    showHideRepaidMonthSpinners(View.GONE)
                }
            }

            R.id.sp_repayment_freq_nth_day -> repaymentFrequencyNthDayType =
                if (mListRepaymentFrequencyNthDayTypeOptions[position]
                    == resources.getString(R.string.select_week_hint)
                ) {
                    null
                } else {
                    mLoanTemplate
                        .repaymentFrequencyNthDayTypeOptions[position].id
                }

            R.id.sp_repayment_freq_day_of_week -> repaymentFrequencyDayOfWeek =
                if (mListRepaymentFrequencyDayOfWeekTypeOptions[position]
                    == resources.getString(R.string.select_day_hint)
                ) {
                    null
                } else {
                    mLoanTemplate
                        .repaymentFrequencyDaysOfWeekTypeOptions[position].id
                }

            R.id.sp_fund -> fundId = mLoanTemplate.fundOptions[position].id
            R.id.sp_loan_officer -> loanOfficerId = mLoanTemplate.loanOfficerOptions[position].id
            R.id.sp_interest_type -> interestTypeId = mLoanTemplate.interestTypeOptions[position].id
            R.id.sp_linking_options -> linkAccountId = if (mListAccountLinkingOptions[position]
                == resources.getString(R.string.select_linkage_account_hint)
            ) {
                null
            } else {
                mLoanTemplate.accountLinkingOptions[position].id
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    private fun showHideRepaidMonthSpinners(visibility: Int) {
        binding.spRepaymentFreqNthDay.visibility = visibility
        binding.spRepaymentFreqDayOfWeek.visibility = visibility
        binding.tvRepaidNthfreqLabelOn.visibility = visibility
    }

    private fun showDefaultValues() {
        interestRatePerPeriod = mLoanTemplate.interestRatePerPeriod
        loanTermFrequency = mLoanTemplate.termPeriodFrequencyType?.id
        termFrequency = mLoanTemplate.termFrequency
        binding.etPrincipal.setText(mLoanTemplate.principal.toString())
        binding.etNumberofrepayments.setText(mLoanTemplate.numberOfRepayments.toString())
        binding.tvNominalRateYearMonth.text = mLoanTemplate.interestRateFrequencyType?.value
        binding.etNominalInterestRate.setText(mLoanTemplate.interestRatePerPeriod.toString())
        binding.etLoanterm.setText(termFrequency.toString())
        if (mLoanTemplate.repaymentEvery != null) {
            repaymentEvery = mLoanTemplate.repaymentEvery
            binding.etRepaidevery.setText(repaymentEvery.toString())
        }
        if (mLoanTemplate.fundId != null) {
            fundId = mLoanTemplate.fundId
            binding.spFund.setSelection(mLoanTemplate.fundId!!)
        }
        binding.spLinkingOptions.setSelection(mListAccountLinkingOptions.size)
    }
}