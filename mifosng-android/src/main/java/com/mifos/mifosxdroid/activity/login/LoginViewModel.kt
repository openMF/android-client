package com.mifos.mifosxdroid.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.apache.fineract.client.models.PostAuthenticationResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    private val _loginUiState = MutableLiveData<LoginUiState>()
    val loginUiState: LiveData<LoginUiState>
        get() = _loginUiState

    fun login(username: String, password: String) {
        _loginUiState.value = LoginUiState.ShowProgress(true)
        loginRepository.login(username, password)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(object : Subscriber<PostAuthenticationResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _loginUiState.value = LoginUiState.ShowError(e.message.toString())
                }

                override fun onNext(user: PostAuthenticationResponse) {
                    _loginUiState.value = LoginUiState.ShowLoginSuccessful(user)
                }
            })
    }
}