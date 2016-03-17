package com.mifos.mifosxdroid.online.collectionsheetfragment;

import com.mifos.api.model.SaveResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.db.CollectionSheet;


/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface CollectionSheetMvpView extends MvpView {

    void showcollectionsheet(CollectionSheet collectionSheet);

    void showFetchToFailedCollectionsheet();

    void showsaveCollectionsheetResponse(SaveResponse saveResponse);

    void showsavecaollectionsheelfailed();
}
