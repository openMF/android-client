package com.mifos.mifosxdroid.online.datatable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.mifos.core.objects.noncore.DataTable
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.DataTableAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentDatatablesBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 12/02/17.
 */
@AndroidEntryPoint
class DataTableFragment : MifosBaseFragment(), OnRefreshListener {

    private lateinit var binding: FragmentDatatablesBinding
    private val arg: DataTableFragmentArgs by navArgs()

    private lateinit var viewModel: DataTableViewModel


    private val dataTableAdapter by lazy {
        DataTableAdapter(
            onDateTableClick = { dataTable ->
                val action =
                    DataTableFragmentDirections.actionDataTableFragmentToDataTableDataFragment(
                        dataTable,
                        entityId
                    )
                findNavController().navigate(action)
            }
        )
    }
    private var tableName: String? = null
    private var entityId = 0
    private var dataTables: List<DataTable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableName = arg.tableName
        entityId = arg.entityId
        dataTables = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDatatablesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DataTableViewModel::class.java]
        showUserInterface()
        viewModel.loadDataTable(tableName)

        viewModel.dataTableUiState.observe(viewLifecycleOwner) {
            when (it) {
                is DataTableUiState.ShowDataTables -> {
                    showProgressbar(false)
                    showDataTables(it.dataTables)
                }

                is DataTableUiState.ShowEmptyDataTables -> {
                    showProgressbar(false)
                    showEmptyDataTables()
                }

                is DataTableUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is DataTableUiState.ShowProgressbar -> showProgressbar(true)
                is DataTableUiState.ShowResetVisibility -> {
                    showProgressbar(false)
                    showResetVisibility()
                }
            }
        }

        return binding.root
    }

    override fun onRefresh() {
        viewModel.loadDataTable(tableName)
    }

    private fun showUserInterface() {
        setToolbarTitle(resources.getString(R.string.datatables))
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvDataTable.layoutManager = mLayoutManager
        binding.rvDataTable.setHasFixedSize(true)
        binding.rvDataTable.adapter = dataTableAdapter
        binding.swipeContainer.setColorSchemeColors(
            *activity
                ?.resources!!.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)
    }

    private fun showDataTables(dataTables: List<DataTable>?) {
        this.dataTables = dataTables
        dataTableAdapter.dataTables = dataTables ?: emptyList()
    }

    private fun showEmptyDataTables() {
        binding.llError.visibility = View.VISIBLE
        binding.rvDataTable.visibility = View.GONE
        binding.tvError.setText(R.string.empty_data_table)
    }

    private fun showResetVisibility() {
        binding.llError.visibility = View.GONE
        binding.rvDataTable.visibility = View.VISIBLE
    }

    private fun showError(message: Int) {
        Toaster.show(binding.root, message)
        binding.llError.visibility = View.VISIBLE
        binding.rvDataTable.visibility = View.GONE
        binding.tvError.text = getString(R.string.failed_to_fetch_datatable)
    }

    private fun showProgressbar(show: Boolean) {
        if (show && dataTableAdapter.itemCount == 0) {
            binding.progressbarDataTable.visibility = View.VISIBLE
            binding.swipeContainer.isRefreshing = false
        } else {
            binding.progressbarDataTable.visibility = View.GONE
            binding.swipeContainer.isRefreshing = show
        }
    }
}