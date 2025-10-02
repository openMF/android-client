package com.mifos.feature.client.createShareAccount

import ChargesPage
import PreviewPage
import TermsPage
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect

@Composable
internal fun ShareAccountScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShareAccountViewModel = viewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ShareAccountEvent.NavigateBack -> onNavigateBack()
            ShareAccountEvent.Finish -> onFinish()
        }
    }

    ShareAccountScaffold(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )
}

@Composable
private fun ShareAccountScaffold(
    state: ShareAccountState,
    modifier: Modifier = Modifier,
    onAction: (ShareAccountAction) -> Unit,
) {
    // Using literal strings so this file can live in commonMain without Android 'R'
    val steps = remember {
        listOf(
            Step("Details") { DetailsPage(onContinue = { onAction(ShareAccountAction.NextStep) }, state = state) },
            Step("Terms") { TermsPage { onAction(ShareAccountAction.NextStep) } },
            Step("Charges") { ChargesPage { onAction(ShareAccountAction.NextStep) } },
            Step("Preview") { PreviewPage { onAction(ShareAccountAction.Finish) } },
        )
    }

    MifosScaffold(
        title = "Create Share Account", // literal so commonMain is happy
        onBackPressed = { onAction(ShareAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState == null) {
            MifosStepper(
                steps = steps,
                currentIndex = state.currentStep,
                onStepChange = { newIndex ->
                    onAction(ShareAccountAction.OnStepChange(newIndex))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
fun DetailsPage(onContinue: () -> Unit, state: ShareAccountState) {
    TODO("Not yet implemented")
}



