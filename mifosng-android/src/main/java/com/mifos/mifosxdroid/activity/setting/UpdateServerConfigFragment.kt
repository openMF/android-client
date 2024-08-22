package com.mifos.mifosxdroid.activity.setting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mifos.feature.settings.updateServer.UpdateServerConfigScreenRoute
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
                    },
                    onSuccessful = {
                        this@UpdateServerConfigFragment.dismiss()

//                        showRestartCountdownToast(requireContext(), 2)
                    }
                )
            }
        }
    }
}

