package com.mifos.feature.client.NewFixedDepositAccount

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.NewFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.core.ui.components.MifosStepper

import com.mifos.core.ui.components.Step
import com.mifos.feature.client.newFixedDepositAccount.Pages.TermsPage
import com.mifos.feature.client.newFixedDepositAccount.Pages.ChargesPage
import com.mifos.feature.client.newFixedDepositAccount.Pages.DetailsPage
import com.mifos.feature.client.newFixedDepositAccount.Pages.InterestPage
import com.mifos.feature.client.newFixedDepositAccount.Pages.SettingPage

@Composable
internal fun FixedDepositAccountScreen(
    onNavigateBack : () -> Unit,
    onFinish : () -> Unit,
    modifier : Modifier = Modifier,
    viewModel: NewFixedDepositAccountViewmodel = viewModel()
){
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel.eventFlow){ event ->
        when (event){
            NewFixedDepositAccountEvent.NavigateBack -> onNavigateBack()
            NewFixedDepositAccountEvent.Finish -> onFinish()

        }

    }
    FixeDepositAccountScaffold(
        state = state,
        onAction =  { viewModel.trySendAction(it) },
        modifier = modifier
    )



}
@Composable
private fun FixeDepositAccountScaffold(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
    modifier: Modifier = Modifier

){
    val steps = remember {
        listOf(
            Step(name = "Details") {
                DetailsPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
            Step(name = "Terms") {
                TermsPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },

            Step(name = "Settings") {
                SettingPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
            Step(name = "Interest") {
                InterestPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
            Step(name = "Charges") {
                ChargesPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
        )
    }

    MifosScaffold(
        title = "New Fixed Deposit Account",
        onBackPressed = { onAction(NewFixedDepositAccountAction.NavigateBack) },
        modifier = modifier

    ) { paddingValues ->
        if (state.dialogState == null) {
            MifosStepper(
                steps = steps,
                currentIndex = state.currentStep,
                onStepChange = { newIndex ->
                    onAction(NewFixedDepositAccountAction.OnStepChange(newIndex))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
            )
        }
    }


}

