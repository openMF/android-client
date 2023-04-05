/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.grouploanaccount

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentAddLoanBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.GroupLoanTemplate
import com.mifos.objects.templates.loans.RepaymentFrequencyDaysOfWeekTypeOptions
import com.mifos.objects.templates.loans.RepaymentFrequencyNthDayTypeOptions
import com.mifos.services.data.GroupLoanPayload
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this  Fragment to Create and/or Update loan
 */
class GroupLoanAccountFragment : ProgressableDialogFragment(), OnDatePickListener, GroupLoanAccountMvpView, OnItemSelectedListener {
    val LOG_TAG = javaClass.simpleName
    private lateinit var binding: FragmentAddLoanBinding

    @JvmField
    @Inject
    var mGroupLoanAccountPresenter: GroupLoanAccountPresenter? = null
    var submissionDate: String? = null
    var disbursementDate: String? = null
    private val mListener: OnDialogFragmentInteractionListener? = null
    private var mfDatePicker: DialogFragment? = null
    private var productId = 0
    private var groupId = 0
    private var loanPurposeId = 0
    private var loanTermFrequency = 0
    private var loanTermFrequencyType = 0
    private var termFrequency: Int? = null
    private var repaymentEvery: Int? = null
    private var transactionProcessingStrategyId = 0
    private var amortizationTypeId = 0
    private var interestCalculationPeriodTypeId = 0
    private var fundId: Int = 0
    private var loanOfficerId = 0
    private var interestTypeMethodId = 0
    private var repaymentFrequencyNthDayType: Int? = null
    private var repaymentFrequencyDayOfWeek: Int? = null
    private var interestRatePerPeriod: Double? = null
    private val linkAccountId: Int? = null
    // Boolean values to act as flags for date selection
    var isdisbursementDate = false
    var issubmittedDate = false
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
    var mRepaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = ArrayList()
    var mRepaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) groupId = requireArguments().getInt(Constants.GROUP_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)

        binding = FragmentAddLoanBinding.inflate(inflater)
        mGroupLoanAccountPresenter!!.attachView(this)

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
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submissionDate).replace("-", " ")
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate).replace("-", " ")
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSubmittedonDate.setOnClickListener { onClickSubmittedonDate() }
        binding.tvDisbursementonDate.setOnClickListener { onClickDisbursementonDate() }
    }

    fun inflateLoanSpinner() {
        amortizationTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, amortizationType)
        amortizationTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAmortization.adapter = amortizationTypeAdapter
        binding.spAmortization.onItemSelectedListener = this
        interestCalculationPeriodTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, interestCalculationPeriodType)
        interestCalculationPeriodTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spInterestcalculationperiod.adapter = interestCalculationPeriodTypeAdapter
        binding.spInterestcalculationperiod.onItemSelectedListener = this
        transactionProcessingStrategyAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, transactionProcessingStrategy)
        transactionProcessingStrategyAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentstrategy.adapter = transactionProcessingStrategyAdapter
        binding.spRepaymentstrategy.onItemSelectedListener = this
        termFrequencyTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, termFrequencyType)
        termFrequencyTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPaymentPeriods.adapter = termFrequencyTypeAdapter
        binding.spPaymentPeriods.onItemSelectedListener = this
        loanProductAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListLoanProductsNames)
        loanProductAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLproduct.adapter = loanProductAdapter
        binding.spLproduct.onItemSelectedListener = this
        loanPurposeTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, loanPurposeType)
        loanPurposeTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanPurpose.adapter = loanPurposeTypeAdapter
        binding.spLoanPurpose.onItemSelectedListener = this
        interestTypeOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, interestTypeOptions)
        interestTypeOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spInterestType.adapter = interestTypeOptionsAdapter
        binding.spInterestType.onItemSelectedListener = this
        loanOfficerOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, loanOfficerOptions)
        loanOfficerOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanOfficer.adapter = loanOfficerOptionsAdapter
        binding.spLoanOfficer.onItemSelectedListener = this
        fundOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, fundOptions)
        fundOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFund.adapter = fundOptionsAdapter
        binding.spFund.onItemSelectedListener = this
    }

    private fun inflateRepaidMonthSpinners() {
        mRepaymentFrequencyNthDayTypeOptionsAdapter = ArrayAdapter(
                requireActivity(), android.R.layout.simple_spinner_item,
                mListRepaymentFrequencyNthDayTypeOptions)
        mRepaymentFrequencyNthDayTypeOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentFreqNthDay.adapter = mRepaymentFrequencyNthDayTypeOptionsAdapter
        binding.spRepaymentFreqNthDay.onItemSelectedListener = this
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter = ArrayAdapter(
                requireActivity(), android.R.layout.simple_spinner_item,
                mListRepaymentFrequencyDayOfWeekTypeOptions)
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRepaymentFreqDayOfWeek.adapter = mRepaymentFrequencyDayOfWeekTypeOptionsAdapter
        binding.spRepaymentFreqDayOfWeek.onItemSelectedListener = this
    }

    override fun onDatePicked(date: String) {
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
        mGroupLoanAccountPresenter!!.loadAllLoans()
    }

    private fun inflateLoanPurposeSpinner() {
        mGroupLoanAccountPresenter!!.loadGroupLoansAccountTemplate(groupId, productId)
    }

    private fun initiateLoanCreation(loansPayload: GroupLoanPayload) {
        mGroupLoanAccountPresenter!!.createGroupLoanAccount(loansPayload)
    }

    fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvSubmittedonDate.text = MFDatePicker.getDatePickedAsString()
    }

    fun onClickSubmittedonDate() {
        issubmittedDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    fun inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvDisbursementonDate.text = MFDatePicker.getDatePickedAsString()
    }

    fun onClickDisbursementonDate() {
        isdisbursementDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun showAllLoans(productLoans: List<LoanProducts?>?) {
        mLoanProducts = loans as List<LoanProducts>
        mGroupLoanAccountPresenter
                ?.filterLoanProducts(loans)?.let { mListLoanProductsNames.addAll(it) }
        loanProductAdapter!!.notifyDataSetChanged()
    }

    override fun showGroupLoanTemplate(groupLoanTemplate: GroupLoanTemplate?) {
        mGroupLoanTemplate = groupLoanTemplate
        amortizationType.clear()
        amortizationType.addAll(mGroupLoanAccountPresenter!!.filterAmortizations(groupLoanTemplate?.amortizationTypeOptions))
        amortizationTypeAdapter!!.notifyDataSetChanged()
        interestCalculationPeriodType.clear()
        if (groupLoanTemplate != null) {
            interestCalculationPeriodType.addAll(mGroupLoanAccountPresenter!!.filterInterestCalculationPeriods(
                    groupLoanTemplate.interestCalculationPeriodTypeOptions))
        }
        interestCalculationPeriodTypeAdapter!!.notifyDataSetChanged()
        transactionProcessingStrategy.clear()
        transactionProcessingStrategy.addAll(mGroupLoanAccountPresenter!!.filterTransactionProcessingStrategies(groupLoanTemplate?.transactionProcessingStrategyOptions))
        transactionProcessingStrategyAdapter!!.notifyDataSetChanged()
        termFrequencyType.clear()
        termFrequencyType.addAll(mGroupLoanAccountPresenter!!.filterTermFrequencyTypes(groupLoanTemplate?.termFrequencyTypeOptions))
        termFrequencyTypeAdapter!!.notifyDataSetChanged()
        loanPurposeType.clear()
        loanPurposeType.addAll(mGroupLoanAccountPresenter!!.filterLoanPurposeTypes(groupLoanTemplate?.loanPurposeOptions))
        loanPurposeTypeAdapter!!.notifyDataSetChanged()
        interestTypeOptions.clear()
        interestTypeOptions.addAll(mGroupLoanAccountPresenter!!.filterInterestTypeOptions(groupLoanTemplate?.interestTypeOptions))
        interestTypeOptionsAdapter!!.notifyDataSetChanged()
        loanOfficerOptions.clear()
        loanOfficerOptions.addAll(mGroupLoanAccountPresenter!!.filterLoanOfficers(groupLoanTemplate?.loanOfficerOptions))
        loanOfficerOptionsAdapter!!.notifyDataSetChanged()
        fundOptions.clear()
        fundOptions.addAll(mGroupLoanAccountPresenter!!.filterFunds(groupLoanTemplate?.fundOptions))
        fundOptionsAdapter!!.notifyDataSetChanged()
        mListRepaymentFrequencyNthDayTypeOptions.clear()
        mRepaymentFrequencyNthDayTypeOptions = mGroupLoanTemplate!!
                .repaymentFrequencyNthDayTypeOptions
        for (options in mRepaymentFrequencyNthDayTypeOptions) {
            mListRepaymentFrequencyNthDayTypeOptions.add(options.value)
        }
        mListRepaymentFrequencyDayOfWeekTypeOptions.clear()
        mRepaymentFrequencyDaysOfWeekTypeOptions = mGroupLoanTemplate!!
                .repaymentFrequencyDaysOfWeekTypeOptions
        for (options in mRepaymentFrequencyDaysOfWeekTypeOptions) {
            mListRepaymentFrequencyDayOfWeekTypeOptions.add(options.value)
        }
        showDefaultValues()
    }

    override fun showGroupLoansAccountCreatedSuccessfully(loans: Loans?) {
        Toast.makeText(requireActivity(), "The Loan has been submitted for Approval", Toast.LENGTH_LONG).show()
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(binding.root, s)
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGroupLoanAccountPresenter!!.detachView()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
    }

    interface OnDialogFragmentInteractionListener

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_lproduct -> {
                productId = mLoanProducts!![position].id
                inflateLoanPurposeSpinner()
            }
            R.id.sp_amortization -> amortizationTypeId = mGroupLoanTemplate
                    ?.amortizationTypeOptions!![position].id
            R.id.sp_interestcalculationperiod -> interestCalculationPeriodTypeId = mGroupLoanTemplate
                    ?.interestCalculationPeriodTypeOptions!![position].id
            R.id.sp_repaymentstrategy -> transactionProcessingStrategyId = mGroupLoanTemplate
                    ?.transactionProcessingStrategyOptions!![position].id
            R.id.sp_payment_periods -> {
                loanTermFrequency = mGroupLoanTemplate
                        ?.termFrequencyTypeOptions!![position].id
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE)
                    inflateRepaidMonthSpinners()
                } else {
                    showHideRepaidMonthSpinners(View.GONE)
                }
            }
            R.id.sp_repayment_freq_nth_day -> repaymentFrequencyNthDayType = mGroupLoanTemplate
                    ?.repaymentFrequencyNthDayTypeOptions!![position].id
            R.id.sp_repayment_freq_day_of_week -> repaymentFrequencyDayOfWeek = mGroupLoanTemplate
                    ?.repaymentFrequencyDaysOfWeekTypeOptions!![position].id
            R.id.sp_loan_purpose -> loanPurposeId = mGroupLoanTemplate!!.loanPurposeOptions[position].id
            R.id.sp_interest_type -> interestTypeMethodId = mGroupLoanTemplate!!.interestTypeOptions[position].id
            R.id.sp_loan_officer -> loanOfficerId = mGroupLoanTemplate!!.loanOfficerOptions[position].id
            R.id.sp_fund -> fundId = mGroupLoanTemplate!!.fundOptions[position].id
        }
    }

    private fun showHideRepaidMonthSpinners(visibility: Int) {
        binding.spRepaymentFreqNthDay.visibility = visibility
        binding.spRepaymentFreqDayOfWeek.visibility = visibility
        binding.tvRepaidNthfreqLabelOn.visibility = visibility
    }

    private fun showDefaultValues() {
        interestRatePerPeriod = mGroupLoanTemplate!!.interestRatePerPeriod
        loanTermFrequencyType = mGroupLoanTemplate!!.interestRateFrequencyType.id
        termFrequency = mGroupLoanTemplate!!.termFrequency
        binding.etPrincipal.setText(mGroupLoanTemplate!!.principal.toString())
        binding.etNumberofrepayments.setText(mGroupLoanTemplate!!.numberOfRepayments.toString())
        binding.tvNominalRateYearMonth.text = mGroupLoanTemplate!!.interestRateFrequencyType.value
        binding.etNominalInterestRate.setText(mGroupLoanTemplate!!.interestRatePerPeriod.toString())
        binding.etLoanterm.setText(termFrequency.toString())
        if (mGroupLoanTemplate!!.repaymentEvery != null) {
            repaymentEvery = mGroupLoanTemplate!!.repaymentEvery
            binding.etRepaidevery.setText(repaymentEvery.toString())
        }
        if (mGroupLoanTemplate!!.fundId != null) {
            fundId = mGroupLoanTemplate!!.fundId
            binding.spFund.setSelection(mGroupLoanTemplate!!.getFundNameFromId(fundId))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        fun newInstance(groupId: Int): GroupLoanAccountFragment {
            val grouploanAccountFragment = GroupLoanAccountFragment()
            val args = Bundle()
            args.putInt(Constants.GROUP_ID, groupId)
            grouploanAccountFragment.arguments = args
            return grouploanAccountFragment
        }
    }
}