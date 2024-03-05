package com.mifos.mifosxdroid.online.runreports

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.mifos.core.common.utils.Constants
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.runreports.report.ReportFragment
import com.mifos.mifosxdroid.online.runreports.reportcategory.ReportCategoryFragment
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 02-08-17.
 */
@AndroidEntryPoint
class RunReportsActivity : MifosBaseActivity(), OnItemSelectedListener {
    private var intent: Intent? = null
    private var spinner: Spinner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        spinner = Spinner(supportActionBar?.themedContext)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.array_runreport, R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = this
        toolbar.addView(spinner)
        intent = Intent(Constants.ACTION_REPORT)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            popBackStack()
            navigate(R.id.reportCategoryFragment)
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
        when (i) {
            0 -> {
                intent?.putExtra(Constants.REPORT_CATEGORY, Constants.CLIENT)
                sendBroadcast(intent)
            }

            1 -> {
                intent?.putExtra(Constants.REPORT_CATEGORY, Constants.LOAN)
                sendBroadcast(intent)
            }

            2 -> {
                intent?.putExtra(Constants.REPORT_CATEGORY, Constants.SAVINGS)
                sendBroadcast(intent)
            }

            3 -> {
                intent?.putExtra(Constants.REPORT_CATEGORY, Constants.FUND)
                sendBroadcast(intent)
            }

            4 -> {
                intent?.putExtra(Constants.REPORT_CATEGORY, Constants.ACCOUNTING)
                sendBroadcast(intent)
            }

            5 -> {}
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    private fun addOnBackStackChangedListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val fragmentManager = supportFragmentManager
            val fragment = fragmentManager.findFragmentById(R.id.container)
            if (fragment is ReportDetailFragment) {
                spinner?.visibility = View.INVISIBLE
            } else if (fragment is ReportFragment) {
                spinner?.visibility = View.INVISIBLE
                spinner?.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View, i: Int, l: Long
                    ) {
                        when (i) {
                            0 -> sendBroadcastFromReportFragment(
                                fragmentManager, Constants.CLIENT
                            )

                            1 -> sendBroadcastFromReportFragment(
                                fragmentManager, Constants.LOAN
                            )

                            2 -> sendBroadcastFromReportFragment(
                                fragmentManager, Constants.SAVINGS
                            )

                            3 -> sendBroadcastFromReportFragment(
                                fragmentManager, Constants.FUND
                            )

                            4 -> sendBroadcastFromReportFragment(
                                fragmentManager, Constants.ACCOUNTING
                            )

                            5 -> {}
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                }
            } else if (fragment is ReportCategoryFragment) {
                spinner?.visibility = View.INVISIBLE
            }
        }
    }

    private fun sendBroadcastFromReportFragment(
        fragmentManager: FragmentManager,
        reportCategory: String
    ) {
        fragmentManager.popBackStack()
        fragmentManager.popBackStack()
        fragmentManager.executePendingTransactions()
        intent?.putExtra(Constants.REPORT_CATEGORY, reportCategory)
        sendBroadcast(intent)
    }
}