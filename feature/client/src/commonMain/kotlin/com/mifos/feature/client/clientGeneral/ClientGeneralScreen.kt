package com.mifos.feature.client.clientGeneral

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosScaffold


@Composable
fun ClientGeneralScreen(){



    MifosScaffold(){

    }

}


@Composable
fun PerformanceHistory() {

    Column(

    ) {
        Text("Performance History")

        Box() {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier.padding()
                ){
                    Text("No. Of Loan Cycles :")
                    Text("24")
                }
            }
        }
    }

}