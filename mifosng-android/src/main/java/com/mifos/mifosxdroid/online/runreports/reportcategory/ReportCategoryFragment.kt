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
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.ClientReportAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment
import com.mifos.objects.runreports.client.ClientReportTypeItem
import com.mifos.utils.Constants
import javax.inject.Inject

/**
 * Created by Tarun on 02-08-17.
 */
class ReportCategoryFragment : MifosBaseFragment(), ReportCategoryMvpView {
    @JvmField
    @BindView(R.id.recycler_report)
    var rvReports: RecyclerView? = null

    @JvmField
    @Inject
    var presenter: ReportCategoryPresenter? = null
    var reportAdapter: ClientReportAdapter? = null
    private lateinit var rootView: View
    private var reportTypeItems: List<ClientReportTypeItem>? = null
    private var reportCategory: String? = null
    var broadCastNewMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            reportCategory = intent.getStringExtra(Constants.REPORT_CATEGORY)
            presenter!!.fetchCategories(reportCategory, false, true)
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
        (activity as MifosBaseActivity?)!!.activityComponent!!.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_runreport, container, false)
        setHasOptionsMenu(true)
        ButterKnife.bind(this, rootView)
        presenter!!.attachView(this)
        presenter!!.fetchCategories(reportCategory, false, true)
        return rootView
    }

    override fun showError(error: String) {
        show(rootView, error)
    }

    override fun showReportCategories(reportTypes: List<ClientReportTypeItem>) {
        reportTypeItems = reportTypes
        val layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            rvReports!!.context, layoutManager.orientation
        )
        rvReports!!.layoutManager = layoutManager
        rvReports!!.addItemDecoration(dividerItemDecoration)
        reportAdapter = ClientReportAdapter { position: Int ->
            openDetailFragment(position)
            null
        }
        rvReports!!.adapter = reportAdapter
        reportAdapter!!.setReportItems(reportTypes)
        reportAdapter!!.notifyDataSetChanged()
    }

    private fun openDetailFragment(pos: Int) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.CLIENT_REPORT_ITEM, reportTypeItems!![pos])
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