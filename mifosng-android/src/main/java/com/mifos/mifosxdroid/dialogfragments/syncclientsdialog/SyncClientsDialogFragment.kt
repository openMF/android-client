package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import com.mifos.core.objects.client.Client
import com.mifos.feature.client.sync_client_dialog.SyncClientsDialogScreen
import com.mifos.feature.client.sync_client_dialog.SyncClientsDialogViewModel
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 08/08/16.
 */
@AndroidEntryPoint
class SyncClientsDialogFragment : DialogFragment() {

    private lateinit var viewModel: SyncClientsDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val clients: List<Client>? = requireArguments().getParcelableArrayList(Constants.GROUPS)
            clients?.let {
                viewModel.setClientList(clientsList = clients)
            }
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
                SyncClientsDialogScreen(
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
        val LOG_TAG = SyncClientsDialogFragment::class.java.simpleName
        fun newInstance(client: ArrayList<Client>): SyncClientsDialogFragment {
            val syncClientsDialogFragment = SyncClientsDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.CLIENT, client)
            syncClientsDialogFragment.arguments = args
            return syncClientsDialogFragment
        }
    }
}