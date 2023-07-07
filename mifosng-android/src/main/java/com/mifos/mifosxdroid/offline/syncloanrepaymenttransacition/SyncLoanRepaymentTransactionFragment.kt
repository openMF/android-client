package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SyncLoanRepaymentAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager.userStatus
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 28/07/16.
 */
class SyncLoanRepaymentTransactionFragment : MifosBaseFragment(),
    SyncLoanRepaymentTransactionMvpView, DialogInterface.OnClickListener {

    private lateinit var binding: FragmentSyncpayloadBinding

    val LOG_TAG = javaClass.simpleName

    @Inject
    lateinit var mSyncLoanRepaymentTransactionPresenter: SyncLoanRepaymentTransactionPresenter

    @JvmField
    @Inject
    var mSyncLoanRepaymentAdapter: SyncLoanRepaymentAdapter? = null
    private var mLoanRepaymentRequests: MutableList<LoanRepaymentRequest>? = null
    private var mClientSyncIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        mLoanRepaymentRequests = ArrayList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false)
        mSyncLoanRepaymentTransactionPresenter.attachView(this)
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSyncPayload.layoutManager = mLayoutManager
        binding.rvSyncPayload.setHasFixedSize(true)
        binding.rvSyncPayload.adapter = mSyncLoanRepaymentAdapter
        /**
         * Loading All Loan Repayment Offline Transaction from Database
         */
        binding.swipeContainer.setColorSchemeColors(
            *requireActivity()
                .resources.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener { //Loading LoanRepayment Transactions and PaymentTypeOptions From Database
            mSyncLoanRepaymentTransactionPresenter.loadDatabaseLoanRepaymentTransactions()
            mSyncLoanRepaymentTransactionPresenter.loanPaymentTypeOption()
            if (binding.swipeContainer.isRefreshing) binding.swipeContainer.isRefreshing = false
        }

        //Loading LoanRepayment Transactions  and PaymentTypeOptions From Database
        mSyncLoanRepaymentTransactionPresenter.loadDatabaseLoanRepaymentTransactions()
        mSyncLoanRepaymentTransactionPresenter.loanPaymentTypeOption()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noPayloadIcon.setOnClickListener {
            reloadOnError()
        }
    }

    /**
     * Show when Database response is null or failed to fetch the List<LoanRepayment>
     * Onclick Send Fresh Request for List<LoanRepayment>.
    </LoanRepayment></LoanRepayment> */

    private fun reloadOnError() {
        binding.llError.visibility = View.GONE
        mSyncLoanRepaymentTransactionPresenter.loadDatabaseLoanRepaymentTransactions()
    }

    override fun showOfflineModeDialog() {
        MaterialDialog.Builder().init(activity)
            .setTitle(R.string.offline_mode)
            .setMessage(R.string.dialog_message_offline_sync_alert)
            .setPositiveButton(R.string.dialog_action_go_online, this)
            .setNegativeButton(R.string.dialog_action_cancel, this)
            .createMaterialDialog()
            .show()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> {}
            DialogInterface.BUTTON_POSITIVE -> {
                userStatus = Constants.USER_ONLINE
                if (mLoanRepaymentRequests!!.isNotEmpty()) {
                    mClientSyncIndex = 0
                    syncGroupPayload()
                } else {
                    show(
                        binding.root,
                        requireActivity().resources.getString(R.string.nothing_to_sync)
                    )
                }
            }

            else -> {}
        }
    }

    override fun showLoanRepaymentTransactions(loanRepaymentRequests: List<LoanRepaymentRequest>) {
        mLoanRepaymentRequests = loanRepaymentRequests as MutableList<LoanRepaymentRequest>
        if (loanRepaymentRequests.isEmpty()) {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = requireActivity()
                .resources.getString(R.string.no_repayment_to_sync)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        } else {
            mSyncLoanRepaymentAdapter!!.setLoanRepaymentRequests(loanRepaymentRequests)
        }
    }

    override fun showPaymentTypeOption(paymentTypeOptions: List<PaymentTypeOption>) {
        mSyncLoanRepaymentAdapter!!.setPaymentTypeOptions(paymentTypeOptions)
    }

    override fun showPaymentSubmittedSuccessfully() {
        mSyncLoanRepaymentTransactionPresenter
            .deleteAndUpdateLoanRepayments(
                mLoanRepaymentRequests
                !![mClientSyncIndex].loanId
            )
    }

    override fun showPaymentFailed(errorMessage: String) {
        val loanRepaymentRequest = mLoanRepaymentRequests!![mClientSyncIndex]
        loanRepaymentRequest.errorMessage = errorMessage
        mSyncLoanRepaymentTransactionPresenter.updateLoanRepayment(loanRepaymentRequest)
    }

    override fun showLoanRepaymentUpdated(loanRepaymentRequest: LoanRepaymentRequest) {
        mLoanRepaymentRequests?.set(mClientSyncIndex, loanRepaymentRequest)
        mSyncLoanRepaymentAdapter!!.notifyDataSetChanged()
        mClientSyncIndex += 1
        if (mLoanRepaymentRequests!!.size != mClientSyncIndex) {
            syncGroupPayload()
        }
    }

    override fun showLoanRepaymentDeletedAndUpdateLoanRepayment(loanRepaymentRequests: List<LoanRepaymentRequest>) {
        mClientSyncIndex = 0
        mLoanRepaymentRequests = loanRepaymentRequests as MutableList<LoanRepaymentRequest>
        mSyncLoanRepaymentAdapter!!.setLoanRepaymentRequests(loanRepaymentRequests)
        if (mLoanRepaymentRequests!!.isNotEmpty()) {
            syncGroupPayload()
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = requireActivity()
                .resources.getString(R.string.all_repayment_synced)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showError(stringId: Int) {
        binding.llError.visibility = View.VISIBLE
        val message =
            stringId.toString() + requireActivity().resources.getString(R.string.click_to_refresh)
        binding.noPayloadText.text = message
        show(binding.root, stringId)
    }

    override fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && mSyncLoanRepaymentAdapter!!.itemCount == 0) {
            showMifosProgressBar()
            binding.swipeContainer.isRefreshing = false
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sync, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sync) {
            when (userStatus) {
                0 -> if (mLoanRepaymentRequests!!.isNotEmpty()) {
                    mClientSyncIndex = 0
                    syncGroupPayload()
                } else {
                    show(
                        binding.root,
                        requireActivity().resources.getString(R.string.nothing_to_sync)
                    )
                }

                1 -> showOfflineModeDialog()
                else -> {}
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun syncGroupPayload() {
        for (i in mLoanRepaymentRequests!!.indices) {
            if (mLoanRepaymentRequests!![i].errorMessage == null) {
                mSyncLoanRepaymentTransactionPresenter.syncLoanRepayment(
                    mLoanRepaymentRequests!![i]
                        .loanId, mLoanRepaymentRequests!![i]
                )
                mClientSyncIndex = i
                break
            } else {
                Log.d(
                    LOG_TAG,
                    requireActivity().resources.getString(R.string.error_fix_before_sync) +
                            mLoanRepaymentRequests!![i].errorMessage
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSyncLoanRepaymentTransactionPresenter.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance(): SyncLoanRepaymentTransactionFragment {
            val arguments = Bundle()
            val fragment = SyncLoanRepaymentTransactionFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}