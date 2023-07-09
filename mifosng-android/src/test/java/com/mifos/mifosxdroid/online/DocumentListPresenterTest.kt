package com.mifos.mifosxdroid.online

import com.mifos.api.datamanager.DataManagerDocument
import com.mifos.mifosxdroid.FakeRemoteDataSource.documents
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.documentlist.DocumentListMvpView
import com.mifos.mifosxdroid.online.documentlist.DocumentListPresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.noncore.Document
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Rajan Maurya on 21/06/16.
 */
@RunWith(MockitoJUnitRunner::class)
class DocumentListPresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var mDocumentListPresenter: DocumentListPresenter

    @Mock
    lateinit var mDataManager: DataManagerDocument

    @Mock
    lateinit var mDocumentListMvpView: DocumentListMvpView
    private lateinit var documentList: List<Document>
    private val entityType = "clients"
    private val entityId = 9

    @Before
    fun setUp() {
        mDocumentListPresenter = DocumentListPresenter(mDataManager)
        mDocumentListPresenter.attachView(mDocumentListMvpView)
        documentList = documents
    }

    @After
    fun tearDown() {
        mDocumentListPresenter.detachView()
    }

    @Test
    fun testLoadDocumentList() {
        stubDataManagerGetDocuments(Observable.just(documentList))
        mDocumentListPresenter.loadDocumentList(entityType, entityId)
        Mockito.verify(mDocumentListMvpView).showDocumentList(documentList)
        Mockito.verify(mDocumentListMvpView, Mockito.never()).showEmptyDocuments()
        Mockito.verify(mDocumentListMvpView, Mockito.never())
            .showFetchingError(R.string.failed_to_fetch_documents)
    }

    @Test
    fun testLoadDocumentEmptyList() {
        val emptyDocuments: List<Document> = ArrayList()
        stubDataManagerGetDocuments(Observable.just(emptyDocuments))
        mDocumentListPresenter.loadDocumentList(entityType, entityId)
        Mockito.verify(mDocumentListMvpView, Mockito.never()).showDocumentList(
            documentList
        )
        Mockito.verify(mDocumentListMvpView, Mockito.never())
            .showFetchingError(R.string.failed_to_fetch_documents)
    }

    @Test
    fun testLoadDocumentListFails() {
        stubDataManagerGetDocuments(Observable.error<Any>(RuntimeException()))
        mDocumentListPresenter.loadDocumentList(entityType, entityId)
        Mockito.verify(mDocumentListMvpView).showFetchingError(R.string.failed_to_fetch_documents)
        Mockito.verify(mDocumentListMvpView, Mockito.never()).showDocumentList(
            documentList
        )
        Mockito.verify(mDocumentListMvpView, Mockito.never()).showEmptyDocuments()
    }

    private fun stubDataManagerGetDocuments(observable: Observable<*>) {
        Mockito.doReturn(observable)
            .`when`(mDataManager)
            .getDocumentsList(entityType, entityId)
    }
}