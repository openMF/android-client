/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loantransactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentLoanTransactionsBinding
import com.mifos.mifosxdroid.online.datatable.DataTableScreen
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanTransactionsFragment : MifosBaseFragment() {

    private val arg: LoanTransactionsFragmentArgs by navArgs()
    private val viewModel: LoanTransactionsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loanId = arg.loanId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoanTransactionsScreen(
                    navigateBack = { findNavController().popBackStack() })
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