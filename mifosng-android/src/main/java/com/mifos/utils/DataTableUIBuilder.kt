/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.mifos.core.objects.noncore.DataTable

/**
 * Created by ishankhanna on 17/06/14.
 *
 *
 * This is a helper class that is used to generate a layout for
 * Data Table Fragments dynamically based on the data received.
 */

class DataTableUIBuilder {
    private var tableIndex = 0
    private var dataTableActionListener: DataTableActionListener? =
        null

    fun getDataTableLayout(
        dataTable: DataTable,
        jsonElements: JsonArray,
        parentLayout: LinearLayout,
        context: Context,
        entityId: Int,
        mListener: DataTableActionListener?
    ) {
        dataTableActionListener = mListener

        /**
         * Create a Iterator with Json Elements to Iterate over the DataTable
         * Response.
         */
        val jsonElementIterator: Iterator<JsonElement> = jsonElements.iterator()
        /*
         * Each Row of the Data Table is Treated as a Table Here.
         * Creating the First Table for First Row
         */
        tableIndex = 0
        while (jsonElementIterator.hasNext()) {

            /*
             * Creating CardView
             */
            val cardView = CardView(context)
            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            params.setMargins(8, 8, 8, 8)
            cardView.layoutParams = params
            cardView.radius = 8.toFloat()
            cardView.setPadding(16, 16, 16, 16)
            cardView.cardElevation = 2.toFloat()

            /*
             * Creating TableLayout
             */
            val tableLayout = TableLayout(context)
            tableLayout.setPadding(10, 10, 10, 10)

            val jsonElement: JsonElement = jsonElementIterator.next()
            /*
            * Each Entry in a Data Table is Displayed in the
            * form of a table where each row contains one Key-Value Pair
            * i.e a Column Name - Column Value from the DataTable
            */
            var rowIndex = 0
            while (rowIndex < dataTable.columnHeaderData.size) {
                val tableRow = TableRow(context)
                tableRow.layoutParams = TableRow.LayoutParams(
                    ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                tableRow.setPadding(10, 10, 10, 10)
                if (rowIndex % 2 == 0) {
                    tableRow.setBackgroundColor(Color.LTGRAY)
                } else {
                    tableRow.setBackgroundColor(Color.WHITE)
                }

                val key = TextView(context)
                key.text = dataTable.columnHeaderData[rowIndex].dataTableColumnName
                key.gravity = Gravity.LEFT
                val value = TextView(context)
                value.gravity = Gravity.RIGHT
                if (jsonElement.asJsonObject.get(
                        dataTable.columnHeaderData
                            [rowIndex].dataTableColumnName
                    ).toString().contains("\"")
                ) {
                    value.text = jsonElement.asJsonObject.get(
                        dataTable.columnHeaderData
                            [rowIndex].dataTableColumnName
                    ).toString().replace("\"", "")
                } else {
                    value.text = jsonElement.asJsonObject.get(
                        dataTable.columnHeaderData
                            [rowIndex].dataTableColumnName
                    ).toString()
                }

                tableRow.addView(
                    key, TableRow.LayoutParams(
                        ViewGroup.LayoutParams
                            .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                    )
                )
                tableRow.addView(
                    value, TableRow.LayoutParams(
                        ViewGroup.LayoutParams
                            .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                    )
                )
                val layoutParams = TableRow.LayoutParams(
                    ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                layoutParams.setMargins(12, 16, 12, 16)
                tableLayout.addView(tableRow, layoutParams)
                rowIndex++
            }

            cardView.addView(tableLayout)

            tableLayout.setOnLongClickListener { v: View ->
                //show DataTableOptions
                dataTable.registeredTableName?.let {
                    dataTableActionListener?.showDataTableOptions(
                        it, entityId,
                        Integer.parseInt(
                            jsonElement.asJsonObject
                                .get(dataTable.columnHeaderData[0].dataTableColumnName).toString()
                        )
                    )
                }

                true
            }

            val v = View(context)
            parentLayout.addView(cardView)
            parentLayout.addView(
                v, LayoutParams(
                    LayoutParams
                        .MATCH_PARENT, 5
                )
            )
            Log.i("TABLE INDEX", "" + tableIndex)
            tableIndex++
        }
    }

    interface DataTableActionListener {
        fun showDataTableOptions(table: String, entity: Int, rowId: Int)
    }
}