package com.mifos.mifosxdroid.offline.syncgrouppayloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.findNavController
import com.mifos.feature.groups.sync_group_payloads.SyncGroupPayloadsScreenRoute
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 19/07/16.
 */
@AndroidEntryPoint
class SyncGroupPayloadsFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncGroupPayloadsScreenRoute(
                    onBackPressed = { findNavController().popBackStack() }
                )
            }
        }
    }

    companion object {
        fun newInstance(): SyncGroupPayloadsFragment {
            val arguments = Bundle()
            val fragment = SyncGroupPayloadsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}