/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanrepaymentschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanRepaymentScheduleAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.loan.RepaymentSchedule
import com.mifos.utils.Constants
import javax.inject.Inject

class LoanRepaymentScheduleFragment : ProgressableFragment(), LoanRepaymentScheduleMvpView {
    private val LOG_TAG = javaClass.simpleName

    @kotlin.jvm.JvmField
    @BindView(R.id.lv_repayment_schedule)
    var lv_repaymentSchedule: ListView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_paid)
    var tv_totalPaid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_upcoming)
    var tv_totalUpcoming: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_overdue)
    var tv_totalOverdue: TextView? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanRepaymentSchedulePresenter: LoanRepaymentSchedulePresenter? = null
    private var loanAccountNumber = 0
    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_loan_repayment_schedule, container, false)
        setToolbarTitle(resources.getString(R.string.loan_repayment_schedule))
        ButterKnife.bind(this, rootView)
        mLoanRepaymentSchedulePresenter!!.attachView(this)
        inflateRepaymentSchedule()
        return rootView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    fun inflateRepaymentSchedule() {
        mLoanRepaymentSchedulePresenter!!.loadLoanRepaySchedule(loanAccountNumber)
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanRepaymentSchedulePresenter!!.detachView()
    }

    override fun showLoanRepaySchedule(loanWithAssociations: LoanWithAssociations) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        val listOfActualPeriods = loanWithAssociations
                .repaymentSchedule
                .getlistOfActualPeriods()
        val loanRepaymentScheduleAdapter = LoanRepaymentScheduleAdapter(activity, listOfActualPeriods)
        lv_repaymentSchedule!!.adapter = loanRepaymentScheduleAdapter
        val totalRepaymentsCompleted = resources.getString(R.string.complete) + "" +
                " : "
        val totalRepaymentsOverdue = resources.getString(R.string.overdue) + " : "
        val totalRepaymentsPending = resources.getString(R.string.pending) + " : "
        //Implementing the Footer here
        tv_totalPaid!!.text = totalRepaymentsCompleted + RepaymentSchedule
                .getNumberOfRepaymentsComplete(listOfActualPeriods)
        tv_totalOverdue!!.text = totalRepaymentsOverdue + RepaymentSchedule
                .getNumberOfRepaymentsOverDue(listOfActualPeriods)
        tv_totalUpcoming!!.text = totalRepaymentsPending + RepaymentSchedule
                .getNumberOfRepaymentsPending(listOfActualPeriods)
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(rootView, s)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(loanAccountNumber: Int): LoanRepaymentScheduleFragment {
            val fragment = LoanRepaymentScheduleFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            fragment.arguments = args
            return fragment
        }
    }
}