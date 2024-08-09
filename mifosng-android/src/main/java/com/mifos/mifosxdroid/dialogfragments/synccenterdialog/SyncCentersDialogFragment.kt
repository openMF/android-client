package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import com.mifos.core.objects.group.Center
import com.mifos.feature.center.sync_centers_dialog.SyncCenterDialogScreen
import com.mifos.feature.center.sync_centers_dialog.SyncCentersDialogViewModel
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncCentersDialogFragment : DialogFragment() {

    private lateinit var viewModel: SyncCentersDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val centers: List<Center>? = requireArguments().getParcelableArrayList(Constants.CENTER)
        centers?.let {
            viewModel.setCentersList(centersList = centers)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncCenterDialogScreen(
                    hide = { hideDialog() },
                    dismiss = { dismissDialog() }
                )
            }
        }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun hideDialog() {
        dialog?.hide()
    }

    companion object {
        fun newInstance(center: List<Center>?): SyncCentersDialogFragment {
            val syncCentersDialogFragment = SyncCentersDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.CENTER, center as ArrayList<out Parcelable?>?)
            syncCentersDialogFragment.arguments = args
            return syncCentersDialogFragment
        }
    }
}