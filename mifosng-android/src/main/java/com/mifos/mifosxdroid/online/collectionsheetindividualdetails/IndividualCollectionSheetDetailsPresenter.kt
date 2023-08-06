package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import com.mifos.api.DataManager
import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerCollectionSheet
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.PaymentTypeOptions
import com.mifos.objects.collectionsheet.ClientCollectionSheet
import com.mifos.objects.collectionsheet.LoanAndClientName
import com.mifos.utils.MFErrorParser
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by aksh on 20/6/18.
 */
class IndividualCollectionSheetDetailsPresenter @Inject internal constructor(private val mDataManager: DataManager,
                                                                             private val mDataManagerCollection: DataManagerCollectionSheet) : BasePresenter<IndividualCollectionSheetDetailsMvpView?>() {
    private val mSubscription: CompositeSubscription?
    override fun attachView(mvpView: IndividualCollectionSheetDetailsMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscription?.unsubscribe()
    }

    fun submitIndividualCollectionSheet(individualCollectionSheetPayload: IndividualCollectionSheetPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription!!.add(mDataManagerCollection
                .saveIndividualCollectionSheet(individualCollectionSheetPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                        ?.string()
                                mvpView!!.showError(MFErrorParser.parseError(errorMessage)
                                        .errors[0].defaultUserMessage)
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSuccess()
                    }
                }))
    }

    fun filterPaymentTypeOptions(paymentTypeOptionsList: List<PaymentTypeOptions>?): List<String> {
        val paymentList: MutableList<String> = ArrayList()
        Observable.from(paymentTypeOptionsList)
                .subscribe { paymentTypeOption -> paymentTypeOption.name?.let { paymentList.add(it) } }
        return paymentList
    }

    fun filterLoanAndClientNames(clientCollectionSheets: List<ClientCollectionSheet>?): List<LoanAndClientName> {
        val loansAndClientNames: MutableList<LoanAndClientName> = ArrayList()
        Observable.from(clientCollectionSheets)
                .subscribe { clientCollectionSheet ->
                    if (clientCollectionSheet.loans != null) {
                        for (loanCollectionSheet in clientCollectionSheet.loans!!) {
                            loansAndClientNames.add(LoanAndClientName(loanCollectionSheet,
                                    clientCollectionSheet.clientName,
                                    clientCollectionSheet.clientId))
                        }
                    }
                }
        return loansAndClientNames
    }

    init {
        mSubscription = CompositeSubscription()
    }
}