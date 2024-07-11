package com.mifos.mifosxdroid.online.runreports.reportdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.runreports.FullParameterListResponse
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import com.mifos.feature.report.report_detail.ReportDetailScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 04-08-17.
 */
@AndroidEntryPoint
class ReportDetailFragment : MifosBaseFragment() {

    private val arg: ReportDetailFragmentArgs by navArgs()
    private lateinit var reportItem: ClientReportTypeItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        reportItem = arg.clientReportTypeItem
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ReportDetailScreen(
                    reportItem = reportItem,
                    onBackPressed = {
                        findNavController().popBackStack()
                    },
                    runReport = {
                        showRunReport(it)
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


    private fun showRunReport(response: FullParameterListResponse) {
        val action =
            ReportDetailFragmentDirections.actionReportDetailFragmentToReportFragment(response)
        findNavController().navigate(action)
    }
}