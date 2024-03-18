package com.mifos.mifosxdroid.online.collectionsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.db.CollectionSheet
import com.mifos.core.objects.response.SaveResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CollectionSheetViewModel @Inject constructor(private val repository: CollectionSheetRepository) :
    ViewModel() {

    private val _collectionSheetUiState = MutableLiveData<CollectionSheetUiState>()

    val collectionSheetUiState: LiveData<CollectionSheetUiState>
        get() = _collectionSheetUiState


    fun loadCollectionSheet(id: Long, payload: Payload?) {
        repository.getCollectionSheet(id, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CollectionSheet?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _collectionSheetUiState.value =
                        CollectionSheetUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(collectionSheet: CollectionSheet?) {
                    _collectionSheetUiState.value = collectionSheet?.let {
                        CollectionSheetUiState.ShowCollectionSheet(
                            it
                        )
                    }
                }
            })
    }

    fun saveCollectionSheet(id: Int, payload: CollectionSheetPayload?) {
        repository.saveCollectionSheetAsync(id, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        _collectionSheetUiState.value =
                            CollectionSheetUiState.ShowFailedToSaveCollectionSheet(e)
                    }
                }

                override fun onNext(saveResponse: SaveResponse?) {
                    _collectionSheetUiState.value =
                        CollectionSheetUiState.ShowCollectionSheetSuccessfullySaved(saveResponse)
                }
            })
    }
}