/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingaccounttransaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.jakewharton.fliptables.FlipTable
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentSavingsAccountTransactionBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Network
import com.mifos.utils.PrefManager
import com.mifos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavingsAccountTransactionFragment : ProgressableFragment(), OnDatePickListener {


    private lateinit var binding: FragmentSavingsAccountTransactionBinding
    private val arg: SavingsAccountTransactionFragmentArgs by navArgs()
    val LOG_TAG = javaClass.simpleName

    private lateinit var viewModel: SavingsAccountTransactionViewModel

    private var savingsAccountNumber: String? = null
    private var savingsAccountId: Int? = null
    private var savingsAccountType: DepositType? = null
    private var transactionType //Defines if the Transaction is a Deposit to an Account
            : String? = null

    // or a Withdrawal from an Account
    private var clientName: String? = null

    // Values to be fetched from Savings Account Template
    private var paymentTypeOptionId = 0
    private var mfDatePicker: DialogFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savingsAccountId = arg.savingsAccountWithAssociations.accountNo
        savingsAccountId = arg.savingsAccountWithAssociations.id
        transactionType = arg.transactionType
        clientName = arg.savingsAccountWithAssociations.clientName
        savingsAccountType = arg.accountType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavingsAccountTransactionBinding.inflate(inflater, container, false)
        if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
            setToolbarTitle(
                resources.getString(R.string.savingsAccount) + resources
                    .getString(R.string.space) + resources.getString(R.string.deposit)
            )
        } else {
            setToolbarTitle(
                resources.getString(R.string.savingsAccount) + resources
                    .getString(R.string.space) + resources.getString(R.string.withdrawal)
            )
        }
        viewModel = ViewModelProvider(this)[SavingsAccountTransactionViewModel::class.java]

        //This Method Checking SavingAccountTransaction made before in Offline mode or not.
        //If yes then User have to sync that first then he will be able to make transaction.
        //If not then User able to make SavingAccountTransaction in Online or Offline.
        checkSavingAccountTransactionStatusInDatabase()

        viewModel.savingsAccountTransactionUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SavingsAccountTransactionUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SavingsAccountTransactionUiState.ShowProgressbar -> showProgressbar(true)

                is SavingsAccountTransactionUiState.ShowSavingAccountTemplate -> {
                    showProgressbar(false)
                    showSavingAccountTemplate(it.savingsAccountTransactionTemplate)
                }

                is SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase -> {
                    showProgressbar(false)
                    showSavingAccountTransactionDoesNotExistInDatabase()
                }

                is SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone -> {
                    showProgressbar(false)
                    showTransactionSuccessfullyDone(it.savingsAccountTransactionResponse)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btReviewTransaction.setOnClickListener {
            onReviewTransactionButtonClicked()
        }

        binding.btCancelTransaction.setOnClickListener {
            onCancelTransactionButtonClicked()
        }

    }

    private fun checkSavingAccountTransactionStatusInDatabase() {
        // Checking SavingAccountTransaction Already made in Offline mode or Not.
        savingsAccountId?.let {
            viewModel
                .checkInDatabaseSavingAccountTransaction(it)
        }
    }

    private fun showSavingAccountTransactionExistInDatabase() {
        //Visibility of ParentLayout GONE, If SavingAccountTransaction Already made in Offline Mode
        binding.viewFlipper.visibility = View.GONE
        MaterialDialog.Builder().init(activity)
            .setTitle(R.string.sync_previous_transaction)
            .setMessage(R.string.dialog_message_sync_savingaccounttransaction)
            .setPositiveButton(
                R.string.dialog_action_ok
            ) { dialog, which -> requireActivity().supportFragmentManager.popBackStackImmediate() }
            .setCancelable(false)
            .createMaterialDialog()
            .show()
    }

    private fun showSavingAccountTransactionDoesNotExistInDatabase() {
        // This Method Inflating UI and Initializing the Loading SavingAccountTransaction
        // Template for transaction.
        inflateUI()
    }

    private fun inflateUI() {
        binding.tvClientName.text = clientName
        binding.tvSavingsAccountNumber.text = savingsAccountNumber
        //TODO Implement QuickContactBadge here
        inflateRepaymentDate()
        inflateSavingsAccountTemplate()
    }

    private fun inflateSavingsAccountTemplate() {
        savingsAccountId?.let {
            viewModel.loadSavingAccountTemplate(
                savingsAccountType?.endpoint, it, transactionType
            )
        }
    }

    private fun onReviewTransactionButtonClicked() {
        // Notify user if Amount field is blank and Review
        // Transaction button is pressed.
        if (binding.etTransactionAmount.editableText.toString().isEmpty()) {
            RequiredFieldException(
                getString(R.string.amount),
                getString(R.string.message_field_required)
            ).notifyUserWithToast(activity)
            return
        }
        // Notify the user if zero is entered in the Amount
        // field or only "." (Decimal point) is entered.
        try {
            if (binding.etTransactionAmount.editableText.toString().toFloat() == 0f) {
                RequiredFieldException(
                    getString(R.string.amount),
                    getString(R.string.message_cannot_be_zero)
                ).notifyUserWithToast(activity)
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(activity, R.string.error_invalid_amount, Toast.LENGTH_SHORT).show()
            return
        }
        val headers = arrayOf(
            resources.getString(R.string.field),
            resources.getString(R.string.value)
        )
        val data = arrayOf(
            arrayOf(
                resources.getString(R.string.transaction_date),
                binding.tvTransactionDate.text.toString()
            ), arrayOf(
                resources.getString(R.string.payment_type),
                binding.spPaymentType.selectedItem.toString()
            ), arrayOf(
                resources.getString(R.string.amount),
                binding.etTransactionAmount.editableText.toString()
            )
        )
        Log.d(LOG_TAG, FlipTable.of(headers, data))
        val formReviewStringBuilder = StringBuilder()
        for (i in 0..2) {
            for (j in 0..1) {
                formReviewStringBuilder.append(data[i][j])
                if (j == 0) formReviewStringBuilder.append(" : ")
            }
            formReviewStringBuilder.append('\n')
        }
        MaterialDialog.Builder().init(activity)
            .setTitle(resources.getString(R.string.review_transaction_details))
            .setMessage(formReviewStringBuilder.toString())
            .setPositiveButton(
                resources.getString(R.string.process_transaction)
            ) { dialogInterface, i -> processTransaction() }
            .setNegativeButton(
                resources.getString(R.string.dialog_action_cancel)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .createMaterialDialog()
            .show()
    }

    private fun processTransaction() {
        val dateString = binding.tvTransactionDate.text.toString().replace("-", " ")
        val savingsAccountTransactionRequest = SavingsAccountTransactionRequest()
        savingsAccountTransactionRequest.locale = "en"
        savingsAccountTransactionRequest.dateFormat = "dd MM yyyy"
        savingsAccountTransactionRequest.transactionDate = dateString
        savingsAccountTransactionRequest.transactionAmount = binding.etTransactionAmount
            .editableText.toString()
        savingsAccountTransactionRequest.paymentTypeId = paymentTypeOptionId.toString()
        val builtTransactionRequestAsJson = Gson().toJson(savingsAccountTransactionRequest)
        Log.i(resources.getString(R.string.transaction_body), builtTransactionRequestAsJson)
        if (!Network.isOnline(requireActivity())) PrefManager.userStatus = Constants.USER_OFFLINE
        savingsAccountId?.let {
            viewModel.processTransaction(
                savingsAccountType?.endpoint,
                it, transactionType, savingsAccountTransactionRequest
            )
        }
    }

    private fun onCancelTransactionButtonClicked() {
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    private fun inflateRepaymentDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvTransactionDate.text = MFDatePicker.datePickedAsString
        // TODO Add Validation to make sure :
        // 1. Date Is in Correct Format
        // 2. Date Entered is not greater than Date Today i.e Date is not in future
        binding.tvTransactionDate.setOnClickListener {
            (mfDatePicker as MFDatePicker?)?.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    override fun onDatePicked(date: String?) {
        binding.tvTransactionDate.text = date
    }

    private fun showSavingAccountTemplate(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate?) {
        if (savingsAccountTransactionTemplate != null) {
            val listOfPaymentTypes = Utils.getPaymentTypeOptions(
                savingsAccountTransactionTemplate.paymentTypeOptions
            )
            val paymentTypeAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item, listOfPaymentTypes
            )
            paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spPaymentType.adapter = paymentTypeAdapter
            binding.spPaymentType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    paymentTypeOptionId = savingsAccountTransactionTemplate
                        .paymentTypeOptions[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun showTransactionSuccessfullyDone(savingsAccountTransactionResponse: SavingsAccountTransactionResponse?) {
        if (savingsAccountTransactionResponse?.resourceId == null) {
            Toaster.show(binding.root, resources.getString(R.string.transaction_saved_in_db))
        } else {
            if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
                Toaster.show(
                    binding.root, "Deposit Successful, Transaction ID = " +
                            savingsAccountTransactionResponse.resourceId
                )
            } else if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL) {
                Toaster.show(
                    binding.root, "Withdrawal Successful, Transaction ID = "
                            + savingsAccountTransactionResponse.resourceId
                )
            }
        }
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    private fun showError(errorMessage: String) {
        Toaster.show(binding.root, errorMessage)
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }
}