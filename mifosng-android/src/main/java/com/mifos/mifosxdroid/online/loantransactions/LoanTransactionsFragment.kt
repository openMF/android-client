/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loantransactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentLoanTransactionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanTransactionsFragment : MifosBaseFragment() {

    private lateinit var binding: FragmentLoanTransactionsBinding
    private val arg: LoanTransactionsFragmentArgs by navArgs()

    private lateinit var viewModel: LoanTransactionsViewModel

    private var adapter: LoanTransactionAdapter? = null
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
        binding = FragmentLoanTransactionsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoanTransactionsViewModel::class.java]
        inflateLoanTransactions()

        viewModel.loanTransactionsUiState.observe(viewLifecycleOwner) {
            when (it) {
                is LoanTransactionsUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is LoanTransactionsUiState.ShowLoanTransaction -> {
                    showProgressbar(false)
                    showLoanTransaction(it.loanWithAssociations)
                }

                is LoanTransactionsUiState.ShowProgressBar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    private fun inflateLoanTransactions() {
        viewModel.loadLoanTransaction(loanAccountNumber)
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    private fun showLoanTransaction(loanWithAssociations: LoanWithAssociations) {
        Log.i("Transaction List Size", "" + loanWithAssociations.transactions.size)
        adapter = LoanTransactionAdapter(
            requireActivity(),
            loanWithAssociations.transactions
        )
        binding.elvLoanTransactions.setAdapter(adapter)
        binding.elvLoanTransactions.setGroupIndicator(null)
    }

    private fun showFetchingError(s: String?) {
        Toaster.show(binding.root, s)
    }
}