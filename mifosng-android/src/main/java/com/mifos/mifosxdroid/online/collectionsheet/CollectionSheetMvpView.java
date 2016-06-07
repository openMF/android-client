package com.mifos.mifosxdroid.online.collectionsheet;

import com.mifos.api.model.SaveResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.db.CollectionSheet;

import retrofit.client.Response;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public interface CollectionSheetMvpView extends MvpView {

    void showCollectionSheet(CollectionSheet collectionSheet);

    void showCollectionSheetSuccessfullySaved(SaveResponse saveResponse);

    void showFailedToSaveCollectionSheet(Response response);

    void showFetchingError(String s);
}
