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
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxViewModel
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxViewModelFactory
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.utils.FragmentConstants
import java.lang.ClassCastException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

class CheckerTaskFilterDialogFragment : DialogFragment(), MFDatePicker.OnDatePickListener,
    AdapterView.OnItemSelectedListener {

    @BindView(R.id.sp_action)
    lateinit var spAction: AppCompatSpinner


    @BindView(R.id.sp_entity)
    lateinit var spEntity: AppCompatSpinner


    @BindView(R.id.tv_from_date)
    lateinit var tvFromDate: TextView


    @BindView(R.id.tv_to_date)
    lateinit var tvToDate: TextView

    @BindView(R.id.btn_clear_filter)
    lateinit var btnClearFilter: Button

    @BindView(R.id.btn_apply_filter)
    lateinit var btnApplyFilter: Button

    @BindView(R.id.et_resource_id)
    lateinit var etResourceId: EditText

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.dialog_fragment_checker_task_filter, container, false)
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spAction.onItemSelectedListener = this
        spEntity.onItemSelectedListener = this
        setOnClickListeners()
        tvFromDate.text = getString(R.string.select_from_date)
        toDate = "${MFDatePicker.getDatePickedAsString()} $TO_TIME"
        tvToDate.text = toDate.substringBefore(" ")
    }

    private fun setOnClickListeners() {
        tvFromDate.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                datePickerFromDate.show(
                    it1,
                    FragmentConstants.DFRAG_DATE_PICKER
                )
            }
            mCurrentDateView = tvFromDate
        }

        tvToDate.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                datePickerToDate.show(
                    it1,
                    FragmentConstants.DFRAG_DATE_PICKER
                )
            }
            mCurrentDateView = tvToDate
        }

        btnApplyFilter.setOnClickListener {
            val resourceId = etResourceId.text.toString().trim()
            var fromDateTimeStamp: Timestamp? = null
            if (fromDate.isNotEmpty()) {
                fromDateTimeStamp = Timestamp(
                    SimpleDateFormat("dd-MM-yyyy").parse(fromDate).time
                )
            }
            val toDateTimeStamp = Timestamp(
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(toDate).time
            )
            mOnInputSelected.sendInput(
                fromDateTimeStamp, toDateTimeStamp,
                selectedAction, selectedEntity, resourceId
            )
            dialog?.dismiss()
        }

        btnClearFilter.setOnClickListener {
            mOnInputSelected.sendInput(
                null, null,
                selectedAction, selectedEntity, ""
            )
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

            actionOptionsAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item, actionOptionsList
            )
            actionOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            entityOptionsAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item, entityOptionsList
            )
            entityOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            spAction.adapter = actionOptionsAdapter
            spEntity.adapter = entityOptionsAdapter

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
        fun sendInput(
            fromDate: Timestamp?, toDate: Timestamp?, action: String = "",
            entity: String = "", resourceId: String = ""
        )
    }

    override fun onDatePicked(date: String) {
        if (mCurrentDateView === tvFromDate) {
            fromDate = date
            tvFromDate.text = fromDate
        } else if (mCurrentDateView === tvToDate) {
            toDate = "$date $TO_TIME"
            tvToDate.text = toDate.substringBefore(" ")
        }
    }
}
