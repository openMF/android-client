package com.mifos.feature.client.NewFixedDepositAccount



import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_signature_delete
import androidclient.feature.client.generated.resources.feature_client

import androidclient.feature.client.generated.resources.feature_client_accounts
import androidclient.feature.client.generated.resources.feature_client_id
import androidclient.feature.client.generated.resources.from_camera
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect

import com.mifos.feature.client.NewFixedDepositAccount.Pages.ChargesPage
import com.mifos.feature.client.NewFixedDepositAccount.Pages.DetailPage
import com.mifos.feature.client.NewFixedDepositAccount.Pages.InterestChartsPage
import com.mifos.feature.client.NewFixedDepositAccount.Pages.SettingPage

import com.mifos.feature.client.NewFixedDepositAccount.Pages.TermsPage

import org.jetbrains.compose.resources.stringResource

import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun NewFixedDepositAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewFixedDepositAccountViewModel = koinViewModel()

){
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            FixedDepositAccountEvent.NavigateBack -> onNavigateBack()
            FixedDepositAccountEvent.Finish -> onFinish()
        }
    }
    FixedDepositAccountScafflold(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,

        )



}
@Composable
private fun  FixedDepositAccountScafflold(
    navController: NavController,
    state: FixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (FixedDepositAccountAction) -> Unit,
){
    val steps = listOf(
        Step (stringResource(Res.string.from_camera)){
            DetailPage {
                onAction(FixedDepositAccountAction.NextStep)
            }},
        Step(stringResource(Res.string.feature_client)) {
            TermsPage {
                onAction(FixedDepositAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.feature_client_accounts)) {
            SettingPage {
                onAction(FixedDepositAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.feature_client_id)) {
            InterestChartsPage {
                onAction(FixedDepositAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.feature_client_id)) {
            ChargesPage {
                onAction(FixedDepositAccountAction.NextStep)
            }
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.from_camera),
        onBackPressed = { onAction(FixedDepositAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        when (state.screenState){
            is FixedDepositAccountState.ScreenState.Loading -> MifosProgressIndicator()
            is FixedDepositAccountState.ScreenState.Success -> {
                Column(
                    Modifier.fillMaxSize().padding(paddingValues),
                ) {
                    MifosBreadcrumbNavBar(
                        navController,
                    )
                    MifosStepper(
                        steps = steps,
                        currentIndex = state.currentStep,
                        onStepChange = { newIndex ->
                            onAction( FixedDepositAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
            is  FixedDepositAccountState.ScreenState.NetworkError -> {
                MifosSweetError(
                    message = stringResource(Res.string.client_signature_delete),

                    )
            }

        }
        if (state.isOverLayLoadingActive) {
            MifosProgressIndicatorOverlay()
            }
        }



}