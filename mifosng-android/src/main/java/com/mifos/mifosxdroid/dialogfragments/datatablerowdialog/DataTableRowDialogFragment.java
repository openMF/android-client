/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mifos.api.GenericResponse;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.formwidgets.FormEditText;
import com.mifos.mifosxdroid.formwidgets.FormNumericEditText;
import com.mifos.mifosxdroid.formwidgets.FormSpinner;
import com.mifos.mifosxdroid.formwidgets.FormWidget;
import com.mifos.objects.noncore.ColumnHeader;
import com.mifos.objects.noncore.ColumnValue;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.MFErrorParser;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class DataTableRowDialogFragment extends DialogFragment
        implements DataTableRowDialogMvpView {

    private final String LOG_TAG = getClass().getSimpleName();
    @BindView(R.id.ll_data_table_entry_form)
    LinearLayout linearLayout;
    @Inject
    DataTableRowDialogPresenter mDataTableRowDialogPresenter;
    private DataTable dataTable;
    private int entityId;
    private SharedPreferences sharedPreferences;
    private View rootView;
    private SafeUIBlockingUtility safeUIBlockingUtility;

    private List<FormWidget> formWidgets = new ArrayList<FormWidget>();


    //TODO Check for Static vs Bundle Approach
    public static DataTableRowDialogFragment newInstance(DataTable dataTable, int entityId) {
        DataTableRowDialogFragment dataTableRowDialogFragment = new DataTableRowDialogFragment();
        Bundle args = new Bundle();
        dataTableRowDialogFragment.dataTable = dataTable;
        dataTableRowDialogFragment.entityId = entityId;
        dataTableRowDialogFragment.setArguments(args);
        return dataTableRowDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        /**
         * This is very Important
         * It is used to auto resize the dialog when a Keyboard appears.
         * And User can still easily scroll through the form. Sweet, isn't it?
         */
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        rootView = inflater.inflate(R.layout.dialog_fragment_add_entry_to_datatable, container,
                false);

        ButterKnife.bind(this, rootView);
        mDataTableRowDialogPresenter.attachView(this);


        getDialog().setTitle(dataTable.getRegisteredTableName());

        safeUIBlockingUtility = new SafeUIBlockingUtility(DataTableRowDialogFragment.this
                .getActivity());

        createForm();


        return rootView;
    }

    public void createForm() {


        Iterator<ColumnHeader> columnHeaderIterator = dataTable.getColumnHeaderData().iterator();
        while (columnHeaderIterator.hasNext()) {

            ColumnHeader columnHeader = columnHeaderIterator.next();
            if (!columnHeader.getIsColumnPrimaryKey()) {

                if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_STRING) ||
                        columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_TEXT)) {

                    FormEditText formEditText = new FormEditText(getActivity(), columnHeader
                            .getColumnName());
                    formWidgets.add(formEditText);
                    linearLayout.addView(formEditText.getView());

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_INT)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity
                            (), columnHeader.getColumnName());
                    formNumericEditText.setReturnType(FormWidget.SCHEMA_KEY_INT);
                    formWidgets.add(formNumericEditText);
                    linearLayout.addView(formNumericEditText.getView());


                } else if (columnHeader.getColumnDisplayType().equals(FormWidget
                        .SCHEMA_KEY_DECIMAL)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity
                            (), columnHeader.getColumnName());
                    formNumericEditText.setReturnType(FormWidget.SCHEMA_KEY_DECIMAL);
                    formWidgets.add(formNumericEditText);
                    linearLayout.addView(formNumericEditText.getView());


                } else if (columnHeader.getColumnDisplayType().equals(FormWidget
                        .SCHEMA_KEY_CODELOOKUP) || columnHeader.getColumnDisplayType().equals
                        (FormWidget.SCHEMA_KEY_CODEVALUE)) {

                    List<String> columnValueStrings = new ArrayList<String>();
                    List<Integer> columnValueIds = new ArrayList<Integer>();

                    for (ColumnValue columnValue : columnHeader.getColumnValues()) {
                        columnValueStrings.add(columnValue.getValue());
                        columnValueIds.add(columnValue.getId());
                    }

                    FormSpinner formSpinner = new FormSpinner(getActivity(), columnHeader
                            .getColumnName(), columnValueStrings, columnValueIds);
                    formSpinner.setReturnType(FormWidget.SCHEMA_KEY_CODEVALUE);
                    formWidgets.add(formSpinner);
                    linearLayout.addView(formSpinner.getView());

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_DATE)) {

                    FormEditText formEditText = new FormEditText(getActivity(), columnHeader
                            .getColumnName());
                    formEditText.setIsDateField(true, getActivity().getSupportFragmentManager());
                    formWidgets.add(formEditText);
                    linearLayout.addView(formEditText.getView());
                }
            }


        }

        Button bt_processForm = new Button(getActivity());
        bt_processForm.setLayoutParams(FormWidget.defaultLayoutParams);
        bt_processForm.setText(getString(R.string.save));

        linearLayout.addView(bt_processForm);
        bt_processForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onSaveActionRequested();
                } catch (RequiredFieldException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
        });

    }

    public void onSaveActionRequested() throws RequiredFieldException {


        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put(Constants.DATE_FORMAT, "dd-mm-YYYY");
        payload.put(Constants.LOCALE, "en");
        Iterator<FormWidget> widgetIterator = formWidgets.iterator();
        while (widgetIterator.hasNext()) {

            FormWidget formWidget = widgetIterator.next();
            if (formWidget.getReturnType().equals(FormWidget.SCHEMA_KEY_INT)) {
                payload.put(formWidget.getPropertyName(), Integer.parseInt(formWidget.getValue()
                        .equals("") ? "0" : formWidget.getValue()));
            } else if (formWidget.getReturnType().equals(FormWidget.SCHEMA_KEY_DECIMAL)) {
                payload.put(formWidget.getPropertyName(), Double.parseDouble(formWidget.getValue
                        ().equals("") ? "0.0" : formWidget.getValue()));
            } else if (formWidget.getReturnType().equals(FormWidget.SCHEMA_KEY_CODEVALUE)) {
                FormSpinner formSpinner = (FormSpinner) formWidget;
                payload.put(formWidget.getPropertyName(), formSpinner.getIdOfSelectedItem
                        (formWidget.getValue()));
            } else {
                payload.put(formWidget.getPropertyName(), formWidget.getValue());
            }

        }


        //AddDataTableEntry ApI
        mDataTableRowDialogPresenter.addDataTableEntry(
                dataTable.getRegisteredTableName(), entityId, payload);


    }

    @Override
    public void showDataTableEntrySuccessfully(GenericResponse genericResponse) {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String s, Response response) {
        Toaster.show(rootView, s);
        MFErrorParser.parseError(response);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            safeUIBlockingUtility.safelyBlockUI();
        } else {
            safeUIBlockingUtility.safelyUnBlockUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDataTableRowDialogPresenter.detachView();
    }
}
