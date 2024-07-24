package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class SyncSurveysDialogComposeFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                SyncSurveysDialogScreen(
                    closeDialog = { dialog?.dismiss() }
                )
            }
        }
    }

    companion object {
        val LOG_TAG = SyncSurveysDialogComposeFragment::class.java.simpleName
        fun newInstance(): SyncSurveysDialogComposeFragment {
            val syncSurveysDialogFragment = SyncSurveysDialogComposeFragment()
            val args = Bundle()
            syncSurveysDialogFragment.arguments = args
            return syncSurveysDialogFragment
        }
    }
}