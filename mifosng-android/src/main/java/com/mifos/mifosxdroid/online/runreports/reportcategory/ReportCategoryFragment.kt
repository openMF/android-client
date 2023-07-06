package com.mifos.mifosxdroid.online.runreports.reportcategory

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.ClientReportAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentRunreportBinding
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment
import com.mifos.objects.runreports.client.ClientReportTypeItem
import com.mifos.utils.Constants
import javax.inject.Inject

/**
 * Created by Tarun on 02-08-17.
 */
class ReportCategoryFragment : MifosBaseFragment(), ReportCategoryMvpView {

    private lateinit var binding: FragmentRunreportBinding

    @Inject
    lateinit var presenter: ReportCategoryPresenter
    var reportAdapter: ClientReportAdapter? = null
    private var reportTypeItems: List<ClientReportTypeItem>? = null
    private var reportCategory: String? = null
    var broadCastNewMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            reportCategory = intent.getStringExtra(Constants.REPORT_CATEGORY)
            presenter.fetchCategories(reportCategory, false, true)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunreportBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        presenter.attachView(this)
        presenter.fetchCategories(reportCategory, genericResultSet = false, parameterType = true)
        return binding.root
    }

    override fun showError(error: String) {
        show(binding.root, error)
    }

    override fun showReportCategories(reportTypes: List<ClientReportTypeItem>) {
        reportTypeItems = reportTypes
        val layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerReport.context, layoutManager.orientation
        )
        binding.recyclerReport.layoutManager = layoutManager
        binding.recyclerReport.addItemDecoration(dividerItemDecoration)
        reportAdapter = ClientReportAdapter { position: Int ->
            openDetailFragment(position)
            null
        }
        binding.recyclerReport.adapter = reportAdapter
        reportAdapter?.setReportItems(reportTypes)
        reportAdapter?.notifyDataSetChanged()
    }

    private fun openDetailFragment(pos: Int) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.CLIENT_REPORT_ITEM, reportTypeItems?.getOrNull(pos))
        val fragmentTransaction = requireActivity()
            .supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack("ClientCategory")
        fragmentTransaction.replace(
            R.id.container,
            ReportDetailFragment.newInstance(bundle)
        ).commit()
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    companion object {
        fun newInstance(): ReportCategoryFragment {
            val fragment = ReportCategoryFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}