/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mifos.objects.noncore.DataTable;

import java.util.Iterator;

/**
 * Created by ishankhanna on 17/06/14.
 * <p/>
 * This is a helper class that is used to generate a layout for
 * Data Table Fragments dynamically based on the data received.
 */
@SuppressWarnings("deprecation")
public class DataTableUIBuilder {

    int tableIndex;
    private DataTableActionListener dataTableActionListener;

    public LinearLayout getDataTableLayout(final DataTable dataTable,
                                           JsonArray jsonElements,
                                           LinearLayout parentLayout,
                                           final Context context,
                                           final int entityId,
                                           DataTableActionListener mListener) {
        dataTableActionListener = mListener;

        /**
         * Create a Iterator with Json Elements to Iterate over the DataTable
         * Response.
         */
        Iterator<JsonElement> jsonElementIterator = jsonElements.iterator();
        /*
         * Each Row of the Data Table is Treated as a Table Here.
         * Creating the First Table for First Row
         */
        tableIndex = 0;
        while (jsonElementIterator.hasNext()) {

            /*
             * Creating CardView
             */
            CardView cardView = new CardView(context);
            LinearLayout.LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );
            params.setMargins(8, 8, 8, 8);
            cardView.setLayoutParams(params);
            cardView.setRadius(8);
            cardView.setPadding(16, 16, 16, 16);
            cardView.setCardElevation(2);


            /*
             * Creating TableLayout
             */
            TableLayout tableLayout = new TableLayout(context);
            tableLayout.setPadding(10, 10, 10, 10);

            final JsonElement jsonElement = jsonElementIterator.next();
            /*
            * Each Entry in a Data Table is Displayed in the
            * form of a table where each row contains one Key-Value Pair
            * i.e a Column Name - Column Value from the DataTable
            */
            int rowIndex = 0;
            while (rowIndex < dataTable.getColumnHeaderData().size()) {
                TableRow tableRow = new TableRow(context);
                tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                tableRow.setPadding(10, 10, 10, 10);
                if (rowIndex % 2 == 0) {
                    tableRow.setBackgroundColor(Color.LTGRAY);
                } else {
                    tableRow.setBackgroundColor(Color.WHITE);
                }

                TextView key = new TextView(context);
                key.setText(dataTable.getColumnHeaderData().get(rowIndex).getColumnName());
                key.setGravity(Gravity.LEFT);
                TextView value = new TextView(context);
                value.setGravity(Gravity.RIGHT);
                if (jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get
                        (rowIndex).getColumnName()).toString().contains("\"")) {
                    value.setText(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData
                            ().get(rowIndex).getColumnName()).toString().replace("\"", ""));
                } else {
                    value.setText(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData
                            ().get(rowIndex).getColumnName()).toString());
                }

                tableRow.addView(key, new TableRow.LayoutParams(ViewGroup.LayoutParams
                        .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tableRow.addView(value, new TableRow.LayoutParams(ViewGroup.LayoutParams
                        .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(12, 16, 12, 16);
                tableLayout.addView(tableRow, layoutParams);
                rowIndex++;
            }

            cardView.addView(tableLayout);

            tableLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //show DataTableOptions
                    dataTableActionListener.showDataTableOptions(
                            dataTable.getRegisteredTableName(), entityId,
                            Integer.parseInt(jsonElement.getAsJsonObject()
                                    .get(dataTable.getColumnHeaderData()
                                            .get(0).getColumnName()).toString()));

                    return true;
                }
            });

            View v = new View(context);
            parentLayout.addView(cardView);
            parentLayout.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, 5));
            Log.i("TABLE INDEX", "" + tableIndex);
            tableIndex++;
        }
        return parentLayout;
    }

    public interface DataTableActionListener {
        void showDataTableOptions(String table, int entity, int rowId);
    }
}
