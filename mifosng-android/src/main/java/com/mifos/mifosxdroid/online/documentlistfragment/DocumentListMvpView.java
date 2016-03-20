package com.mifos.mifosxdroid.online.documentlistfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.noncore.Document;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface DocumentListMvpView extends MvpView {

    void showDocumentList(List<Document> documents);

    void ResponseErrorDocumentList(String s);
}
