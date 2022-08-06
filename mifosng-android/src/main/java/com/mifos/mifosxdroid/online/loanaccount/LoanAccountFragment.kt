/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccount

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
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.LoanTemplate
import com.mifos.objects.templates.loans.RepaymentFrequencyDaysOfWeekTypeOptions
import com.mifos.objects.templates.loans.RepaymentFrequencyNthDayTypeOptions
import com.mifos.services.data.LoansPayload
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this  Fragment to Create and/or Update loan
 */
class LoanAccountFragment : ProgressableDialogFragment(), OnDatePickListener, LoanAccountMvpView, OnItemSelectedListener {
    val LOG_TAG = javaClass.simpleName
    lateinit var rootView: View

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_lproduct)
    var spLoanProduct: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_loan_purpose)
    var spLoanPurpose: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_submittedon_date)
    var tvSubmittedOnDate: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_client_external_id)
    var etClientExternalId: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_principal)
    var etPrincipal: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_linking_options)
    var spLinkingOptions: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_loanterm)
    var etLoanTerm: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_numberofrepayments)
    var etNumberOfRepayments: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_repaidevery)
    var etRepaidEvery: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_payment_periods)
    var spPaymentPeriods: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_repaid_nthfreq_label_on)
    var tvRepaidNthFreqLabelOn: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_repayment_freq_nth_day)
    var spRepaymentFreqNthDay: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_loan_term_periods)
    var spLoanTermFrequencyType: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_repayment_freq_day_of_week)
    var spRepaymentFreqDayOfWeek: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_nominal_interest_rate)
    var etNominalInterestRate: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_nominal_rate_year_month)
    var tvNominalRatePerYearMonth: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_amortization)
    var spAmortization: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_interestcalculationperiod)
    var spInterestCalculationPeriod: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_repaymentstrategy)
    var spRepaymentStrategy: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_interest_type)
    var spInterestType: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_loan_officer)
    var spLoanOfficer: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_fund)
    var spFund: Spinner? = null

    @BindView(R.id.cb_calculateinterest)
    lateinit var cbCalculateInterest: CheckBox

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_disbursementon_date)
    var tvDisbursementOnDate: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.btn_loan_submit)
    var btnLoanSubmit: Button? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanAccountPresenter: LoanAccountPresenter? = null
    var submissionDate: String? = null
    var disbursementDate: String? = null
    private var hasDataTables = false
    private var mfDatePicker: DialogFragment? = null
    private var productId = 0
    private var clientId = 0
    private var loanPurposeId = 0
    private var loanTermFrequency = 0
    private val loanTermFrequencyType = 0
    private var termFrequency: Int? = null
    private var repaymentEvery: Int? = null
    private var transactionProcessingStrategyId = 0
    private var amortizationTypeId = 0
    private var interestCalculationPeriodTypeId = 0
    private var fundId by Delegates.notNull<Int>()
    private var loanOfficerId = 0
    private var interestTypeId = 0
    private var repaymentFrequencyNthDayType: Int? = null
    private var repaymentFrequencyDayOfWeek: Int? = null
    private var interestRatePerPeriod: Double? = null
    private var linkAccountId: Int? = null
    private var isDisbursebemntDate = false
    private var isSubmissionDate = false
    var mLoanProducts: List<LoanProducts> = ArrayList()
    var mRepaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = ArrayList()
    var mRepaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = ArrayList()
    var mLoanTemplate = LoanTemplate()
    var mListLoanProducts: MutableList<String> = ArrayList()
    var mListLoanPurposeOptions: MutableList<String> = ArrayList()
    var mListAccountLinkingOptions: MutableList<String> = ArrayList()
    var mListAmortizationTypeOptions: MutableList<String> = ArrayList()
    var mListInterestCalculationPeriodTypeOptions: MutableList<String> = ArrayList()
    var mListTransactionProcessingStrategyOptions: MutableList<String> = ArrayList()
    var mListTermFrequencyTypeOptions: MutableList<String> = ArrayList()
    var mListLoanTermFrequencyTypeOptions: MutableList<String> = ArrayList()
    var mListRepaymentFrequencyNthDayTypeOptions: MutableList<String> = ArrayList()
    var mListRepaymentFrequencyDayOfWeekTypeOptions: MutableList<String> = ArrayList()
    var mListLoanFundOptions: MutableList<String> = ArrayList()
    var mListLoanOfficerOptions: MutableList<String> = ArrayList()
    var mListInterestTypeOptions: MutableList<String> = ArrayList()
    var mLoanProductAdapter: ArrayAdapter<String>? = null
    var mLoanPurposeOptionsAdapter: ArrayAdapter<String>? = null
    var mAccountLinkingOptionsAdapter: ArrayAdapter<String>? = null
    var mAmortizationTypeOptionsAdapter: ArrayAdapter<String>? = null
    var mInterestCalculationPeriodTypeOptionsAdapter: ArrayAdapter<String>? = null
    var mTransactionProcessingStrategyOptionsAdapter: ArrayAdapter<String>? = null
    var mTermFrequencyTypeOptionsAdapter: ArrayAdapter<String>? = null
    var mLoanTermFrequencyTypeAdapter: ArrayAdapter<String>? = null
    var mRepaymentFrequencyNthDayTypeOptionsAdapter: ArrayAdapter<String>? = null
    var mRepaymentFrequencyDayOfWeekTypeOptionsAdapter: ArrayAdapter<String>? = null
    var mLoanFundOptionsAdapter: ArrayAdapter<String>? = null
    var mLoanOfficerOptionsAdapter: ArrayAdapter<String>? = null
    var mInterestTypeOptionsAdapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) clientId = requireArguments().getInt(Constants.CLIENT_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        rootView = inflater.inflate(R.layout.fragment_add_loan, null)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        ButterKnife.bind(this, rootView)
        mLoanAccountPresenter!!.attachView(this)
        inflateSubmissionDate()
        inflateDisbursementDate()
        inflateLoansProductSpinner()
        disbursementDate = tvDisbursementOnDate!!.text.toString()
        submissionDate = tvSubmittedOnDate!!.text.toString()
        submissionDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submissionDate).replace("-", " ")
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate).replace("-", " ")
        inflateSpinners()
        return rootView
    }

    @OnClick(R.id.btn_loan_submit)
    fun submit() {
        val loansPayload = LoansPayload()
        loansPayload.isAllowPartialPeriodInterestCalcualtion = cbCalculateInterest
                .isChecked()
        loansPayload.amortizationType = amortizationTypeId
        loansPayload.clientId = clientId
        loansPayload.dateFormat = "dd MMMM yyyy"
        loansPayload.expectedDisbursementDate = disbursementDate
        loansPayload.interestCalculationPeriodType = interestCalculationPeriodTypeId
        loansPayload.loanType = "individual"
        loansPayload.locale = "en"
        loansPayload.numberOfRepayments = etNumberOfRepayments!!.editableText
                .toString()
        loansPayload.principal = etPrincipal!!.editableText.toString()
        loansPayload.productId = productId
        loansPayload.repaymentEvery = etRepaidEvery!!.editableText.toString()
        loansPayload.submittedOnDate = submissionDate
        loansPayload.loanPurposeId = loanPurposeId
        loansPayload.loanTermFrequency = etLoanTerm!!.editableText.toString().toInt()
        loansPayload.loanTermFrequencyType = loanTermFrequency

        //loanTermFrequencyType and repaymentFrequencyType should be the same.
        loansPayload.repaymentFrequencyType = loanTermFrequency
        loansPayload.repaymentFrequencyDayOfWeekType = if (repaymentFrequencyDayOfWeek != null) repaymentFrequencyDayOfWeek else null
        loansPayload.repaymentFrequencyNthDayType = if (repaymentFrequencyNthDayType != null) repaymentFrequencyNthDayType else null
        loansPayload.transactionProcessingStrategyId = transactionProcessingStrategyId
        loansPayload.fundId = fundId!!
        loansPayload.interestType = interestTypeId
        loansPayload.loanOfficerId = loanOfficerId
        loansPayload.linkAccountId = linkAccountId
        interestRatePerPeriod =
                etNominalInterestRate!!.editableText.toString().toDouble()
        loansPayload.interestRatePerPeriod = interestRatePerPeriod
        if (hasDataTables) {
            val fragment = DataTableListFragment.newInstance(
                    mLoanTemplate.dataTables,
                    loansPayload, Constants.CLIENT_LOAN)
            val fragmentTransaction = requireActivity().supportFragmentManager
                    .beginTransaction()
            fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
            fragmentTransaction.replace(R.id.container, fragment).commit()
        } else {
            initiateLoanCreation(loansPayload)
        }
    }

    override fun onDatePicked(date: String) {
        if (isSubmissionDate) {
            tvSubmittedOnDate!!.text = date
            submissionDate = date
            isSubmissionDate = false
        }
        if (isDisbursebemntDate) {
            tvDisbursementOnDate!!.text = date
            disbursementDate = date
            isDisbursebemntDate = false
        }
    }

    private fun inflateSpinners() {

        //Inflating the LoanProducts Spinner
        mLoanProductAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item,
                mListLoanProducts)
        mLoanProductAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoanProduct!!.adapter = mLoanProductAdapter
        spLoanProduct!!.onItemSelectedListener = this

        //Inflating the LoanPurposeOptions
        mLoanPurposeOptionsAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item,
                mListLoanPurposeOptions)
        mLoanPurposeOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoanPurpose!!.adapter = mLoanPurposeOptionsAdapter
        spLoanPurpose!!.onItemSelectedListener = this

        //Inflating Linking Options
        mAccountLinkingOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListAccountLinkingOptions)
        mAccountLinkingOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLinkingOptions!!.adapter = mAccountLinkingOptionsAdapter
        spLinkingOptions!!.onItemSelectedListener = this

        //Inflating AmortizationTypeOptions Spinner
        mAmortizationTypeOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListAmortizationTypeOptions)
        mAmortizationTypeOptionsAdapter!!.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
        spAmortization!!.adapter = mAmortizationTypeOptionsAdapter
        spAmortization!!.onItemSelectedListener = this

        //Inflating InterestCalculationPeriodTypeOptions Spinner
        mInterestCalculationPeriodTypeOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListInterestCalculationPeriodTypeOptions)
        mInterestCalculationPeriodTypeOptionsAdapter!!.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
        spInterestCalculationPeriod!!.adapter = mInterestCalculationPeriodTypeOptionsAdapter
        spInterestCalculationPeriod!!.onItemSelectedListener = this

        //Inflate TransactionProcessingStrategyOptions Spinner
        mTransactionProcessingStrategyOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListTransactionProcessingStrategyOptions)
        mTransactionProcessingStrategyOptionsAdapter!!.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
        spRepaymentStrategy!!.adapter = mTransactionProcessingStrategyOptionsAdapter
        spRepaymentStrategy!!.onItemSelectedListener = this

        //Inflate TermFrequencyTypeOptionsAdapter Spinner
        mTermFrequencyTypeOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListTermFrequencyTypeOptions)
        mTermFrequencyTypeOptionsAdapter!!.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
        spPaymentPeriods!!.adapter = mTermFrequencyTypeOptionsAdapter
        spPaymentPeriods!!.onItemSelectedListener = this

        //Inflate LoanTerm Frequency Type adapter
        mLoanTermFrequencyTypeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListLoanTermFrequencyTypeOptions)
        mLoanTermFrequencyTypeAdapter!!.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)
        spLoanTermFrequencyType!!.adapter = mLoanTermFrequencyTypeAdapter
        spLoanTermFrequencyType!!.onItemSelectedListener = this

        //Inflate FondOptions Spinner
        mLoanFundOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListLoanFundOptions)
        mLoanFundOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFund!!.adapter = mLoanFundOptionsAdapter
        spFund!!.onItemSelectedListener = this

        //Inflating LoanOfficerOptions Spinner
        mLoanOfficerOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListLoanOfficerOptions)
        mLoanOfficerOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLoanOfficer!!.adapter = mLoanOfficerOptionsAdapter
        spLoanOfficer!!.onItemSelectedListener = this

        //Inflating InterestTypeOptions Spinner
        mInterestTypeOptionsAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mListInterestTypeOptions)
        mInterestTypeOptionsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spInterestType!!.adapter = mInterestTypeOptionsAdapter
        spInterestType!!.onItemSelectedListener = this
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
        spRepaymentFreqNthDay!!.setSelection(mListRepaymentFrequencyNthDayTypeOptions.size - 1)
        spRepaymentFreqDayOfWeek!!.setSelection(
                mListRepaymentFrequencyDayOfWeekTypeOptions.size - 1)
    }

    private fun inflateLoansProductSpinner() {
        mLoanAccountPresenter!!.loadAllLoans()
    }

    private fun inflateLoanPurposeSpinner() {
        mLoanAccountPresenter!!.loadLoanAccountTemplate(clientId, productId)
    }

    private fun initiateLoanCreation(loansPayload: LoansPayload) {
        mLoanAccountPresenter!!.createLoansAccount(loansPayload)
    }

    fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tvSubmittedOnDate!!.text = MFDatePicker.getDatePickedAsString()
    }

    @OnClick(R.id.tv_submittedon_date)
    fun setTvSubmittedOnDate() {
        isSubmissionDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    fun inflateDisbursementDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tvDisbursementOnDate!!.text = MFDatePicker.getDatePickedAsString()
    }

    @OnClick(R.id.tv_disbursementon_date)
    fun setTvDisbursementOnDate() {
        isDisbursebemntDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun showAllLoan(loans: List<LoanProducts>) {
        mLoanProducts = loans
        mListLoanProducts.clear()
        for (loanProducts in mLoanProducts) {
            mListLoanProducts.add(loanProducts.name)
        }
        mLoanProductAdapter!!.notifyDataSetChanged()
    }

    override fun showLoanAccountTemplate(loanTemplate: LoanTemplate) {
        mLoanTemplate = loanTemplate
        hasDataTables = mLoanTemplate.dataTables.size > 0
        mListRepaymentFrequencyNthDayTypeOptions.clear()
        mRepaymentFrequencyNthDayTypeOptions = mLoanTemplate
                .repaymentFrequencyNthDayTypeOptions
        for (options in mRepaymentFrequencyNthDayTypeOptions) {
            mListRepaymentFrequencyNthDayTypeOptions.add(options.value)
        }
        mListRepaymentFrequencyNthDayTypeOptions.add(
                resources.getString(R.string.select_week_hint))
        mListRepaymentFrequencyDayOfWeekTypeOptions.clear()
        mRepaymentFrequencyDaysOfWeekTypeOptions = mLoanTemplate
                .repaymentFrequencyDaysOfWeekTypeOptions
        for (options in mRepaymentFrequencyDaysOfWeekTypeOptions) {
            mListRepaymentFrequencyDayOfWeekTypeOptions.add(options.value)
        }
        mListRepaymentFrequencyDayOfWeekTypeOptions.add(
                resources.getString(R.string.select_day_hint))
        mListLoanPurposeOptions.clear()
        for (loanPurposeOptions in mLoanTemplate.loanPurposeOptions) {
            mListLoanPurposeOptions.add(loanPurposeOptions.name)
        }
        mLoanPurposeOptionsAdapter!!.notifyDataSetChanged()
        mListAccountLinkingOptions.clear()
        for (options in mLoanTemplate.accountLinkingOptions) {
            mListAccountLinkingOptions.add(options.productName)
        }
        mListAccountLinkingOptions.add(
                resources.getString(R.string.select_linkage_account_hint))
        mAccountLinkingOptionsAdapter!!.notifyDataSetChanged()
        mListAmortizationTypeOptions.clear()
        for (amortizationTypeOptions in mLoanTemplate.amortizationTypeOptions) {
            mListAmortizationTypeOptions.add(amortizationTypeOptions.value)
        }
        mAmortizationTypeOptionsAdapter!!.notifyDataSetChanged()
        mListInterestCalculationPeriodTypeOptions.clear()
        for (interestCalculationPeriodType in mLoanTemplate
                .interestCalculationPeriodTypeOptions) {
            mListInterestCalculationPeriodTypeOptions.add(interestCalculationPeriodType.value)
        }
        mInterestCalculationPeriodTypeOptionsAdapter!!.notifyDataSetChanged()
        mListTransactionProcessingStrategyOptions.clear()
        for (transactionProcessingStrategyOptions in mLoanTemplate.transactionProcessingStrategyOptions) {
            mListTransactionProcessingStrategyOptions.add(transactionProcessingStrategyOptions
                    .name)
        }
        mTransactionProcessingStrategyOptionsAdapter!!.notifyDataSetChanged()
        mListTermFrequencyTypeOptions.clear()
        for (termFrequencyTypeOptions in mLoanTemplate.termFrequencyTypeOptions) {
            mListTermFrequencyTypeOptions.add(termFrequencyTypeOptions.value)
        }
        mTermFrequencyTypeOptionsAdapter!!.notifyDataSetChanged()
        mListLoanTermFrequencyTypeOptions.clear()
        for (termFrequencyTypeOptions in mLoanTemplate.termFrequencyTypeOptions) {
            mListLoanTermFrequencyTypeOptions.add(termFrequencyTypeOptions.value)
        }
        mLoanTermFrequencyTypeAdapter!!.notifyDataSetChanged()
        mListLoanFundOptions.clear()
        for (fundOptions in mLoanTemplate.fundOptions) {
            mListLoanFundOptions.add(fundOptions.name)
        }
        mLoanFundOptionsAdapter!!.notifyDataSetChanged()
        mListLoanOfficerOptions.clear()
        for (loanOfficerOptions in mLoanTemplate.loanOfficerOptions) {
            mListLoanOfficerOptions.add(loanOfficerOptions.displayName)
        }
        mLoanOfficerOptionsAdapter!!.notifyDataSetChanged()
        mListInterestTypeOptions.clear()
        for (interestTypeOptions in mLoanTemplate.interestTypeOptions) {
            mListInterestTypeOptions.add(interestTypeOptions.value)
        }
        mInterestTypeOptionsAdapter!!.notifyDataSetChanged()
        showDefaultValues()
    }

    override fun showLoanAccountCreatedSuccessfully(loans: Loans?) {
        Toast.makeText(activity, R.string.loan_creation_success, Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showMessage(messageId: Int) {
        Toaster.show(rootView, messageId)
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(rootView, s)
    }

    override fun showProgressbar(show: Boolean) {
        showProgress(show)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanAccountPresenter!!.detachView()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_lproduct -> {
                productId = mLoanProducts[position].id
                inflateLoanPurposeSpinner()
            }
            R.id.sp_loan_purpose -> loanPurposeId = mLoanTemplate.loanPurposeOptions[position].id
            R.id.sp_amortization -> amortizationTypeId = mLoanTemplate.amortizationTypeOptions[position].id
            R.id.sp_interestcalculationperiod -> interestCalculationPeriodTypeId = mLoanTemplate
                    .interestCalculationPeriodTypeOptions[position].id
            R.id.sp_repaymentstrategy -> transactionProcessingStrategyId = mLoanTemplate
                    .transactionProcessingStrategyOptions[position].id
            R.id.sp_payment_periods -> {
                loanTermFrequency = mLoanTemplate.termFrequencyTypeOptions[position]
                        .id
                spLoanTermFrequencyType!!.setSelection(loanTermFrequency)
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
                spPaymentPeriods!!.setSelection(loanTermFrequency)
                if (loanTermFrequency == 2) {
                    // Show and inflate Nth day and week spinners
                    showHideRepaidMonthSpinners(View.VISIBLE)
                    inflateRepaidMonthSpinners()
                } else {
                    showHideRepaidMonthSpinners(View.GONE)
                }
            }
            R.id.sp_repayment_freq_nth_day -> repaymentFrequencyNthDayType = if (mListRepaymentFrequencyNthDayTypeOptions[position]
                    == resources.getString(R.string.select_week_hint)) {
                null
            } else {
                mLoanTemplate
                        .repaymentFrequencyNthDayTypeOptions[position].id
            }
            R.id.sp_repayment_freq_day_of_week -> repaymentFrequencyDayOfWeek = if (mListRepaymentFrequencyDayOfWeekTypeOptions[position]
                    == resources.getString(R.string.select_day_hint)) {
                null
            } else {
                mLoanTemplate
                        .repaymentFrequencyDaysOfWeekTypeOptions[position].id
            }
            R.id.sp_fund -> fundId = mLoanTemplate.fundOptions[position].id
            R.id.sp_loan_officer -> loanOfficerId = mLoanTemplate.loanOfficerOptions[position].id
            R.id.sp_interest_type -> interestTypeId = mLoanTemplate.interestTypeOptions[position].id
            R.id.sp_linking_options -> linkAccountId = if (mListAccountLinkingOptions[position]
                    == resources.getString(R.string.select_linkage_account_hint)) {
                null
            } else {
                mLoanTemplate.accountLinkingOptions[position].id
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    private fun showHideRepaidMonthSpinners(visibility: Int) {
        spRepaymentFreqNthDay!!.visibility = visibility
        spRepaymentFreqDayOfWeek!!.visibility = visibility
        tvRepaidNthFreqLabelOn!!.visibility = visibility
    }

    private fun showDefaultValues() {
        interestRatePerPeriod = mLoanTemplate.interestRatePerPeriod
        loanTermFrequency = mLoanTemplate.termPeriodFrequencyType.id
        termFrequency = mLoanTemplate.termFrequency
        etPrincipal!!.setText(mLoanTemplate.principal.toString())
        etNumberOfRepayments!!.setText(mLoanTemplate.numberOfRepayments.toString())
        tvNominalRatePerYearMonth
                ?.setText(mLoanTemplate.interestRateFrequencyType.value)
        etNominalInterestRate!!.setText(mLoanTemplate.interestRatePerPeriod.toString())
        etLoanTerm!!.setText(termFrequency.toString())
        if (mLoanTemplate.repaymentEvery != null) {
            repaymentEvery = mLoanTemplate.repaymentEvery
            etRepaidEvery!!.setText(repaymentEvery.toString())
        }
        if (mLoanTemplate.fundId != null) {
            fundId = mLoanTemplate.fundId
            spFund!!.setSelection(mLoanTemplate.getFundNameFromId(fundId))
        }
        spLinkingOptions!!.setSelection(mListAccountLinkingOptions.size)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(clientId: Int): LoanAccountFragment {
            val loanAccountFragment = LoanAccountFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            loanAccountFragment.arguments = args
            return loanAccountFragment
        }
    }
}