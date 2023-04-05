/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccount

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
import com.mifos.mifosxdroid.databinding.FragmentAddSavingsAccountBinding
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
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this Dialog Fragment to Create and/or Update charges
 */
class SavingsAccountFragment : ProgressableDialogFragment(), OnDatePickListener, SavingsAccountMvpView, OnItemSelectedListener {

    private lateinit var binding: FragmentAddSavingsAccountBinding

    @JvmField
    @Inject
    var mSavingsAccountPresenter: SavingsAccountPresenter? = null
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

        binding = FragmentAddSavingsAccountBinding.inflate(inflater)
        mSavingsAccountPresenter!!.attachView(this)
        inflateSubmissionDate()
        inflateSavingsSpinners()
        mSavingsAccountPresenter!!.loadSavingsAccountsAndTemplate()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener { submitSavingsAccount() }
        binding.tvSubmittedonDate.setOnClickListener { onClickTextViewSubmissionDate() }
        binding.cbEnforceRequiredBalance.setOnCheckedChangeListener { compoundButton, b -> onClickOverdraftAllowedCheckBox() }
        binding.cbOverdraftAllowed.setOnCheckedChangeListener{ _,_ -> onClickMinRequiredCheckBox()}
    }

    fun onClickOverdraftAllowedCheckBox() {
        binding.etMinRequiredBalance.visibility = if (binding.cbEnforceRequiredBalance.isChecked) View.VISIBLE else View.GONE
    }

    fun onClickMinRequiredCheckBox() {
        binding.etMaxOverdraftAmount.visibility = if (binding.cbOverdraftAllowed.isChecked) View.VISIBLE else View.GONE
        binding.etNominalAnnualOverdraft.visibility = if (binding.cbOverdraftAllowed.isChecked) View.VISIBLE else View.GONE
        binding.etMinOverdraftRequired.visibility = if (binding.cbOverdraftAllowed.isChecked) View.VISIBLE else View.GONE
    }

    fun inflateSavingsSpinners() {
        mFieldOfficerAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, mFieldOfficerNames)
        mFieldOfficerAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFieldOfficer.adapter = mFieldOfficerAdapter
        binding.spFieldOfficer.onItemSelectedListener = this
        mSavingProductsAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, mListSavingProductsNames)
        mSavingProductsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spProduct.adapter = mSavingProductsAdapter
        binding.spProduct.onItemSelectedListener = this
    }

    fun submitSavingsAccount() {
        if (Network.isOnline(context)) {
            val savingsPayload = SavingsPayload()
            savingsPayload.externalId = binding.etClientExternalId.editableText.toString()
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
            savingsPayload.nominalAnnualInterestRate = binding.etNominalAnnual.editableText
                    .toString()
            savingsPayload.allowOverdraft = binding.cbOverdraftAllowed.isChecked
            savingsPayload.nominalAnnualInterestRateOverdraft = binding.etNominalAnnualOverdraft.editableText.toString()
            savingsPayload.overdraftLimit = binding.etMaxOverdraftAmount.editableText
                    .toString()
            savingsPayload.minOverdraftForInterestCalculation = binding.etMinOverdraftRequired.editableText.toString()
            savingsPayload.enforceMinRequiredBalance = binding.cbEnforceRequiredBalance.isChecked
            savingsPayload.minRequiredOpeningBalance = binding.etMinRequiredBalance.editableText
                    .toString()
            mSavingsAccountPresenter!!.createSavingsAccount(savingsPayload)
        } else {
            Toaster.show(binding.root, R.string.error_network_not_available)
        }
    }

    override fun onDatePicked(date: String) {
        binding.tvSubmittedonDate.text = date
        submission_date = date
        setSubmissionDate()
    }

    fun inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvSubmittedonDate.text = MFDatePicker.getDatePickedAsString()
        setSubmissionDate()
    }

    fun onClickTextViewSubmissionDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    fun setSubmissionDate() {
        submission_date = binding.tvSubmittedonDate.text.toString()
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
        binding.spInterestCalc.text = savingProductsTemplate.interestCalculationType.value
        binding.spInterestComp.text = savingProductsTemplate.interestCompoundingPeriodType.value
        binding.spInterestPPeriod.text = savingProductsTemplate.interestPostingPeriodType.value
        binding.spDaysInYear.text = savingProductsTemplate.interestCalculationDaysInYearType.value
    }

    override fun showSavingsAccountCreatedSuccessfully(savings: Savings?) {
        Toast.makeText(activity,
                resources.getString(R.string.savings_account_submitted_for_approval),
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showFetchingError(errorMessage: Int) {
        Toaster.show(binding.root, resources.getString(errorMessage))
    }

    override fun showFetchingError(errorMessage: String?) {
        Toaster.show(binding.root, errorMessage)
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
                    ?.fieldOfficerOptions!![position].id
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