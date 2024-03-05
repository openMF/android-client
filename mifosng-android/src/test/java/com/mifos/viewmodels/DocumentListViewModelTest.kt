package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.documentlist.DocumentListRepository
import com.mifos.mifosxdroid.online.documentlist.DocumentListUiState
import com.mifos.mifosxdroid.online.documentlist.DocumentListViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Aditya Gupta on 06/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class DocumentListViewModelTest {

    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var documentListRepository: DocumentListRepository

    private lateinit var documentListViewModel: DocumentListViewModel

    @Mock
    private lateinit var documentListObserver: Observer<DocumentListUiState>

    @Mock
    private lateinit var mockDocumentList: List<Document>

    @Mock
    private lateinit var responseBody: ResponseBody

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        documentListViewModel = DocumentListViewModel(documentListRepository)
        documentListViewModel.documentListUiState.observeForever(documentListObserver)
    }


    @Test
    fun testDocumentList_SuccessfulDocumentListReceivedFromRepository_ReturnsDocumentList() {
        Mockito.`when`(
            documentListRepository.getDocumentsList(
                Mockito.anyString(),
                Mockito.anyInt()
            )
        )
            .thenReturn(Observable.just(mockDocumentList))
        documentListViewModel.loadDocumentList("type", 1)
        Mockito.verify(documentListObserver).onChanged(DocumentListUiState.ShowProgressbar)
        Mockito.verify(documentListObserver, Mockito.never())
            .onChanged(DocumentListUiState.ShowFetchingError(R.string.failed_to_fetch_documents))
        Mockito.verify(documentListObserver)
            .onChanged(DocumentListUiState.ShowDocumentList(mockDocumentList))
        Mockito.verifyNoMoreInteractions(documentListObserver)
    }

    @Test
    fun testDocumentList_UnsuccessfulDocumentListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            documentListRepository.getDocumentsList(
                Mockito.anyString(),
                Mockito.anyInt()
            )
        )
            .thenReturn(Observable.error(RuntimeException("some error message")))
        documentListViewModel.loadDocumentList("type", 1)
        Mockito.verify(documentListObserver).onChanged(DocumentListUiState.ShowProgressbar)
        Mockito.verify(documentListObserver)
            .onChanged(DocumentListUiState.ShowFetchingError(R.string.failed_to_fetch_documents))
        Mockito.verify(documentListObserver, Mockito.never())
            .onChanged(DocumentListUiState.ShowDocumentList(mockDocumentList))
        Mockito.verifyNoMoreInteractions(documentListObserver)
    }

    @Test
    fun testDownloadDocument_SuccessfulDownloadDocumentFromRepository_ReturnsResponse() {
        Mockito.`when`(
            documentListRepository.downloadDocument(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        )
            .thenReturn(Observable.just(responseBody))
        documentListViewModel.downloadDocument("type", 1, 1)
        Mockito.verify(documentListObserver).onChanged(DocumentListUiState.ShowProgressbar)
        Mockito.verify(documentListObserver, Mockito.never())
            .onChanged(DocumentListUiState.ShowFetchingError(R.string.failed_to_fetch_documents))
        Mockito.verify(documentListObserver)
            .onChanged(DocumentListUiState.ShowDocumentFetchSuccessfully(responseBody))
        Mockito.verifyNoMoreInteractions(documentListObserver)
    }

    @Test
    fun testDownloadDocument_UnsuccessfulDownloadDocumentFromRepository_ReturnsError() {
        Mockito.`when`(
            documentListRepository.downloadDocument(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        )
            .thenReturn(Observable.error(RuntimeException("some error message")))
        documentListViewModel.downloadDocument("type", 1, 1)
        Mockito.verify(documentListObserver).onChanged(DocumentListUiState.ShowProgressbar)
        Mockito.verify(documentListObserver)
            .onChanged(DocumentListUiState.ShowFetchingError(R.string.failed_to_fetch_documents))
        Mockito.verify(documentListObserver, Mockito.never())
            .onChanged(DocumentListUiState.ShowDocumentFetchSuccessfully(responseBody))
        Mockito.verifyNoMoreInteractions(documentListObserver)
    }

    @Test
    fun testRemoveDocument_UnsuccessfulRemoveDocumentReceivedFromRepository_ReturnsRemoveDocument() {
        Mockito.`when`(
            documentListRepository.removeDocument(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        )
            .thenReturn(Observable.just(GenericResponse()))
        documentListViewModel.removeDocument("type", 1, 1)
        Mockito.verify(documentListObserver).onChanged(DocumentListUiState.ShowProgressbar)
        Mockito.verify(documentListObserver, Mockito.never())
            .onChanged(DocumentListUiState.ShowFetchingError(R.string.failed_to_remove_document))
        Mockito.verify(documentListObserver)
            .onChanged(DocumentListUiState.ShowDocumentRemovedSuccessfully)
        Mockito.verifyNoMoreInteractions(documentListObserver)
    }

    @Test
    fun testRemoveDocument_UnsuccessfulRemoveDocumentReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            documentListRepository.removeDocument(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        )
            .thenReturn(Observable.error(RuntimeException("some error message")))
        documentListViewModel.removeDocument("type", 1, 1)
        Mockito.verify(documentListObserver).onChanged(DocumentListUiState.ShowProgressbar)
        Mockito.verify(documentListObserver)
            .onChanged(DocumentListUiState.ShowFetchingError(R.string.failed_to_remove_document))
        Mockito.verify(documentListObserver, Mockito.never())
            .onChanged(DocumentListUiState.ShowDocumentRemovedSuccessfully)
        Mockito.verifyNoMoreInteractions(documentListObserver)
    }

    @After
    fun tearDown() {
        documentListViewModel.documentListUiState.removeObserver(documentListObserver)
    }
}