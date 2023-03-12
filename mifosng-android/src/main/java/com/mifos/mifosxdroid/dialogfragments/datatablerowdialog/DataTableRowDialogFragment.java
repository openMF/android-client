/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.mifos.api.GenericResponse;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.databinding.DialogFragmentAddEntryToDatatableBinding;
import com.mifos.mifosxdroid.formwidgets.FormEditText;
import com.mifos.mifosxdroid.formwidgets.FormNumericEditText;
import com.mifos.mifosxdroid.formwidgets.FormSpinner;
import com.mifos.mifosxdroid.formwidgets.FormToggleButton;
import com.mifos.mifosxdroid.formwidgets.FormWidget;
import com.mifos.objects.noncore.ColumnHeader;
import com.mifos.objects.noncore.ColumnValue;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by ishankhanna on 01/08/14.
 */
public class DataTableRowDialogFragment extends DialogFragment
        implements DataTableRowDialogMvpView {

    private final String LOG_TAG = getClass().getSimpleName();
    private DialogFragmentAddEntryToDatatableBinding binding;


    @Inject
    DataTableRowDialogPresenter dataTableRowDialogPresenter;

    private DataTable dataTable;
    private int entityId;
    private SafeUIBlockingUtility safeUIBlockingUtility;
    private List<FormWidget> listFormWidgets = new ArrayList<>();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        /**
         * This is very Important
         * It is used to auto resize the dialog when a Keyboard appears.
         * And User can still easily scroll through the form. Sweet, isn't it?
         */
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        binding = DialogFragmentAddEntryToDatatableBinding.inflate(inflater, container, false);
        dataTableRowDialogPresenter.attachView(this);


        getDialog().setTitle(dataTable.getRegisteredTableName());

        safeUIBlockingUtility = new SafeUIBlockingUtility(DataTableRowDialogFragment.this
                .getActivity(), getString(R.string.data_table_row_dialog_loading_message));

        createForm(dataTable);
        addSaveButton();

        return binding.getRoot();
    }

    public void createForm(DataTable table) {
        List<FormWidget> formWidgets = new ArrayList<>();

        for (ColumnHeader columnHeader : table.getColumnHeaderData()) {

            if (!columnHeader.getColumnPrimaryKey()) {

                if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_STRING) ||
                        columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_TEXT)) {

                    FormEditText formEditText = new FormEditText(getActivity(), columnHeader
                            .getColumnName());
                    formWidgets.add(formEditText);
                    binding.llDataTableEntryForm.addView(formEditText.getView());

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_INT)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity
                            (), columnHeader.getColumnName());
                    formNumericEditText.setReturnType(FormWidget.SCHEMA_KEY_INT);
                    formWidgets.add(formNumericEditText);
                    binding.llDataTableEntryForm.addView(formNumericEditText.getView());


                } else if (columnHeader.getColumnDisplayType().equals(FormWidget
                        .SCHEMA_KEY_DECIMAL)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity
                            (), columnHeader.getColumnName());
                    formNumericEditText.setReturnType(FormWidget.SCHEMA_KEY_DECIMAL);
                    formWidgets.add(formNumericEditText);
                    binding.llDataTableEntryForm.addView(formNumericEditText.getView());


                } else if (columnHeader.getColumnDisplayType().equals(FormWidget
                        .SCHEMA_KEY_CODELOOKUP) || columnHeader.getColumnDisplayType().equals
                        (FormWidget.SCHEMA_KEY_CODEVALUE)) {

                    if (columnHeader.getColumnValues().size() > 0) {
                        List<String> columnValueStrings = new ArrayList<>();
                        List<Integer> columnValueIds = new ArrayList<>();

                        for (ColumnValue columnValue : columnHeader.getColumnValues()) {
                            columnValueStrings.add(columnValue.getValue());
                            columnValueIds.add(columnValue.getId());
                        }

                        FormSpinner formSpinner = new FormSpinner(getActivity(), columnHeader
                                .getColumnName(), columnValueStrings, columnValueIds);
                        formSpinner.setReturnType(FormWidget.SCHEMA_KEY_CODEVALUE);
                        formWidgets.add(formSpinner);
                        binding.llDataTableEntryForm.addView(formSpinner.getView());
                    }

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_DATE)) {

                    FormEditText formEditText = new FormEditText(getActivity(), columnHeader
                            .getColumnName());
                    formEditText.setIsDateField(true, getActivity().getSupportFragmentManager());
                    formWidgets.add(formEditText);
                    binding.llDataTableEntryForm.addView(formEditText.getView());
                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_BOOL)) {

                    FormToggleButton formToggleButton = new FormToggleButton(getActivity(),
                            columnHeader.getColumnName());
                    formWidgets.add(formToggleButton);
                    binding.llDataTableEntryForm.addView(formToggleButton.getView());
                }
            }
        }
        listFormWidgets.addAll(formWidgets);
    }

    private void addSaveButton() {
        Button bt_processForm = new Button(getActivity());
        bt_processForm.setLayoutParams(FormWidget.defaultLayoutParams);
        bt_processForm.setText(getString(R.string.save));
        bt_processForm.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_dark));

        binding.llDataTableEntryForm.addView(bt_processForm);
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
        dataTableRowDialogPresenter.addDataTableEntry(dataTable.getRegisteredTableName(),
                entityId, addDataTableInput());
    }

    private HashMap<String, Object> addDataTableInput() {
        List<FormWidget> formWidgets = listFormWidgets;
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(Constants.DATE_FORMAT, "dd-mm-YYYY");
        payload.put(Constants.LOCALE, "en");
        for (FormWidget formWidget : formWidgets) {
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
        return payload;
    }

    @Override
    public void showDataTableEntrySuccessfully(GenericResponse genericResponse) {
        Toast.makeText(getActivity(), R.string.data_table_entry_added, Toast.LENGTH_LONG).show();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                getActivity().getIntent());
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
        dataTableRowDialogPresenter.detachView();
    }
}
