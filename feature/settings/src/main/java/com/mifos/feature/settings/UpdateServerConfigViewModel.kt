package com.mifos.feature.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.ServerConfigPrefManager
import com.mifos.core.domain.use_cases.ServerConfigValidatorUseCase
import com.mifos.core.model.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mifos.core.apimanager.BaseApiManager
import javax.inject.Inject

@HiltViewModel
class UpdateServerConfigViewModel @Inject constructor(
    private val prefManager: ServerConfigPrefManager,
    private val validator: ServerConfigValidatorUseCase,
    private val baseApiManager: BaseApiManager,
) : ViewModel() {

    private val serverConfig = prefManager.getServerConfig

    private val _state = mutableStateOf(serverConfig)
    val state: State<ServerConfig> get() = _state

    val protocolError = snapshotFlow { _state.value.protocol }.mapLatest {
        validator.validateServerProtocol(it).message
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val endPointError = snapshotFlow { _state.value.endPoint }.mapLatest {
        validator.validateEndPoint(it).message
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val apiPathError = snapshotFlow { _state.value.apiPath }.mapLatest {
        validator.validateApiPath(it).message
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val portError = snapshotFlow { _state.value.port }.mapLatest {
        validator.validatePort(it).message
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val tenantError = snapshotFlow { _state.value.tenant }.mapLatest {
        validator.validateTenant(it).message
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )


    fun onEvent(event: UpdateServerConfigEvent) {
        when (event) {
            is UpdateServerConfigEvent.UpdateProtocol -> {
                _state.value = _state.value.copy(
                    protocol = event.protocol
                )
            }

            is UpdateServerConfigEvent.UpdateEndPoint -> {
                _state.value = _state.value.copy(
                    endPoint = event.endPoint
                )
            }

            is UpdateServerConfigEvent.UpdateApiPath -> {
                _state.value = _state.value.copy(
                    apiPath = event.apiPath
                )
            }

            is UpdateServerConfigEvent.UpdatePort -> {
                _state.value = _state.value.copy(
                    port = event.port
                )
            }

            is UpdateServerConfigEvent.UpdateTenant -> {
                _state.value = _state.value.copy(
                    tenant = event.tenant
                )
            }

            is UpdateServerConfigEvent.UpdateServerConfig -> {
                viewModelScope.launch {
                    val hasAnyError = listOf(
                        protocolError,
                        apiPathError,
                        endPointError,
                        portError,
                        tenantError
                    ).any { it.value != null }

                    if (!hasAnyError) {
                        prefManager.updateServerConfig(_state.value)
                    }
                }
            }

            is UpdateServerConfigEvent.DeleteServerConfig -> {
                viewModelScope.launch {
                    prefManager.updateServerConfig(config = null)
                }
            }
        }
    }
}