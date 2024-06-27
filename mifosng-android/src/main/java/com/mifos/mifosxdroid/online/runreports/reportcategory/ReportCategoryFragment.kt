package com.mifos.mifosxdroid.online.runreports.reportcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import com.mifos.feature.report.run_report.RunReportScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 02-08-17.
 */
@AndroidEntryPoint
class ReportCategoryFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                RunReportScreen(
                    onBackPressed = {
                        requireActivity().onBackPressed()
                    },
                    onReportClick = {
                        openDetailFragment(it)
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

    private fun openDetailFragment(runReport: ClientReportTypeItem) {
        val action =
            ReportCategoryFragmentDirections.actionReportCategoryFragmentToReportDetailFragment(
                runReport
            )
        findNavController().navigate(action)
    }
}