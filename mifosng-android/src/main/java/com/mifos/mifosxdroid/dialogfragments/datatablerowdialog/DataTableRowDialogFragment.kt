/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.DataTable
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.DialogFragmentAddEntryToDatatableBinding
import com.mifos.mifosxdroid.formwidgets.FormEditText
import com.mifos.mifosxdroid.formwidgets.FormNumericEditText
import com.mifos.mifosxdroid.formwidgets.FormSpinner
import com.mifos.mifosxdroid.formwidgets.FormToggleButton
import com.mifos.mifosxdroid.formwidgets.FormWidget
import com.mifos.utils.Constants
import com.mifos.utils.SafeUIBlockingUtility
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ishankhanna on 01/08/14.
 */
@AndroidEntryPoint
class DataTableRowDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentAddEntryToDatatableBinding

    private val LOG_TAG = javaClass.simpleName

    private lateinit var viewModel: DataTableRowDialogViewModel

    private var dataTable: DataTable? = null
    private var entityId = 0
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    private val listFormWidgets: MutableList<FormWidget> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /**
         * This is very Important
         * It is used to auto resize the dialog when a Keyboard appears.
         * And User can still easily scroll through the form. Sweet, isn't it?
         */
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = DialogFragmentAddEntryToDatatableBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DataTableRowDialogViewModel::class.java]
        dialog?.setTitle(dataTable?.registeredTableName)
        safeUIBlockingUtility = SafeUIBlockingUtility(
            requireContext(), getString(R.string.data_table_row_dialog_loading_message)
        )
        createForm(dataTable)
        addSaveButton()

        viewModel.dataTableRowDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is DataTableRowDialogUiState.ShowDataTableEntrySuccessfully -> {
                    showProgressbar(false)
                    showDataTableEntrySuccessfully(it.genericResponse)
                }

                is DataTableRowDialogUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is DataTableRowDialogUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    private fun createForm(table: DataTable?) {
        val formWidgets: MutableList<FormWidget> = ArrayList()
        if (table != null) {
            for (columnHeader in table.columnHeaderData) {
                if (!columnHeader.columnPrimaryKey!!) {
                    if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_STRING || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_TEXT) {
                        val formEditText = FormEditText(
                            activity, columnHeader
                                .dataTableColumnName
                        )
                        formWidgets.add(formEditText)
                        binding.llDataTableEntryForm.addView(formEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_INT) {
                        val formNumericEditText =
                            FormNumericEditText(activity, columnHeader.dataTableColumnName)
                        formNumericEditText.returnType = FormWidget.SCHEMA_KEY_INT
                        formWidgets.add(formNumericEditText)
                        binding.llDataTableEntryForm.addView(formNumericEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DECIMAL) {
                        val formNumericEditText =
                            FormNumericEditText(activity, columnHeader.dataTableColumnName)
                        formNumericEditText.returnType = FormWidget.SCHEMA_KEY_DECIMAL
                        formWidgets.add(formNumericEditText)
                        binding.llDataTableEntryForm.addView(formNumericEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODELOOKUP || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODEVALUE) {
                        if (columnHeader.columnValues.isNotEmpty()) {
                            val columnValueStrings: MutableList<String> = ArrayList()
                            val columnValueIds: MutableList<Int> = ArrayList()
                            for (columnValue in columnHeader.columnValues) {
                                columnValue.value?.let { columnValueStrings.add(it) }
                                columnValue.id?.let { columnValueIds.add(it) }
                            }
                            val formSpinner = FormSpinner(
                                activity, columnHeader
                                    .dataTableColumnName, columnValueStrings, columnValueIds
                            )
                            formSpinner.returnType = FormWidget.SCHEMA_KEY_CODEVALUE
                            formWidgets.add(formSpinner)
                            binding.llDataTableEntryForm.addView(formSpinner.view)
                        }
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DATE) {
                        val formEditText = FormEditText(
                            activity, columnHeader
                                .dataTableColumnName
                        )
                        formEditText.setIsDateField(true, requireActivity().supportFragmentManager)
                        formWidgets.add(formEditText)
                        binding.llDataTableEntryForm.addView(formEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_BOOL) {
                        val formToggleButton = FormToggleButton(
                            activity,
                            columnHeader.dataTableColumnName
                        )
                        formWidgets.add(formToggleButton)
                        binding.llDataTableEntryForm.addView(formToggleButton.view)
                    }
                }
            }
        }
        listFormWidgets.addAll(formWidgets)
    }

    private fun addSaveButton() {
        val bt_processForm = Button(activity)
        bt_processForm.layoutParams = FormWidget.defaultLayoutParams
        bt_processForm.text = getString(R.string.save)
        bt_processForm.setBackgroundColor(requireActivity().resources.getColor(R.color.blue_dark))
        binding.llDataTableEntryForm.addView(bt_processForm)
        bt_processForm.setOnClickListener {
            try {
                onSaveActionRequested()
            } catch (e: RequiredFieldException) {
                Log.d(LOG_TAG, e.message.toString())
            }
        }
    }

    private fun onSaveActionRequested() {
        viewModel.addDataTableEntry(
            dataTable?.registeredTableName,
            entityId, addDataTableInput()
        )
    }

    private fun addDataTableInput(): HashMap<String, String> {
        val formWidgets: List<FormWidget> = listFormWidgets
        val payload = HashMap<String, String>()
        payload[Constants.DATE_FORMAT] = "dd-mm-YYYY"
        payload[Constants.LOCALE] = "en"
        for (formWidget in formWidgets) {
            when (formWidget.returnType) {
                FormWidget.SCHEMA_KEY_INT -> payload[formWidget.propertyName] =
                    (if (formWidget.value
                        == ""
                    ) "0" else formWidget.value).toInt().toString()

                FormWidget.SCHEMA_KEY_DECIMAL -> payload[formWidget.propertyName] =
                    (if (formWidget.value == "") "0.0" else formWidget.value).toDouble().toString()

                FormWidget.SCHEMA_KEY_CODEVALUE -> {
                    val formSpinner = formWidget as FormSpinner
                    payload[formWidget.propertyName] =
                        formSpinner.getIdOfSelectedItem(formWidget.value).toString()
                }

                else -> payload[formWidget.propertyName] = formWidget.value
            }
        }
        return payload
    }

    private fun showDataTableEntrySuccessfully(genericResponse: GenericResponse) {
        Toast.makeText(activity, R.string.data_table_entry_added, Toast.LENGTH_LONG).show()
        targetFragment?.onActivityResult(
            targetRequestCode, Activity.RESULT_OK,
            requireActivity().intent
        )
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility?.safelyBlockUI()
        } else {
            safeUIBlockingUtility?.safelyUnBlockUI()
        }
    }

    companion object {
        //TODO Check for Static vs Bundle Approach
        fun newInstance(dataTable: DataTable?, entityId: Int): DataTableRowDialogFragment {
            val dataTableRowDialogFragment = DataTableRowDialogFragment()
            val args = Bundle()
            dataTableRowDialogFragment.dataTable = dataTable
            dataTableRowDialogFragment.entityId = entityId
            dataTableRowDialogFragment.arguments = args
            return dataTableRowDialogFragment
        }
    }
}