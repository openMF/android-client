package com.mifos.mifosxdroid.activity.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mifos.feature.settings.UpdateServerConfigScreenRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateServerConfigFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                UpdateServerConfigScreenRoute(
                    onCloseClick = {
                        // TODO:: make use of navController
//                        findNavController().navigateUp()

                        this@UpdateServerConfigFragment.dismiss()
                    }
                )
            }
        }
    }
}