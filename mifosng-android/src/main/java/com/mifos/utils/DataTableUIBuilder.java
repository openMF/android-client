package com.mifos.utils;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mifos.objects.noncore.DataTable;

import java.util.Iterator;

/**
 * Created by ishankhanna on 17/06/14.
 *
 * This is a helper class that is used to generate a layout for
 * Data Table Fragments dynamically based on the data received.
 */
public class DataTableUIBuilder {


    public static LinearLayout getDataTableLayout(DataTable dataTable, JsonArray jsonElements, LinearLayout linearLayout, Context context){

        Log.i("Number of Column Headers", "" + dataTable.getColumnHeaderData().size());

        Iterator<JsonElement> jsonElementIterator = jsonElements.iterator();
        int tableIndex = 0;
        while(jsonElementIterator.hasNext())
        {
            TableLayout tableLayout = new TableLayout(context);
            tableLayout.setPadding(10,10,10,10);

            JsonElement jsonElement = jsonElementIterator.next();

            int rowIndex=0;
            while(rowIndex<dataTable.getColumnHeaderData().size())
            {
                TableRow tableRow = new TableRow(context);
                tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                tableRow.setPadding(10,10,10,10);
                if(rowIndex % 2 == 0) {
                    tableRow.setBackgroundColor(Color.LTGRAY);
                }else {
                    tableRow.setBackgroundColor(Color.WHITE);
                }

                TextView key = new TextView(context);
                key.setText(dataTable.getColumnHeaderData().get(rowIndex).getColumnName());
                key.setGravity(Gravity.LEFT);
                TextView value = new TextView(context);
                value.setGravity(Gravity.END);
                if(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(rowIndex).getColumnName()).toString().contains("\""))
                {
                    value.setText(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(rowIndex).getColumnName()).toString().replace("\"",""));
                }else{
                    value.setText(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(rowIndex).getColumnName()).toString());
                }

                tableRow.addView(key, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tableRow.addView(value, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tableLayout.addView(tableRow, new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                rowIndex++;
            }


            linearLayout.addView(tableLayout);
            Log.i("TABLE INDEX", ""+tableIndex);
            tableIndex++;
        }

        return linearLayout;

    }

}
