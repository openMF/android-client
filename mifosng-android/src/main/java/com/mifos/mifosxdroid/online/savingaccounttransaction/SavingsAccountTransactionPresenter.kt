package com.mifos.mifosxdroid.online.savingaccounttransaction

import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 07/06/16.
 */
class SavingsAccountTransactionPresenter @Inject constructor(private val mDataManagerSavings: DataManagerSavings) : BasePresenter<SavingsAccountTransactionMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    override fun attachView(mvpView: SavingsAccountTransactionMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadSavingAccountTemplate(type: String?, accountId: Int, transactionType: String?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings
                .getSavingsAccountTransactionTemplate(type, accountId, transactionType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionTemplate>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_fetch_savings_template)
                    }

                    override fun onNext(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showSavingAccountTemplate(savingsAccountTransactionTemplate)
                    }
                }))
    }

    fun processTransaction(type: String?, accountId: Int, transactionType: String?,
                           request: SavingsAccountTransactionRequest?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings
                .processTransaction(type, accountId, transactionType, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.transaction_failed)
                    }

                    override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse?) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showTransactionSuccessfullyDone(savingsAccountTransactionResponse)
                    }
                }))
    }

    fun checkInDatabaseSavingAccountTransaction(savingAccountId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings.getSavingsAccountTransaction(savingAccountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_load_savingaccounttransaction)
                    }

                    override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showSavingAccountTransactionExistInDatabase()
                    }
                })
        )
    }

}