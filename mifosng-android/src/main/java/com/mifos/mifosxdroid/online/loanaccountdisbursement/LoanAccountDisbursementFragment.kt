/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountdisbursement

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
import com.mifos.api.GenericResponse
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.loan.LoanDisbursement
import com.mifos.objects.templates.loans.LoanTransactionTemplate
import com.mifos.utils.*
import java.util.*
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 */
class LoanAccountDisbursementFragment : MifosBaseFragment(), OnDatePickListener, LoanAccountDisbursementMvpView, OnItemSelectedListener {
    val LOG_TAG = javaClass.simpleName

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_disbursement_dates)
    var tvLoanDisbursementDates: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.btn_disburse_loan)
    var btnDisburseLoan: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.sp_loan_payment_type)
    var spPaymentType: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_disbursed_amount)
    var etDisbursedAmount: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_disbursement_note)
    var etDisbursementNote: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.ll_disburse)
    var llDisburse: LinearLayout? = null

    @kotlin.jvm.JvmField
    @Inject
    var loanAccountDisbursementPresenter: LoanAccountDisbursementPresenter? = null
    lateinit var rootView: View
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
        rootView = inflater.inflate(R.layout.dialog_fragment_disburse_loan, null)
        ButterKnife.bind(this, rootView)
        loanAccountDisbursementPresenter!!.attachView(this)
        showUserInterface()
        loanAccountDisbursementPresenter!!.loadLoanTemplate(loanAccountNumber)
        return rootView
    }

    @OnClick(R.id.btn_disburse_loan)
    fun onSubmitDisburse() {
        if (Network.isOnline(context)) {
            // Notify the user if Amount field is blank
            if (etDisbursedAmount!!.editableText.toString().isEmpty()) {
                RequiredFieldException(getString(R.string.amount), getString(R.string.message_field_required)).notifyUserWithToast(activity)
                return
            }
            val loanDisbursement = LoanDisbursement()
            loanDisbursement.note = etDisbursementNote!!.editableText.toString()
            loanDisbursement.actualDisbursementDate = disbursementDates
            loanDisbursement.transactionAmount = java.lang.Double.valueOf(etDisbursedAmount!!.editableText.toString())
            loanDisbursement.paymentId = paymentTypeId
            loanAccountDisbursementPresenter!!.disburseLoan(loanAccountNumber, loanDisbursement)
        } else {
            Toaster.show(rootView, R.string.error_network_not_available, Toaster.LONG)
        }
    }

    override fun showUserInterface() {
        setToolbarTitle(getString(R.string.disburse_loan))
        mfDatePicker = MFDatePicker.newInsance(this)
        tvLoanDisbursementDates!!.text = MFDatePicker.getDatePickedAsString()
        showDisbursementDate(tvLoanDisbursementDates!!.text.toString())
        paymentTypeOptionAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, paymentTypeOptions ?: emptyList())
        paymentTypeOptionAdapter!!
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPaymentType!!.adapter = paymentTypeOptionAdapter
        spPaymentType!!.onItemSelectedListener = this
    }

    override fun showDisbursementDate(date: String?) {
        disbursementDates = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
                .replace("-", " ")
    }

    @OnClick(R.id.tv_loan_disbursement_dates)
    fun onClickTvDisburseDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun onDatePicked(date: String) {
        tvLoanDisbursementDates!!.text = date
        showDisbursementDate(date)
    }

    override fun showLoanTransactionTemplate(loanTransactionTemplate: LoanTransactionTemplate) {
        this.loanTransactionTemplate = loanTransactionTemplate
        etDisbursedAmount!!.setText(loanTransactionTemplate.amount.toString())
        paymentTypeOptions.addAll()
        paymentTypeOptionAdapter!!.notifyDataSetChanged()
    }

    override fun showDisburseLoanSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, R.string.loan_disburse_successfully,
                Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showError(message: String?) {
        Toaster.show(rootView, message, Toaster.INDEFINITE)
    }

    override fun showError(errorMessage: Int) {
        Toaster.show(rootView, errorMessage)
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressBar()
            llDisburse!!.visibility = View.GONE
        } else {
            hideMifosProgressBar()
            llDisburse!!.visibility = View.VISIBLE
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
