package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.noncore.DocumentType
import com.mifos.objects.noncore.IdentifierCreationResponse
import com.mifos.objects.noncore.IdentifierPayload
import com.mifos.objects.noncore.IdentifierTemplate
import com.mifos.utils.MFErrorParser.parseError
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 01/10/16.
 */
class IdentifierDialogPresenter @Inject constructor(private val mDataManagerClient: DataManagerClient) :
    BasePresenter<IdentifierDialogMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: IdentifierDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun loadClientIdentifierTemplate(clientId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerClient.getClientIdentifierTemplate(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<IdentifierTemplate>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_fetch_identifier_template)
                    }

                    override fun onNext(identifierTemplate: IdentifierTemplate) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showClientIdentifierTemplate(identifierTemplate)
                    }
                })
        )
    }

    fun createClientIdentifier(clientId: Int, identifierPayload: IdentifierPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerClient.createClientIdentifier(clientId, identifierPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<IdentifierCreationResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                    ?.string()
                                mvpView?.showErrorMessage(
                                    parseError(errorMessage)
                                        .errors[0].defaultUserMessage
                                )
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(identifierCreationResponse: IdentifierCreationResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showIdentifierCreatedSuccessfully(identifierCreationResponse)
                    }
                })
        )
    }

    fun getIdentifierDocumentTypeNames(documentTypes: List<DocumentType>): List<String> {
        val documentTypeList = ArrayList<String>()
        Observable.from(documentTypes)
            .subscribe { documentType -> documentType.name?.let { documentTypeList.add(it) } }
        return documentTypeList
    }

    /**
     * Method to map Document Type with the corresponding name.
     * @param documentTypeList List of DocumentType
     * @return HashMap of <Name></Name>,DocumentType>
     */
    fun mapDocumentTypesWithName(documentTypeList: List<DocumentType>): HashMap<String, DocumentType> {
        val hashMap = HashMap<String, DocumentType>()
        Observable.from(documentTypeList)
            .subscribe { documentType -> hashMap[documentType.name!!] = documentType }
        return hashMap
    }
}