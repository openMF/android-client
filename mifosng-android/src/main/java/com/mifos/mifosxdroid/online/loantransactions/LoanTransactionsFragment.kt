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
import android.widget.ExpandableListView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.utils.Constants
import javax.inject.Inject

class LoanTransactionsFragment : MifosBaseFragment(), LoanTransactionsMvpView {
    @kotlin.jvm.JvmField
    @BindView(R.id.elv_loan_transactions)
    var elv_loanTransactions: ExpandableListView? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanTransactionsPresenter: LoanTransactionsPresenter? = null
    private var adapter: LoanTransactionAdapter? = null
    private var loanAccountNumber = 0
    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_loan_transactions, container, false)
        ButterKnife.bind(this, rootView)
        mLoanTransactionsPresenter!!.attachView(this)
        inflateLoanTransactions()
        return rootView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    fun inflateLoanTransactions() {
        mLoanTransactionsPresenter!!.loadLoanTransaction(loanAccountNumber)
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun showLoanTransaction(loanWithAssociations: LoanWithAssociations) {
        Log.i("Transaction List Size", "" + loanWithAssociations.transactions.size)
        adapter = LoanTransactionAdapter(activity,
                loanWithAssociations.transactions)
        elv_loanTransactions!!.setAdapter(adapter)
        elv_loanTransactions!!.setGroupIndicator(null)
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(rootView, s)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanTransactionsPresenter!!.detachView()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(loanAccountNumber: Int): LoanTransactionsFragment {
            val fragment = LoanTransactionsFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            fragment.arguments = args
            return fragment
        }
    }
}