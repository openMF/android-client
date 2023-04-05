/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountdisbursement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mifos.api.GenericResponse
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.DialogFragmentDisburseLoanBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.loan.LoanDisbursement
import com.mifos.objects.templates.loans.LoanTransactionTemplate
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Network
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */
class LoanAccountDisbursementFragment : MifosBaseFragment(), OnDatePickListener,
    LoanAccountDisbursementMvpView, OnItemSelectedListener {
    val LOG_TAG = javaClass.simpleName
    private lateinit var binding: DialogFragmentDisburseLoanBinding

    @JvmField
    @Inject
    var loanAccountDisbursementPresenter: LoanAccountDisbursementPresenter? = null
    private var paymentTypeId = 0
    private var disbursementDates: String? = null
    private var loanAccountNumber = 0
    private var mfDatePicker: DialogFragment? = null
    private var paymentTypeOptions: List<String>? = null
    private var paymentTypeOptionAdapter: ArrayAdapter<String>? = null
    private var loanTransactionTemplate: LoanTransactionTemplate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
        }
        paymentTypeOptions = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DialogFragmentDisburseLoanBinding.inflate(inflater,container,false)
        loanAccountDisbursementPresenter!!.attachView(this)
        showUserInterface()
        loanAccountDisbursementPresenter!!.loadLoanTemplate(loanAccountNumber)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDisburseLoan.setOnClickListener { onSubmitDisburse() }
        binding.tvLoanDisbursementDates.setOnClickListener { onClickTvDisburseDate() }
    }

    fun onSubmitDisburse() {
        if (Network.isOnline(context)) {
            // Notify the user if Amount field is blank
            if (binding.etDisbursedAmount.editableText.toString().isEmpty()) {
                RequiredFieldException(getString(R.string.amount), getString(R.string.message_field_required)).notifyUserWithToast(activity)
                return
            }
            val loanDisbursement = LoanDisbursement()
            loanDisbursement.note = binding.etDisbursementNote.editableText.toString()
            loanDisbursement.actualDisbursementDate = disbursementDates
            loanDisbursement.transactionAmount = java.lang.Double.valueOf(binding.etDisbursedAmount.editableText.toString())
            loanDisbursement.paymentId = paymentTypeId
            loanAccountDisbursementPresenter!!.disburseLoan(loanAccountNumber, loanDisbursement)
        } else {
            Toaster.show(binding.root, R.string.error_network_not_available, Toaster.LONG)
        }
    }

    override fun showUserInterface() {
        setToolbarTitle(getString(R.string.disburse_loan))
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvLoanDisbursementDates.text = MFDatePicker.getDatePickedAsString()
        showDisbursementDate(binding.tvLoanDisbursementDates.text.toString())
        paymentTypeOptionAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, paymentTypeOptions ?: emptyList())
        paymentTypeOptionAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanPaymentType.adapter = paymentTypeOptionAdapter
        binding.spLoanPaymentType.onItemSelectedListener = this
    }

    override fun showDisbursementDate(date: String?) {
        disbursementDates = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
                .replace("-", " ")
    }

    fun onClickTvDisburseDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun onDatePicked(date: String) {
        binding.tvLoanDisbursementDates.text = date
        showDisbursementDate(date)
    }

    override fun showLoanTransactionTemplate(loanTransactionTemplate: LoanTransactionTemplate) {
        this.loanTransactionTemplate = loanTransactionTemplate
        binding.etDisbursedAmount.setText(loanTransactionTemplate.amount.toString())
        paymentTypeOptions.addAll()
        paymentTypeOptionAdapter!!.notifyDataSetChanged()
    }

    override fun showDisburseLoanSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, R.string.loan_disburse_successfully,
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showError(message: String?) {
        Toaster.show(binding.root, message, Toaster.INDEFINITE)
    }

    override fun showError(errorMessage: Int) {
        Toaster.show(binding.root, errorMessage)
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressBar()
            binding.llDisburse.visibility = View.GONE
        } else {
            hideMifosProgressBar()
            binding.llDisburse.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loanAccountDisbursementPresenter!!.detachView()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (parent.id == R.id.sp_loan_payment_type) {
            paymentTypeId = loanTransactionTemplate!!.paymentTypeOptions[position].id
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        fun newInstance(loanAccountNumber: Int): LoanAccountDisbursementFragment {
            val loanAccountDisbursement = LoanAccountDisbursementFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            loanAccountDisbursement.arguments = args
            return loanAccountDisbursement
        }
    }
}

private fun <E> List<E>?.addAll() {

}
