package com.mifos.mifosxdroid.online.runreports.reportcategory

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import com.mifos.mifosxdroid.adapters.ClientReportAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentRunreportBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 02-08-17.
 */
@AndroidEntryPoint
class ReportCategoryFragment : MifosBaseFragment() {

    private lateinit var binding: FragmentRunreportBinding

    private lateinit var viewModel: ReportCategoryViewModel

    private var reportAdapter: ClientReportAdapter? = null
    private var reportTypeItems: List<ClientReportTypeItem>? = null
    private var reportCategory: String? = null
    private var broadCastNewMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            reportCategory = intent.getStringExtra(Constants.REPORT_CATEGORY)
            viewModel.fetchCategories(
                reportCategory,
                genericResultSet = false,
                parameterType = true
            )
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            broadCastNewMessage,
            IntentFilter(Constants.ACTION_REPORT)
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(broadCastNewMessage)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunreportBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[ReportCategoryViewModel::class.java]
        viewModel.fetchCategories(reportCategory, genericResultSet = false, parameterType = true)

        viewModel.reportCategoryUiState.observe(viewLifecycleOwner) {
            when (it) {
                is ReportCategoryUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is ReportCategoryUiState.ShowProgressbar -> showProgressbar(true)

                is ReportCategoryUiState.ShowReportCategories -> {
                    showProgressbar(false)
                    showReportCategories(it.clientReportTypeItems)
                }
            }
        }

        return binding.root
    }

    private fun showError(error: String) {
        show(binding.root, error)
    }

    private fun showReportCategories(reportTypes: List<ClientReportTypeItem>) {
        reportTypeItems = reportTypes
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerReport.layoutManager = layoutManager
        reportAdapter = ClientReportAdapter { position: Int ->
            openDetailFragment(position)
            null
        }
        binding.recyclerReport.adapter = reportAdapter
        reportAdapter?.setReportItems(reportTypes)
        reportAdapter?.notifyDataSetChanged()
    }

    private fun openDetailFragment(pos: Int) {
        val action = reportTypeItems?.getOrNull(pos)?.let {
            ReportCategoryFragmentDirections.actionReportCategoryFragmentToReportDetailFragment(it)
        }
        action?.let { findNavController().navigate(it) }
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }
}