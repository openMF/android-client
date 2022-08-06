package com.mifos.mifosxdroid.dialogfragments.checkertaskfilterdialog

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxViewModel
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxViewModelFactory
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.utils.FragmentConstants
import kotlinx.android.synthetic.main.dialog_fragment_checker_task_filter.*
import java.lang.ClassCastException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

class CheckerTaskFilterDialogFragment : DialogFragment(), MFDatePicker.OnDatePickListener,
        AdapterView.OnItemSelectedListener {

    private lateinit var mOnInputSelected: OnInputSelected

    private lateinit var datePickerFromDate: DialogFragment
    private lateinit var datePickerToDate: DialogFragment
    private lateinit var mCurrentDateView: View
    private lateinit var selectedAction: String
    private lateinit var selectedEntity: String

    private lateinit var actionOptionsList: MutableList<String>
    private lateinit var entityOptionsList: MutableList<String>
    private lateinit var actionOptionsAdapter: ArrayAdapter<String>
    private lateinit var entityOptionsAdapter: ArrayAdapter<String>

    private lateinit var fromDate: String
    private lateinit var toDate: String

    private val ALL = "ALL"
    private val TO_TIME = "23:59:59"

    @Inject
    lateinit var factory: CheckerInboxViewModelFactory
    private lateinit var viewModel: CheckerInboxViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionOptionsList = mutableListOf()
        entityOptionsList = mutableListOf()
        actionOptionsList.add(ALL)
        entityOptionsList.add(ALL)
        fromDate = ""
        selectedAction = ALL
        selectedEntity = ALL
        datePickerFromDate = MFDatePicker.newInsance(this)
        datePickerToDate = MFDatePicker.newInsance(this)

        (activity as MifosBaseActivity).activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.dialog_fragment_checker_task_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp_action.onItemSelectedListener = this
        sp_entity.onItemSelectedListener = this
        setOnClickListeners()
        tv_from_date.text = getString(R.string.select_from_date)
        toDate = "${MFDatePicker.getDatePickedAsString()} $TO_TIME"
        tv_to_date.text = toDate.substringBefore(" ")
    }

    private fun setOnClickListeners() {
        tv_from_date.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                datePickerFromDate.show(it1,
                        FragmentConstants.DFRAG_DATE_PICKER)
            }
            mCurrentDateView = tv_from_date
        }

        tv_to_date.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                datePickerToDate.show(it1,
                        FragmentConstants.DFRAG_DATE_PICKER)
            }
            mCurrentDateView = tv_to_date
        }

        btn_apply_filter.setOnClickListener {
            val resourceId = et_resource_id.text.toString().trim()
            var fromDateTimeStamp: Timestamp? = null
            if (fromDate.isNotEmpty()) {
                fromDateTimeStamp = Timestamp(
                        SimpleDateFormat("dd-MM-yyyy").parse(fromDate).time
                )
            }
            val toDateTimeStamp = Timestamp(
                    SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(toDate).time
            )
            mOnInputSelected.sendInput(fromDateTimeStamp, toDateTimeStamp,
                    selectedAction, selectedEntity, resourceId)
            dialog?.dismiss()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory)
                .get(CheckerInboxViewModel::class.java)

        viewModel.getSearchTemplate().observe(this, Observer {
            val checkerInboxSearchTemplate = it!!
            actionOptionsList.addAll(checkerInboxSearchTemplate.actionNames)
            entityOptionsList.addAll(checkerInboxSearchTemplate.entityNames)

            actionOptionsAdapter = ArrayAdapter(requireActivity(),
                    android.R.layout.simple_spinner_item, actionOptionsList)
            actionOptionsAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item)

            entityOptionsAdapter = ArrayAdapter(requireActivity(),
                    android.R.layout.simple_spinner_item, entityOptionsList)
            entityOptionsAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item)

            sp_action.adapter = actionOptionsAdapter
            sp_entity.adapter = entityOptionsAdapter

            selectedAction = checkerInboxSearchTemplate.actionNames[0]
            selectedEntity = checkerInboxSearchTemplate.entityNames[0]
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnInputSelected = targetFragment as OnInputSelected
        } catch (e: ClassCastException) {
            Log.e("TaskFilterDialog", e.message.toString())
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0?.id) {
            R.id.sp_action -> {
                selectedAction = p0.getItemAtPosition(p2) as String
            }
            R.id.sp_entity -> {
                selectedEntity = p0.getItemAtPosition(p2) as String
            }
        }
    }

    interface OnInputSelected {
        fun sendInput(fromDate: Timestamp?, toDate: Timestamp, action: String = "",
                      entity: String = "", resourceId: String = "")
    }

    override fun onDatePicked(date: String) {
        if (mCurrentDateView === tv_from_date) {
            fromDate = date
            tv_from_date.text = fromDate
        } else if (mCurrentDateView === tv_to_date) {
            toDate = "$date $TO_TIME"
            tv_to_date.text = toDate.substringBefore(" ")
        }
    }
}
