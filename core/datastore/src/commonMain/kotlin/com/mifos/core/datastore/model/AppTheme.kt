package com.mifos.core.datastore.model

enum class AppTheme(
    val themeName: String,
) {
    SYSTEM(themeName = "System Theme"),
    LIGHT(themeName = "Light Theme"),
    DARK(themeName = "Dark Theme"),
}