/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanrepayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.feature.loan.loan_repayment.LoanRepaymentScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanRepaymentFragment : MifosBaseFragment() {

    private val arg: LoanRepaymentFragmentArgs by navArgs()
    private var loanId: Int = 0
    private lateinit var clientName: String
    private lateinit var loanAccountNumber: String
    private var amountInArrears: Double? = null
    private lateinit var loanProductName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanId = arg.loanWithAssociations.id
        clientName = arg.loanWithAssociations.clientName
        loanAccountNumber = arg.loanWithAssociations.accountNo
        amountInArrears = arg.loanWithAssociations.summary.totalOverdue
        loanProductName = arg.loanWithAssociations.loanProductName
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
                    loanId = loanId,
                    clientName = clientName,
                    loanAccountNumber = loanAccountNumber,
                    amountInArrears = amountInArrears,
                    loanProductName = loanProductName,
                    navigateBack = { findNavController().popBackStack() }
                )
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