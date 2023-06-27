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
import com.mifos.api.GenericResponse
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.DialogFragmentAddEntryToDatatableBinding
import com.mifos.mifosxdroid.formwidgets.FormEditText
import com.mifos.mifosxdroid.formwidgets.FormNumericEditText
import com.mifos.mifosxdroid.formwidgets.FormSpinner
import com.mifos.mifosxdroid.formwidgets.FormToggleButton
import com.mifos.mifosxdroid.formwidgets.FormWidget
import com.mifos.objects.noncore.DataTable
import com.mifos.utils.Constants
import com.mifos.utils.SafeUIBlockingUtility
import javax.inject.Inject

/**
 * Created by ishankhanna on 01/08/14.
 */
class DataTableRowDialogFragment : DialogFragment(), DataTableRowDialogMvpView {

    private lateinit var binding: DialogFragmentAddEntryToDatatableBinding

    private val LOG_TAG = javaClass.simpleName

    @JvmField
    @Inject
    var dataTableRowDialogPresenter: DataTableRowDialogPresenter? = null
    private var dataTable: DataTable? = null
    private var entityId = 0
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    private val listFormWidgets: MutableList<FormWidget> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
    }

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
        dataTableRowDialogPresenter?.attachView(this)
        dialog?.setTitle(dataTable?.registeredTableName)
        safeUIBlockingUtility = SafeUIBlockingUtility(
            this@DataTableRowDialogFragment
                .activity, getString(R.string.data_table_row_dialog_loading_message)
        )
        createForm(dataTable)
        addSaveButton()
        return binding.root
    }

    private fun createForm(table: DataTable?) {
        val formWidgets: MutableList<FormWidget> = ArrayList()
        if (table != null) {
            for (columnHeader in table.columnHeaderData) {
                if (!columnHeader.columnPrimaryKey) {
                    if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_STRING || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_TEXT) {
                        val formEditText = FormEditText(
                            activity, columnHeader
                                .columnName
                        )
                        formWidgets.add(formEditText)
                        binding.llDataTableEntryForm.addView(formEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_INT) {
                        val formNumericEditText = FormNumericEditText(activity, columnHeader.columnName)
                        formNumericEditText.returnType = FormWidget.SCHEMA_KEY_INT
                        formWidgets.add(formNumericEditText)
                        binding.llDataTableEntryForm.addView(formNumericEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DECIMAL) {
                        val formNumericEditText = FormNumericEditText(activity, columnHeader.columnName)
                        formNumericEditText.returnType = FormWidget.SCHEMA_KEY_DECIMAL
                        formWidgets.add(formNumericEditText)
                        binding.llDataTableEntryForm.addView(formNumericEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODELOOKUP || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODEVALUE) {
                        if (columnHeader.columnValues.size > 0) {
                            val columnValueStrings: MutableList<String> = ArrayList()
                            val columnValueIds: MutableList<Int> = ArrayList()
                            for (columnValue in columnHeader.columnValues) {
                                columnValueStrings.add(columnValue.value)
                                columnValueIds.add(columnValue.id)
                            }
                            val formSpinner = FormSpinner(
                                activity, columnHeader
                                    .columnName, columnValueStrings, columnValueIds
                            )
                            formSpinner.returnType = FormWidget.SCHEMA_KEY_CODEVALUE
                            formWidgets.add(formSpinner)
                            binding.llDataTableEntryForm.addView(formSpinner.view)
                        }
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DATE) {
                        val formEditText = FormEditText(
                            activity, columnHeader
                                .columnName
                        )
                        formEditText.setIsDateField(true, requireActivity().supportFragmentManager)
                        formWidgets.add(formEditText)
                        binding.llDataTableEntryForm.addView(formEditText.view)
                    } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_BOOL) {
                        val formToggleButton = FormToggleButton(
                            activity,
                            columnHeader.columnName
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

    @Throws(RequiredFieldException::class)
    fun onSaveActionRequested() {
        dataTableRowDialogPresenter?.addDataTableEntry(
            dataTable?.registeredTableName,
            entityId, addDataTableInput()
        )
    }

    private fun addDataTableInput(): HashMap<String, Any> {
        val formWidgets: List<FormWidget> = listFormWidgets
        val payload = HashMap<String, Any>()
        payload[Constants.DATE_FORMAT] = "dd-mm-YYYY"
        payload[Constants.LOCALE] = "en"
        for (formWidget in formWidgets) {
            when (formWidget.returnType) {
                FormWidget.SCHEMA_KEY_INT -> payload[formWidget.propertyName] =
                    (if (formWidget.value
                        == ""
                    ) "0" else formWidget.value).toInt()

                FormWidget.SCHEMA_KEY_DECIMAL -> payload[formWidget.propertyName] =
                    (if (formWidget.value == "") "0.0" else formWidget.value).toDouble()

                FormWidget.SCHEMA_KEY_CODEVALUE -> {
                    val formSpinner = formWidget as FormSpinner
                    payload[formWidget.getPropertyName()] =
                        formSpinner.getIdOfSelectedItem(formWidget.getValue())
                }

                else -> payload[formWidget.propertyName] = formWidget.value
            }
        }
        return payload
    }

    override fun showDataTableEntrySuccessfully(genericResponse: GenericResponse) {
        Toast.makeText(activity, R.string.data_table_entry_added, Toast.LENGTH_LONG).show()
        targetFragment?.onActivityResult(
            targetRequestCode, Activity.RESULT_OK,
            requireActivity().intent
        )
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility?.safelyBlockUI()
        } else {
            safeUIBlockingUtility?.safelyUnBlockUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataTableRowDialogPresenter?.detachView()
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