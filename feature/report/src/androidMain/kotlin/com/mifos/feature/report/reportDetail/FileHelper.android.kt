/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.reportDetail

import com.mifos.core.model.objects.runreport.FullParameterListResponse
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import java.io.File

actual fun getFileHelper(reportName: String): FileHelper = AndroidFileHelper(reportName)

class AndroidFileHelper(private val reportName: String) : FileHelper {
    override suspend fun exportCsv(report: FullParameterListResponse): Boolean {
        return try {
            val timestamp = System.currentTimeMillis()
            val fileName = "$reportName-$timestamp.csv"
            val exportFolder = File("/storage/emulated/0/Download/MifosReports")

            if (!exportFolder.exists()) {
                exportFolder.mkdirs()
            }

            val filePath = File(exportFolder, fileName).absolutePath.toPath()

            FileSystem.SYSTEM.sink(filePath).buffer().use { sink ->
                val headers = report.columnHeaders.joinToString(",") { it.columnName }
                sink.writeUtf8("$headers\n")
                report.data.forEach { row ->
                    val line = row.row.joinToString(",")
                    sink.writeUtf8("$line\n")
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
