package com.mifos.mifosxdroid.offlinejobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.utils.MFErrorParser.errorMessage
import com.mifos.utils.PrefManager.userStatus
import com.mifos.utils.Tags
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by aksh on 27/7/18.
 */
class OfflineSyncLoanRepayment : Job() {

    @Inject
    lateinit var mDataManagerLoan: DataManagerLoan
    private var mSubscriptions: CompositeSubscription? = null
    private var mLoanRepaymentRequests: MutableList<LoanRepaymentRequest> = ArrayList()
    private var mClientSyncIndex = 0
    override fun onRunJob(params: Params): Result {
        mSubscriptions = CompositeSubscription()
        return if (!userStatus) {
            loadDatabaseLoanRepaymentTransactions()
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

    private fun loadDatabaseLoanRepaymentTransactions() {
        mSubscriptions?.add(
            mDataManagerLoan.databaseLoanRepayments
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                    override fun onCompleted() {
                        mClientSyncIndex = 0
                        syncGroupPayload()
                    }

                    override fun onError(e: Throwable) {}
                    override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                        showLoanRepaymentTransactions(loanRepaymentRequests)
                    }
                })
        )
    }

    fun showLoanRepaymentTransactions(loanRepaymentRequests: List<LoanRepaymentRequest>) {
        mLoanRepaymentRequests = loanRepaymentRequests as MutableList<LoanRepaymentRequest>
    }

    fun syncGroupPayload() {
        for (i in mClientSyncIndex until mLoanRepaymentRequests.size) {
            if (mLoanRepaymentRequests[i].errorMessage == null) {
                mLoanRepaymentRequests[i]
                    .loanId?.let {
                        syncLoanRepayment(
                            it, mLoanRepaymentRequests[i]
                        )
                    }
                mClientSyncIndex = i
                break
            }
        }
    }

    private fun syncLoanRepayment(loanId: Int, loanRepaymentRequest: LoanRepaymentRequest?) {
        mSubscriptions?.add(
            mDataManagerLoan.submitPayment(loanId, loanRepaymentRequest!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanRepaymentResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        showPaymentFailed(errorMessage(e))
                    }

                    override fun onNext(loanRepaymentResponse: LoanRepaymentResponse) {
                        showPaymentSubmittedSuccessfully()
                    }
                })
        )
    }

    fun showPaymentFailed(errorMessage: String?) {
        val loanRepaymentRequest = mLoanRepaymentRequests[mClientSyncIndex]
        loanRepaymentRequest.errorMessage = errorMessage
        updateLoanRepayment(loanRepaymentRequest)
    }

    private fun updateLoanRepayment(loanRepaymentRequest: LoanRepaymentRequest?) {
        mSubscriptions?.add(
            mDataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanRepaymentRequest>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(loanRepaymentRequest: LoanRepaymentRequest) {
                        showLoanRepaymentUpdated(loanRepaymentRequest)
                    }
                })
        )
    }

    fun showLoanRepaymentUpdated(loanRepaymentRequest: LoanRepaymentRequest) {
        mLoanRepaymentRequests[mClientSyncIndex] = loanRepaymentRequest
        mClientSyncIndex += 1
        if (mLoanRepaymentRequests.size != mClientSyncIndex) {
            syncGroupPayload()
        }
    }

    fun showPaymentSubmittedSuccessfully() {
        mLoanRepaymentRequests[mClientSyncIndex].loanId?.let {
            deleteAndUpdateLoanRepayments(
                it
            )
        }
    }

    private fun deleteAndUpdateLoanRepayments(loanId: Int) {
        mSubscriptions?.add(
            mDataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                        showLoanRepaymentDeletedAndUpdateLoanRepayment(
                            loanRepaymentRequests
                        )
                    }
                })
        )
    }

    fun showLoanRepaymentDeletedAndUpdateLoanRepayment(loanRepaymentRequests: List<LoanRepaymentRequest>) {
        mClientSyncIndex = 0
        mLoanRepaymentRequests = loanRepaymentRequests as MutableList<LoanRepaymentRequest>
        if (mLoanRepaymentRequests.size != 0) {
            syncGroupPayload()
        }
    }

    companion object {
        fun schedulePeriodic() {
            JobRequest.Builder(Tags.OfflineSyncLoanRepayment)
                .setPeriodic(
                    TimeUnit.MINUTES.toMillis(15),
                    TimeUnit.MINUTES.toMillis(5)
                )
                .build()
                .schedule()
        }
    }
}