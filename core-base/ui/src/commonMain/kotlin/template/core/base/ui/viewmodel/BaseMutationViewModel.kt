/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import template.core.base.store.screen.ScreenState
import template.core.base.store.submit.MutationUiState
import template.core.base.store.submit.SubmitState
import template.core.base.store.submit.submitHandler

/**
 * Optional base class for edit-screen ViewModels that combine a read stream with a form submit.
 *
 * Provides [uiState] (a [MutationUiState] combining screen + submit state), and three
 * no-argument handlers ([onSubmit], [onRetry], [onDismissResult]) that delegate to the
 * internal [submitHandler].
 *
 * Subclasses override [performSubmit] to provide the actual network/repository call.
 *
 * Usage:
 * ```kotlin
 * class EditClientViewModel(
 *     private val repo: ClientRepository,
 *     private val clientId: String,
 * ) : BaseMutationViewModel<ClientDetail, Unit, EditClientEvent>() {
 *
 *     init {
 *         viewModelScope.launch {
 *             mutableScreenState.value = try {
 *                 ScreenState.Content(repo.getClient(clientId), DataFreshness.FRESH)
 *             } catch (e: Exception) {
 *                 ScreenState.Error(e)
 *             }
 *         }
 *     }
 *
 *     override suspend fun performSubmit(payload: ClientDetail): Unit =
 *         repo.updateClient(clientId, payload)
 * }
 * ```
 *
 * @param T Domain type loaded for display.
 * @param R Result type returned by the server on a successful submit.
 */
abstract class BaseMutationViewModel<T, R> : ViewModel() {

    private val submitHandler = viewModelScope.submitHandler<R>()

    protected val mutableScreenState: MutableStateFlow<ScreenState<T>> =
        MutableStateFlow(ScreenState.Loading)

    val uiState: StateFlow<MutationUiState<T, R>> = combine(
        mutableScreenState,
        submitHandler.state,
    ) { screen, submit ->
        MutationUiState(screen = screen, submit = submit)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MutationUiState(),
    )

    /** Override to provide the network/repository call. Called with the current form payload. */
    protected abstract suspend fun performSubmit(payload: T): R

    /** Trigger a form submission. No-op if already [SubmitState.Submitting]. */
    fun onSubmit(payload: T) = submitHandler.submit { performSubmit(payload) }

    /** Retry the last failed submission. */
    fun onRetry() = submitHandler.retry()

    /** Dismiss the result overlay and return to [SubmitState.Idle]. */
    fun onDismissResult() = submitHandler.reset()
}
