/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.datatabledata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonArray
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogFragment
import com.mifos.objects.noncore.DataTable
import com.mifos.utils.Constants
import com.mifos.utils.DataTableUIBuilder
import com.mifos.utils.DataTableUIBuilder.DataTableActionListener
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

class DataTableDataFragment : MifosBaseFragment(), DataTableActionListener, DataTableDataMvpView, OnRefreshListener {
    @JvmField
    @BindView(R.id.linear_layout_datatables)
    var linearLayout: LinearLayout? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.progressbar_data_table)
    var pbDataTable: ProgressBar? = null

    @JvmField
    @BindView(R.id.ll_error)
    var llError: LinearLayout? = null

    @JvmField
    @BindView(R.id.tv_error)
    var tvError: TextView? = null

    @JvmField
    @Inject
    var mDataTableDataPresenter: DataTableDataPresenter? = null
    private var dataTable: DataTable? = null
    private var entityId = 0
    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_datatable, container, false)
        ButterKnife.bind(this, rootView)
        mDataTableDataPresenter!!.attachView(this)
        setToolbarTitle(dataTable!!.registeredTableName)
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
        mDataTableDataPresenter!!.loadDataTableInfo(dataTable!!.registeredTableName, entityId)
        return rootView
    }

    override fun onRefresh() {
        linearLayout!!.visibility = View.GONE
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
            linearLayout!!.visibility = View.VISIBLE
            llError!!.visibility = View.GONE
            linearLayout!!.removeAllViews()
            linearLayout!!.invalidate()
            val mListener = activity
                    ?.getSupportFragmentManager()
                    ?.findFragmentByTag(FragmentConstants.FRAG_DATA_TABLE) as DataTableActionListener?
            linearLayout = DataTableUIBuilder().getDataTableLayout(dataTable,
                    jsonElements, linearLayout, activity, entityId, mListener)
        }
    }

    override fun showDataTableDeletedSuccessfully() {
        mDataTableDataPresenter!!.loadDataTableInfo(dataTable!!.registeredTableName, entityId)
    }

    override fun showEmptyDataTable() {
        linearLayout!!.visibility = View.GONE
        llError!!.visibility = View.VISIBLE
        tvError!!.setText(R.string.empty_data_table)
        Toaster.show(rootView, R.string.empty_data_table)
    }

    override fun showFetchingError(message: Int) {
        showFetchingError(getString(message))
    }

    override fun showFetchingError(errorMessage: String?) {
        linearLayout!!.visibility = View.GONE
        llError!!.visibility = View.VISIBLE
        tvError!!.text = errorMessage
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = false
        if (show) {
            linearLayout!!.visibility = View.GONE
            pbDataTable!!.visibility = View.VISIBLE
        } else {
            linearLayout!!.visibility = View.VISIBLE
            pbDataTable!!.visibility = View.GONE
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