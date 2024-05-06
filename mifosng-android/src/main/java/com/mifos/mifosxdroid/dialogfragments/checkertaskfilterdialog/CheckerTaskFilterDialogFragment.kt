package com.mifos.mifosxdroid.dialogfragments.checkertaskfilterdialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.DialogFragmentCheckerTaskFilterBinding
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxViewModel
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxViewModelFactory
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class CheckerTaskFilterDialogFragment : DialogFragment(), MFDatePicker.OnDatePickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: DialogFragmentCheckerTaskFilterBinding

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentCheckerTaskFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.spAction.onItemSelectedListener = this
        binding.spEntity.onItemSelectedListener = this
        setOnClickListeners()
        binding.tvFromDate.text = getString(R.string.select_from_date)
        toDate = "${MFDatePicker.datePickedAsString} $TO_TIME"
        binding.tvToDate.text = toDate.substringBefore(" ")
    }

    private fun setOnClickListeners() {
        binding.tvFromDate.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                datePickerFromDate.show(
                    it1,
                    FragmentConstants.DFRAG_DATE_PICKER
                )
            }
            mCurrentDateView = binding.tvFromDate
        }

        binding.tvToDate.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                datePickerToDate.show(
                    it1,
                    FragmentConstants.DFRAG_DATE_PICKER
                )
            }
            mCurrentDateView = binding.tvToDate
        }

        binding.btnApplyFilter.setOnClickListener {
            val resourceId = binding.etResourceId.text.toString().trim()
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

        binding.btnClearFilter.setOnClickListener {
            mOnInputSelected.sendInput(
                null, null,
                selectedAction, selectedEntity, ""
            )
            dialog?.dismiss()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory)[CheckerInboxViewModel::class.java]

        viewModel.getSearchTemplate().observe(this, Observer {
            val checkerInboxSearchTemplate = it
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

            binding.spAction.adapter = actionOptionsAdapter
            binding.spEntity.adapter = entityOptionsAdapter

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

    override fun onDatePicked(date: String?) {
        if (mCurrentDateView === binding.tvFromDate) {
            if (date != null) {
                fromDate = date
            }
            binding.tvFromDate.text = fromDate
        } else if (mCurrentDateView === binding.tvToDate) {
            toDate = "$date $TO_TIME"
            binding.tvToDate.text = toDate.substringBefore(" ")
        }
    }
}
