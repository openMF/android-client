package com.mifos.feature.passcode.passcode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasscodeViewmodel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val passcodeStatus =
        savedStateHandle.getStateFlow(key = Constants.PASSCODE_INITIAL_LOGIN, initialValue = false)

}