package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.feature.savings.sync_account_transaction.SyncSavingsAccountTransactionScreenRoute
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 19/08/16.
 */
@AndroidEntryPoint
class SyncSavingsAccountTransactionFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncSavingsAccountTransactionScreenRoute(
                    onBackPressed = {
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncSavingsAccountTransactionFragment {
            val arguments = Bundle()
            val sync = SyncSavingsAccountTransactionFragment()
            sync.arguments = arguments
            return sync
        }
    }
}