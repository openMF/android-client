package com.mifos.mifosxdroid.online.createnewcenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CreateNewCenterViewModel @Inject constructor(private val repository: CreateNewCenterRepository) :
    ViewModel() {

    private val _createNewCenterUiState = MutableLiveData<CreateNewCenterUiState>()

    val createNewCenterUiState: LiveData<CreateNewCenterUiState>
        get() = _createNewCenterUiState

    fun loadOffices() {
        _createNewCenterUiState.value = CreateNewCenterUiState.ShowProgressbar
        repository.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.ShowFetchingError(R.string.failed_to_fetch_offices)
                }

                override fun onNext(offices: List<Office>) {
                    _createNewCenterUiState.value = CreateNewCenterUiState.ShowOffices(offices)
                }
            })
    }

    fun createCenter(centerPayload: CenterPayload) {
        _createNewCenterUiState.value = CreateNewCenterUiState.ShowProgressbar
        repository.createCenter(centerPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.ShowFetchingErrorString(e.message.toString())
                }

                override fun onNext(saveResponse: SaveResponse) {
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.CenterCreatedSuccessfully(saveResponse)
                }
            })
    }
}