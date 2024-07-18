package com.mifos.mifosxdroid.online.runreports.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.runreports.FullParameterListResponse
import com.mifos.feature.report.report.ReportScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 05-08-17.
 */
@AndroidEntryPoint
class ReportFragment : MifosBaseFragment() {

    private val arg: ReportFragmentArgs by navArgs()

    private lateinit var report: FullParameterListResponse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        report = arg.respose
        return ComposeView(requireContext()).apply {
            setContent {
                ReportScreen(report = report, onBackPressed = {
                    findNavController().popBackStack()
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
}