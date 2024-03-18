/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanrepayment

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.jakewharton.fliptables.FlipTable
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentLoanRepaymentBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanRepaymentFragment : MifosBaseFragment(), OnDatePickListener,
    DialogInterface.OnClickListener {

    private lateinit var binding: FragmentLoanRepaymentBinding
    val LOG_TAG = javaClass.simpleName
    private val arg: LoanRepaymentFragmentArgs by navArgs()

    private lateinit var viewModel: LoanRepaymentViewModel

    // Arguments Passed From the Loan Account Summary Fragment
    private var clientName: String? = null
    private var loanId: String? = null
    private var loanAccountNumber: String? = null
    private var loanProductName: String? = null
    private var amountInArrears: Double? = null
    private var paymentTypeOptionId: Int? = null
    private var mfDatePicker: DialogFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientName = arg.loanWithAssociations.clientName
        loanAccountNumber = arg.loanWithAssociations.accountNo
        loanId = arg.loanWithAssociations.id.toString()
        loanProductName = arg.loanWithAssociations.loanProductName
        amountInArrears = arg.loanWithAssociations.summary.totalOverdue
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoanRepaymentBinding.inflate(inflater, container, false)
        setToolbarTitle("Loan Repayment")
        viewModel = ViewModelProvider(this)[LoanRepaymentViewModel::class.java]

        //This Method Checking LoanRepayment made before in Offline mode or not.
        //If yes then User have to sync this first then he can able to make transaction.
        //If not then User able to make LoanRepayment in Online or Offline.
        checkLoanRepaymentStatusInDatabase()

        viewModel.loanRepaymentUiState.observe(viewLifecycleOwner) {
            when (it) {
                is LoanRepaymentUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is LoanRepaymentUiState.ShowLoanRepayTemplate -> {
                    showProgressbar(false)
                    showLoanRepayTemplate(it.loanRepaymentTemplate)
                }

                is LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase -> {
                    showProgressbar(false)
                    showLoanRepaymentDoesNotExistInDatabase()
                }

                is LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase -> {
                    showProgressbar(false)
                    showLoanRepaymentExistInDatabase()
                }

                is LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully -> {
                    showProgressbar(false)
                    showPaymentSubmittedSuccessfully(it.loanRepaymentResponse)
                }

                is LoanRepaymentUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btPaynow.setOnClickListener {
            onPayNowButtonClicked()
        }

        binding.btCancelPayment.setOnClickListener {
            onCancelPaymentButtonClicked()
        }
    }

    private fun checkLoanRepaymentStatusInDatabase() {
        // Checking LoanRepayment Already made in Offline mode or Not.
        viewModel.checkDatabaseLoanRepaymentByLoanId(loanId!!.toInt())
    }

    private fun showLoanRepaymentExistInDatabase() {
        //Visibility of ParentLayout GONE, If Repayment Already made in Offline Mode
        binding.rlLoanRepayment.visibility = View.GONE
        MaterialDialog.Builder().init(activity)
            .setTitle(R.string.sync_previous_transaction)
            .setMessage(R.string.dialog_message_sync_transaction)
            .setPositiveButton(R.string.dialog_action_ok, this)
            .setCancelable(false)
            .createMaterialDialog()
            .show()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun showLoanRepaymentDoesNotExistInDatabase() {
        // This Method Inflating UI and Initializing the Loading LoadRepayment
        // Template for transaction
        inflateUI()

        // Loading PaymentOptions.
        viewModel.loanLoanRepaymentTemplate(loanId!!.toInt())
    }

    /**
     * This Method Setting UI and Initializing the Object, TextView or EditText.
     */
    private fun inflateUI() {
        binding.tvClientName.text = clientName
        binding.tvLoanProductShortName.text = loanProductName
        binding.tvLoanAccountNumber.text = loanId
        binding.tvInArrears.text = amountInArrears.toString()

        //Setup Form with Default Values
        binding.etAmount.setText("0.0")
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                try {
                    binding.tvTotal.text = calculateTotal().toString()
                } catch (nfe: NumberFormatException) {
                    binding.etAmount.setText("0")
                } finally {
                    binding.tvTotal.text = calculateTotal().toString()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.etAdditionalPayment.setText("0.0")
        binding.etAdditionalPayment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                try {
                    binding.tvTotal.text = calculateTotal().toString()
                } catch (nfe: NumberFormatException) {
                    binding.etAdditionalPayment.setText("0")
                } finally {
                    binding.tvTotal.text = calculateTotal().toString()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.etFees.setText("0.0")
        binding.etFees.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                try {
                    binding.tvTotal.text = calculateTotal().toString()
                } catch (nfe: NumberFormatException) {
                    binding.etFees.setText("0")
                } finally {
                    binding.tvTotal.text = calculateTotal().toString()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        inflateRepaymentDate()
        binding.tvTotal.text = calculateTotal().toString()
    }

    /**
     * Calculating the Total of the  Amount, Additional Payment and Fee
     *
     * @return Total of the Amount + Additional Payment + Fee Amount
     */
    fun calculateTotal(): Double {
        return binding.etAmount.text.toString()
            .toDouble() + binding.etAdditionalPayment.text.toString()
            .toDouble() + binding.etFees.text.toString().toDouble()
    }

    /**
     * Setting the Repayment Date
     */
    private fun inflateRepaymentDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvRepaymentDate.text = MFDatePicker.datePickedAsString
        /*
            TODO Add Validation to make sure :
            1. Date Is in Correct Format
            2. Date Entered is not greater than Date Today i.e Date is not in future
         */binding.tvRepaymentDate.setOnClickListener {
            (mfDatePicker as MFDatePicker).show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    /**
     * Whenever user click on Date Picker and in Result, setting Date in TextView.
     *
     * @param date Selected Date by Date picker
     */
    override fun onDatePicked(date: String?) {
        binding.tvRepaymentDate.text = date
    }

    /**
     * Submitting the LoanRepayment after setting all arguments and Displaying the Dialog
     * First, So that user make sure. He/She wanna make LoanRepayment
     */

    private fun onPayNowButtonClicked() {
        try {
            val headers = arrayOf("Field", "Value")
            val data = arrayOf(
                arrayOf("Account Number", loanAccountNumber),
                arrayOf<String?>("Repayment Date", binding.tvRepaymentDate.text.toString()),
                arrayOf<String?>("Payment Type", binding.spPaymentType.selectedItem.toString()),
                arrayOf<String?>("Amount", binding.etAmount.text.toString()),
                arrayOf<String?>("Addition Payment", binding.etAdditionalPayment.text.toString()),
                arrayOf<String?>("Fees", binding.etFees.text.toString()),
                arrayOf<String?>("Total", calculateTotal().toString())
            )
            Log.d(LOG_TAG, FlipTable.of(headers, data))
            val formReviewString =
                StringBuilder().append(data[0][0].toString() + " : " + data[0][1])
                    .append("\n")
                    .append(data[1][0].toString() + " : " + data[1][1])
                    .append("\n")
                    .append(data[2][0].toString() + " : " + data[2][1])
                    .append("\n")
                    .append(data[3][0].toString() + " : " + data[3][1])
                    .append("\n")
                    .append(data[4][0].toString() + " : " + data[4][1])
                    .append("\n")
                    .append(data[5][0].toString() + " : " + data[5][1])
                    .append("\n")
                    .append(data[6][0].toString() + " : " + data[6][1]).toString()
            MaterialDialog.Builder().init(activity)
                .setTitle(R.string.review_payment)
                .setMessage(formReviewString)
                .setPositiveButton(
                    R.string.dialog_action_pay_now
                ) { dialog, which -> submitPayment() }
                .setNegativeButton(
                    R.string.dialog_action_back
                ) { dialog, which -> dialog.dismiss() }
                .createMaterialDialog()
                .show()
        } catch (npe: NullPointerException) {
            Toaster.show(
                binding.root, "Please make sure every field has a value, before submitting " +
                        "repayment!"
            )
        }
    }

    /**
     * Cancel button on Home UI
     */

    private fun onCancelPaymentButtonClicked() {
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    /**
     * Submit the Final LoanRepayment
     */
    private fun submitPayment() {
        //TODO Implement a proper builder method here
        val dateString = binding.tvRepaymentDate.text.toString().replace("-", " ")
        val request = LoanRepaymentRequest()
        request.accountNumber = loanAccountNumber
        request.paymentTypeId = paymentTypeOptionId.toString()
        request.locale = "en"
        request.transactionAmount = calculateTotal().toString()
        request.dateFormat = "dd MM yyyy"
        request.transactionDate = dateString
        val builtRequest = Gson().toJson(request)
        Log.i("LOG_TAG", builtRequest)
        viewModel.submitPayment(loanId!!.toInt(), request)
    }

    private fun showLoanRepayTemplate(loanRepaymentTemplate: LoanRepaymentTemplate?) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        if (loanRepaymentTemplate != null) {
            binding.tvAmountDue.text = loanRepaymentTemplate.amount.toString()
            inflateRepaymentDate()
            val listOfPaymentTypes =
                Utils.getPaymentTypeOptions(loanRepaymentTemplate.paymentTypeOptions)
            val paymentTypeAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item, listOfPaymentTypes
            )
            paymentTypeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            binding.spPaymentType.adapter = paymentTypeAdapter
            binding.spPaymentType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    paymentTypeOptionId = loanRepaymentTemplate
                        .paymentTypeOptions?.get(position)?.id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            binding.etAmount.setText(
                (loanRepaymentTemplate.interestPortion?.let {
                    loanRepaymentTemplate
                        .principalPortion?.plus(it)
                }).toString()
            )
            binding.etAdditionalPayment.setText("0.0")
            binding.etFees.setText(
                loanRepaymentTemplate
                    .feeChargesPortion.toString()
            )
        }
    }

    private fun showPaymentSubmittedSuccessfully(loanRepaymentResponse: LoanRepaymentResponse?) {
        if (loanRepaymentResponse != null) {
            Toaster.show(
                binding.root, "Payment Successful, Transaction ID = " +
                        loanRepaymentResponse.resourceId
            )
        }
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    private fun showError(errorMessage: Int) {
        Toaster.show(binding.root, errorMessage)
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            binding.rlLoanRepayment.visibility = View.GONE
            showMifosProgressBar()
        } else {
            binding.rlLoanRepayment.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }
}