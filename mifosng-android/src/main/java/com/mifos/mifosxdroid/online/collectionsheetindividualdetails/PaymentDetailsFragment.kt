package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.objects.accounts.loan.PaymentTypeOptions
import com.mifos.objects.collectionsheet.LoanAndClientName
import com.mifos.utils.Constants
import com.mifos.utils.ImageLoaderUtils
import java.util.*

/**
 * Created by aksh on 21/6/18.
 */
class PaymentDetailsFragment : MifosBaseFragment(), View.OnClickListener, OnItemSelectedListener {
    @JvmField
    @BindView(R.id.tv_name)
    var tvName: TextView? = null

    @JvmField
    @BindView(R.id.tv_product)
    var tvProduct: TextView? = null

    @JvmField
    @BindView(R.id.et_total_due)
    var etDue: TextView? = null

    @JvmField
    @BindView(R.id.tv_total_charges)
    var tvCharges: TextView? = null

    @JvmField
    @BindView(R.id.no_payment)
    var tvNoPayment: TextView? = null

    @JvmField
    @BindView(R.id.table_additional_details)
    var tableAdditional: TableLayout? = null

    @JvmField
    @BindView(R.id.sp_payment_type_options)
    var spPaymentOption: Spinner? = null

    @JvmField
    @BindView(R.id.et_account_number)
    var etAccountNumber: EditText? = null

    @JvmField
    @BindView(R.id.et_cheque_number)
    var etChequeNumber: EditText? = null

    @JvmField
    @BindView(R.id.et_routing_code)
    var etRoutingCode: EditText? = null

    @JvmField
    @BindView(R.id.et_receipt_number)
    var etReceiptNumber: EditText? = null

    @JvmField
    @BindView(R.id.et_bank_number)
    var etBankNumber: EditText? = null

    @JvmField
    @BindView(R.id.btn_cancel_additional)
    var btnCancelAdditional: Button? = null

    @JvmField
    @BindView(R.id.btn_save_additional)
    var btnSaveAdditional: Button? = null

    @JvmField
    @BindView(R.id.btn_add_payment)
    var btnAddPayment: Button? = null

    @JvmField
    @BindView(R.id.iv_user_picture)
    var ivUserPicture: ImageView? = null
    var paymentTypeList: List<String>? = null
    var paymentTypeOptionsList: List<PaymentTypeOptions>? = null
    var payload: IndividualCollectionSheetPayload? = null
    var mCallback: OnPayloadSelectedListener? = null
    private var bulkRepaymentTransaction: BulkRepaymentTransactions? = null
    private var position = 0
    private var clientId = 0
    private lateinit var rootView: View
    private var loanAndClientNameItem: LoanAndClientName? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mCallback = try {
            activity as OnPayloadSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement OnPayloadSelectedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        position = requireArguments().getInt(Constants.ADAPTER_POSITION)
        loanAndClientNameItem = requireArguments().getParcelable(Constants.LOAN_AND_CLIENT)
        paymentTypeList = requireArguments().getStringArrayList(Constants.PAYMENT_LIST)
        payload = requireArguments().getParcelable(Constants.PAYLOAD)
        paymentTypeOptionsList = requireArguments().getParcelableArrayList(Constants.PAYMENT_OPTIONS)
        clientId = requireArguments().getInt(Constants.CLIENT_ID)
        bulkRepaymentTransaction = BulkRepaymentTransactions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.add_payment_detail, container, false)
        ButterKnife.bind(this, rootView)
        return rootView
    }

    fun showUI() {
        val loanCollectionSheetItem = loanAndClientNameItem!!.loan
        tvName!!.text = loanAndClientNameItem!!.clientName
        tvProduct!!.text = concatProductWithAccount(loanCollectionSheetItem
                .productShortName, loanCollectionSheetItem.accountId)
        if (loanCollectionSheetItem.chargesDue != null) {
            tvCharges!!.text = String.format(Locale.getDefault(), "%f",
                    loanCollectionSheetItem.chargesDue)
        }
        if (loanCollectionSheetItem.totalDue != null) {
            etDue!!.text = String.format(Locale.getDefault(), "%f",
                    loanCollectionSheetItem.totalDue)
        }
        ImageLoaderUtils.loadImage(context, clientId,
                ivUserPicture)
        val defaultBulkRepaymentTransaction = BulkRepaymentTransactions()
        defaultBulkRepaymentTransaction.setLoanId(loanCollectionSheetItem.loanId)
        defaultBulkRepaymentTransaction.setTransactionAmount(
                if (loanCollectionSheetItem.chargesDue != null) loanCollectionSheetItem.chargesDue +
                        loanCollectionSheetItem.totalDue else loanCollectionSheetItem.totalDue)
        onShowSheetMandatoryItem(defaultBulkRepaymentTransaction,
                position)
        btnAddPayment!!.setOnClickListener(this)
    }

    private fun concatProductWithAccount(productCode: String, accountNo: String): String {
        return "$productCode (#$accountNo)"
    }

    private fun showAdditional() {
        tableAdditional!!.visibility = View.VISIBLE
        val adapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, paymentTypeList ?: emptyList())
        spPaymentOption!!.adapter = adapter
        spPaymentOption!!.setSelection(paymentTypeList!!.size - 1)
        spPaymentOption!!.onItemSelectedListener = this
        btnSaveAdditional!!.setOnClickListener(this)
        btnCancelAdditional!!.setOnClickListener(this)
    }

    private fun cancelAdditional() {
        bulkRepaymentTransaction!!.setLoanId(loanAndClientNameItem
                ?.getLoan()!!.loanId)
        val charge1: Double = if (!tvCharges!!.text
                        .toString().isEmpty()) tvCharges!!.text.toString().toDouble() else 0.0
        val charge2: Double = if (!etDue!!.text.toString()
                        .isEmpty()) etDue!!.text.toString().toDouble() else 0.0
        bulkRepaymentTransaction!!.setTransactionAmount(charge1+charge2)
        tableAdditional!!.visibility = View.GONE
        bulkRepaymentTransaction!!.paymentTypeId = null
        bulkRepaymentTransaction!!.accountNumber = null
        bulkRepaymentTransaction!!.checkNumber = null
        bulkRepaymentTransaction!!.routingCode = null
        bulkRepaymentTransaction!!.receiptNumber = null
        bulkRepaymentTransaction!!.bankNumber = null
        onSaveAdditionalItem(bulkRepaymentTransaction, position)
    }

    private fun saveAdditional() {
        var isAnyDetailNull = false
        bulkRepaymentTransaction!!.setLoanId(loanAndClientNameItem
                ?.getLoan()!!.loanId)
        val charge1: Double =   if (!tvCharges?.getText().toString().isEmpty())
            tvCharges!!.text.toString().toDouble() else 0.0
        val charge2: Double = if (!etDue!!.text.toString().isEmpty()) {
            etDue!!.text.toString().toDouble()
        }
        else 0.0

        bulkRepaymentTransaction!!.setTransactionAmount((charge1+charge2))

        if (!etAccountNumber!!.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.accountNumber = etAccountNumber!!.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!etChequeNumber!!.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.checkNumber = etChequeNumber!!.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!etRoutingCode!!.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.routingCode = etRoutingCode!!.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!etReceiptNumber!!.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.receiptNumber = etReceiptNumber!!.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!etBankNumber!!.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.bankNumber = etBankNumber!!.text.toString()
        } else {
            isAnyDetailNull = true;
        }

        if(!isAnyDetailNull) {
            tvNoPayment!!.visibility = View.GONE
        }
        onSaveAdditionalItem(bulkRepaymentTransaction, position)
        tableAdditional!!.visibility = View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showUI()
    }

    fun onShowSheetMandatoryItem(transaction: BulkRepaymentTransactions?, position: Int) {
        payload!!.getBulkRepaymentTransactions()[position] = transaction
    }

    fun onSaveAdditionalItem(transaction: BulkRepaymentTransactions?, position: Int) {
        payload!!.getBulkRepaymentTransactions()[position] = transaction
        mCallback!!.onPayloadSelected(payload)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_add_payment -> if (tableAdditional!!.visibility == View.VISIBLE) {
                tableAdditional!!.visibility = View.GONE
            } else if (tableAdditional!!.visibility == View.GONE) {
                showAdditional()
            }
            R.id.btn_cancel_additional -> cancelAdditional()
            R.id.btn_save_additional -> saveAdditional()
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
        if (i != paymentTypeList!!.size - 1) {
            bulkRepaymentTransaction!!.paymentTypeId = paymentTypeOptionsList!![i].id
        } else {
            bulkRepaymentTransaction!!.paymentTypeId = null
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    interface OnPayloadSelectedListener {
        fun onPayloadSelected(payload: IndividualCollectionSheetPayload?)
    }

    fun newInstance(position: Int,
                    payload: IndividualCollectionSheetPayload?,
                    paymentTypeList: ArrayList<String?>?,
                    loanAndClientNameItem: LoanAndClientName?,
                    paymentTypeOptions: ArrayList<PaymentTypeOptions?>?
                    , clientId: Int): PaymentDetailsFragment {
        val args = Bundle()
        args.putInt(Constants.ADAPTER_POSITION, position)
        args.putParcelable(Constants.PAYLOAD, payload)
        args.putStringArrayList(Constants.PAYMENT_LIST, paymentTypeList)
        args.putParcelable(Constants.LOAN_AND_CLIENT, loanAndClientNameItem)
        args.putParcelableArrayList(Constants.PAYMENT_OPTIONS, paymentTypeOptions)
        args.putInt(Constants.CLIENT_ID, clientId)
        val fragment = PaymentDetailsFragment()
        fragment.arguments = args
        return fragment
    }

    companion object {
    }
}