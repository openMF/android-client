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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleScreen
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanRepaymentFragment : MifosBaseFragment() {

//    private lateinit var binding: FragmentLoanRepaymentBinding
//    val LOG_TAG = javaClass.simpleName
    private val arg: LoanRepaymentFragmentArgs by navArgs()

//    private lateinit var viewModel: LoanRepaymentViewModel
    val viewmodel : LoanRepaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.loanId = arg.loanWithAssociations.id
        viewmodel.clientName = arg.loanWithAssociations.clientName
        viewmodel.loanAccountNumber = arg.loanWithAssociations.accountNo
        viewmodel.amountInArrears = arg.loanWithAssociations.summary.totalOverdue
        viewmodel.loanProductName = arg.loanWithAssociations.loanProductName

//        clientName = arg.loanWithAssociations.clientName
//        loanAccountNumber = arg.loanWithAssociations.accountNo
//        loanId = arg.loanWithAssociations.id.toString()
//        loanProductName = arg.loanWithAssociations.loanProductName
//        amountInArrears = arg.loanWithAssociations.summary.totalOverdue
    }


    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        toolbar?.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoanRepaymentScreen(
                    navigateBack = { findNavController().popBackStack() }
                )
            }
        }
    }

}