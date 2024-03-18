package com.mifos.mifosxdroid.activity.pathtracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.user.UserLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class PathTrackingViewModel @Inject constructor(private val repository: PathTrackingRepository) :
    ViewModel() {

    private val _pathTrackingUiState = MutableLiveData<PathTrackingUiState>()

    val pathTrackingUiState: LiveData<PathTrackingUiState>
        get() = _pathTrackingUiState

    fun loadPathTracking(userId: Int) {
        _pathTrackingUiState.value = PathTrackingUiState.ShowProgress(true)
        repository.getUserPathTracking(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<UserLocation>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _pathTrackingUiState.value = PathTrackingUiState.ShowError
                }

                override fun onNext(userLocations: List<UserLocation>) {
                    if (userLocations.isNotEmpty()) {
                        _pathTrackingUiState.value =
                            PathTrackingUiState.ShowPathTracking(userLocations)
                    } else {
                        _pathTrackingUiState.value = PathTrackingUiState.ShowEmptyPathTracking
                    }
                }
            })
    }

}