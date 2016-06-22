package com.mifos.mifosxdroid.online;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
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

import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rajan Maurya on 21/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentListPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    DocumentListPresenter mDocumentListPresenter;

    @Mock
    DataManager mDataManager;

    @Mock
    DocumentListMvpView mDocumentListMvpView;

    List<Document> documentList;

    private String entityType = "clients";
    private int entityId = 9;

    @Before
    public void setUp() {
        mDocumentListPresenter = new DocumentListPresenter(mDataManager);
        mDocumentListPresenter.attachView(mDocumentListMvpView);

        documentList = FakeRemoteDataSource.getDouments();
    }

    @After
    public void tearDown() {
        mDocumentListPresenter.detachView();
    }

    @Test
    public void testLoadDocumentList() {
        when(mDataManager.getDocumentsList(entityType, entityId))
                .thenReturn(Observable.just(documentList));

        mDocumentListPresenter.loadDocumentList(entityType, entityId);

        verify(mDocumentListMvpView).showDocumentList(documentList);
        verify(mDocumentListMvpView, never()).showFetchingError("Failed to fetch documents");
    }

    @Test
    public void testLoadDocumentListFails() {
        when(mDataManager.getDocumentsList(entityType, entityId))
                .thenReturn(Observable.<List<Document>>error(new RuntimeException()));

        mDocumentListPresenter.loadDocumentList(entityType, entityId);

        verify(mDocumentListMvpView).showFetchingError("Failed to fetch documents");
        verify(mDocumentListMvpView, never()).showDocumentList(documentList);
    }

}