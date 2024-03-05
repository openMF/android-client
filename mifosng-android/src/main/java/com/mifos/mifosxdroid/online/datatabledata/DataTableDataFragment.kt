/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.datatabledata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.JsonArray
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.noncore.DataTable
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentDatatableBinding
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogFragment
import com.mifos.utils.DataTableUIBuilder
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DataTableDataFragment : MifosBaseFragment(), DataTableUIBuilder.DataTableActionListener,
    OnRefreshListener {

    private lateinit var binding: FragmentDatatableBinding
    private val arg: DataTableDataFragmentArgs by navArgs()

    private lateinit var viewModel: DataTableDataViewModel

    private var dataTable: DataTable? = null
    private var entityId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataTable = arg.dataTable
        entityId = arg.entityId
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDatatableBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DataTableDataViewModel::class.java]
        dataTable!!.registeredTableName?.let { setToolbarTitle(it) }
        binding.swipeContainer.setColorSchemeColors(
            *activity
                ?.resources!!.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)
        viewModel.loadDataTableInfo(dataTable?.registeredTableName, entityId)

        viewModel.dataTableDataUiState.observe(viewLifecycleOwner) {
            when (it) {
                is DataTableDataUiState.ShowDataTableDeletedSuccessfully -> {
                    showProgressbar(false)
                    showDataTableDeletedSuccessfully()
                }

                is DataTableDataUiState.ShowDataTableInfo -> {
                    showProgressbar(false)
                    showDataTableInfo(it.jsonElements)
                }

                is DataTableDataUiState.ShowEmptyDataTable -> {
                    showProgressbar(false)
                    showEmptyDataTable()
                }

                is DataTableDataUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is DataTableDataUiState.ShowFetchingErrorString -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is DataTableDataUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onRefresh() {
        binding.linearLayoutDatatables.visibility = View.GONE
        viewModel.loadDataTableInfo(dataTable?.registeredTableName, entityId)
    }

    override fun showDataTableOptions(table: String, entity: Int, rowId: Int) {
        MaterialDialog.Builder().init(activity)
            .setItems(R.array.datatable_options) { dialog, which ->
                when (which) {
                    0 -> viewModel.deleteDataTableEntry(table, entity, rowId)
                    else -> {
                    }
                }
            }
            .createMaterialDialog()
            .show()
    }

    private fun showDataTableInfo(jsonElements: JsonArray?) {
        if (jsonElements != null) {
            binding.linearLayoutDatatables.visibility = View.VISIBLE
            binding.llError.visibility = View.GONE
            binding.linearLayoutDatatables.removeAllViews()
            binding.linearLayoutDatatables.invalidate()
            val mListener = activity
                ?.supportFragmentManager
                ?.findFragmentByTag(FragmentConstants.FRAG_DATA_TABLE) as DataTableUIBuilder.DataTableActionListener?
            DataTableUIBuilder().getDataTableLayout(
                dataTable!!,
                jsonElements, binding.linearLayoutDatatables, requireContext(), entityId,
                mListener
            )
        }
    }

    private fun showDataTableDeletedSuccessfully() {
        viewModel.loadDataTableInfo(dataTable?.registeredTableName, entityId)
    }

    private fun showEmptyDataTable() {
        binding.linearLayoutDatatables.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        binding.tvError.setText(R.string.empty_data_table)
        Toaster.show(binding.root, R.string.empty_data_table)
    }

    private fun showFetchingError(message: Int) {
        showFetchingError(getString(message))
    }

    private fun showFetchingError(errorMessage: String?) {
        binding.linearLayoutDatatables.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        binding.tvError.text = errorMessage
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = false
        if (show) {
            binding.linearLayoutDatatables.visibility = View.GONE
            binding.progressbarDataTable.visibility = View.VISIBLE
        } else {
            binding.linearLayoutDatatables.visibility = View.VISIBLE
            binding.progressbarDataTable.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            val dataTableRowDialogFragment = DataTableRowDialogFragment
                .newInstance(dataTable, entityId)
            dataTableRowDialogFragment.setTargetFragment(this, Constants.DIALOG_FRAGMENT)
            val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
            fragmentTransaction.addToBackStack(FragmentConstants.DFRAG_DATATABLE_ENTRY_FORM)
            dataTableRowDialogFragment.show(fragmentTransaction, "Document Dialog Fragment")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.DIALOG_FRAGMENT -> if (resultCode == Activity.RESULT_OK) {
                viewModel
                    .loadDataTableInfo(dataTable?.registeredTableName, entityId)
            }
        }
    }
}