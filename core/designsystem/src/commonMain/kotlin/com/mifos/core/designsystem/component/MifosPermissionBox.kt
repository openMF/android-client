package com.mifos.core.designsystem.component

import androidx.compose.runtime.Composable

@Composable
expect fun PermissionBox(
    requiredPermissions: List<String>,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String,
    description: String? = null,
    onGranted: @Composable (() -> Unit)? = null,
)

expect fun getRequiredPermissionsForExport(): List<String>