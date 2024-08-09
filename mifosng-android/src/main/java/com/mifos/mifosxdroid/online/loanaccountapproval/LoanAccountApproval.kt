/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.feature.loan.loan_approval.LoanAccountApprovalScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author nellyk
 */
@AndroidEntryPoint
class LoanAccountApproval : MifosBaseFragment() {

    private val arg: LoanAccountApprovalArgs by navArgs()
    private var loanId : Int = 0
    private lateinit var loanWithAssociations : LoanWithAssociations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanId = arg.loanAccountNumber
        loanWithAssociations = arg.loanWithAssociations
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoanAccountApprovalScreen(
                    loanId = loanId,
                    loanWithAssociations = loanWithAssociations,
                    navigateBack = { findNavController().popBackStack() },
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