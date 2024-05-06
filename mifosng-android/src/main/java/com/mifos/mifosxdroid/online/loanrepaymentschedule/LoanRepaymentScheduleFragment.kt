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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.loan.RepaymentSchedule
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanRepaymentScheduleAdapter
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentLoanRepaymentScheduleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanRepaymentScheduleFragment : ProgressableFragment() {

    private lateinit var binding: FragmentLoanRepaymentScheduleBinding
    private val arg: LoanRepaymentScheduleFragmentArgs by navArgs()

    private lateinit var viewModel: LoanRepaymentScheduleViewModel

    private var loanAccountNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanAccountNumber = arg.loanId
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoanRepaymentScheduleBinding.inflate(inflater, container, false)
        setToolbarTitle(resources.getString(R.string.loan_repayment_schedule))
        viewModel = ViewModelProvider(this)[LoanRepaymentScheduleViewModel::class.java]
        inflateRepaymentSchedule()

        viewModel.loanRepaymentScheduleUiState.observe(viewLifecycleOwner) {
            when (it) {
                is LoanRepaymentScheduleUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is LoanRepaymentScheduleUiState.ShowLoanRepaySchedule -> {
                    showProgressbar(false)
                    showLoanRepaySchedule(it.loanWithAssociations)
                }

                is LoanRepaymentScheduleUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    private fun inflateRepaymentSchedule() {
        viewModel.loadLoanRepaySchedule(loanAccountNumber)
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    private fun showLoanRepaySchedule(loanWithAssociations: LoanWithAssociations) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        val listOfActualPeriods = loanWithAssociations
            .repaymentSchedule
            .getlistOfActualPeriods()
        val loanRepaymentScheduleAdapter =
            LoanRepaymentScheduleAdapter(requireActivity(), listOfActualPeriods)
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

    private fun showFetchingError(s: String?) {
        Toaster.show(binding.root, s)
    }
}