package com.mifos.feature.client.newFixedDepositAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.step_terms
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

@Composable
fun TermsPage(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(Res.string.step_terms))
        Spacer(Modifier.height(8.dp))
        Button(onClick = onNext) {
            Text("Next Button")
        }
    }
}