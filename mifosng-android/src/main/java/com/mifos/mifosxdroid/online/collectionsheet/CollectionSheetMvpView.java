package com.mifos.mifosxdroid.online.collectionsheet;

import com.mifos.objects.response.SaveResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.db.CollectionSheet;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public interface CollectionSheetMvpView extends MvpView {

    void showCollectionSheet(CollectionSheet collectionSheet);

    void showCollectionSheetSuccessfullySaved(SaveResponse saveResponse);

    void showFailedToSaveCollectionSheet(HttpException response);

    void showFetchingError(String s);
}
