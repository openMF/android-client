/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.DialogFragmentApproveSavingsBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Network
import com.mifos.utils.SafeUIBlockingUtility
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author nellyk
 */
@AndroidEntryPoint
class SavingsAccountApprovalFragment : MifosBaseFragment(), OnDatePickListener {

    private lateinit var binding: DialogFragmentApproveSavingsBinding
    private val arg: SavingsAccountApprovalFragmentArgs by navArgs()

    private lateinit var viewModel: SavingsAccountApprovalViewModel

    var approvaldate: String? = null
    var savingsAccountNumber = 0
    var savingsAccountType: DepositType? = null
    private var mfDatePicker: DialogFragment? = null
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savingsAccountNumber = arg.savingsAccountNumber
        savingsAccountType = arg.type
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DialogFragmentApproveSavingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SavingsAccountApprovalViewModel::class.java]
        safeUIBlockingUtility = SafeUIBlockingUtility(
            requireContext(),
            getString(R.string.savings_account_approval_fragment_loading_message)
        )
        showUserInterface()

        viewModel.savingsAccountApprovalUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SavingsAccountApprovalUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SavingsAccountApprovalUiState.ShowProgressbar -> showProgressbar(true)

                is SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully -> {
                    showProgressbar(false)
                    showSavingAccountApprovedSuccessfully(it.genericResponse)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApproveSavings.setOnClickListener {
            onClickApproveSavings()
        }

        binding.tvApprovalDate.setOnClickListener {
            onClickApprovalDate()
        }
    }

    private fun showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvApprovalDate.text = MFDatePicker.datePickedAsString
        approvaldate = binding.tvApprovalDate.text.toString()
        showApprovalDate()
    }

    private fun onClickApproveSavings() {
        if (Network.isOnline(requireContext())) {
            val savingsApproval = SavingsApproval()
            savingsApproval.note = binding.etSavingsApprovalReason.editableText.toString()
            savingsApproval.approvedOnDate = approvaldate
            initiateSavingsApproval(savingsApproval)
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.error_network_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onClickApprovalDate() {
        mfDatePicker!!.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    override fun onDatePicked(date: String?) {
        binding.tvApprovalDate.text = date
        approvaldate = date
        showApprovalDate()
    }

    private fun showApprovalDate() {
        approvaldate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvaldate)
            .replace("-", " ")
    }

    private fun initiateSavingsApproval(savingsApproval: SavingsApproval) {
        viewModel.approveSavingsApplication(
            savingsAccountNumber, savingsApproval
        )
    }

    private fun showSavingAccountApprovedSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, "Savings Approved", Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showError(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility!!.safelyBlockUI()
        } else {
            safeUIBlockingUtility!!.safelyUnBlockUI()
        }
    }
}