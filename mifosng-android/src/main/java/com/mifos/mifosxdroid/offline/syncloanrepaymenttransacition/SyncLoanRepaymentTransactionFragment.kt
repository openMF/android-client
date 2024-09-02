package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.feature.offline.syncLoanRepaymentTransaction.SyncLoanRepaymentTransactionScreenRoute
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 28/07/16.
 */
@AndroidEntryPoint
class SyncLoanRepaymentTransactionFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncLoanRepaymentTransactionScreenRoute(
                    onBackPressed = {
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncLoanRepaymentTransactionFragment {
            val arguments = Bundle()
            val fragment = SyncLoanRepaymentTransactionFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}