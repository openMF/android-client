package com.mifos.mifosxdroid.offline.synccenterpayloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.feature.center.sync_center_payloads.SyncCenterPayloadsScreenRoute
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncCenterPayloadsFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncCenterPayloadsScreenRoute(
                    onBackPressed = {
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncCenterPayloadsFragment {
            val arguments = Bundle()
            val fragment = SyncCenterPayloadsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}