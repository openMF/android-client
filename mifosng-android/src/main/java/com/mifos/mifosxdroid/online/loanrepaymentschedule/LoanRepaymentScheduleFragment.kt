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
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanRepaymentScheduleAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentLoanRepaymentScheduleBinding
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.loan.RepaymentSchedule
import com.mifos.utils.Constants
import javax.inject.Inject

class LoanRepaymentScheduleFragment : ProgressableFragment(), LoanRepaymentScheduleMvpView {
    private val LOG_TAG = javaClass.simpleName
    private lateinit var binding: FragmentLoanRepaymentScheduleBinding

    @JvmField
    @Inject
    var mLoanRepaymentSchedulePresenter: LoanRepaymentSchedulePresenter? = null
    private var loanAccountNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoanRepaymentScheduleBinding.inflate(inflater,container,false)
        setToolbarTitle(resources.getString(R.string.loan_repayment_schedule))
        mLoanRepaymentSchedulePresenter!!.attachView(this)
        inflateRepaymentSchedule()
        return binding.root
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
        binding.lvRepaymentSchedule.adapter = loanRepaymentScheduleAdapter
        val totalRepaymentsCompleted = resources.getString(R.string.complete) + "" +
                " : "
        val totalRepaymentsOverdue = resources.getString(R.string.overdue) + " : "
        val totalRepaymentsPending = resources.getString(R.string.pending) + " : "
        //Implementing the Footer here
        binding.flrsFooter.tvTotalPaid.text = totalRepaymentsCompleted + RepaymentSchedule
                .getNumberOfRepaymentsComplete(listOfActualPeriods)
        binding.flrsFooter.tvTotalOverdue.text = totalRepaymentsOverdue + RepaymentSchedule
                .getNumberOfRepaymentsOverDue(listOfActualPeriods)
        binding.flrsFooter.tvTotalUpcoming.text = totalRepaymentsPending + RepaymentSchedule
                .getNumberOfRepaymentsPending(listOfActualPeriods)
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(binding.root, s)
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