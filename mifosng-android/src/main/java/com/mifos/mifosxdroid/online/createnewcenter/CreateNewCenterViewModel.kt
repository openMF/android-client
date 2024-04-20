package com.mifos.mifosxdroid.online.createnewcenter

import androidx.lifecycle.ViewModel
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import com.mifos.utils.ValidationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CreateNewCenterViewModel @Inject constructor(
    private val repository: CreateNewCenterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateNewCenterUiState())

    val uiState: StateFlow<CreateNewCenterUiState>
        get() = _uiState.asStateFlow()

    init {
        loadOffices()
    }

    private fun loadOffices() {
        _uiState.value = CreateNewCenterUiState(isLoading = true)
        repository.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _uiState.value =
                        CreateNewCenterUiState(message = "Failed to fetch office")
                }

                override fun onNext(offices: List<Office>) {
                    if (offices.isEmpty()) {
                        _uiState.value =
                            CreateNewCenterUiState(message = "Failed to fetch office")
                    } else {
                        _uiState.value = CreateNewCenterUiState(offices = offices)
                    }
                }
            })
    }

    private fun createCenter(centerPayload: CenterPayload) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        repository.createCenter(centerPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _uiState.value = _uiState.value.copy(message = e.message.toString(), isLoading = false)
                }

                override fun onNext(saveResponse: SaveResponse) {
                    _uiState.value = _uiState.value.copy(message = "Center created successfully", isLoading = false)
                }
            })
    }

    fun initiateCenterCreation(centerPayload: CenterPayload) {
        if (isCenterNameValid(centerPayload.name)) {
            createCenter(centerPayload)
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun resetErrorMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun isCenterNameValid(name: String?): Boolean {
        if (name.isNullOrEmpty()) {
            _uiState.value = _uiState.value.copy(message = "Center name cannot be empty")
            return false
        }
        if (name.trim().length < 4) {
            _uiState.value =
                _uiState.value.copy(message = "Center name cannot be less than 4 characters")
            return false
        }
        if (!ValidationUtil.isNameValid(name)) {
            _uiState.value =
                _uiState.value.copy(message = "Center name should contain only alphabets")
            return false
        }
        return true
    }

}