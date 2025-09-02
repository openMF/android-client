package com.mifos.feature.loan.ClientCollateral

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// Koin ViewModel import
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCollateralScreen(
    viewModel: ClientCollateralViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                val titleText = when (val state = uiState) {
                    is ClientCollateralUiState.Success -> "Collateral Data (${state.totalItems} ${if (state.totalItems == 1) "Item" else "Items"})"
                    is ClientCollateralUiState.Empty -> "Collateral Data (0 Items)"
                    else -> "Collateral Data"
                }
                Text(text = titleText)
            })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter // Changed to TopCenter for list display
        ) {
            when (val state = uiState) {
                is ClientCollateralUiState.Loading -> {
                    // Centered loading indicator
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ClientCollateralUiState.Success -> {
                    CollateralList(items = state.items)
                }
                is ClientCollateralUiState.Empty -> {
                    // Centered empty state message
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyCollateralState()
                    }
                }
                is ClientCollateralUiState.Error -> {
                    // Centered error state message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorState(message = state.message, onRetry = { viewModel.loadCollateralItems() })
                    }
                }
            }
        }
    }
}

@Composable
fun CollateralList(items: List<CollateralDisplayItem>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
    ) {
        items(items, key = { it.id }) { item -> // Use item.id as a key for better performance
            CollateralListItem(item = item, onActionClick = { /* TODO: Handle action click */ })
        }
    }
}

@Composable
fun CollateralListItem(item: CollateralDisplayItem, onActionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Type/Name: ${item.typeName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Unit Value: ${item.unitValue}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Total Collateral Value: ${item.totalCollateralValue}", style = MaterialTheme.typography.bodyMedium)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                 IconButton(onClick = onActionClick) {

                }
            }
        }
    }
}

@Composable
fun EmptyCollateralState() {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No Item Found", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Error: $message", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

