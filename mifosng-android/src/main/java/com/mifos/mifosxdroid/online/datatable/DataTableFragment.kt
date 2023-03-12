package com.mifos.mifosxdroid.online.datatable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.DataTableAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentDatatablesBinding
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataFragment
import com.mifos.objects.noncore.DataTable
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 12/02/17.
 */
class DataTableFragment : MifosBaseFragment(), DataTableMvpView, OnRefreshListener {

    private lateinit var binding: FragmentDatatablesBinding

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

        binding = FragmentDatatablesBinding.inflate(inflater,container,false)
        dataTablePresenter!!.attachView(this)
        showUserInterface()
        dataTablePresenter!!.loadDataTable(tableName)
        return binding.root
    }

    override fun onRefresh() {
        dataTablePresenter!!.loadDataTable(tableName)
    }

    override fun showUserInterface() {
        setToolbarTitle(resources.getString(R.string.datatables))
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvDataTable.layoutManager = mLayoutManager
        binding.rvDataTable.setHasFixedSize(true)
        binding.rvDataTable.adapter = dataTableAdapter
        binding.swipeContainer.setColorSchemeColors(*activity
                ?.resources!!.getIntArray(R.array.swipeRefreshColors))
        binding.swipeContainer.setOnRefreshListener(this)
    }

    override fun showDataTables(dataTables: List<DataTable>?) {
        this.dataTables = dataTables
        dataTableAdapter!!.dataTables = dataTables ?: emptyList()
    }

    override fun showEmptyDataTables() {
        binding.llError.visibility = View.VISIBLE
        binding.rvDataTable.visibility = View.GONE
        binding.tvError.setText(R.string.empty_data_table)
    }

    override fun showResetVisibility() {
        binding.llError.visibility = View.GONE
        binding.rvDataTable.visibility = View.VISIBLE
    }

    override fun showError(message: Int) {
        Toaster.show(binding.root, message)
        binding.llError.visibility = View.VISIBLE
        binding.rvDataTable.visibility = View.GONE
        binding.tvError.text = getString(R.string.failed_to_fetch_datatable)
    }

    override fun showProgressbar(show: Boolean) {
        if (show && dataTableAdapter.itemCount == 0) {
            binding.progressbarDataTable.visibility = View.VISIBLE
            binding.swipeContainer.isRefreshing = false
        } else {
            binding.progressbarDataTable.visibility = View.GONE
            binding.swipeContainer.isRefreshing = show
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