package com.mifos.mifosxdroid.online.centerdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class CenterDetailsViewModel @Inject constructor(
    private val repository: CenterDetailsRepository
) : ViewModel() {

    private val _centerDetailsUiState = MutableLiveData<CenterDetailsUiState>()

    val centerDetailsUiState: LiveData<CenterDetailsUiState>
        get() = _centerDetailsUiState

    fun loadCentersGroupAndMeeting(centerId: Int) {
        _centerDetailsUiState.value = CenterDetailsUiState.ShowProgressbar(true)
        repository.getCentersGroupAndMeeting(centerId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _centerDetailsUiState.value =
                        CenterDetailsUiState.ShowErrorMessage(R.string.failed_to_fetch_Group_and_meeting)
                }

                override fun onNext(centerWithAssociations: CenterWithAssociations) {
                    _centerDetailsUiState.value =
                        CenterDetailsUiState.ShowMeetingDetails(centerWithAssociations)
                    _centerDetailsUiState.value =
                        CenterDetailsUiState.ShowCenterDetails(centerWithAssociations)
                }
            })
    }

    fun loadSummaryInfo(centerId: Int) {
        _centerDetailsUiState.value = CenterDetailsUiState.ShowProgressbar(true)
        repository.getCenterSummaryInfo(centerId, false)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<CenterInfo>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _centerDetailsUiState.value =
                        CenterDetailsUiState.ShowErrorMessage(R.string.failed_to_fetch_center_info)
                }

                override fun onNext(centerInfos: List<CenterInfo>) {
                    _centerDetailsUiState.value = CenterDetailsUiState.ShowSummaryInfo(centerInfos)
                }
            })
    }

}