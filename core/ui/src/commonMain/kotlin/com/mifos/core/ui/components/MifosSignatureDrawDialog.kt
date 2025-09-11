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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.DialogProperties
import com.mifos.core.designsystem.component.MifosCustomDialog
import com.mifos.core.designsystem.component.MifosTopBar
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.niyajali.compose.sign.ComposeSign
import com.niyajali.compose.sign.SignatureConfig
import com.niyajali.compose.sign.exportSignature
import com.niyajali.compose.sign.isNotEmpty
import com.niyajali.compose.sign.rememberSignatureState

@Composable
fun MifosSignatureDrawDialog(
    onSaveFile: (ImageBitmap) -> Unit,
    onDismiss: () -> Unit,
) {
    val signatureState = rememberSignatureState()
    var size = remember {
        Size.Zero
    }

    MifosCustomDialog(
        onDismiss = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            modifier = Modifier.padding(DesignToken.padding.large),
            shape = DesignToken.shapes.small,
        ) {
            Column {
                MifosTopBar(
                    modifier = Modifier.height(DesignToken.sizes.topBarStandardHeight),
                    topBarTitle = "",
                    backPress = {
                        onDismiss()
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                signatureState.clear()
                            },
                        ) {
                            Icon(MifosIcons.Refresh, null)
                        }

                        IconButton(
                            onClick = {
                                signatureState.undo()
                            },
                        ) {
                            Icon(MifosIcons.Undo, null)
                        }
                        IconButton(
                            onClick = {
                                signatureState.redo()
                            },
                        ) {
                            Icon(MifosIcons.Redo, null)
                        }
                        IconButton(
                            onClick = {
                                val data = signatureState.exportSignature(
                                    width = size.width.toInt(),
                                    height = size.height.toInt(),
                                    backgroundColor = Color.White,
                                )

                                data?.let {
                                    onSaveFile(it)
                                }
                            },
                            enabled = signatureState.isNotEmpty(),
                        ) {
                            Icon(MifosIcons.Check, null)
                        }
                    },
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                ComposeSign(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            size = it.size.toSize()
                        },
                    config = SignatureConfig(
                        backgroundColor = Color.Transparent,
                        borderStroke = null,
                        showGrid = true,
                    ),
                    state = signatureState,
                    onSignatureUpdate = {},
                )
            }
        }
    }
}
