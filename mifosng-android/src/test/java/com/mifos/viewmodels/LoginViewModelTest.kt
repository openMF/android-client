package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.mifosxdroid.activity.login.LoginRepository
import com.mifos.mifosxdroid.activity.login.LoginUiState
import com.mifos.mifosxdroid.activity.login.LoginViewModel
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Aditya Gupta on 02/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var loginRepository: LoginRepository

    @Mock
    lateinit var loginUiStateObserver: Observer<LoginUiState>

    private lateinit var loginViewModel: LoginViewModel

    @Mock
    private lateinit var mockUser: PostAuthenticationResponse


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(loginRepository)
        loginViewModel.loginUiState.observeForever(loginUiStateObserver)
    }

    @Test
    fun testLogin_SuccessfulLoginReceivedFromRepository_ReturnsLoginSuccess() {

        Mockito.`when`(loginRepository.login(Mockito.anyString(), Mockito.anyString())).thenReturn(
            Observable.just(mockUser)
        )
        loginViewModel.login("username", "password")
        Mockito.verify(loginUiStateObserver).onChanged(LoginUiState.ShowProgress(true))
        Mockito.verify(loginUiStateObserver).onChanged(LoginUiState.ShowLoginSuccessful(mockUser))
        Mockito.verify(loginUiStateObserver, Mockito.never())
            .onChanged(LoginUiState.ShowError("Some error message"))
        Mockito.verifyNoMoreInteractions(loginUiStateObserver)
    }

    @Test
    fun testLogin_unsuccessfulLoginReceivedFromRepository_ReturnError() {

        Mockito.`when`(loginRepository.login(Mockito.anyString(), Mockito.anyString())).thenReturn(
            Observable.error(RuntimeException("Some error message"))
        )
        loginViewModel.login("username", "password")
        Mockito.verify(loginUiStateObserver).onChanged(LoginUiState.ShowProgress(true))
        Mockito.verify(loginUiStateObserver).onChanged(LoginUiState.ShowError("Some error message"))
        Mockito.verify(loginUiStateObserver, Mockito.never())
            .onChanged(LoginUiState.ShowLoginSuccessful(mockUser))
        Mockito.verifyNoMoreInteractions(loginUiStateObserver)
    }

    @After
    fun tearDown() {
        loginViewModel.loginUiState.removeObserver(loginUiStateObserver)
    }
}