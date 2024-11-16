/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.savedIndividualCollectionSheet.ui

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
import com.mifos.feature.collection_sheet.R

@Composable
internal fun SavedIndividualCollectionSheetCompose(
    modifier: Modifier = Modifier,
) {
    // Saved Individual Collection Sheet need to be integrated with API.

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.feature_collection_sheet_no_saved_collection_sheet),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SavedIndividualCollectionSheetComposePreview() {
    SavedIndividualCollectionSheetCompose()
}
