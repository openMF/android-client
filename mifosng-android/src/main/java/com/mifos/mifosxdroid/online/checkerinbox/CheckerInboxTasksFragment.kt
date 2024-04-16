package com.mifos.mifosxdroid.online.checkerinbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.ui.CheckerInboxTasksScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckerInboxTasksFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                CheckerInboxTasksScreen(onBackPressed = {
                    requireActivity().onBackPressed()
                }) {
                    findNavController().navigate(R.id.action_checkerInboxTasksFragment_to_checkerInboxFragment)
                }
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
}