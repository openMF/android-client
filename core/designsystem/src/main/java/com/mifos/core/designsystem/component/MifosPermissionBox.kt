/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun PermissionBox(
    requiredPermissions: List<String>,
    title: Int,
    confirmButtonText: Int,
    dismissButtonText: Int,
    description: Int? = null,
    onGranted: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionGranted by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(
                    context,
                    it,
                ) == PackageManager.PERMISSION_GRANTED
            },
        )
    }

    var shouldShowPermissionRationale =
        requiredPermissions.all {
            (context as? Activity)?.let { it1 ->
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it1, it,
                )
            } == true
        }

    var shouldDirectUserToApplicationSettings by remember {
        mutableStateOf(false)
    }

    val decideCurrentPermissionStatus: (Boolean, Boolean) -> String =
        { granted, rationale ->
            if (granted) {
                "Granted"
            } else if (rationale) {
                "Rejected"
            } else {
                "Denied"
            }
        }

    var currentPermissionStatus by remember {
        mutableStateOf(
            decideCurrentPermissionStatus(
                permissionGranted,
                shouldShowPermissionRationale,
            ),
        )
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionResults ->
            val isGranted =
                requiredPermissions.all { permissionResults[it] == true }

            permissionGranted = isGranted

            if (!isGranted) {
                shouldShowPermissionRationale =
                    requiredPermissions.all {
                        (context as? Activity)?.let { it1 ->
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                it1, it,
                            )
                        } == false
                    }
            }
            shouldDirectUserToApplicationSettings =
                !shouldShowPermissionRationale && !permissionGranted
            currentPermissionStatus = decideCurrentPermissionStatus(
                permissionGranted,
                shouldShowPermissionRationale,
            )
        },
    )

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START &&
                    !permissionGranted &&
                    !shouldShowPermissionRationale
                ) {
                    multiplePermissionLauncher.launch(requiredPermissions.toTypedArray())
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        },
    )

    if (shouldShowPermissionRationale) {
        MifosDialogBox(
            showDialogState = true,
            onDismiss = { shouldShowPermissionRationale = false },
            title = title,
            message = description,
            confirmButtonText = confirmButtonText,
            onConfirm = {
                shouldShowPermissionRationale = false
                multiplePermissionLauncher.launch(requiredPermissions.toTypedArray())
            },
            dismissButtonText = dismissButtonText,
        )
    }

    if (shouldDirectUserToApplicationSettings) {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).also {
            context.startActivity(it)
        }
    }

    if (permissionGranted) {
        onGranted?.invoke()
    }
}
