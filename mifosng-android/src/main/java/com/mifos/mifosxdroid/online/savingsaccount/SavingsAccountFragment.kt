/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.client.Savings
import com.mifos.objects.organisation.ProductSavings
import com.mifos.objects.templates.savings.SavingProductsTemplate
import com.mifos.services.data.SavingsPayload
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Network
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this Dialog Fragment to Create and/or Update charges
 */
class SavingsAccountFragment : ProgressableDialogFragment(), OnDatePickListener, SavingsAccountMvpView, OnItemSelectedListener {
    @JvmField
    @BindView(R.id.sp_product)
    var spProduct: Spinner? = null

    @JvmField
    @BindView(R.id.sp_field_officer)
    var spFieldOfficer: Spinner? = null

    @JvmField
    @BindView(R.id.et_client_external_id)
    var etClientExternalId: EditText? = null

    @JvmField
    @BindView(R.id.tv_submittedon_date)
    var tvSubmissionDate: TextView? = null

    @JvmField
    @BindView(R.id.et_nominal_annual)
    var etNominalAnnual: EditText? = null

    @JvmField
    @BindView(R.id.sp_interest_calc)
    var tvInterestCalc: TextView? = null

    @JvmField
    @BindView(R.id.sp_interest_comp)
    var tvInterestComp: TextView? = null

    @JvmField
    @BindView(R.id.sp_interest_p_period)
    var tvInterestPeriod: TextView? = null

    @JvmField
    @BindView(R.id.sp_days_in_year)
    var tvDaysInYear: TextView? = null

    @JvmField
    @BindView(R.id.cb_enforce_required_balance)
    var cbEnforceRequiredBalance: CheckBox? = null

    @JvmField
    @BindView(R.id.et_min_required_balance)
    var etMinRequiredBalance: EditText? = null

    @JvmField
    @BindView(R.id.cb_overdraft_allowed)
    var cbOverdraftAllowed: CheckBox? = null

    @JvmField
    @BindView(R.id.et_max_overdraft_amount)
    var etMaxOverdraftAmount: EditText? = null

    @JvmField
    @BindView(R.id.et_nominal_annual_overdraft)
    var etNominalAnnualOverdraft: EditText? = null

    @JvmField
    @BindView(R.id.et_min_overdraft_required)
    var etMinOverdraftRequired: EditText? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var btnSubmit: Button? = null

    @JvmField
    @Inject
    var mSavingsAccountPresenter: SavingsAccountPresenter? = null
    private lateinit var rootView: View
    private var mfDatePicker: DialogFragment? = null
    private var productId = 0
    private var clientId = 0
    private var fieldOfficerId = 0
    private var groupId = 0
    private var submission_date: String? = null
    var mFieldOfficerNames: MutableList<String> = ArrayList()
    var mListSavingProductsNames: MutableList<String> = ArrayList()
    var mFieldOfficerAdapter: ArrayAdapter<String>? = null
    var mSavingProductsAdapter: ArrayAdapter<String>? = null
    private var mSavingProductsTemplateByProductId: SavingProductsTemplate? = null
    private var mProductSavings: List<ProductSavings>? = null
    private var isGroupAccount = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        val arguments = arguments
        if (arguments != null) {
            isGroupAccount = arguments.getBoolean(Constants.GROUP_ACCOUNT)
            clientId = arguments.getInt(Constants.CLIENT_ID)
            groupId = arguments.getInt(Constants.GROUP_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add_savings_account, null)
        ButterKnife.bind(this, rootView)
        mSavingsAccountPresenter!!.attachView(this)
        inflateSubmissionDate()
        inflateSavingsSpinners()
        mSavingsAccountPresenter!!.loadSavingsAccountsAndTemplate()
        return rootView
    }

    @OnCheckedChanged(R.id.cb_enforce_required_balance)
    fun onClickOverdraftAllowedCheckBox() {
        etMinRequiredBalance!!.visibility = if (cbEnforceRequiredBalance!!.isChecked) View.VISIBLE else View.GONE
    }

    @OnCheckedChanged(R.id.cb_overdraft_allowed)
    fun onClickMinRequiredCheckBox() {
        etMaxOverdraftAmount!!.visibility = if (cbOverdraftAllowed!!.isChecked) View.VISIBLE else View.GONE
        etNominalAnnualOverdraft!!.visibility = if (cbOverdraftAllowed!!.isChecked) View.VISIBLE else View.GONE
        etMinOverdraftRequired!!.visibility = if (cbOverdraftAllowed!!.isChecked) View.VISIBLE else View.GONE
    }

    fun inflateSavingsSpinners() {
        mFieldOfficerAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mFieldOfficerNames)
        mFieldOfficerAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFieldOfficer!!.adapter = mFieldOfficerAdapter
        spFieldOfficer!!.onItemSelectedListener = this
        mSavingProductsAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, mListSavingProductsNames)
        mSavingProductsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spProduct!!.adapter = mSavingProductsAdapter
        spProduct!!.onItemSelectedListener = this
    }

    @OnClick(R.id.btn_submit)
    fun submitSavingsAccount() {
        if (Network.isOnline(context)) {
            val savingsPayload = SavingsPayload()
            savingsPayload.externalId = etClientExternalId!!.editableText.toString()
            savingsPayload.locale = "en"
            savingsPayload.submittedOnDate = submission_date
            savingsPayload.dateFormat = "dd MMMM yyyy"
            if (isGroupAccount) {
                savingsPayload.groupId = groupId
            } else {
                savingsPayload.clientId = clientId
            }
            savingsPayload.productId = productId
            savingsPayload.fieldOfficerId = fieldOfficerId
            savingsPayload.nominalAnnualInterestRate = etNominalAnnual!!.editableText
                    .toString()
            savingsPayload.allowOverdraft = cbOverdraftAllowed!!.isChecked
            savingsPayload.nominalAnnualInterestRateOverdraft = etNominalAnnualOverdraft!!.editableText.toString()
            savingsPayload.overdraftLimit = etMaxOverdraftAmount!!.editableText
                    .toString()
            savingsPayload.minOverdraftForInterestCalculation = etMinOverdraftRequired!!.editableText.toString()
            savingsPayload.enforceMinRequiredBalance = cbEnforceRequiredBalance!!.isChecked
            savingsPayload.minRequiredOpeningBalance = etMinRequiredBalance!!.editableText
                    .toString()
            mSavingsAccountPresenter!!.createSavingsAccount(savingsPayload)
        } else {
            Toaster.show(rootView, R.string.error_network_not_available)
        }
    }

    override fun onDatePicked(date: String) {
        tvSubmissionDate!!.text = date
        submission_date = date
        setSubmissionDate()
    }

    fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tvSubmissionDate!!.text = MFDatePicker.getDatePickedAsString()
        setSubmissionDate()
    }

    @OnClick(R.id.tv_submittedon_date)
    fun onClickTextViewSubmissionDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    fun setSubmissionDate() {
        submission_date = tvSubmissionDate!!.text.toString()
        submission_date = DateHelper.getDateAsStringUsedForCollectionSheetPayload(submission_date).replace("-", " ")
    }

    override fun showSavingsAccounts(productSavings: List<ProductSavings>?) {
        mProductSavings = productSavings
        mSavingsAccountPresenter
                ?.filterSavingProductsNames(productSavings)?.let { mListSavingProductsNames.addAll(it) }
        mSavingProductsAdapter!!.notifyDataSetChanged()
    }

    override fun showSavingsAccountTemplateByProduct(savingProductsTemplate: SavingProductsTemplate) {
        mSavingProductsTemplateByProductId = savingProductsTemplate
        mFieldOfficerNames.addAll(mSavingsAccountPresenter!!.filterFieldOfficerNames(savingProductsTemplate.fieldOfficerOptions))
        mFieldOfficerAdapter!!.notifyDataSetChanged()
        tvInterestCalc!!.text = savingProductsTemplate.interestCalculationType.value
        tvInterestComp!!.text = savingProductsTemplate.interestCompoundingPeriodType.value
        tvInterestPeriod!!.text = savingProductsTemplate.interestPostingPeriodType.value
        tvDaysInYear!!.text = savingProductsTemplate.interestCalculationDaysInYearType.value
    }

    override fun showSavingsAccountCreatedSuccessfully(savings: Savings?) {
        Toast.makeText(activity,
                resources.getString(R.string.savings_account_submitted_for_approval),
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showFetchingError(errorMessage: Int) {
        Toaster.show(rootView, resources.getString(errorMessage))
    }

    override fun showFetchingError(errorMessage: String?) {
        Toaster.show(rootView, errorMessage)
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSavingsAccountPresenter!!.detachView()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_field_officer -> fieldOfficerId = mSavingProductsTemplateByProductId
                    ?.getFieldOfficerOptions()!![position].id
            R.id.sp_product -> {
                productId = mProductSavings!![position].id
                if (isGroupAccount) {
                    mSavingsAccountPresenter!!.loadGroupSavingAccountTemplateByProduct(groupId, productId)
                } else {
                    mSavingsAccountPresenter!!.loadClientSavingAccountTemplateByProduct(clientId, productId)
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        val LOG_TAG = SavingsAccountFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(id: Int, isGroupAccount: Boolean): SavingsAccountFragment {
            val savingsAccountFragment = SavingsAccountFragment()
            val args = Bundle()
            args.putInt(if (isGroupAccount) Constants.GROUP_ID else Constants.CLIENT_ID, id)
            args.putBoolean(Constants.GROUP_ACCOUNT, isGroupAccount)
            savingsAccountFragment.arguments = args
            return savingsAccountFragment
        }
    }
}