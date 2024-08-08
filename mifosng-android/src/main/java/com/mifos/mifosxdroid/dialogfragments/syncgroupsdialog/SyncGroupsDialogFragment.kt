package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import com.mifos.core.objects.group.Group
import com.mifos.feature.groups.sync_group_dialog.SyncGroupDialogScreen
import com.mifos.feature.groups.sync_group_dialog.SyncGroupsDialogViewModel
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 11/09/16.
 */
@AndroidEntryPoint
class SyncGroupsDialogFragment : DialogFragment() {

    private lateinit var viewModel: SyncGroupsDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val groups: List<Group>? = requireArguments().getParcelableArrayList(Constants.GROUPS)
            groups?.let {
                viewModel.setGroupList(groupList = groups)
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
                SyncGroupDialogScreen(
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
        fun newInstance(groups: List<Group>?): SyncGroupsDialogFragment {
            val syncGroupsDialogFragment = SyncGroupsDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(Constants.GROUPS, groups as ArrayList<out Parcelable?>?)
            syncGroupsDialogFragment.arguments = args
            return syncGroupsDialogFragment
        }
    }
}