package com.mifos.utils;


import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
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
        int viewIndex = 0;

        while(columnHeaderIterator.hasNext())
        {
            TextView currentTextView = new TextView(context);
            currentTextView.setId(viewIndex);
            currentTextView.setText(columnHeaderIterator.next().getColumnName());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


//            layoutParams.topMargin = 8;
//            layoutParams.bottomMargin = 8;
//            layoutParams.leftMargin = 8;
//            layoutParams.rightMargin = 8;

//            if(viewIndex==1)
//
//            {
//                previousTextView = currentTextView;
//                //layoutParams.addRule(LinearLayout.ALIGN_PARENT_LEFT);
//                layoutParams.setMargins(8,8,8,8);
//            }else {
//                layoutParams.setMargins(8,8,8,8);
//                previousTextView = currentTextView;
//             }
            layoutParams.setMargins(8,8,8,8);
            linearLayout.addView(currentTextView, viewIndex, layoutParams);
            viewIndex++;
        }

        return linearLayout;

    }

}
