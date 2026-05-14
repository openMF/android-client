/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalForeignApi::class)

package com.mifos.core.ui.util.pdf

import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS implementation of PDF generator.
 * Uses UIMarkupTextPrintFormatter and UIPrintPageRenderer to render HTML to PDF data.
 */
actual class PdfGenerator {
    actual suspend fun generateAndSharePdf(
        htmlContent: String,
        fileName: String,
        pageConfig: PageConfig,
    ) {
        TODO("iOS PDF generation not yet implemented")
    }
}
