package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.CheckerTask
import com.mifos.objects.checkerinboxandtasks.RescheduleLoansTask
import kotlinx.android.synthetic.main.checker_inbox_tasks_fragment.*
import javax.inject.Inject

class CheckerInboxTasksFragment : MifosBaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        viewModel.loadCheckerTasks()
        viewModel.loadRescheduleLoanTasks()
    }

    companion object {
        fun newInstance() = CheckerInboxTasksFragment()
    }

    @Inject
    lateinit var factory: CheckerInboxViewModelFactory
    private lateinit var viewModel: CheckerInboxTasksViewModel
    private lateinit var rootView: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setToolbarTitle(resources.getString(R.string.checker_inbox_and_pending_tasks))
        rootView = inflater.inflate(R.layout.checker_inbox_tasks_fragment,
                container, false)
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener(this)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showMifosProgressBar()
        viewModel = ViewModelProviders.of(
                this, factory
        ).get(CheckerInboxTasksViewModel::class.java)

        viewModel.getCheckerTasks().observe(this,
                Observer<List<CheckerTask>> {
                    badge_checker_inbox.text = it?.size.toString()
                })

        viewModel.getRescheduleLoanTasks().observe(this,
                Observer<List<RescheduleLoansTask>> {
                    badge_reschedule_loan.text = it?.size.toString()
                })

        viewModel.status.observe(this,
                Observer { status ->
                    status?.let {
                        hideMifosProgressBar()
                        swipeRefreshLayout.isRefreshing = false
                        if (status) {
                            ll_checker_inbox_tasks.visibility = View.VISIBLE
                        } else {
                            Toaster.show(rootView, getString(R.string.network_issue))
                        }
                    }
                })

        rl_checker_inbox.setOnClickListener {
            val fragmentTransaction = requireActivity()
                    .supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack("Checker Inbox")
            fragmentTransaction.replace(R.id.container,
                    CheckerInboxFragment.newInstance()).commit()
        }
    }
}