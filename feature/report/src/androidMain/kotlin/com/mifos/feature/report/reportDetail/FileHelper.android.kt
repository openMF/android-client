package com.mifos.feature.report.reportDetail

import android.os.Environment
import co.touchlab.kermit.Logger
import com.mifos.core.model.objects.runreport.FullParameterListResponse
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import java.io.File

actual fun getFileHelper(reportName: String): FileHelper= AndroidFileHelper(reportName)

class AndroidFileHelper(private val reportName: String) : FileHelper {
    override suspend fun exportCsv(report: FullParameterListResponse): Boolean {
        return try {
            val timestamp = System.currentTimeMillis()
            val fileName = "$reportName-$timestamp.csv"

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exportFolder = File(downloadsDir, "MifosReports")

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
            Logger.e("Revanth"){
                e.toString()
            }
            false
        }
    }
}