/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountdisbursement

import android.os.Bundle
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
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanDisbursement
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.DialogFragmentDisburseLoanBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Network
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 1/22/2016.
 */
@AndroidEntryPoint
class LoanAccountDisbursementFragment : MifosBaseFragment(), OnDatePickListener,
    OnItemSelectedListener {

    private lateinit var binding: DialogFragmentDisburseLoanBinding
    private val arg: LoanAccountDisbursementFragmentArgs by navArgs()

    private lateinit var viewModel: LoanAccountDisbursementViewModel

    private var paymentTypeId = 0
    private var disbursementDates: String? = null
    private var loanAccountNumber = 0
    private var mfDatePicker: DialogFragment? = null
    private var paymentTypeOptions: List<String>? = null
    private var paymentTypeOptionAdapter: ArrayAdapter<String>? = null
    private var loanTransactionTemplate: LoanTransactionTemplate? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanAccountNumber = arg.loanAccountNumber
        paymentTypeOptions = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DialogFragmentDisburseLoanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoanAccountDisbursementViewModel::class.java]
        showUserInterface()
        viewModel.loadLoanTemplate(loanAccountNumber)

        viewModel.loanAccountDisbursementUiState.observe(viewLifecycleOwner) {
            when (it) {
                is LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully -> {
                    showProgressbar(false)
                    showDisburseLoanSuccessfully(it.genericResponse)
                }

                is LoanAccountDisbursementUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is LoanAccountDisbursementUiState.ShowLoanTransactionTemplate -> {
                    showProgressbar(false)
                    showLoanTransactionTemplate(it.loanTransactionTemplate)
                }

                is LoanAccountDisbursementUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDisburseLoan.setOnClickListener {
            onSubmitDisburse()
        }

        binding.tvLoanDisbursementDates.setOnClickListener {
            onClickTvDisburseDate()
        }
    }

    private fun onSubmitDisburse() {
        if (Network.isOnline(requireContext())) {
            // Notify the user if Amount field is blank
            if (binding.etDisbursedAmount.editableText.toString().isEmpty()) {
                RequiredFieldException(
                    getString(R.string.amount),
                    getString(R.string.message_field_required)
                ).notifyUserWithToast(activity)
                return
            }
            val loanDisbursement = LoanDisbursement()
            loanDisbursement.note = binding.etDisbursementNote.editableText.toString()
            loanDisbursement.actualDisbursementDate = disbursementDates
            loanDisbursement.transactionAmount =
                java.lang.Double.valueOf(binding.etDisbursedAmount.editableText.toString())
            loanDisbursement.paymentId = paymentTypeId
            viewModel.disburseLoan(loanAccountNumber, loanDisbursement)
        } else {
            Toaster.show(binding.root, R.string.error_network_not_available, Toaster.LONG)
        }
    }

    private fun showUserInterface() {
        setToolbarTitle(getString(R.string.disburse_loan))
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvLoanDisbursementDates.text = MFDatePicker.datePickedAsString
        showDisbursementDate(binding.tvLoanDisbursementDates.text.toString())
        paymentTypeOptionAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, paymentTypeOptions ?: emptyList()
        )
        paymentTypeOptionAdapter
            ?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLoanPaymentType.adapter = paymentTypeOptionAdapter
        binding.spLoanPaymentType.onItemSelectedListener = this
    }

    private fun showDisbursementDate(date: String?) {
        disbursementDates = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
            .replace("-", " ")
    }


    private fun onClickTvDisburseDate() {
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    override fun onDatePicked(date: String?) {
        binding.tvLoanDisbursementDates.text = date
        showDisbursementDate(date)
    }

    private fun showLoanTransactionTemplate(loanTransactionTemplate: LoanTransactionTemplate) {
        this.loanTransactionTemplate = loanTransactionTemplate
        binding.etDisbursedAmount.setText(loanTransactionTemplate.amount.toString())
        paymentTypeOptions.addAll()
        paymentTypeOptionAdapter?.notifyDataSetChanged()
    }

    private fun showDisburseLoanSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(
            activity, R.string.loan_disburse_successfully,
            Toast.LENGTH_LONG
        ).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showError(message: String?) {
        Toaster.show(binding.root, message, Toaster.INDEFINITE)
    }

    private fun showError(errorMessage: Int) {
        Toaster.show(binding.root, errorMessage)
    }

    private fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressBar()
            binding.llDisburse.visibility = View.GONE
        } else {
            hideMifosProgressBar()
            binding.llDisburse.visibility = View.VISIBLE
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (parent.id == R.id.sp_loan_payment_type) {
            paymentTypeId = loanTransactionTemplate!!.paymentTypeOptions[position].id
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}

private fun <E> List<E>?.addAll() {

}
