package com.mifos.feature.client.clientIdentitiesList

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientIdentitiesListScreenRoute(
    viewDocument : (Int) -> Unit,
    createNewIdentities : (Int) -> Unit,
    viewModel: ClientIdentitiesListViewModel = koinViewModel(),
){
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
}