/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.ui.submit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import template.core.base.store.submit.DraftResumeState
import template.core.base.store.submit.MutationUiState
import template.core.base.store.submit.SubmitState
import template.core.base.ui.screen.ScreenContent

/**
 * Convenience composable for edit/mutation screens.
 *
 * Combines [ScreenContent] + [SubmitProgressOverlay] + [SubmitResultHandler] into a single
 * call. The overlay appears automatically while submitting; result callbacks fire on terminal
 * [SubmitState] transitions.
 *
 * ```kotlin
 * MutationScreenContent(
 *     screenState = uiState.screen,
 *     submitState = uiState.submit,
 *     onRetry = viewModel::onRetry,
 *     onSubmitted = { navigateBack() },
 *     onFailed = { _, category -> showError(category) },
 * ) { data, _ ->
 *     EditForm(data = data, enabled = uiState.canInteract, onSave = viewModel::onSave)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    screenState: ScreenState<T>,
    submitState: SubmitState<R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    modifier: Modifier = Modifier,
    onFailed: ((error: Throwable, category: template.core.base.store.error.ErrorCategory) -> Unit)? = null,
    content: @Composable (data: T, freshness: DataFreshness) -> Unit,
) {
    SubmitResultHandler(
        state = submitState,
        onSubmitted = onSubmitted,
        onFailed = onFailed,
    )
    Box(modifier = modifier.fillMaxSize()) {
        ScreenContent(
            state = screenState,
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize(),
            content = content,
        )
        SubmitProgressOverlay(state = submitState)
    }
}

/**
 * [MutationUiState]-typed overload. Reduces boilerplate when the ViewModel exposes a
 * single [MutationUiState] property.
 *
 * ```kotlin
 * MutationScreenContent(
 *     state = uiState.mutation,
 *     onRetry = viewModel::onRetry,
 *     onSubmitted = { navigateBack() },
 * ) { data, _ ->
 *     EditForm(data = data, enabled = uiState.mutation.canInteract, onSave = viewModel::onSave)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    state: MutationUiState<T, R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    modifier: Modifier = Modifier,
    onFailed: ((error: Throwable, category: template.core.base.store.error.ErrorCategory) -> Unit)? = null,
    content: @Composable (data: T, freshness: DataFreshness) -> Unit,
) {
    MutationScreenContent(
        screenState = state.screen,
        submitState = state.submit,
        onRetry = onRetry,
        onSubmitted = onSubmitted,
        modifier = modifier,
        onFailed = onFailed,
        content = content,
    )
}

/**
 * Draft-aware overload of [MutationScreenContent].
 *
 * Shows a [DraftResumeBanner] above the form content when [draftResumeState] is
 * [DraftResumeState.HasDraft]. The banner is omitted when [draftResumeState] is
 * [DraftResumeState.None] (default).
 *
 * ```kotlin
 * MutationScreenContent(
 *     screenState = uiState.screen,
 *     submitState = uiState.submit,
 *     draftResumeState = draftState,
 *     onResumeClick = viewModel::onResumeDraft,
 *     onDiscardClick = viewModel::onDiscardDraft,
 *     onRetry = viewModel::onRetry,
 *     onSubmitted = { navigateBack() },
 * ) { data, _ ->
 *     EditForm(data = data, enabled = uiState.canInteract)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    screenState: ScreenState<T>,
    submitState: SubmitState<R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    draftResumeState: DraftResumeState<*>,
    onResumeClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFailed: ((error: Throwable, category: template.core.base.store.error.ErrorCategory) -> Unit)? = null,
    content: @Composable (data: T, freshness: DataFreshness) -> Unit,
) {
    SubmitResultHandler(
        state = submitState,
        onSubmitted = onSubmitted,
        onFailed = onFailed,
    )
    Column(modifier = modifier.fillMaxSize()) {
        DraftResumeBanner(
            state = draftResumeState,
            onResume = onResumeClick,
            onDiscard = onDiscardClick,
        )
        Box(modifier = Modifier.weight(1f)) {
            ScreenContent(
                state = screenState,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
                content = content,
            )
            SubmitProgressOverlay(state = submitState)
        }
    }
}

/**
 * [MutationUiState]-typed draft-aware overload. Combines read state, write state, and
 * draft resume into a single call.
 */
@Composable
fun <T, R> MutationScreenContent(
    state: MutationUiState<T, R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    draftResumeState: DraftResumeState<*>,
    onResumeClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFailed: ((error: Throwable, category: template.core.base.store.error.ErrorCategory) -> Unit)? = null,
    content: @Composable (data: T, freshness: DataFreshness) -> Unit,
) {
    MutationScreenContent(
        screenState = state.screen,
        submitState = state.submit,
        onRetry = onRetry,
        onSubmitted = onSubmitted,
        draftResumeState = draftResumeState,
        onResumeClick = onResumeClick,
        onDiscardClick = onDiscardClick,
        modifier = modifier,
        onFailed = onFailed,
        content = content,
    )
}
