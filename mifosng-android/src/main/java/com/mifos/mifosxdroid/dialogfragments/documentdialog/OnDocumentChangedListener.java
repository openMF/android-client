package com.mifos.mifosxdroid.dialogfragments.documentdialog;

import com.mifos.objects.noncore.Document;

/**
 * Created by Tarun on 01-09-17.
 */

public interface OnDocumentChangedListener {

    void onDocumentCreationSuccess(Document document);

    void onDocumentUpdateSuccess(Document document);

    void onDocumentChangeFailure(String errorMessage, int type);

}
