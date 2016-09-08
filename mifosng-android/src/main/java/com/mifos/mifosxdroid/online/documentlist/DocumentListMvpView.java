package com.mifos.mifosxdroid.online.documentlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.Document;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface DocumentListMvpView extends MvpView {

    void showDocumentList(List<Document> documents);

    void showFetchingError(int errorMessage);
}
