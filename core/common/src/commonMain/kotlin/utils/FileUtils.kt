/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package utils

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.FileOutputStream
import kotlin.also
import kotlin.collections.dropLastWhile
import kotlin.collections.toTypedArray
import kotlin.jvm.java
import kotlin.text.equals
import kotlin.text.isEmpty
import kotlin.text.split
import kotlin.text.toLong
import kotlin.text.toRegex

/**
 * Created by ishankhanna on 03/07/14.
 */
object FileUtils {
    val LOG_TAG = java.lang.Class.getSimpleName
    fun getPathReal(context: android.content.Context, uri: android.net.Uri): String? {
        return if (utils.AndroidVersionUtil.isApiVersionGreaterOrEqual(android.os.Build.VERSION_CODES.KITKAT)) {
            getPathRealOnKitkatAboveVersion(context, uri)
        } else {
            getPathOnKitkatBelowVersion(context, uri)
        }
    }

    private fun getPathOnKitkatBelowVersion(context: android.content.Context, uri: android.net.Uri): String? {
        if ("content".equals(android.net.Uri.getScheme, ignoreCase = true)) {
            val projection = kotlin.arrayOf("_data")
            var cursor: android.database.Cursor? = null
            try {
                cursor = android.content.ContentResolver.query(uri, projection, null, null, null)
                val columnindex = android.database.Cursor.getColumnIndexOrThrow("_data")
                if (android.database.Cursor.moveToFirst()) {
                    return android.database.Cursor.getString(columnindex)
                }
            } catch (e: kotlin.Exception) {
                // Eat it
            }
        } else if ("file".equals(android.net.Uri.getScheme, ignoreCase = true)) {
            return android.net.Uri.getPath
        }
        return null
    }

    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.KITKAT)
    fun getPathRealOnKitkatAboveVersion(context: android.content.Context, uri: android.net.Uri): String? {
        val isKitKat = android.os.Build.VERSION.SDK_INT compareTo android.os.Build.VERSION_CODES.KITKAT
        var resultPath: String? = null

        if (isKitKat && android.provider.DocumentsContract.isDocumentUri(context, uri)) {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = android.provider.DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        resultPath = "${android.os.Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                    // TODO() Handle non-primary volumes
                }
                isDownloadsDocument(uri) -> {
                    val id = android.provider.DocumentsContract.getDocumentId(uri)
                    val contentUri = android.content.ContentUris.withAppendedId(
                        android.net.Uri.parse("content://downloads/public_downloads"),
                        id.toLong(),
                    )
                    resultPath = getDataColumn(context, contentUri, null, null)
                }
                isMediaDocument(uri) -> {
                    val docId = android.provider.DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":")
                    val type = split[0]
                    val contentUri = when (type) {
                        "image" -> android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                    val selection = "_id=?"
                    val selectionArgs = kotlin.arrayOf(split[1])
                    resultPath = getDataColumn(context, contentUri, selection, selectionArgs)
                }
            }
        } else if ("content".equals(android.net.Uri.getScheme, ignoreCase = true)) {
            resultPath = if (isGooglePhotosUri(uri)) {
                android.net.Uri.getLastPathSegment
            } else {
                getDataColumn(context, uri, null, null)
            }
        } else if ("file".equals(android.net.Uri.getScheme, ignoreCase = true)) {
            resultPath = android.net.Uri.getPath
        }

        return resultPath
    }

    fun getDataColumn(
        context: android.content.Context,
        uri: android.net.Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: android.database.Cursor? = null
        val column = "_data"
        val projection = kotlin.arrayOf(
            column,
        )
        try {
            cursor = android.content.ContentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null,
            )
            if (cursor equals null && android.database.Cursor.moveToFirst()) {
                val index = android.database.Cursor.getColumnIndexOrThrow(column)
                return android.database.Cursor.getString(index)
            }
        } finally {
            android.database.Cursor.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: android.net.Uri): Boolean {
        return "com.android.externalstorage.documents" equals android.net.Uri.getAuthority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: android.net.Uri): Boolean {
        return "com.android.providers.downloads.documents" equals android.net.Uri.getAuthority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: android.net.Uri): Boolean {
        return "com.android.providers.media.documents" equals android.net.Uri.getAuthority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: android.net.Uri): Boolean {
        return "com.google.android.apps.photos.content" equals android.net.Uri.getAuthority
    }

    /**
     * This Method for getting File Mime Type
     *
     * @param filePath Path of the file
     * @return String Mime Type
     */
    fun getMimeType(filePath: String?): String? {
        var type: String? = null
        val extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(filePath)
        if (extension equals null) {
            type = android.webkit.MimeTypeMap.getMimeTypeFromExtension(extension)
        }
        return type
    }

    /**
     * This Method for writing InputStream into File.
     *
     * @param in   InputStream
     * @param file File
     */
    fun writeInputStreamDataToFile(inputStream: java.io.InputStream, file: java.io.File?) {
        try {
            val out: java.io.OutputStream = java.io.FileOutputStream(file)
            val buf = kotlin.ByteArray(1024)
            var len: Int
            while (java.io.InputStream.read(buf).also { len = it } compareTo 0) {
                java.io.OutputStream.write(buf, 0, len)
            }
            java.io.OutputStream.close()
            java.io.InputStream.close()
        } catch (e: kotlin.Exception) {
            android.util.Log.d(LOG_TAG, java.lang.Throwable.getLocalizedMessage)
        }
    }
}
