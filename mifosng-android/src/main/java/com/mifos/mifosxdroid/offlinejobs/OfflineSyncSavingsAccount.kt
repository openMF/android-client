package com.mifos.mifosxdroid.offlinejobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
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
class OfflineSyncSavingsAccount : Job() {
    @Inject
    lateinit var mDataManagerSavings: DataManagerSavings

    @JvmField
    @Inject
    var mDataManagerLoan: DataManagerLoan? = null
    private var mSubscriptions: CompositeSubscription? = null
    private var mSavingsAccountTransactionRequests: MutableList<SavingsAccountTransactionRequest> =
        ArrayList()
    private var mTransactionIndex = 0
    override fun onRunJob(params: Params): Result {
        mSubscriptions = CompositeSubscription()
        mSavingsAccountTransactionRequests = ArrayList()
        return if (!userStatus) {
            loadDatabaseSavingsAccountTransactions()
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

    private fun loadDatabaseSavingsAccountTransactions() {
        mSubscriptions!!.add(
            mDataManagerSavings.allSavingsAccountTransactions
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                    override fun onCompleted() {
                        syncNow()
                    }

                    override fun onError(e: Throwable) {}
                    override fun onNext(transactionRequests: List<SavingsAccountTransactionRequest>) {
                        if (transactionRequests.isNotEmpty()) {
                            mSavingsAccountTransactionRequests = transactionRequests.toMutableList()
                        }
                    }
                })
        )
    }

    fun syncNow() {
        if (mSavingsAccountTransactionRequests.isNotEmpty()) {
            mTransactionIndex = 0
            checkErrorAndSync()
        }
    }

    private fun checkErrorAndSync() {
        for (i in mTransactionIndex until mSavingsAccountTransactionRequests.size) {
            if (mSavingsAccountTransactionRequests[i].errorMessage == null) {
                mTransactionIndex = i
                val savingAccountType = mSavingsAccountTransactionRequests[i].savingsAccountType
                val savingAccountId = mSavingsAccountTransactionRequests[i].savingAccountId
                val transactionType = mSavingsAccountTransactionRequests
                    .get(i).transactionType
                if (savingAccountId != null) {
                    processTransaction(
                        savingAccountType, savingAccountId, transactionType,
                        mSavingsAccountTransactionRequests[i]
                    )
                }
                break
            }
        }
    }

    private fun processTransaction(
        type: String?, accountId: Int, transactionType: String?,
        request: SavingsAccountTransactionRequest
    ) {
        mSubscriptions?.add(
            mDataManagerSavings
                .processTransaction(type, accountId, transactionType, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        showTransactionSyncFailed(e.message.toString())
                    }

                    override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse) {
                        showTransactionSyncSuccessfully()
                    }
                })
        )
    }

    fun showTransactionSyncFailed(errorMessage: String?) {
        val transaction = mSavingsAccountTransactionRequests
            .get(mTransactionIndex)
        transaction.errorMessage = errorMessage
        updateSavingsAccountTransaction(transaction)
    }

    private fun updateSavingsAccountTransaction(request: SavingsAccountTransactionRequest) {
        mSubscriptions!!.add(
            mDataManagerSavings.updateLoanRepaymentTransaction(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
                        showTransactionUpdatedSuccessfully(savingsAccountTransactionRequest)
                    }
                })
        )
    }

    fun showTransactionUpdatedSuccessfully(transaction: SavingsAccountTransactionRequest) {
        mSavingsAccountTransactionRequests[mTransactionIndex] = transaction
        mTransactionIndex += 1
        if (mSavingsAccountTransactionRequests.size != mTransactionIndex) {
            syncSavingsAccountTransactions()
        }
    }

    fun showTransactionSyncSuccessfully() {
        mSavingsAccountTransactionRequests[mTransactionIndex].savingAccountId?.let {
            deleteAndUpdateSavingsAccountTransaction(
                it
            )
        }
    }

    private fun deleteAndUpdateSavingsAccountTransaction(savingsAccountId: Int) {
        mSubscriptions!!.add(
            mDataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(savingsAccountTransactionRequests: List<SavingsAccountTransactionRequest>) {
                        showTransactionDeletedAndUpdated(savingsAccountTransactionRequests)
                    }
                })
        )
    }

    fun showTransactionDeletedAndUpdated(transactions: List<SavingsAccountTransactionRequest>) {
        mTransactionIndex = 0
        mSavingsAccountTransactionRequests = transactions.toMutableList()
        if (mSavingsAccountTransactionRequests.isNotEmpty()) {
            syncSavingsAccountTransactions()
        }
    }

    private fun syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.isNotEmpty()) {
            mTransactionIndex = 0
            checkErrorAndSync()
        }
    }

    companion object {
        @JvmStatic
        fun schedulePeriodic() {
            JobRequest.Builder(Tags.OfflineSyncSavingsAccount)
                .setPeriodic(
                    TimeUnit.MINUTES.toMillis(15),
                    TimeUnit.MINUTES.toMillis(5)
                )
                .build()
                .schedule()
        }
    }
}