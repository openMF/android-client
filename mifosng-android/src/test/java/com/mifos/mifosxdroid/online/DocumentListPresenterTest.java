package com.mifos.mifosxdroid.online;

import com.mifos.api.datamanager.DataManagerDocument;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.documentlist.DocumentListMvpView;
import com.mifos.mifosxdroid.online.documentlist.DocumentListPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.noncore.Document;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Rajan Maurya on 21/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentListPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    DocumentListPresenter mDocumentListPresenter;

    @Mock
    DataManagerDocument mDataManager;

    @Mock
    DocumentListMvpView mDocumentListMvpView;

    List<Document> documentList;

    private String entityType = "clients";
    private int entityId = 9;

    @Before
    public void setUp() {
        mDocumentListPresenter = new DocumentListPresenter(mDataManager);
        mDocumentListPresenter.attachView(mDocumentListMvpView);

        documentList = FakeRemoteDataSource.getDocuments();
    }

    @After
    public void tearDown() {
        mDocumentListPresenter.detachView();
    }

    @Test
    public void testLoadDocumentList() {
        stubDataManagerGetDocuments(Observable.just(documentList));

        mDocumentListPresenter.loadDocumentList(entityType, entityId);

        verify(mDocumentListMvpView).showDocumentList(documentList);
        verify(mDocumentListMvpView, never()).showEmptyDocuments();
        verify(mDocumentListMvpView, never()).showFetchingError(R.string.failed_to_fetch_documents);
    }

    @Test
    public void testLoadDocumentEmptyList() {
        List<Document> emptyDocuments = new ArrayList<>();
        stubDataManagerGetDocuments(Observable.just(emptyDocuments));

        mDocumentListPresenter.loadDocumentList(entityType, entityId);
        
        verify(mDocumentListMvpView, never()).showDocumentList(documentList);
        verify(mDocumentListMvpView, never()).showFetchingError(R.string.failed_to_fetch_documents);
    }

    @Test
    public void testLoadDocumentListFails() {
        stubDataManagerGetDocuments(Observable.error(new RuntimeException()));

        mDocumentListPresenter.loadDocumentList(entityType, entityId);

        verify(mDocumentListMvpView).showFetchingError(R.string.failed_to_fetch_documents);
        verify(mDocumentListMvpView, never()).showDocumentList(documentList);
        verify(mDocumentListMvpView, never()).showEmptyDocuments();
    }

    private void stubDataManagerGetDocuments(Observable observable) {
        doReturn(observable)
                .when(mDataManager)
                .getDocumentsList(entityType, entityId);
    }
}