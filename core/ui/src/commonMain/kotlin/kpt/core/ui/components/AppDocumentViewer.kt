/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import io.github.limuyang2.pdf.core.PdfDocument
import io.github.limuyang2.pdf.core.PdfSource
import io.github.limuyang2.pdf.core.PdfViewer
import io.github.limuyang2.pdf.viewer.PdfView
import io.github.limuyang2.pdf.viewer.rememberPdfViewState
import kotlinx.coroutines.CancellationException
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.designsystem.theme.AppTypography
import kpt.core.ui.generated.resources.Res
import kpt.core.ui.generated.resources.core_ui_document_viewer_image_description
import kpt.core.ui.generated.resources.core_ui_document_viewer_open_failed
import kpt.core.ui.generated.resources.core_ui_document_viewer_unsupported
import org.jetbrains.compose.resources.stringResource

/** What a document's bytes can be shown as. */
enum class DocumentKind { Pdf, Image, Unsupported }

/**
 * Decides how to show a document.
 *
 * The CONTENT is asked first — a file's leading bytes cannot be mislabelled, while a Fineract
 * `fileName` or `type` is whatever the uploading client sent. The original keyed on the extension
 * alone (`if (extension == "pdf")`), so a scan uploaded as `IMG_0042` rendered as nothing. The
 * declared MIME type and the extension are the fallbacks for a format the signatures below do not
 * cover.
 */
fun documentKindOf(bytes: ByteArray, fileName: String? = null, mimeType: String? = null): DocumentKind {
    fun startsWith(vararg signature: Int) =
        bytes.size >= signature.size && signature.indices.all { bytes[it] == signature[it].toByte() }

    when {
        startsWith(0x25, 0x50, 0x44, 0x46) -> return DocumentKind.Pdf // %PDF
        startsWith(0x89, 0x50, 0x4E, 0x47) -> return DocumentKind.Image // PNG
        startsWith(0xFF, 0xD8, 0xFF) -> return DocumentKind.Image // JPEG
        startsWith(0x47, 0x49, 0x46, 0x38) -> return DocumentKind.Image // GIF8
        startsWith(0x52, 0x49, 0x46, 0x46) && bytes.size >= 12 &&
            bytes.copyOfRange(8, 12).decodeToString() == "WEBP" -> return DocumentKind.Image
    }

    val mime = mimeType?.lowercase().orEmpty()
    val extension = fileName?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase().orEmpty()
    return when {
        mime == "application/pdf" || extension == "pdf" -> DocumentKind.Pdf
        mime.startsWith("image/") || extension in IMAGE_EXTENSIONS -> DocumentKind.Image
        else -> DocumentKind.Unsupported
    }
}

private val IMAGE_EXTENSIONS = setOf("png", "jpg", "jpeg", "gif", "webp", "bmp")

/**
 * Shows a downloaded document in place — a PDF through PDFium (`pdf-viewer-kmp`), an image through
 * Coil, anything else as an explanation pointing at Share.
 *
 * Replaces the original's `PdfPreview` expect/actuals (`6b66e8a43:feature/client/.../utils/PdfViewer.*`)
 * and `MifosViewPdf`. Those rendered EVERY page up front — at 3x scale on Android, ~18 MB per A4
 * page, on the main thread — and the web actuals were empty bodies. `PdfView` renders lazily as
 * pages scroll into view, zooms, and runs on every target this app ships.
 *
 * The caller owns the bytes (it downloaded them); this composable owns the opened [PdfDocument]
 * and closes it when it leaves composition, which is the library's documented contract.
 */
@Composable
fun AppDocumentViewer(
    bytes: ByteArray,
    modifier: Modifier = Modifier,
    fileName: String? = null,
    mimeType: String? = null,
) {
    val kind = remember(bytes, fileName, mimeType) { documentKindOf(bytes, fileName, mimeType) }
    when (kind) {
        DocumentKind.Pdf -> PdfContent(bytes = bytes, modifier = modifier)

        DocumentKind.Image -> AsyncImage(
            model = bytes,
            contentDescription = stringResource(Res.string.core_ui_document_viewer_image_description),
            // core/ui depends on coil-compose-CORE, which takes the loader explicitly.
            imageLoader = SingletonImageLoader.get(LocalPlatformContext.current),
            contentScale = ContentScale.Fit,
            modifier = modifier.fillMaxSize(),
        )

        DocumentKind.Unsupported -> ViewerMessage(
            text = stringResource(Res.string.core_ui_document_viewer_unsupported),
            modifier = modifier,
        )
    }
}

@Composable
private fun PdfContent(bytes: ByteArray, modifier: Modifier) {
    var document by remember(bytes) { mutableStateOf<PdfDocument?>(null) }
    var failed by remember(bytes) { mutableStateOf(false) }

    LaunchedEffect(bytes) {
        try {
            document = PdfViewer.open(PdfSource.Bytes(bytes))
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Throwable) {
            // A corrupt or password-protected file. The original had no handler here at all, so a
            // bad PDF took the screen down.
            failed = true
        }
    }

    val current = document
    DisposableEffect(current) {
        onDispose { current?.close() }
    }

    when {
        current != null -> PdfView(
            document = current,
            state = rememberPdfViewState(),
            modifier = modifier.fillMaxSize(),
            maxZoom = MAX_ZOOM,
            gestureZoomEnabled = true,
        )

        failed -> ViewerMessage(
            text = stringResource(Res.string.core_ui_document_viewer_open_failed),
            modifier = modifier,
        )

        else -> AppProgressIndicator(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun ViewerMessage(text: String, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(KptTheme.spacing.lg),
        )
    }
}

private const val MAX_ZOOM = 4f
