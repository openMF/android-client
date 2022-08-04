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
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster
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
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this  Fragment to Create and/or Update loan
 */
class GroupLoanAccountFragment : ProgressableDialogFragment(), OnDatePickListener, GroupLoanAccountMvpView, OnItemSelectedListener {
    val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.sp_lproduct)
    var spLoanProduct: Spinner? = null

    @JvmField
    @BindView(R.id.sp_loan_purpose)
    var spLoanPurpose: Spinner? = null

    @JvmField
    @BindView(R.id.tv_submittedon_date)
    var tvSubmittedOnDate: TextView? = null

    @JvmField
    @BindView(R.id.et_client_external_id)
    var etClientExternalId: EditText? = null

    @JvmField
    @BindView(R.id.et_principal)
    var etPrincipal: EditText? = null

    @JvmField
    @BindView(R.id.et_loanterm)
    var etLoanTerm: EditText? = null

    @JvmField
    @BindView(R.id.et_numberofrepayments)
    var etNumberOfRepayments: EditText? = null

    @JvmField
    @BindView(R.id.et_repaidevery)
    var etRepaidEvery: EditText? = null

    @JvmField
    @BindView(R.id.sp_payment_periods)
    var spPaymentPeriods: Spinner? = null

    @JvmField
    @BindView(R.id.tv_repaid_nthfreq_label_on)
    var tvRepaidNthFreqLabelOn: TextView? = null

    @JvmField
    @BindView(R.id.sp_repayment_freq_nth_day)
    var spRepaymentFreqNthDay: Spinner? = null

    @JvmField
    @BindView(R.id.sp_repayment_freq_day_of_week)
    var spRepaymentFreqDayOfWeek: Spinner? = null

    @JvmField
    @BindView(R.id.et_nominal_interest_rate)
    var etNominalInterestRate: EditText? = null

    @JvmField
    @BindView(R.id.tv_nominal_rate_year_month)
    var tvNominalRatePerYearMonth: TextView? = null

    @JvmField
    @BindView(R.id.sp_amortization)
    var spAmortization: Spinner? = null

    @JvmField
    @BindView(R.id.sp_interestcalculationperiod)
    var spInterestCalculationPeriod: Spinner? = null

    @JvmField
    @BindView(R.id.sp_fund)
    var spFund: Spinner? = null

    @JvmField
    @BindView(R.id.sp_loan_officer)
    var spLoanOfficer: Spinner? = null

    @JvmField
    @BindView(R.id.sp_interest_type)
    var spInterestType: Spinner? = null

    @JvmField
    @BindView(R.id.sp_repaymentstrategy)
    var spRepaymentStrategy: Spinner? = null

    @JvmField
    @BindView(R.id.cb_calculateinterest)
    var cbCalculateInterest: CheckBox? = null

    @JvmField
    @BindView(R.id.tv_disbursementon_date)
    var tvDisbursementonDate: TextView? = null

    @JvmField
    @BindView(R.id.btn_loan_submit)
    var btLoanSubmit: Button? = null

    @JvmField
    @BindView(R.id.tv_linking_options)
    var tvLinkingOptions: TextView? = null

    @JvmField
    @BindView(R.id.sp_linking_options)
    var spLinkingOptions: Spinner? = null

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
    private lateinit  var rootView: View
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
        rootView = inflater.inflate(R.layout.fragment_add_loan, null)
        ButterKnife.bind(this, rootView)
        mGroupLoanAccountPresenter!!.attachView(this)

        //Linking Options not yet implemented for Groups but the layout file is shared.
        //So, hiding the widgets
        tvLinkingOptions!!.visibility = View.GONE
        spLinkingOptions!!.visibility = View.GONE
        inflateSubmissionDate()
        inflateDisbursementDate()
        inflateLoansProductSpinner()
        inflateLoanSpinner()
        disbursementDate = tvDisbursementonDate!!.text.toString()
        submissionDate = tvSubmittedOnDate!!.text.toString()
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submissionDate).replace("-", " ")
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate).replace("-", " ")
        btLoanSubmit!!.setOnClickListener {
            val loansPayload = GroupLoanPayload()
            loansPayload.isAllowPartialPeriodInterestCalcualtion = cbCalculateInterest!!
                    .isChecked
            loansPayload.amortizationType = amortizationTypeId
            loansPayload.groupId = groupId
            loansPayload.dateFormat = "dd MMMM yyyy"
            loansPayload.expectedDisbursementDate = disbursementDate
            loansPayload.interestCalculationPeriodType = interestCalculationPeriodTypeId
            loansPayload.loanType = "group"
            loansPayload.locale = "en"
            loansPayload.numberOfRepayments = etNumberOfRepayments!!.editableText
                    .toString()
            loansPayload.principal = etPrincipal!!.editableText.toString()
            loansPayload.productId = productId
            loansPayload.repaymentEvery = etRepaidEvery!!.editableText.toString()
            loansPayload.submittedOnDate = submissionDate
            loansPayload.loanPurposeId = loanPurposeId
            loansPayload.loanPurposeId = loanPurposeId
            loansPayload.loanTermFrequency = etLoanTerm!!.editableText.toString().toInt()

            //loanTermFrequencyType and repaymentFrequencyType must take the same value.
            loansPayload.loanTermFrequencyType = loanTermFrequencyType
            loansPayload.repaymentFrequencyType = loanTermFrequencyType
            loansPayload.repaymentFrequencyDayOfWeekType = repaymentFrequencyDayOfWeek
            loansPayload.repaymentFrequencyNthDayType = repaymentFrequencyNthDayType
            loansPayload.transactionProcessingStrategyId = transactionProcessingStrategyId
            loansPayload.linkAccountId = linkAccountId
            interestRatePerPeriod = etNominalInterestRate
                    ?.getEditableText().toString().toDouble()
            loansPayload.interestRatePerPeriod = interestRatePerPeriod
            initiateLoanCreation(loansPayload)
        }
        return rootView
    }

    fun inflateLoanSpinner() {
        amortizationTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, amortizationType)
        amortizationTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spAmortization!!.adapter = amortizationTypeAdapter
        spAmortization!!.onItemSelectedListener = this
        interestCalculationPeriodTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, interestCalculationPeriodType)
        interestCalculationPeriodTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spInterestCalculationPeriod!!.adapter = interestCalculationPeriodTypeAdapter
        spInterestCalculationPeriod!!.onItemSelectedListener = this
        transactionProcessingStrategyAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, transactionProcessingStrategy)
        transactionProcessingStrategyAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRepaymentStrategy!!.adapter = transactionProcessingStrategyAdapter
        spRepaymentStrategy!!.onItemSelectedListener = this
        termFrequencyTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, termFrequencyType)
        termFrequencyTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPaymentPeriods!!.adapter = termFrequencyTypeAdapter
        spPaymentPeriods!!.onItemSelectedListener = this
        loanProductAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListLoanProductsNames)
        loanProductAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoanProduct!!.adapter = loanProductAdapter
        spLoanProduct!!.onItemSelectedListener = this
        loanPurposeTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, loanPurposeType)
        loanPurposeTypeAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoanPurpose!!.adapter = loanPurposeTypeAdapter
        spLoanPurpose!!.onItemSelectedListener = this
        interestTypeOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, interestTypeOptions)
        interestTypeOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spInterestType!!.adapter = interestTypeOptionsAdapter
        spInterestType!!.onItemSelectedListener = this
        loanOfficerOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, loanOfficerOptions)
        loanOfficerOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoanOfficer!!.adapter = loanOfficerOptionsAdapter
        spLoanOfficer!!.onItemSelectedListener = this
        fundOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, fundOptions)
        fundOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFund!!.adapter = fundOptionsAdapter
        spFund!!.onItemSelectedListener = this
    }

    private fun inflateRepaidMonthSpinners() {
        mRepaymentFrequencyNthDayTypeOptionsAdapter = ArrayAdapter(
                requireActivity(), android.R.layout.simple_spinner_item,
                mListRepaymentFrequencyNthDayTypeOptions)
        mRepaymentFrequencyNthDayTypeOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRepaymentFreqNthDay!!.adapter = mRepaymentFrequencyNthDayTypeOptionsAdapter
        spRepaymentFreqNthDay!!.onItemSelectedListener = this
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter = ArrayAdapter(
                requireActivity(), android.R.layout.simple_spinner_item,
                mListRepaymentFrequencyDayOfWeekTypeOptions)
        mRepaymentFrequencyDayOfWeekTypeOptionsAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRepaymentFreqDayOfWeek!!.adapter = mRepaymentFrequencyDayOfWeekTypeOptionsAdapter
        spRepaymentFreqDayOfWeek!!.onItemSelectedListener = this
    }

    override fun onDatePicked(date: String) {
        if (isdisbursementDate) {
            tvDisbursementonDate!!.text = date
            disbursementDate = date
            isdisbursementDate = false
        }
        if (issubmittedDate) {
            tvSubmittedOnDate!!.text = date
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
        tvSubmittedOnDate!!.text = MFDatePicker.getDatePickedAsString()
    }

    @OnClick(R.id.tv_submittedon_date)
    fun onClickSubmittedonDate() {
        issubmittedDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    fun inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tvDisbursementonDate!!.text = MFDatePicker.getDatePickedAsString()
    }

    @OnClick(R.id.tv_disbursementon_date)
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
        Toaster.show(rootView, s)
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
                    ?.getAmortizationTypeOptions()!![position].id
            R.id.sp_interestcalculationperiod -> interestCalculationPeriodTypeId = mGroupLoanTemplate
                    ?.getInterestCalculationPeriodTypeOptions()!![position].id
            R.id.sp_repaymentstrategy -> transactionProcessingStrategyId = mGroupLoanTemplate
                    ?.getTransactionProcessingStrategyOptions()!![position].id
            R.id.sp_payment_periods -> {
                loanTermFrequency = mGroupLoanTemplate
                        ?.getTermFrequencyTypeOptions()!![position].id
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE)
                    inflateRepaidMonthSpinners()
                } else {
                    showHideRepaidMonthSpinners(View.GONE)
                }
            }
            R.id.sp_repayment_freq_nth_day -> repaymentFrequencyNthDayType = mGroupLoanTemplate
                    ?.getRepaymentFrequencyNthDayTypeOptions()!![position].id
            R.id.sp_repayment_freq_day_of_week -> repaymentFrequencyDayOfWeek = mGroupLoanTemplate
                    ?.getRepaymentFrequencyDaysOfWeekTypeOptions()!![position].id
            R.id.sp_loan_purpose -> loanPurposeId = mGroupLoanTemplate!!.loanPurposeOptions[position].id
            R.id.sp_interest_type -> interestTypeMethodId = mGroupLoanTemplate!!.interestTypeOptions[position].id
            R.id.sp_loan_officer -> loanOfficerId = mGroupLoanTemplate!!.loanOfficerOptions[position].id
            R.id.sp_fund -> fundId = mGroupLoanTemplate!!.fundOptions[position].id
        }
    }

    private fun showHideRepaidMonthSpinners(visibility: Int) {
        spRepaymentFreqNthDay!!.visibility = visibility
        spRepaymentFreqDayOfWeek!!.visibility = visibility
        tvRepaidNthFreqLabelOn!!.visibility = visibility
    }

    private fun showDefaultValues() {
        interestRatePerPeriod = mGroupLoanTemplate!!.interestRatePerPeriod
        loanTermFrequencyType = mGroupLoanTemplate!!.interestRateFrequencyType.id
        termFrequency = mGroupLoanTemplate!!.termFrequency
        etPrincipal!!.setText(mGroupLoanTemplate!!.principal.toString())
        etNumberOfRepayments!!.setText(mGroupLoanTemplate!!.numberOfRepayments.toString())
        tvNominalRatePerYearMonth
                ?.setText(mGroupLoanTemplate!!.interestRateFrequencyType.value)
        etNominalInterestRate!!.setText(mGroupLoanTemplate!!.interestRatePerPeriod.toString())
        etLoanTerm!!.setText(termFrequency.toString())
        if (mGroupLoanTemplate!!.repaymentEvery != null) {
            repaymentEvery = mGroupLoanTemplate!!.repaymentEvery
            etRepaidEvery!!.setText(repaymentEvery.toString())
        }
        if (mGroupLoanTemplate!!.fundId != null) {
            fundId = mGroupLoanTemplate!!.fundId
            spFund!!.setSelection(mGroupLoanTemplate!!.getFundNameFromId(fundId))
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