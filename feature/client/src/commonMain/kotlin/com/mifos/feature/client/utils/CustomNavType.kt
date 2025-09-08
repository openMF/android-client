package com.mifos.feature.client.utils

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.mifos.feature.client.clientAddDocuments.DocumentState
import kotlinx.serialization.json.Json

object CustomNavType {

    val DocumentStateNavType = object : NavType<DocumentState>(
        isNullableAllowed = false
    ) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): DocumentState? {
            return Json.Default.decodeFromString(bundle.getString(key)?: return null)
        }

        override fun parseValue(value: String): DocumentState {
            return Json.Default.decodeFromString(value)
        }

        override fun serializeAsValue(value: DocumentState): String {
            return Json.Default.encodeToString(DocumentState.serializer(),value)
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: DocumentState,
        ) {
            bundle.putString(key, Json.Default.encodeToString(DocumentState.serializer(), value))
        }

    }

}