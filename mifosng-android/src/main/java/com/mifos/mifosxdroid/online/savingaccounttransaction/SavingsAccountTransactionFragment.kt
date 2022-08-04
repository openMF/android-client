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
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.jakewharton.fliptables.FlipTable
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.utils.*
import javax.inject.Inject

class SavingsAccountTransactionFragment : ProgressableFragment(), OnDatePickListener, SavingsAccountTransactionMvpView {
    val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.view_flipper)
    var viewFlipper: ViewFlipper? = null

    @JvmField
    @BindView(R.id.tv_clientName)
    var tv_clientName: TextView? = null

    @JvmField
    @BindView(R.id.tv_savingsAccountNumber)
    var tv_accountNumber: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_date)
    var tv_transactionDate: TextView? = null

    @JvmField
    @BindView(R.id.et_transaction_amount)
    var et_transactionAmount: EditText? = null

    @JvmField
    @BindView(R.id.sp_payment_type)
    var sp_paymentType: Spinner? = null

    @JvmField
    @Inject
    var mSavingAccountTransactionPresenter: SavingsAccountTransactionPresenter? = null
    private lateinit var rootView: View
    private var savingsAccountNumber: String? = null
    private var savingsAccountId = 0
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
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            savingsAccountNumber = requireArguments().getString(Constants.SAVINGS_ACCOUNT_NUMBER)
            savingsAccountId = requireArguments().getInt(Constants.SAVINGS_ACCOUNT_ID)
            transactionType = requireArguments().getString(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE)
            clientName = requireArguments().getString(Constants.CLIENT_NAME)
            savingsAccountType = requireArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_savings_account_transaction, container,
                false)
        if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
            setToolbarTitle(resources.getString(R.string.savingsAccount) + resources
                    .getString(R.string.space) + resources.getString(R.string.deposit))
        } else {
            setToolbarTitle(resources.getString(R.string.savingsAccount) + resources
                    .getString(R.string.space) + resources.getString(R.string.withdrawal))
        }
        ButterKnife.bind(this, rootView)
        mSavingAccountTransactionPresenter!!.attachView(this)

        //This Method Checking SavingAccountTransaction made before in Offline mode or not.
        //If yes then User have to sync that first then he will be able to make transaction.
        //If not then User able to make SavingAccountTransaction in Online or Offline.
        checkSavingAccountTransactionStatusInDatabase()
        return rootView
    }

    override fun checkSavingAccountTransactionStatusInDatabase() {
        // Checking SavingAccountTransaction Already made in Offline mode or Not.
        mSavingAccountTransactionPresenter
                ?.checkInDatabaseSavingAccountTransaction(savingsAccountId)
    }

    override fun showSavingAccountTransactionExistInDatabase() {
        //Visibility of ParentLayout GONE, If SavingAccountTransaction Already made in Offline Mode
        viewFlipper!!.visibility = View.GONE
        MaterialDialog.Builder().init(activity)
                .setTitle(R.string.sync_previous_transaction)
                .setMessage(R.string.dialog_message_sync_savingaccounttransaction)
                .setPositiveButton(R.string.dialog_action_ok
                ) { dialog, which -> requireActivity().supportFragmentManager.popBackStackImmediate() }
                .setCancelable(false)
                .createMaterialDialog()
                .show()
    }

    override fun showSavingAccountTransactionDoesNotExistInDatabase() {
        // This Method Inflating UI and Initializing the Loading SavingAccountTransaction
        // Template for transaction.
        inflateUI()
    }

    fun inflateUI() {
        tv_clientName!!.text = clientName
        tv_accountNumber!!.text = savingsAccountNumber
        //TODO Implement QuickContactBadge here
        inflateRepaymentDate()
        inflateSavingsAccountTemplate()
    }

    fun inflateSavingsAccountTemplate() {
        mSavingAccountTransactionPresenter!!.loadSavingAccountTemplate(
                savingsAccountType!!.endpoint, savingsAccountId, transactionType)
    }

    @OnClick(R.id.bt_reviewTransaction)
    fun onReviewTransactionButtonClicked() {
        // Notify user if Amount field is blank and Review
        // Transaction button is pressed.
        if (et_transactionAmount!!.editableText.toString().isEmpty()) {
            RequiredFieldException(getString(R.string.amount), getString(R.string.message_field_required)).notifyUserWithToast(activity)
            return
        }
        // Notify the user if zero is entered in the Amount
        // field or only "." (Decimal point) is entered.
        try {
            if (et_transactionAmount!!.editableText.toString().toFloat() == 0f) {
                RequiredFieldException(getString(R.string.amount), getString(R.string.message_cannot_be_zero)).notifyUserWithToast(activity)
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(activity, R.string.error_invalid_amount, Toast.LENGTH_SHORT).show()
            return
        }
        val headers = arrayOf(resources.getString(R.string.field),
                resources.getString(R.string.value))
        val data = arrayOf(arrayOf(resources.getString(R.string.transaction_date),
                tv_transactionDate!!.text.toString()), arrayOf(resources.getString(R.string.payment_type),
                sp_paymentType!!.selectedItem.toString()), arrayOf(resources.getString(R.string.amount),
                et_transactionAmount!!.editableText.toString()))
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
                .setPositiveButton(resources.getString(R.string.process_transaction)
                ) { dialogInterface, i -> processTransaction() }
                .setNegativeButton(resources.getString(R.string.dialog_action_cancel)
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .createMaterialDialog()
                .show()
    }

    fun processTransaction() {
        val dateString = tv_transactionDate!!.text.toString().replace("-", " ")
        val savingsAccountTransactionRequest = SavingsAccountTransactionRequest()
        savingsAccountTransactionRequest.locale = "en"
        savingsAccountTransactionRequest.dateFormat = "dd MM yyyy"
        savingsAccountTransactionRequest.transactionDate = dateString
        savingsAccountTransactionRequest.transactionAmount = et_transactionAmount
                ?.getEditableText().toString()
        savingsAccountTransactionRequest.paymentTypeId = paymentTypeOptionId.toString()
        val builtTransactionRequestAsJson = Gson().toJson(savingsAccountTransactionRequest)
        Log.i(resources.getString(R.string.transaction_body), builtTransactionRequestAsJson)
        if (!Network.isOnline(activity)) PrefManager.setUserStatus(Constants.USER_OFFLINE)
        mSavingAccountTransactionPresenter!!.processTransaction(savingsAccountType!!.endpoint,
                savingsAccountId, transactionType, savingsAccountTransactionRequest)
    }

    @OnClick(R.id.bt_cancelTransaction)
    fun onCancelTransactionButtonClicked() {
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    fun inflateRepaymentDate() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tv_transactionDate!!.text = MFDatePicker.getDatePickedAsString()
        // TODO Add Validation to make sure :
        // 1. Date Is in Correct Format
        // 2. Date Entered is not greater than Date Today i.e Date is not in future
        tv_transactionDate!!.setOnClickListener { (mfDatePicker as MFDatePicker?)?.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER) }
    }

    override fun onDatePicked(date: String) {
        tv_transactionDate!!.text = date
    }

    override fun showSavingAccountTemplate(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate?) {
        if (savingsAccountTransactionTemplate != null) {
            val listOfPaymentTypes = Utils.getPaymentTypeOptions(
                    savingsAccountTransactionTemplate.paymentTypeOptions)
            val paymentTypeAdapter = ArrayAdapter(requireActivity(),
                    android.R.layout.simple_spinner_item, listOfPaymentTypes)
            paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_paymentType!!.adapter = paymentTypeAdapter
            sp_paymentType!!.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    paymentTypeOptionId = savingsAccountTransactionTemplate
                            .paymentTypeOptions[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun showTransactionSuccessfullyDone(savingsAccountTransactionResponse: SavingsAccountTransactionResponse?) {
        if (savingsAccountTransactionResponse!!.resourceId == null) {
            Toaster.show(rootView, resources.getString(R.string.transaction_saved_in_db))
        } else {
            if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
                Toaster.show(rootView, "Deposit Successful, Transaction ID = " +
                        savingsAccountTransactionResponse.resourceId)
            } else if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL) {
                Toaster.show(rootView, "Withdrawal Successful, Transaction ID = "
                        + savingsAccountTransactionResponse.resourceId)
            }
        }
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showError(errorMessage: Int) {
        Toaster.show(rootView, errorMessage)
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSavingAccountTransactionPresenter!!.detachView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param savingsAccountWithAssociations Savings Account of the Client with some additional
         * association details
         * @param transactionType                Type of Transaction (Deposit or Withdrawal)
         * @return A new instance of fragment SavingsAccountTransactionDialogFragment.
         */
        @JvmStatic
        fun newInstance(
                savingsAccountWithAssociations: SavingsAccountWithAssociations,
                transactionType: String?, accountType: DepositType?): SavingsAccountTransactionFragment {
            val fragment = SavingsAccountTransactionFragment()
            val args = Bundle()
            args.putString(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountWithAssociations
                    .accountNo)
            args.putInt(Constants.SAVINGS_ACCOUNT_ID, savingsAccountWithAssociations.id)
            args.putString(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE, transactionType)
            args.putString(Constants.CLIENT_NAME, savingsAccountWithAssociations.clientName)
            args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, accountType)
            fragment.arguments = args
            return fragment
        }
    }
}