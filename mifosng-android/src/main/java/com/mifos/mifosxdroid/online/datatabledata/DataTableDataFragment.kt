/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.datatabledata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.JsonArray
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentDatatableBinding
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogFragment
import com.mifos.objects.noncore.DataTable
import com.mifos.utils.Constants
import com.mifos.utils.DataTableUIBuilder
import com.mifos.utils.DataTableUIBuilder.DataTableActionListener
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

class DataTableDataFragment : MifosBaseFragment(), DataTableActionListener, DataTableDataMvpView, OnRefreshListener {

    private lateinit var binding: FragmentDatatableBinding

    @JvmField
    @Inject
    var mDataTableDataPresenter: DataTableDataPresenter? = null
    private var dataTable: DataTable? = null
    private var entityId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDatatableBinding.inflate(inflater,container,false)
        mDataTableDataPresenter!!.attachView(this)
        setToolbarTitle(dataTable!!.registeredTableName)
        binding.swipeContainer.setColorSchemeColors(*activity
                ?.resources!!.getIntArray(R.array.swipeRefreshColors))
        binding.swipeContainer.setOnRefreshListener(this)
        mDataTableDataPresenter!!.loadDataTableInfo(dataTable!!.registeredTableName, entityId)
        return binding.root
    }

    override fun onRefresh() {
        binding.linearLayoutDatatables.visibility = View.GONE
        mDataTableDataPresenter!!.loadDataTableInfo(dataTable!!.registeredTableName, entityId)
    }

    override fun showDataTableOptions(table: String, entity: Int, rowId: Int) {
        MaterialDialog.Builder().init(activity)
                .setItems(R.array.datatable_options) { dialog, which ->
                    when (which) {
                        0 -> mDataTableDataPresenter!!.deleteDataTableEntry(table, entity, rowId)
                        else -> {
                        }
                    }
                }
                .createMaterialDialog()
                .show()
    }

    override fun showDataTableInfo(jsonElements: JsonArray?) {
        if (jsonElements != null) {
            binding.linearLayoutDatatables.visibility = View.VISIBLE
            binding.llError.visibility = View.GONE
            binding.linearLayoutDatatables.removeAllViews()
            binding.linearLayoutDatatables.invalidate()
            val mListener = activity
                    ?.supportFragmentManager
                    ?.findFragmentByTag(FragmentConstants.FRAG_DATA_TABLE) as DataTableActionListener?
//            binding.linearLayoutDatatables = DataTableUIBuilder().getDataTableLayout(dataTable,
//                    jsonElements, binding.linearLayoutDatatables, activity, entityId, mListener)

            binding.linearLayoutDatatables.addView(DataTableUIBuilder().getDataTableLayout(dataTable,
                jsonElements, binding.linearLayoutDatatables,activity,entityId,mListener))
        }
    }

    override fun showDataTableDeletedSuccessfully() {
        mDataTableDataPresenter!!.loadDataTableInfo(dataTable!!.registeredTableName, entityId)
    }

    override fun showEmptyDataTable() {
        binding.linearLayoutDatatables.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        binding.tvError.setText(R.string.empty_data_table)
        Toaster.show(binding.root, R.string.empty_data_table)
    }

    override fun showFetchingError(message: Int) {
        showFetchingError(getString(message))
    }

    override fun showFetchingError(errorMessage: String?) {
        binding.linearLayoutDatatables.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        binding.tvError.text = errorMessage
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(show: Boolean) {
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
                mDataTableDataPresenter
                        ?.loadDataTableInfo(dataTable!!.registeredTableName, entityId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDataTableDataPresenter!!.detachView()
    }

    companion object {
        fun newInstance(dataTable: DataTable?, entityId: Int): DataTableDataFragment {
            val fragment = DataTableDataFragment()
            fragment.dataTable = dataTable
            fragment.entityId = entityId
            return fragment
        }
    }
}