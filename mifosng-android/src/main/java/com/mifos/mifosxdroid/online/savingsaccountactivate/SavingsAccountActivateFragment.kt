/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountactivate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.DialogFragmentApproveSavingsBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.SafeUIBlockingUtility
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 01/06/17.
 * Fragment to allow user to select a date for account approval.
 * It uses the same layout as Savings Account Approve Fragment.
 */
@AndroidEntryPoint
class SavingsAccountActivateFragment : MifosBaseFragment(), OnDatePickListener {

    private lateinit var binding: DialogFragmentApproveSavingsBinding
    private val arg: SavingsAccountActivateFragmentArgs by navArgs()

    private lateinit var viewModel: SavingsAccountActivateViewModel

    var activationDate: String? = null
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
        viewModel = ViewModelProvider(this)[SavingsAccountActivateViewModel::class.java]
        safeUIBlockingUtility = SafeUIBlockingUtility(
            requireContext(),
            getString(R.string.savings_account_loading_message)
        )
        showUserInterface()

        viewModel.savingsAccountActivateUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SavingsAccountActivateUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SavingsAccountActivateUiState.ShowProgressbar -> showProgressbar(true)

                is SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully -> {
                    showProgressbar(false)
                    showSavingAccountActivatedSuccessfully(it.genericResponse)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvApprovalDate.setOnClickListener {
            onClickApprovalDate()
        }

        binding.btnApproveSavings.setOnClickListener {
            onClickActivateSavings()
        }
    }

    private fun showUserInterface() {
        binding.etSavingsApprovalReason.visibility = View.GONE
        binding.tvApprovalDateOn.text = resources.getString(R.string.activated_on)
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvApprovalDate.text = MFDatePicker.datePickedAsString
        activationDate = binding.tvApprovalDate.text.toString()
        showActivationDate()
    }

    private fun onClickActivateSavings() {
        val hashMap = HashMap<String, String>()
        hashMap["dateFormat"] = "dd MMMM yyyy"
        hashMap["activatedOnDate"] = activationDate.toString()
        hashMap["locale"] = "en"
        viewModel.activateSavings(savingsAccountNumber, hashMap)
    }

    private fun onClickApprovalDate() {
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    override fun onDatePicked(date: String?) {
        binding.tvApprovalDate.text = date
        activationDate = date
        showActivationDate()
    }

    private fun showActivationDate() {
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationDate)
            .replace("-", " ")
    }

    private fun showSavingAccountActivatedSuccessfully(genericResponse: GenericResponse?) {
        Toaster.show(
            binding.tvApprovalDateOn,
            resources.getString(R.string.savings_account_activated)
        )
        Toast.makeText(activity, "Savings Activated", Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showError(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility?.safelyBlockUI()
        } else {
            safeUIBlockingUtility?.safelyUnBlockUI()
        }
    }
}