package com.mifos.mifosxdroid.offline.syncclientpayloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.feature.offline.sync_client_payload.SyncClientPayloadsScreenRoute
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * This Class for Syncing the clients that is created in offline mode.
 * For syncing the clients user make sure that he/she is in the online mode.
 *
 *
 * Created by Rajan Maurya on 08/07/16.
 */
@AndroidEntryPoint
class SyncClientPayloadsFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncClientPayloadsScreenRoute(
                    onBackPressed = {
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncClientPayloadsFragment {
            val arguments = Bundle()
            val fragment = SyncClientPayloadsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}