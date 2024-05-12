package com.mifos.feature.individual_collection_sheet.saved_individual_collection_sheet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mifos.feature.individual_collection_sheet.R

@Composable
fun SavedIndividualCollectionSheetCompose() {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.feature_individual_collection_sheet_no_saved_collection_sheet),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SavedIndividualCollectionSheetComposePreview() {
    SavedIndividualCollectionSheetCompose()
}