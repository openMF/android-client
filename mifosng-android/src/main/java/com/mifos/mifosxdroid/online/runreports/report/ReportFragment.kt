package com.mifos.mifosxdroid.online.runreports.report

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.views.scrollview.CustomScrollView
import com.mifos.mifosxdroid.views.scrollview.ScrollChangeListener
import com.mifos.objects.runreports.FullParameterListResponse
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.Constants
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date
import javax.inject.Inject

/**
 * Created by Tarun on 05-08-17.
 */
class ReportFragment : MifosBaseFragment(), ReportMvpView, ScrollChangeListener {
    @JvmField
    @BindView(R.id.table_report)
    var tableReport: TableLayout? = null

    @JvmField
    @BindView(R.id.sv_horizontal)
    var scrollView: CustomScrollView? = null

    @JvmField
    @Inject
    var presenter: ReportPresenter? = null
    private lateinit var rootView: View
    private var report: FullParameterListResponse? = null
    private var page = 0
    private var bottom = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent!!.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_client_report, container, false)
        setHasOptionsMenu(true)
        ButterKnife.bind(this, rootView)
        presenter!!.attachView(this)
        val time = Date().time
        report = requireArguments().getParcelable(Constants.REPORT_NAME)
        setUpUi()
        return rootView
    }

    private fun setUpUi() {
        showProgressbar(true)
        setUpHeading()
        scrollView!!.setScrollChangeListener(this)
        if (report!!.data?.size!! > 0) {
            setUpValues()
        } else {
            Toast.makeText(activity, getString(R.string.msg_report_empty), Toast.LENGTH_SHORT)
                .show()
        }
        showProgressbar(false)
    }

    private fun setUpHeading() {
        val row = TableRow(context)
        val headingRowParams = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        headingRowParams.gravity = Gravity.CENTER
        headingRowParams.setMargins(0, 0, 0, 10)
        row.layoutParams = headingRowParams
        for (column in report!!.columnHeaders!!) {
            when (column.columnDisplayType) {
                "STRING" -> {
                    val tv = TextView(context)
                    tv.setTypeface(tv.typeface, Typeface.BOLD)
                    tv.gravity = Gravity.CENTER
                    tv.text = column.columnName
                    row.addView(tv)
                }
            }
        }
        tableReport!!.addView(row)
    }

    private fun setUpValues() {

        // For each dataRow, the item in the index i refers to the column
        // i of the columnHeader list.
        val ll = page * 100
        val ul = report!!.data?.size?.let { Math.min(ll + 100, it) }
        for (dataRow in ul?.let { report!!.data?.subList(ll, it) }!!) {
            val row = TableRow(context)
            val rowParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            rowParams.gravity = Gravity.CENTER
            rowParams.setMargins(0, 0, 0, 10)
            row.layoutParams = rowParams
            for (i in report!!.columnHeaders?.indices!!) {

                // Add more cases, if they are, for the other types of data here.
                when (report!!.columnHeaders?.get(i)?.columnDisplayType) {
                    "STRING" -> {
                        val tv = TextView(context)
                        tv.gravity = Gravity.CENTER
                        if (dataRow.row[i] != null) {
                            tv.text = dataRow.row[i]
                        } else {
                            tv.text = "-"
                        }
                        row.addView(tv)
                    }
                }
            }
            tableReport!!.addView(row)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_report, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_download_report -> {
                checkPermissionAndExport()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun checkPermissionAndExport() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            val exportCsvAsyncTask = ExportCsvAsyncTask()
            exportCsvAsyncTask.execute(report)
        } else {
            requestPermission()
        }
    }

    fun requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
            activity as MifosBaseActivity?,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
            resources.getString(
                R.string.dialog_message_write_external_storage_permission_denied
            ),
            resources.getString(R.string.dialog_message_permission_never_ask_again_write),
            Constants.WRITE_EXTERNAL_STORAGE_STATUS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                val exportCsvAsyncTask = ExportCsvAsyncTask()
                exportCsvAsyncTask.execute(report)
            } else {
                show(
                    rootView, resources
                        .getString(R.string.permission_denied_to_write_external_document)
                )
            }
        }
    }

    internal open inner class ExportCsvAsyncTask :
        AsyncTask<FullParameterListResponse?, Void?, String>() {
        var reportDirectoryPath: String? = null
        lateinit var reportPath: String
        var reportDirectory: File? = null
        var fileWriter: FileWriter? = null
        var report: FullParameterListResponse? = null
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressbar(true)
            reportDirectoryPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() +
                        getString(R.string.export_csv_directory)
            val timestamp = System.currentTimeMillis()
            reportPath = "$reportDirectoryPath$timestamp.csv"
            reportDirectory = File(reportDirectoryPath)
        }

        protected override fun doInBackground(vararg p0: FullParameterListResponse?): String? {
            if (!reportDirectory!!.exists()) {
                val makeRequiredDirectories = reportDirectory!!.mkdirs()
                if (!makeRequiredDirectories) {
                    return "Directory Creation Failed"
                }
            }
            report = p0[0]
            try {
                fileWriter = FileWriter(reportPath)

                // write headers
                val columnSize = report!!.columnHeaders?.size
                var count = 1
                for (header in report?.columnHeaders ?: emptyList()) {
                    fileWriter?.append(header.columnName)
                    if (count == columnSize) {
                        fileWriter?.append("\n")
                    } else {
                        fileWriter?.append(",")
                    }
                    count++
                }

                // write row data
                for (row in report!!.data!!) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        fileWriter!!.append(java.lang.String.join(",", row.row))
                    } else {
                        fileWriter!!.append(TextUtils.join(",", row.row))
                    }
                    fileWriter!!.append("\n")
                }
                fileWriter!!.flush()
                fileWriter!!.close()
            } catch (e: IOException) {
                return "File Creation Failed"
            }
            return "Saved at $reportPath"
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            showProgressbar(false)
            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
        }
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        val view = scrollView!!.getChildAt(scrollView!!.childCount - 1)
        val diff = view.bottom - (scrollView!!.height + scrollView!!.scrollY)
        if (diff == 0) {
            if (bottom >= 2) {
                bottom = 0
                page++
                Toast.makeText(context, "Loading more", Toast.LENGTH_SHORT).show()
                setUpValues()
            } else {
                bottom++
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?): ReportFragment {
            val fragment = ReportFragment()
            fragment.arguments = args
            return fragment
        }
    }
}