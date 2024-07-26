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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentSavingsAccountTransactionBinding
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountScreen
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Network
import com.mifos.utils.PrefManager
import com.mifos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavingsAccountTransactionFragment : MifosBaseFragment() {

    private val arg: SavingsAccountTransactionFragmentArgs by navArgs()
    val viewModel: SavingsAccountTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.accountId = arg.savingsAccountWithAssociations.id
        viewModel.transactionType = arg.transactionType
        viewModel.clientName = arg.savingsAccountWithAssociations.clientName
        viewModel.savingsAccountNumber = arg.savingsAccountWithAssociations.accountNo
        viewModel.savingsAccountType = arg.accountType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavingsAccountTransactionScreen {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        toolbar?.visibility = View.VISIBLE
    }
}