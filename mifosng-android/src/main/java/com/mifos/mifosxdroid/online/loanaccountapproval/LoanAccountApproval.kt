/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.api.GenericResponse
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.loan.LoanApproval
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * @author nellyk
 */
class LoanAccountApproval : MifosBaseFragment(), OnDatePickListener, LoanAccountApprovalMvpView {
    val LOG_TAG = javaClass.simpleName

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_approval_dates)
    var tv_loan_approval_dates: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_expected_disbursement_dates)
    var tv_expected_disbursement_dates: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.bt_approve_loan)
    var bt_approve_loan: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_approved_amount)
    var et_approved_amount: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_transaction_amount)
    var et_transaction_amount: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_approval_note)
    var et_approval_note: EditText? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanAccountApprovalPresenter: LoanAccountApprovalPresenter? = null
    lateinit var rootView: View
    private var approvalDate: String? = null
    private var disbursementDate: String? = null
    var loanAccountNumber = 0
    private var isDisbursebemntDate = false
    private var isApprovalDate = false
    private var mfDatePicker: DialogFragment? = null
    private var loanWithAssociations: LoanWithAssociations? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
            loanWithAssociations = requireArguments().getParcelable(Constants.LOAN_SUMMARY)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        rootView = inflater.inflate(R.layout.dialog_fragment_approve_loan, null)
        ButterKnife.bind(this, rootView)
        mLoanAccountApprovalPresenter!!.attachView(this)
        showUserInterface()
        return rootView
    }

    @OnClick(R.id.bt_approve_loan)
    fun onClickApproveLoan() {
        val loanApproval = LoanApproval()
        loanApproval.note = et_approval_note!!.editableText.toString()
        loanApproval.approvedOnDate = approvalDate
        /* Notify the user if Approved Amount &
         * Transaction Amount field is blank */if (et_approved_amount!!.editableText.toString().isEmpty()
                || et_transaction_amount!!.editableText.toString().isEmpty()) {
            RequiredFieldException(getString(R.string.amount), getString(R.string.message_field_required)).notifyUserWithToast(activity)
            return
        }
        loanApproval.approvedLoanAmount = et_approved_amount!!.editableText.toString()
        loanApproval.expectedDisbursementDate = disbursementDate
        initiateLoanApproval(loanApproval)
    }

    @OnClick(R.id.tv_loan_approval_dates)
    fun setApprovalDate() {
        isApprovalDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    @OnClick(R.id.tv_expected_disbursement_dates)
    fun setDisbursementDate() {
        isDisbursebemntDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    fun showApprovalDate() {
        approvalDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvalDate)
                .replace("-", " ")
    }

    fun showDisbursebemntDate() {
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate)
                .replace("-", " ")
    }

    override fun showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tv_loan_approval_dates!!.text = MFDatePicker.getDatePickedAsString()
        approvalDate = tv_loan_approval_dates!!.text.toString()
        tv_expected_disbursement_dates!!.text = DateHelper.getDateAsString(loanWithAssociations
                ?.getTimeline()!!.expectedDisbursementDate)
        disbursementDate = tv_expected_disbursement_dates!!.text.toString()
        showApprovalDate()
        et_approved_amount!!.setText(loanWithAssociations!!.approvedPrincipal.toString())
        et_transaction_amount!!.setText(loanWithAssociations!!.approvedPrincipal.toString())
    }

    override fun onDatePicked(date: String) {
        if (isApprovalDate) {
            tv_loan_approval_dates!!.text = date
            approvalDate = date
            showApprovalDate()
            isApprovalDate = false
        } else if (isDisbursebemntDate) {
            tv_expected_disbursement_dates!!.text = date
            disbursementDate = date
            showDisbursebemntDate()
            isDisbursebemntDate = false
        }
    }

    private fun initiateLoanApproval(loanApproval: LoanApproval) {
        mLoanAccountApprovalPresenter!!.approveLoan(loanAccountNumber, loanApproval)
    }

    override fun showLoanApproveSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, "Loan Approved", Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showLoanApproveFailed(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_LONG).show()
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanAccountApprovalPresenter!!.detachView()
    }

    companion object {
        fun newInstance(loanAccountNumber: Int,
                        loanWithAssociations: LoanWithAssociations?): LoanAccountApproval {
            val loanAccountApproval = LoanAccountApproval()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            args.putParcelable(Constants.LOAN_SUMMARY, loanWithAssociations)
            loanAccountApproval.arguments = args
            return loanAccountApproval
        }
    }
}