/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.feature.savings.account_approval.SavingsAccountApprovalScreen
import com.mifos.feature.savings.account_approval.SavingsAccountApprovalViewModel
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author nellyk
 */
@AndroidEntryPoint
class SavingsAccountApprovalFragment : MifosBaseFragment(){

    private val arg: SavingsAccountApprovalFragmentArgs by navArgs()
    private val viewmodel: SavingsAccountApprovalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.savingsAccountId = arg.savingsAccountNumber
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavingsAccountApprovalScreen(
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