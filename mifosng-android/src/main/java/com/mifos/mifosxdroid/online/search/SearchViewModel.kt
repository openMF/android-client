package com.mifos.mifosxdroid.online.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.SearchedEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    private val _searchUiState = MutableLiveData<SearchUiState>()

    val searchUiState: LiveData<SearchUiState>
        get() = _searchUiState

    fun searchResources(query: String?, resources: String?, exactMatch: Boolean?) {
        _searchUiState.value = SearchUiState.ShowProgress(true)
        repository.searchResources(query, resources, exactMatch)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<SearchedEntity>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _searchUiState.value = SearchUiState.ShowError(e.message.toString())
                }

                override fun onNext(searchedEntities: List<SearchedEntity>) {
                    if (searchedEntities.isEmpty()) {
                        _searchUiState.value = SearchUiState.ShowNoResultFound
                    } else {
                        _searchUiState.value = SearchUiState.ShowSearchedResources(searchedEntities)
                    }
                }
            })
    }
}