package com.mifos.mifosxdroid.online.checkerinbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mifos.core.common.utils.Constants
import com.mifos.feature.checker_inbox_task.checker_inbox.ui.CheckerInboxScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.dialogfragments.checkertaskfilterdialog.CheckerTaskFilterDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp

@AndroidEntryPoint
class CheckerInboxFragment : MifosBaseFragment(), CheckerTaskFilterDialogFragment.OnInputSelected {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                CheckerInboxScreen(onBackPressed = {
                    requireActivity().onBackPressed()
                }, filter = {
                    // TODO implement filter dialog
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    private fun setUpOnClickListeners() {
        val dialogSearchFilter = CheckerTaskFilterDialogFragment()
        dialogSearchFilter.setTargetFragment(this, Constants.DIALOG_FRAGMENT)
        dialogSearchFilter.show(parentFragmentManager, "DialogSearchFilter")
    }

    override fun sendInput(
        fromDate: Timestamp?, toDate: Timestamp?, action: String, entity: String,
        resourceId: String
    ) {
//        val filteredList = getFilteredList(fromDate, toDate, action, entity, resourceId)
//        updateRecyclerViewWithNewList(filteredList)
    }
}