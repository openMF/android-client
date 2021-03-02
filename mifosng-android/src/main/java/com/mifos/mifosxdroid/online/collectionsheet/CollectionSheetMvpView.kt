package com.mifos.mifosxdroid.online.collectionsheet

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.db.CollectionSheet
import com.mifos.objects.response.SaveResponse
import retrofit2.adapter.rxjava.HttpException

/**
 * Created by Rajan Maurya on 7/6/16.
 */
interface CollectionSheetMvpView : MvpView {
    fun showCollectionSheet(collectionSheet: CollectionSheet)
    fun showCollectionSheetSuccessfullySaved(saveResponse: SaveResponse?)
    fun showFailedToSaveCollectionSheet(response: HttpException?)
    fun showFetchingError(s: String?)
}