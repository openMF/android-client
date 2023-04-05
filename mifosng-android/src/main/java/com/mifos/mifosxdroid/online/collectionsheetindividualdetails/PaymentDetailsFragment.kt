package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.AddPaymentDetailBinding
import com.mifos.objects.accounts.loan.PaymentTypeOptions
import com.mifos.objects.collectionsheet.LoanAndClientName
import com.mifos.utils.Constants
import com.mifos.utils.ImageLoaderUtils
import java.util.*

/**
 * Created by aksh on 21/6/18.
 */
class PaymentDetailsFragment : MifosBaseFragment(), View.OnClickListener, OnItemSelectedListener {

    private lateinit var binding: AddPaymentDetailBinding

    var paymentTypeList: List<String>? = null
    var paymentTypeOptionsList: List<PaymentTypeOptions>? = null
    var payload: IndividualCollectionSheetPayload? = null
    var mCallback: OnPayloadSelectedListener? = null
    private var bulkRepaymentTransaction: BulkRepaymentTransactions? = null
    private var position = 0
    private var clientId = 0
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
        binding = AddPaymentDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    fun showUI() {
        val loanCollectionSheetItem = loanAndClientNameItem!!.loan
        binding.tvName.text = loanAndClientNameItem!!.clientName
        binding.tvProduct.text = concatProductWithAccount(loanCollectionSheetItem
                .productShortName, loanCollectionSheetItem.accountId)
        if (loanCollectionSheetItem.chargesDue != null) {
            binding.tvTotalCharges.text = String.format(Locale.getDefault(), "%f",
                    loanCollectionSheetItem.chargesDue)
        }
        if (loanCollectionSheetItem.totalDue != null) {
            val totalDue = String.format(Locale.getDefault(), "%f",
                loanCollectionSheetItem.totalDue)

            binding.etTotalDue.text = totalDue.toEditable()

        }
        ImageLoaderUtils.loadImage(context, clientId,
                binding.ivUserPicture)
        val defaultBulkRepaymentTransaction = BulkRepaymentTransactions()
        defaultBulkRepaymentTransaction.setLoanId(loanCollectionSheetItem.loanId)
        defaultBulkRepaymentTransaction.setTransactionAmount(
                if (loanCollectionSheetItem.chargesDue != null) loanCollectionSheetItem.chargesDue +
                        loanCollectionSheetItem.totalDue else loanCollectionSheetItem.totalDue)
        onShowSheetMandatoryItem(defaultBulkRepaymentTransaction,
                position)
        binding.btnAddPayment.setOnClickListener(this)
    }

    private fun concatProductWithAccount(productCode: String, accountNo: String): String {
        return "$productCode (#$accountNo)"
    }

    private fun showAdditional() {
        binding.tableAdditionalDetails.visibility = View.VISIBLE
        val adapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, paymentTypeList ?: emptyList())
        binding.spPaymentTypeOptions.adapter = adapter
        binding.spPaymentTypeOptions.setSelection(paymentTypeList!!.size - 1)
        binding.spPaymentTypeOptions.onItemSelectedListener = this
        binding.btnSaveAdditional.setOnClickListener(this)
        binding.btnCancelAdditional.setOnClickListener(this)
    }

    private fun cancelAdditional() {
        bulkRepaymentTransaction!!.setLoanId(loanAndClientNameItem
                ?.loan!!.loanId)
        val charge1: Double = if (!binding.tvTotalCharges.text
                        .toString().isEmpty()) binding.tvTotalCharges.text.toString().toDouble() else 0.0
        val charge2: Double = if (!binding.etTotalDue.text.toString()
                        .isEmpty()) binding.etTotalDue.text.toString().toDouble() else 0.0
        bulkRepaymentTransaction!!.setTransactionAmount(charge1+charge2)
        binding.tableAdditionalDetails.visibility = View.GONE
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
                ?.loan!!.loanId)
        val charge1: Double =   if (binding.tvTotalCharges.text.toString().isNotEmpty())
            binding.tvTotalCharges.text.toString().toDouble() else 0.0
        val charge2: Double = if (binding.etTotalDue.text.toString().isNotEmpty()) {
            binding.etTotalDue.text.toString().toDouble()
        }
        else 0.0

        bulkRepaymentTransaction!!.setTransactionAmount((charge1+charge2))

        if (!binding.etAccountNumber.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.accountNumber = binding.etAccountNumber.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!binding.etChequeNumber.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.checkNumber = binding.etChequeNumber.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!binding.etRoutingCode.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.routingCode = binding.etRoutingCode.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!binding.etReceiptNumber.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.receiptNumber = binding.etReceiptNumber.text.toString()
        } else {
            isAnyDetailNull = true;
        }
        if (!binding.etBankNumber.text.toString().isEmpty()) {
            bulkRepaymentTransaction!!.bankNumber = binding.etBankNumber.text.toString()
        } else {
            isAnyDetailNull = true;
        }

        if(!isAnyDetailNull) {
            binding.noPayment.visibility = View.GONE
        }
        onSaveAdditionalItem(bulkRepaymentTransaction, position)
        binding.tableAdditionalDetails.visibility = View.GONE
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
            R.id.btn_add_payment -> if (binding.tableAdditionalDetails.visibility == View.VISIBLE) {
                binding.tableAdditionalDetails.visibility = View.GONE
            } else if (binding.tableAdditionalDetails.visibility == View.GONE) {
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

    /** Extension function to convert string to editable
     */
    fun String.toEditable() : Editable {

        return Editable.Factory.getInstance().newEditable(this)
    }
}