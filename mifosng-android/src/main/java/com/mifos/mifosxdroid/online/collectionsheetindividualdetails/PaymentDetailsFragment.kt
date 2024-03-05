package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.AddPaymentDetailBinding
import com.mifos.utils.ImageLoaderUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/**
 * Created by aksh on 21/6/18.
 */
@AndroidEntryPoint
class PaymentDetailsFragment : MifosBaseFragment(), View.OnClickListener, OnItemSelectedListener {

    private lateinit var binding: AddPaymentDetailBinding
    private val arg: PaymentDetailsFragmentArgs by navArgs()

    private var paymentTypeList: List<String>? = null
    private var paymentTypeOptionsList: List<PaymentTypeOptions>? = null
    var payload: IndividualCollectionSheetPayload? = null
    private var mCallback: OnPayloadSelectedListener? = null
    private lateinit var bulkRepaymentTransaction: BulkRepaymentTransactions
    private var position = 0
    private var clientId = 0
    private var loanAndClientNameItem: LoanAndClientName? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mCallback = try {
            activity as OnPayloadSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnPayloadSelectedListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arg.position
        loanAndClientNameItem = arg.loanAndClientName
        paymentTypeList = arg.paymentTypeList.toList()
        payload = arg.payload
        paymentTypeOptionsList = arg.paymentTypeOptions.toList()
        clientId = arg.clientId
        bulkRepaymentTransaction = BulkRepaymentTransactions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddPaymentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun showUI() {
        val loanCollectionSheetItem = loanAndClientNameItem?.loan
        binding.tvName.text = loanAndClientNameItem?.clientName
        binding.tvProduct.text = concatProductWithAccount(
            loanCollectionSheetItem
                ?.productShortName, loanCollectionSheetItem?.accountId
        )
        binding.tvTotalCharges.text = String.format(
            Locale.getDefault(), "%f",
            loanCollectionSheetItem?.chargesDue
        )
        binding.etTotalDue.setText(
            loanCollectionSheetItem?.totalDue?.let {
                String.format(Locale.getDefault(), "%.2f", it)
            }
        )
        ImageLoaderUtils.loadImage(
            requireContext(), clientId,
            binding.ivUserPicture
        )
        val defaultBulkRepaymentTransaction = BulkRepaymentTransactions()
        if (loanCollectionSheetItem != null) {
            defaultBulkRepaymentTransaction.loanId = loanCollectionSheetItem.loanId
        }
        if (loanCollectionSheetItem != null) {
            defaultBulkRepaymentTransaction.transactionAmount =
                loanCollectionSheetItem.chargesDue +
                        loanCollectionSheetItem.totalDue
        }
        onShowSheetMandatoryItem(
            defaultBulkRepaymentTransaction,
            position
        )
        binding.btnAddPayment.setOnClickListener(this)
    }

    private fun concatProductWithAccount(productCode: String?, accountNo: String?): String {
        return "$productCode (#$accountNo)"
    }

    private fun showAdditional() {
        binding.tableAdditionalDetails.visibility = View.VISIBLE
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, paymentTypeList ?: emptyList()
        )
        binding.spPaymentTypeOptions.adapter = adapter
        binding.spPaymentTypeOptions.setSelection(paymentTypeList!!.size - 1)
        binding.spPaymentTypeOptions.onItemSelectedListener = this
        binding.btnSaveAdditional.setOnClickListener(this)
        binding.btnCancelAdditional.setOnClickListener(this)
    }

    private fun cancelAdditional() {
        bulkRepaymentTransaction.loanId = loanAndClientNameItem
            ?.loan!!.loanId
        val charge1: Double = if (binding.tvTotalCharges.text
                .toString().isNotEmpty()
        ) binding.tvTotalCharges.text.toString().toDouble() else 0.0
        val charge2: Double = if (binding.etTotalDue.text.toString().isNotEmpty()
        ) binding.etTotalDue.text.toString().toDouble() else 0.0
        bulkRepaymentTransaction.transactionAmount = charge1 + charge2
        binding.tableAdditionalDetails.visibility = View.GONE
        bulkRepaymentTransaction.paymentTypeId = null
        bulkRepaymentTransaction.accountNumber = null
        bulkRepaymentTransaction.checkNumber = null
        bulkRepaymentTransaction.routingCode = null
        bulkRepaymentTransaction.receiptNumber = null
        bulkRepaymentTransaction.bankNumber = null
        onSaveAdditionalItem(bulkRepaymentTransaction, position)
    }

    private fun saveAdditional() {
        var isAnyDetailNull = false
        bulkRepaymentTransaction.loanId = loanAndClientNameItem
            ?.loan!!.loanId
        val charge1: Double = if (binding.tvTotalCharges.text.toString().isNotEmpty())
            binding.tvTotalCharges.text.toString().toDouble() else 0.0
        val charge2: Double = if (binding.etTotalDue.text.toString().isNotEmpty()) {
            binding.etTotalDue.text.toString().toDouble()
        } else 0.0

        bulkRepaymentTransaction.transactionAmount = (charge1 + charge2)

        if (binding.etAccountNumber.text.toString().isNotEmpty()) {
            bulkRepaymentTransaction.accountNumber = binding.etAccountNumber.text.toString()
        } else {
            isAnyDetailNull = true
        }
        if (binding.etChequeNumber.text.toString().isNotEmpty()) {
            bulkRepaymentTransaction.checkNumber = binding.etChequeNumber.text.toString()
        } else {
            isAnyDetailNull = true
        }
        if (binding.etRoutingCode.text.toString().isNotEmpty()) {
            bulkRepaymentTransaction.routingCode = binding.etRoutingCode.text.toString()
        } else {
            isAnyDetailNull = true
        }
        if (binding.etReceiptNumber.text.toString().isNotEmpty()) {
            bulkRepaymentTransaction.receiptNumber = binding.etReceiptNumber.text.toString()
        } else {
            isAnyDetailNull = true
        }
        if (binding.etBankNumber.text.toString().isNotEmpty()) {
            bulkRepaymentTransaction.bankNumber = binding.etBankNumber.text.toString()
        } else {
            isAnyDetailNull = true
        }

        if (!isAnyDetailNull) {
            binding.noPayment.visibility = View.GONE
        }
        onSaveAdditionalItem(bulkRepaymentTransaction, position)
        binding.tableAdditionalDetails.visibility = View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showUI()
    }

    private fun onShowSheetMandatoryItem(transaction: BulkRepaymentTransactions, position: Int) {
        payload!!.bulkRepaymentTransactions[position] = transaction
    }

    private fun onSaveAdditionalItem(transaction: BulkRepaymentTransactions, position: Int) {
        payload!!.bulkRepaymentTransactions[position] = transaction
        mCallback?.onPayloadSelected(payload)
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
            bulkRepaymentTransaction.paymentTypeId = paymentTypeOptionsList!![i].id
        } else {
            bulkRepaymentTransaction.paymentTypeId = null
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    interface OnPayloadSelectedListener {
        fun onPayloadSelected(payload: IndividualCollectionSheetPayload?)
    }
}