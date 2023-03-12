/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mifos.api.GenericResponse
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.DialogFragmentApproveLoanBinding
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
    private lateinit var binding: DialogFragmentApproveLoanBinding

    @JvmField
    @Inject
    var mLoanAccountApprovalPresenter: LoanAccountApprovalPresenter? = null
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
        binding = DialogFragmentApproveLoanBinding.inflate(inflater,container,false)
        mLoanAccountApprovalPresenter!!.attachView(this)
        showUserInterface()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btApproveLoan.setOnClickListener { onClickApproveLoan() }
        binding.tvLoanApprovalDates.setOnClickListener { setApprovalDate() }
        binding.tvExpectedDisbursementDates.setOnClickListener { setDisbursementDate() }
    }

    fun onClickApproveLoan() {
        val loanApproval = LoanApproval()
        loanApproval.note = binding.etApprovalNote.editableText.toString()
        loanApproval.approvedOnDate = approvalDate
        /* Notify the user if Approved Amount &
         * Transaction Amount field is blank */if (binding.etApprovedAmount.editableText.toString().isEmpty()
                || binding.etTransactionAmount.editableText.toString().isEmpty()) {
            RequiredFieldException(getString(R.string.amount), getString(R.string.message_field_required)).notifyUserWithToast(activity)
            return
        }
        loanApproval.approvedLoanAmount = binding.etApprovedAmount.editableText.toString()
        loanApproval.expectedDisbursementDate = disbursementDate
        initiateLoanApproval(loanApproval)
    }

    fun setApprovalDate() {
        isApprovalDate = true
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

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
        binding.tvLoanApprovalDates.text = MFDatePicker.getDatePickedAsString()
        approvalDate = binding.tvLoanApprovalDates.text.toString()
        binding.tvExpectedDisbursementDates.text = DateHelper.getDateAsString(loanWithAssociations
                ?.timeline!!.expectedDisbursementDate)
        disbursementDate = binding.tvExpectedDisbursementDates.text.toString()
        showApprovalDate()
        binding.etApprovedAmount.setText(loanWithAssociations!!.approvedPrincipal.toString())
        binding.etTransactionAmount.setText(loanWithAssociations!!.approvedPrincipal.toString())
    }

    override fun onDatePicked(date: String) {
        if (isApprovalDate) {
            binding.tvLoanApprovalDates.text = date
            approvalDate = date
            showApprovalDate()
            isApprovalDate = false
        } else if (isDisbursebemntDate) {
            binding.tvExpectedDisbursementDates.text = date
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