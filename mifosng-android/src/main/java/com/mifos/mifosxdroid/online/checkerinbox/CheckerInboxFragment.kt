package com.mifos.mifosxdroid.online.checkerinbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mifos.feature.checker_inbox_task.checker_inbox.CheckerInboxScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CheckerInboxFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                CheckerInboxScreen(

                    onBackPressed = {
                        requireActivity().onBackPressed()
                    }
                )
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