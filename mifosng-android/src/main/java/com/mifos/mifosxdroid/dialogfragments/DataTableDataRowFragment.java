/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.formwidgets.FormEditText;
import com.mifos.mifosxdroid.formwidgets.FormNumericEditText;
import com.mifos.mifosxdroid.formwidgets.FormSpinner;
import com.mifos.mifosxdroid.formwidgets.FormWidget;
import com.mifos.objects.noncore.ColumnHeader;
import com.mifos.objects.noncore.ColumnValue;
import com.mifos.objects.noncore.DataTable;
import com.mifos.services.API;
import com.mifos.services.GenericResponse;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class DataTableDataRowFragment extends DialogFragment{

    private DataTable dataTable;
    private int entityId;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    View rootView;

    LinearLayout linearLayout;

    SafeUIBlockingUtility safeUIBlockingUtility;

    Map<String,Object> formWidgetsMap = new HashMap<String, Object>();

    List<FormWidget> formWidgets = new ArrayList<FormWidget>();

    @InjectView(R.id.bt_processForm)
    Button bt_processForm;

    //TODO Check for Static vs Bundle Approach
    public static DataTableDataRowFragment newInstance(DataTable dataTable, int entityId) {
        DataTableDataRowFragment dataTableDataRowFragment = new DataTableDataRowFragment();
        Bundle args = new Bundle();
        dataTableDataRowFragment.dataTable = dataTable;
        dataTableDataRowFragment.entityId = entityId;
        dataTableDataRowFragment.setArguments(args);
        return dataTableDataRowFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         * This is very Important
         * It is used to auto resize the dialog when a Keyboard appears.
         * And User can still easily scroll through the form. Sweet, isn't it?
         */
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        rootView = inflater.inflate(R.layout.dialog_fragment_add_entry_to_datatable, container, false);

        ButterKnife.inject(this, rootView);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_data_table_entry_form);

        activity = (ActionBarActivity) getActivity();
        actionBar = activity.getSupportActionBar();
        actionBar.setTitle(dataTable.getRegisteredTableName());

        safeUIBlockingUtility = new SafeUIBlockingUtility(DataTableDataRowFragment.this.getActivity());

        createForm();


        return rootView;
    }

    public void createForm() {


        Iterator<ColumnHeader> columnHeaderIterator = dataTable.getColumnHeaderData().iterator();
        while (columnHeaderIterator.hasNext()) {

            ColumnHeader columnHeader = columnHeaderIterator.next();
            if(!columnHeader.getIsColumnPrimaryKey()) {

                if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_STRING) || columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_TEXT)) {

                    FormEditText formEditText = new FormEditText(getActivity(), columnHeader.getColumnName());
                    formWidgets.add(formEditText);
                    formWidgetsMap.put(columnHeader.getColumnName(), formEditText);
                    linearLayout.addView(formEditText.getView());

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_INT) || columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_DECIMAL)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity(), columnHeader.getColumnName());
                    formWidgets.add(formNumericEditText);

                    formWidgetsMap.put(columnHeader.getColumnName(), formNumericEditText);
                    linearLayout.addView(formNumericEditText.getView());

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_CODELOOKUP) || columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_CODEVALUE)) {

                    List<String> columnValueStrings = new ArrayList<String>();
                    for (ColumnValue columnValue : columnHeader.getColumnValues()) {
                        columnValueStrings.add(columnValue.getValue());
                    }

                    FormSpinner formSpinner = new FormSpinner(getActivity(), columnHeader.getColumnName(), columnValueStrings);
                    formWidgets.add(formSpinner);

                    formWidgetsMap.put(columnHeader.getColumnName(), formSpinner);
                    linearLayout.addView(formSpinner.getView());

                }
            }


        }

        bt_processForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onSaveActionRequested();
                } catch (RequiredFieldException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void onSaveActionRequested() throws RequiredFieldException {

        Map<String, Object> payload = new HashMap<String, Object>();

//        for (ColumnHeader columnHeader : dataTable.getColumnHeaderData()) {
//
//            if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_STRING) || columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_TEXT)) {
//
//                FormEditText formEditText = (FormEditText) formWidgetsMap.get(columnHeader.getColumnName());
//
//                if (formEditText.getValue().equals(""))
//                {
//                    if (!columnHeader.getIsColumnNullable()) {
//
//                        throw new RequiredFieldException(columnHeader.getColumnName(), getString(R.string.message_field_required));
//
//                    }
//                } else {
//
//                    payload.put(columnHeader.getColumnName(), formEditText.getValue());
//
//                }
//
//            } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_INT) || columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_DECIMAL)) {
//
//                FormNumericEditText formNumericEditText = (FormNumericEditText)formWidgetsMap.get(columnHeader.getColumnName());
//
//                if (formNumericEditText.getValue().equals(""))
//                {
//                    if (!columnHeader.getIsColumnNullable()) {
//
//                        throw new RequiredFieldException(columnHeader.getColumnName(), getString(R.string.message_field_required));
//
//                    }
//                } else {
//
//                    payload.put(columnHeader.getColumnName(), formNumericEditText.getValue());
//
//                }
//
//            } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_CODELOOKUP) || columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_CODEVALUE)) {
//
//               FormSpinner formSpinner = (FormSpinner) formWidgetsMap.get(columnHeader.getColumnName());
//
//                if (formSpinner.getValue().equals(""))
//                {
//                    if (!columnHeader.getIsColumnNullable()) {
//
//                        throw new RequiredFieldException(columnHeader.getColumnName(), getString(R.string.message_field_required));
//
//                    }
//                } else {
//
//                    payload.put(columnHeader.getColumnName(), formSpinner.getValue());
//
//                }
//
//            }
//
//        }

        Iterator<FormWidget> widgetIterator = formWidgets.iterator();
        while(widgetIterator.hasNext()) {

            FormWidget formWidget = widgetIterator.next();
            payload.put(formWidget.getPropertyName(), formWidget.getValue());


        }

        safeUIBlockingUtility.safelyBlockUI();

        API.dataTableService.createEntryInDataTable(dataTable.getRegisteredTableName(), entityId, payload, new Callback<GenericResponse>() {
            @Override
            public void success(GenericResponse genericResponse, Response response) {

                System.out.println("DONE");
                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println("FAILED");
                safeUIBlockingUtility.safelyUnBlockUI();


            }
        });



    }

}
