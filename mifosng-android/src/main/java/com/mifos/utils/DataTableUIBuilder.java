package com.mifos.utils;


import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.mifos.objects.noncore.ColumnHeader;
import com.mifos.objects.noncore.DataTable;

import java.util.Iterator;

/**
 * Created by ishankhanna on 17/06/14.
 *
 * This is a helper class that is used to generate a layout for
 * Data Table Fragments dynamically based on the data received.
 */
public class DataTableUIBuilder {


    public static LinearLayout getDataTableLayout(DataTable dataTable, LinearLayout linearLayout, Context context){

        //linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        Iterator<ColumnHeader> columnHeaderIterator = dataTable.getColumnHeaderData().iterator();
        int rowIndex = 0;
        TableLayout tableLayout = new TableLayout(context);


        while(columnHeaderIterator.hasNext())
        {
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tableRow.setPadding(10,10,10,10);
            if(rowIndex % 2 == 0) {
                tableRow.setBackgroundColor(Color.LTGRAY);
            }else {
                tableRow.setBackgroundColor(Color.WHITE);
            }

            TextView key = new TextView(context);
            key.setText(columnHeaderIterator.next().getColumnName());

            tableRow.addView(key, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(tableRow, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rowIndex++;
        }

        linearLayout.addView(tableLayout, new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return linearLayout;

    }

}
