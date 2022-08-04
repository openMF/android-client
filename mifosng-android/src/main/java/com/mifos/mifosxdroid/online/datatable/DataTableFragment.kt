package com.mifos.mifosxdroid.online.datatable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.DataTableAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataFragment
import com.mifos.objects.noncore.DataTable
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 12/02/17.
 */
class DataTableFragment : MifosBaseFragment(), DataTableMvpView, OnRefreshListener {
    @JvmField
    @BindView(R.id.rv_data_table)
    var rvDataTable: RecyclerView? = null

    @JvmField
    @BindView(R.id.progressbar_data_table)
    var pbDataTable: ProgressBar? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.tv_error)
    var tvError: TextView? = null

    @JvmField
    @BindView(R.id.iv_error)
    var ivError: ImageView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null

    @JvmField
    @Inject
    var dataTablePresenter: DataTablePresenter? = null

    val dataTableAdapter by lazy {
        DataTableAdapter(
            onDateTableClick = { dataTable ->
                val dataTableDataFragment: DataTableDataFragment = DataTableDataFragment.Companion.newInstance(dataTable, entityId)
                val fragmentTransaction = requireActivity().supportFragmentManager
                    .beginTransaction()
                fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
                fragmentTransaction.replace(R.id.container, dataTableDataFragment, FragmentConstants.FRAG_DATA_TABLE)
                fragmentTransaction.commit()
            }
        )
    }
    lateinit var rootView: View
    private var tableName: String? = null
    private var entityId = 0
    private var dataTables: List<DataTable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tableName = requireArguments().getString(Constants.DATA_TABLE_NAME)
            entityId = requireArguments().getInt(Constants.ENTITY_ID)
        }
        dataTables = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        rootView = inflater.inflate(R.layout.fragment_datatables, container, false)
        ButterKnife.bind(this, rootView)
        dataTablePresenter!!.attachView(this)
        showUserInterface()
        dataTablePresenter!!.loadDataTable(tableName)
        return rootView
    }

    override fun onRefresh() {
        dataTablePresenter!!.loadDataTable(tableName)
    }

    override fun showUserInterface() {
        setToolbarTitle(resources.getString(R.string.datatables))
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvDataTable!!.layoutManager = mLayoutManager
        rvDataTable!!.setHasFixedSize(true)
        rvDataTable!!.adapter = dataTableAdapter
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
    }

    override fun showDataTables(dataTables: List<DataTable>?) {
        this.dataTables = dataTables
        dataTableAdapter!!.dataTables = dataTables ?: emptyList()
    }

    override fun showEmptyDataTables() {
        ll_error!!.visibility = View.VISIBLE
        rvDataTable!!.visibility = View.GONE
        tvError!!.setText(R.string.empty_data_table)
    }

    override fun showResetVisibility() {
        ll_error!!.visibility = View.GONE
        rvDataTable!!.visibility = View.VISIBLE
    }

    override fun showError(message: Int) {
        Toaster.show(rootView, message)
        ll_error!!.visibility = View.VISIBLE
        rvDataTable!!.visibility = View.GONE
        tvError!!.text = getString(R.string.failed_to_fetch_datatable)
    }

    override fun showProgressbar(show: Boolean) {
        if (show && dataTableAdapter!!.itemCount == 0) {
            pbDataTable!!.visibility = View.VISIBLE
            swipeRefreshLayout!!.isRefreshing = false
        } else {
            pbDataTable!!.visibility = View.GONE
            swipeRefreshLayout!!.isRefreshing = show
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataTablePresenter!!.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance(tableName: String?, entityId: Int): DataTableFragment {
            val arguments = Bundle()
            val dataTableFragment = DataTableFragment()
            arguments.putString(Constants.DATA_TABLE_NAME, tableName)
            arguments.putInt(Constants.ENTITY_ID, entityId)
            dataTableFragment.arguments = arguments
            return dataTableFragment
        }
    }
}