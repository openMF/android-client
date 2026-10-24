/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.network.utils

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kpt.core.model.objects.error.ServerErrorParser
import kpt.core.model.objects.error.userMessage

/**
 * The server's user message for a failed [response], else its HTTP status line. Decoding is
 * [ServerErrorParser]'s — a body read here and one carried in an exception read the same.
 */
suspend fun extractErrorMessage(response: HttpResponse): String =
    ServerErrorParser.parse(response.bodyAsText())?.userMessage
        ?: "HTTP ${response.status.value} ${response.status.description}"
