package com.mifos.mifosxdroid.online.savingsaccount

import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.savings.FieldOfficerOptions
import com.mifos.objects.client.Savings
import com.mifos.objects.common.InterestType
import com.mifos.objects.organisation.ProductSavings
import com.mifos.objects.templates.savings.SavingProductsTemplate
import com.mifos.objects.zipmodels.SavingProductsAndTemplate
import com.mifos.services.data.SavingsPayload
import com.mifos.utils.MFErrorParser
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 8/6/16.
 */
class SavingsAccountPresenter @Inject constructor(private val mDataManagerSavings: DataManagerSavings) : BasePresenter<SavingsAccountMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: SavingsAccountMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadSavingsAccountsAndTemplate() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(Observable.combineLatest(
                mDataManagerSavings.savingsAccounts,
                mDataManagerSavings.savingsAccountTemplate
        ) { productSavings, template -> SavingProductsAndTemplate(productSavings, template) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingProductsAndTemplate?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(
                                R.string.failed_to_load_savings_products_and_template)
                    }

                    override fun onNext(productsAndTemplate: SavingProductsAndTemplate?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSavingsAccounts(productsAndTemplate
                                ?.getmProductSavings())
                    }
                })
        )
    }

    fun loadClientSavingAccountTemplateByProduct(clientId: Int, productId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings.getClientSavingsAccountTemplateByProduct(clientId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingProductsTemplate?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_load_savings_products_and_template)
                    }

                    override fun onNext(savingProductsTemplate: SavingProductsTemplate?) {
                        mvpView!!.showProgressbar(false)
                        savingProductsTemplate?.let { mvpView!!.showSavingsAccountTemplateByProduct(it) }
                    }
                }))
    }

    fun loadGroupSavingAccountTemplateByProduct(groupId: Int, productId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings.getGroupSavingsAccountTemplateByProduct(groupId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingProductsTemplate?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_load_savings_products_and_template)
                    }

                    override fun onNext(savingProductsTemplate: SavingProductsTemplate?) {
                        mvpView!!.showProgressbar(false)
                        savingProductsTemplate?.let { mvpView!!.showSavingsAccountTemplateByProduct(it) }
                    }
                }))
    }

    fun createSavingsAccount(savingsPayload: SavingsPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings.createSavingsAccount(savingsPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Savings?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(savings: Savings?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSavingsAccountCreatedSuccessfully(savings)
                    }
                }))
    }

    fun filterSpinnerOptions(interestTypes: List<InterestType>?): List<String> {
        val interestNameList = ArrayList<String>()
        Observable.from(interestTypes)
                .subscribe { interestType -> interestNameList.add(interestType.value) }
        return interestNameList
    }

    fun filterSavingProductsNames(productSavings: List<ProductSavings>?): List<String> {
        val productsNames = ArrayList<String>()
        Observable.from(productSavings)
                .subscribe { product -> productsNames.add(product.name) }
        return productsNames
    }

    fun filterFieldOfficerNames(fieldOfficerOptions: List<FieldOfficerOptions>?): List<String> {
        val fieldOfficerNames = ArrayList<String>()
        Observable.from(fieldOfficerOptions)
                .subscribe { fieldOfficerOptions -> fieldOfficerNames.add(fieldOfficerOptions.displayName) }
        return fieldOfficerNames
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}