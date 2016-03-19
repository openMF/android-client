/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.api.model.SaveResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.noncore.DataTable;
import com.mifos.api.GenericResponse;

import java.util.Iterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ishankhanna on 17/06/14.
 * <p/>
 * This is a helper class that is used to generate a layout for
 * Data Table Fragments dynamically based on the data received.
 */
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
                tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                if (jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(rowIndex).getColumnName()).toString().contains("\"")) {
                    value.setText(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(rowIndex).getColumnName()).toString().replace("\"", ""));
                } else {
                    value.setText(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(rowIndex).getColumnName()).toString());
                }

                tableRow.addView(key, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tableRow.addView(value, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(12, 16, 12, 16);
                tableLayout.addView(tableRow, layoutParams);

                rowIndex++;
            }

            tableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Toast.makeText(context, "Update Row " + tableIndex, Toast.LENGTH_SHORT).show();
                }
            });

            tableLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(context, "Deleting Row " + tableIndex, Toast.LENGTH_SHORT).show();

                    DataManager dataManager = new DataManager();
                    Observable<GenericResponse> call = dataManager.removeDataTableEntry(dataTable.getRegisteredTableName(), entityId,
                            Integer.parseInt(jsonElement.getAsJsonObject().get(dataTable.getColumnHeaderData().get(0).getColumnName()).toString()));
                    Subscription subscription = call.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<GenericResponse>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(GenericResponse genericResponse) {
                                    Toast.makeText(context, "Deleted Row " + tableIndex, Toast.LENGTH_SHORT).show();
                                    dataTableActionListener.onRowDeleted();
                                }
                            });
                    return true;
                }
            });

            View v = new View(context);
            v.setBackgroundColor(context.getResources().getColor(R.color.black));
            parentLayout.addView(tableLayout);
            parentLayout.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5));
            Log.i("TABLE INDEX", "" + tableIndex);
            tableIndex++;
        }
        return parentLayout;
    }

    public interface DataTableActionListener {
        void onUpdateActionRequested(JsonElement jsonElement);

        void onRowDeleted();
    }
}
