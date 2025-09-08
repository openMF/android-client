/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.ic_icon_error
import androidclient.core.ui.generated.resources.ic_icon_success
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosStatusDialog(
    status: ResultStatus,
    onConfirm: () -> Unit,
    btnText: String,
    successTitle: String,
    successMessage: String,
    failureTitle: String,
    failureMessage: String,
    modifier: Modifier = Modifier,
) {
    val (title, message, icon) = when (status) {
        ResultStatus.SUCCESS -> Triple(
            successTitle,
            successMessage,
            painterResource(Res.drawable.ic_icon_success),
        )
        ResultStatus.FAILURE -> Triple(
            failureTitle,
            failureMessage,
            painterResource(Res.drawable.ic_icon_error),
        )
    }

    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = DesignToken.padding.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.sizes.avatarLargeLarge),
            )
            Spacer(Modifier.height(DesignToken.padding.largeIncreasedExtra))

            Text(
                text = title,
                style = MifosTypography.titleLargeEmphasized,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(DesignToken.padding.small))

            Text(
                text = message,
                style = MifosTypography.bodySmall,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(DesignToken.padding.extraLargeIncreased))

            MifosButton(
                onClick = onConfirm,
                text = {
                    Text(
                        text = btnText,
                        style = MifosTypography.labelLarge,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

enum class ResultStatus {
    SUCCESS,
    FAILURE,
}

@Composable
@Preview
private fun MifosSuccessStatusDialogPreview() {
    MifosTheme {
        MifosStatusDialog(
            status = ResultStatus.SUCCESS,
            onConfirm = {},
            btnText = "OK",
            successTitle = "Success",
            successMessage = "Operation Successful",
            failureTitle = "Failure",
            failureMessage = "Operation Failed",
        )
    }
}

@Composable
@Preview
private fun MifosFailureStatusDialogPreview() {
    MifosTheme {
        MifosStatusDialog(
            status = ResultStatus.FAILURE,
            onConfirm = {},
            btnText = "OK",
            successTitle = "Success",
            successMessage = "Operation Successful",
            failureTitle = "Failure",
            failureMessage = "Operation Failed",
        )
    }
}
