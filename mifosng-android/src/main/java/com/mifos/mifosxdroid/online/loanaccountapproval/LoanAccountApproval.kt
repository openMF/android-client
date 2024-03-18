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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanApproval
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.DialogFragmentApproveLoanBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author nellyk
 */
@AndroidEntryPoint
class LoanAccountApproval : MifosBaseFragment(), OnDatePickListener {

    private lateinit var binding: DialogFragmentApproveLoanBinding
    private val arg: LoanAccountApprovalArgs by navArgs()

    private lateinit var viewModel: LoanAccountApprovalViewModel

    private var approvalDate: String? = null
    private var disbursementDate: String? = null
    var loanAccountNumber = 0
    private var isDisbursementDate = false
    private var isApprovalDate = false
    private var mfDatePicker: DialogFragment? = null
    private var loanWithAssociations: LoanWithAssociations? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanAccountNumber = arg.loanAccountNumber
        loanWithAssociations = arg.loanWithAssociations
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DialogFragmentApproveLoanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoanAccountApprovalViewModel::class.java]
        showUserInterface()

        viewModel.loanAccountApprovalUiState.observe(viewLifecycleOwner) {
            when (it) {
                is LoanAccountApprovalUiState.ShowLoanApproveFailed -> {
                    showProgressbar(false)
                    showLoanApproveFailed(it.message)
                }

                is LoanAccountApprovalUiState.ShowLoanApproveSuccessfully -> {
                    showProgressbar(false)
                    showLoanApproveSuccessfully(it.genericResponse)
                }

                is LoanAccountApprovalUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btApproveLoan.setOnClickListener {
            onClickApproveLoan()
        }

        binding.tvLoanApprovalDates.setOnClickListener {
            setApprovalDate()
        }

        binding.tvExpectedDisbursementDates.setOnClickListener {
            setDisbursementDate()
        }

    }

    private fun onClickApproveLoan() {
        val loanApproval = LoanApproval()
        loanApproval.note = binding.etApprovalNote.editableText.toString()
        loanApproval.approvedOnDate = approvalDate
        /* Notify the user if Approved Amount &
         * Transaction Amount field is blank */if (binding.etApprovedAmount.editableText.toString()
                .isEmpty()
            || binding.etTransactionAmount.editableText.toString().isEmpty()
        ) {
            RequiredFieldException(
                getString(R.string.amount),
                getString(R.string.message_field_required)
            ).notifyUserWithToast(activity)
            return
        }
        loanApproval.approvedLoanAmount = binding.etApprovedAmount.editableText.toString()
        loanApproval.expectedDisbursementDate = disbursementDate
        initiateLoanApproval(loanApproval)
    }


    private fun setApprovalDate() {
        isApprovalDate = true
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun setDisbursementDate() {
        isDisbursementDate = true
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun showApprovalDate() {
        approvalDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvalDate)
            .replace("-", " ")
    }

    private fun showDisbursebemntDate() {
        disbursementDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(disbursementDate)
            .replace("-", " ")
    }

    private fun showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvLoanApprovalDates.text = MFDatePicker.datePickedAsString
        approvalDate = binding.tvLoanApprovalDates.text.toString()
        binding.tvExpectedDisbursementDates.text = loanWithAssociations
            ?.timeline!!.expectedDisbursementDate?.let {
                DateHelper.getDateAsString(
                    it
                )
            }
        disbursementDate = binding.tvExpectedDisbursementDates.text.toString()
        showApprovalDate()
        binding.etApprovedAmount.setText(loanWithAssociations?.approvedPrincipal.toString())
        binding.etTransactionAmount.setText(loanWithAssociations?.approvedPrincipal.toString())
    }

    override fun onDatePicked(date: String?) {
        if (isApprovalDate) {
            binding.tvLoanApprovalDates.text = date
            approvalDate = date
            showApprovalDate()
            isApprovalDate = false
        } else if (isDisbursementDate) {
            binding.tvExpectedDisbursementDates.text = date
            disbursementDate = date
            showDisbursebemntDate()
            isDisbursementDate = false
        }
    }

    private fun initiateLoanApproval(loanApproval: LoanApproval) {
        viewModel.approveLoan(loanAccountNumber, loanApproval)
    }

    private fun showLoanApproveSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, "Loan Approved", Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    private fun showLoanApproveFailed(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_LONG).show()
    }

    private fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }
}