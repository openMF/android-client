package com.mifos.core.designsystem.component

import androidx.compose.runtime.Composable

@Composable
actual fun PermissionBox(
    requiredPermissions: List<String>,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String,
    description: String?,
    onGranted: @Composable (() -> Unit)?
) {
    TODO("Not yet implemented")
}

actual fun getRequiredPermissionsForExport(): List<String> {
    TODO("Not yet implemented")
}