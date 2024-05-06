package com.mifos.feature.auth.login.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.BaseUrl
import com.mifos.core.common.utils.Network
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.PrefManager
import com.mifos.feature.auth.R
import com.mifos.feature.auth.login.domain.use_case.LoginUseCase
import com.mifos.feature.auth.login.domain.use_case.PasswordValidationUseCase
import com.mifos.feature.auth.login.domain.use_case.UsernameValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.apimanager.BaseApiManager
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefManager: PrefManager,
    private val usernameValidationUseCase: UsernameValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val baseApiManager: BaseApiManager,
    private val loginUseCase: LoginUseCase
) :
    ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState = _loginUiState.asStateFlow()


    fun validateUserInputs(username: String, password: String) {

        val usernameValidationResult = usernameValidationUseCase(username)
        val passwordValidationResult = passwordValidationUseCase(password)

        val hasError =
            listOf(usernameValidationResult, passwordValidationResult).any { !it.success }

        if (hasError) {
            _loginUiState.value = LoginUiState.ShowValidationError(
                usernameValidationResult.message,
                passwordValidationResult.message
            )
            return
        }
        viewModelScope.launch {
            setupPrefManger(username, password)
        }
    }

    private fun setupPrefManger(username: String, password: String) {

        prefManager.setTenant(BaseUrl.TENANT)
        // Saving InstanceURL for next usages
        prefManager.setInstanceUrl(BaseUrl.PROTOCOL_HTTPS + BaseUrl.API_ENDPOINT + BaseUrl.API_PATH)
        // Saving domain name
        prefManager.setInstanceDomain(BaseUrl.API_ENDPOINT)
        // Saving port
        prefManager.setPort(BaseUrl.PORT)
        // Updating Services
        baseApiManager.createService(
            username,
            password,
            prefManager.getInstanceUrl(),
            prefManager.getTenant(),
            true
        )
        if (Network.isOnline(context)) {
            login(username, password)
        } else {
            _loginUiState.value =
                LoginUiState.ShowError(R.string.feature_error_not_connected_internet)
        }
    }


    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(username, password).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _loginUiState.value =
                            LoginUiState.ShowError(R.string.feature_error_login_failed)
                    }

                    is Resource.Loading -> {
                        _loginUiState.value = LoginUiState.ShowProgress
                    }

                    is Resource.Success -> {
                        result.data?.let { onLoginSuccessful(it, username, password) }
                    }
                }
            }
        }
    }


    private fun onLoginSuccessful(
        user: PostAuthenticationResponse,
        username: String,
        password: String
    ) {
        // Saving username password
        prefManager.usernamePassword = Pair(username, password)
        // Saving userID
        prefManager.setUserId(user.userId!!.toInt())
        // Saving user's token
        prefManager.saveToken("Basic " + user.base64EncodedAuthenticationKey)
        // Saving user
        prefManager.savePostAuthenticationResponse(user)

        if (prefManager.getPassCodeStatus()) {
            _loginUiState.value = LoginUiState.HomeActivityIntent
        } else {
            _loginUiState.value = LoginUiState.PassCodeActivityIntent
        }
    }

}