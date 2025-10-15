package com.mifos.feature.client.NewFixedDepositAccount.Pages


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingPage(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = "Setting Page")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onNext) {
            Text(text = "Next")
        }


    }


}