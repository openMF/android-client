package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.CheckerTaskListAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.dialogfragments.checkertaskfilterdialog.CheckerTaskFilterDialogFragment
import com.mifos.objects.CheckerTask
import kotlinx.android.synthetic.main.checker_inbox_fragment.*
import java.sql.Timestamp
import javax.inject.Inject

class CheckerInboxFragment : MifosBaseFragment(), TextWatcher,
        CheckerTaskFilterDialogFragment.OnInputSelected,
        CheckerTaskListAdapter.OnItemClickListener,
        CheckerTaskListAdapter.CheckerTaskBadgeProcessMode {

    override fun onItemLongPress(position: Int) {
        view_flipper.showNext()
        if (inBadgeProcessingMode) {
            tv_no_of_selected_tasks.text = "0"
            selectedCheckerTaskList.clear()
            inBadgeProcessingMode = false
            checkerTaskListAdapter.notifyDataSetChanged()
        } else {
            inBadgeProcessingMode = true
            checkerTaskListAdapter.notifyDataSetChanged()
        }
    }

    override fun isInBadgeProcessingMode(): Boolean {
        return inBadgeProcessingMode
    }

    companion object {
        fun newInstance() = CheckerInboxFragment()
    }

    @Inject
    lateinit var factory: CheckerInboxViewModelFactory
    private lateinit var viewModel: CheckerInboxViewModel
    private lateinit var checkerTaskListAdapter: CheckerTaskListAdapter

    /**This is the list of CheckerTasks received from the server.
    It serves as a backup list whenever the 'checkerTaskList' needs to be updated to the
    original fetched list. It updates only when a task is removed from the server in case of
    a successful operation*/
    private var fetchedCheckerTaskList = mutableListOf<CheckerTask>()

    /**This list can be updated every time the RecyclerView needs to be updated. For example in
    of filtering the list according to maker or applying different filter options. When it needs
    to be updated to the full checkerTask list, it is being set to the 'fetchedCheckerTaskList'*/
    private var checkerTaskList = mutableListOf<CheckerTask>()

    /** This list is being used to keep track of selected items in case of Batch processing of
    tasks.*/
    private var selectedCheckerTaskList = mutableListOf<CheckerTask>()

    private var adapterPosition: Int = -1
    var inBadgeProcessingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.checker_inbox_fragment,
                container, false)
        setToolbarTitle(resources.getString(R.string.checker_inbox))
        showMifosProgressBar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_search.addTextChangedListener(this)
        rv_checker_inbox.layoutManager = LinearLayoutManager(activity)
        rv_checker_inbox.hasFixedSize()
        checkerTaskListAdapter = CheckerTaskListAdapter()
        checkerTaskListAdapter.setBadgeProcessMode(this)
        rv_checker_inbox.adapter = checkerTaskListAdapter
        setUpOnClickListeners()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory)
                .get(CheckerInboxViewModel::class.java)

        viewModel.getStatus().observe(this@CheckerInboxFragment,
                Observer {
                    handleStatus(it)
                })

        viewModel.getCheckerTasks().observe(this, Observer {
            hideMifosProgressBar()
            checkerTaskList.clear()
            fetchedCheckerTaskList.addAll(it!!)
            checkerTaskList.addAll(fetchedCheckerTaskList)
            checkerTaskListAdapter.submitList(checkerTaskList)
        })
    }

    private fun showConfirmationDialog(
            title: String, message: String,
            onPositiveClick: DialogInterface.OnClickListener) {
        MaterialDialog.Builder().init(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), onPositiveClick)
                .setNegativeButton(getString(R.string.cancel))
                .show()
    }

    override fun onItemClick(position: Int) {
    }

    /**
     * This Method is executed when user clicks on Approve icon.
     * It makes a network call to the server to Approve the task by
     * passing task id as parameter.
     * @param position Int
     */
    override fun onApproveClick(position: Int) {
        adapterPosition = position
        showConfirmationDialog(getString(R.string.approve),
                getString(R.string.approve_confirmation),
                DialogInterface.OnClickListener { p0, p1 ->
                    showMifosProgressBar()
                    viewModel.approveCheckerEntry(
                            checkerTaskList[adapterPosition].id)
                })
    }

    /**
     * This Method is executed when user clicks on Reject icon.
     * It makes a network call to the server to reject the task by
     * passing task id as parameter.
     * @param position Int
     */
    override fun onRejectClick(position: Int) {
        adapterPosition = position
        showConfirmationDialog(getString(R.string.reject),
                getString(R.string.reject_confirmation),
                DialogInterface.OnClickListener { p0, p1 ->
                    showMifosProgressBar()
                    viewModel.rejectCheckerEntry(
                            checkerTaskList[adapterPosition].id)
                })
    }

    /**
     * This Method is executed when user clicks on Delete icon.
     * It makes a network call to the server to delete the task by
     * passing task id as parameter.
     * @param position Int
     */
    override fun onDeleteClick(position: Int) {
        adapterPosition = position
        showConfirmationDialog(getString(R.string.delete),
                getString(R.string.delete_confirmation),
                DialogInterface.OnClickListener { p0, p1 ->
                    showMifosProgressBar()
                    viewModel.deleteCheckerEntry(
                            checkerTaskList[adapterPosition].id)
                })
    }

    /**
     * This Method is executed whenever 'status' LiveData is updated. It can have 6 different
     * values according to the result of the network call. Depending upon the 'status' value
     * further execution is performed. For example, when the value of 'status' is
     * Status.APPROVE_SUCCESS, it means that Approving of a checker task has been
     * performed successfully. Now we can do further execution like updating of recycler view and
     * other things according to the needs.
     * @param status Status?
     */
    private fun handleStatus(status: Status?) {
        when (status) {
            Status.APPROVE_SUCCESS -> {
                // Check if the tasks are being approved using batch processing mode
                if (selectedCheckerTaskList.isNotEmpty()) {
                    val task = selectedCheckerTaskList.removeAt(0)
                    checkerTaskList.remove(task)
                    fetchedCheckerTaskList.remove(task)

                    // Check if more tasks are available for batch processing
                    if (selectedCheckerTaskList.isNotEmpty()) {
                        viewModel.approveCheckerEntry(
                                selectedCheckerTaskList[0].id)
                    } else {
                        // No more tasks are available for batch processing
                        hideMifosProgressBar()
                        tv_no_of_selected_tasks.text = "0"
                        inBadgeProcessingMode = false
                        checkerTaskListAdapter.submitList(checkerTaskList)
                        view_flipper.showNext()
                    }
                } else {
                    // Single Entry Approved (without batch processing)
                    hideMifosProgressBar()
                    updateRecyclerViewAfterOperation(adapterPosition,
                            "APPROVED")
                }
            }

            Status.REJECT_SUCCESS -> {
                // Check if the tasks are being rejected using batch processing mode
                if (selectedCheckerTaskList.isNotEmpty()) {
                    val task = selectedCheckerTaskList.removeAt(0)
                    checkerTaskList.remove(task)
                    fetchedCheckerTaskList.remove(task)

                    // Check if more tasks are available for batch processing
                    if (selectedCheckerTaskList.isNotEmpty()) {
                        viewModel.rejectCheckerEntry(
                                selectedCheckerTaskList[0].id)
                    } else {
                        // No more tasks are available for batch processing
                        hideMifosProgressBar()
                        tv_no_of_selected_tasks.text = "0"
                        inBadgeProcessingMode = false
                        checkerTaskListAdapter.submitList(checkerTaskList)
                        view_flipper.showNext()
                    }
                } else {
                    // Single Entry Rejected (without batch processing)
                    hideMifosProgressBar()
                    updateRecyclerViewAfterOperation(adapterPosition,
                            "REJECTED")
                }
            }
            Status.DELETE_SUCCESS -> {
                // Check if the tasks are being deleted using batch processing mode
                if (selectedCheckerTaskList.isNotEmpty()) {
                    val task = selectedCheckerTaskList.removeAt(0)
                    checkerTaskList.remove(task)
                    fetchedCheckerTaskList.remove(task)

                    // Check if more tasks are available for batch processing
                    if (selectedCheckerTaskList.isNotEmpty()) {
                        viewModel.deleteCheckerEntry(
                                selectedCheckerTaskList[0].id)
                    } else {
                        // No more tasks are available for batch processing
                        hideMifosProgressBar()
                        tv_no_of_selected_tasks.text = "0"
                        inBadgeProcessingMode = false
                        checkerTaskListAdapter.submitList(checkerTaskList)
                        view_flipper.showNext()
                    }
                } else {
                    // Single Entry Deleted (without batch processing)
                    hideMifosProgressBar()
                    updateRecyclerViewAfterOperation(adapterPosition,
                            "DELETED")
                }
            }
            Status.APPROVE_ERROR -> {
                if (selectedCheckerTaskList.isNotEmpty()) {
                    selectedCheckerTaskList.removeAt(0)
                    if (selectedCheckerTaskList.isNotEmpty()) {
                        viewModel.approveCheckerEntry(
                                selectedCheckerTaskList[0].id)
                    } else {
                        hideMifosProgressBar()
                        tv_no_of_selected_tasks.text = "0"
                        inBadgeProcessingMode = false
                        checkerTaskListAdapter.submitList(checkerTaskList)
                        view_flipper.showNext()
                    }
                } else {
                    hideMifosProgressBar()
                    showNetworkError()
                }
            }
            Status.REJECT_ERROR -> {
                if (selectedCheckerTaskList.isNotEmpty()) {
                    selectedCheckerTaskList.removeAt(0)
                    if (selectedCheckerTaskList.isNotEmpty()) {
                        viewModel.rejectCheckerEntry(
                                selectedCheckerTaskList[0].id)
                    } else {
                        hideMifosProgressBar()
                        tv_no_of_selected_tasks.text = "0"
                        inBadgeProcessingMode = false
                        checkerTaskListAdapter.submitList(checkerTaskList)
                        view_flipper.showNext()
                    }
                } else {
                    showNetworkError()
                }
            }
            Status.DELETE_ERROR -> {
                if (selectedCheckerTaskList.isNotEmpty()) {
                    selectedCheckerTaskList.removeAt(0)
                    if (selectedCheckerTaskList.isNotEmpty()) {
                        viewModel.deleteCheckerEntry(
                                selectedCheckerTaskList[0].id)
                    } else {
                        hideMifosProgressBar()
                        tv_no_of_selected_tasks.text = "0"
                        inBadgeProcessingMode = false
                        checkerTaskListAdapter.submitList(checkerTaskList)
                        view_flipper.showNext()
                    }
                } else {
                    showNetworkError()
                }
            }
            else -> {}
        }
    }

    private fun setUpOnClickListeners() {
        checkerTaskListAdapter.setOnItemClickListener(this)

        iv_filter_search_icon.setOnClickListener {
            val dialogSearchFilter = CheckerTaskFilterDialogFragment()
            dialogSearchFilter.setTargetFragment(this@CheckerInboxFragment, 1)
            dialogSearchFilter.show(requireActivity().supportFragmentManager, "DialogSearchFilter")
        }

        iv_batch_approve_icon.setOnClickListener {
            if (selectedCheckerTaskList.isNotEmpty()) {
                showConfirmationDialog(getString(R.string.approve),
                        getString(R.string.approve_selected_entries),
                        DialogInterface.OnClickListener { p0, p1 ->
                            viewModel.approveCheckerEntry(
                                    selectedCheckerTaskList[0].id)
                            showMifosProgressBar()
                        })
            } else {
                Toast.makeText(activity, getString(R.string.no_task_selected),
                        Toast.LENGTH_SHORT).show()
            }
        }

        iv_batch_reject_icon.setOnClickListener {
            if (selectedCheckerTaskList.isNotEmpty()) {
                showConfirmationDialog(getString(R.string.reject),
                        getString(R.string.reject_selected_entries),
                        DialogInterface.OnClickListener { p0, p1 ->
                            viewModel.rejectCheckerEntry(
                                    selectedCheckerTaskList[0].id)
                            showMifosProgressBar()
                        })
            } else {
                Toast.makeText(activity, getString(R.string.no_task_selected),
                        Toast.LENGTH_SHORT).show()
            }
        }

        iv_batch_delete_icon.setOnClickListener {
            if (selectedCheckerTaskList.isNotEmpty()) {
                showConfirmationDialog(getString(R.string.reject),
                        getString(R.string.reject_selected_entries),
                        DialogInterface.OnClickListener { p0, p1 ->
                            viewModel.rejectCheckerEntry(
                                    selectedCheckerTaskList[0].id)
                            showMifosProgressBar()
                        })
            } else {
                Toast.makeText(activity, getString(R.string.no_task_selected),
                        Toast.LENGTH_SHORT).show()
            }
        }

        iv_deselect_all.setOnClickListener {
            view_flipper.showNext()
            tv_no_of_selected_tasks.text = "0"
            selectedCheckerTaskList.clear()
            inBadgeProcessingMode = false
            checkerTaskListAdapter.notifyDataSetChanged()
        }
    }

    /**
     * This method is executed after a successful Approve, Reject or Delete operation on a single
     * checker task (i.e. in non-batch processing mode)
     * @param position Int
     * @param action String
     */
    private fun updateRecyclerViewAfterOperation(position: Int, action: String) {
        val task = checkerTaskList.removeAt(position)
        fetchedCheckerTaskList.remove(task)
        checkerTaskListAdapter.submitList(checkerTaskList)
        Toast.makeText(activity, getString(R.string.entry) + action, Toast.LENGTH_SHORT).show()
    }

    private fun showNetworkError() {
        Toast.makeText(activity, getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT).show()
    }

    override fun afterTextChanged(p0: Editable?) {
        filter(p0.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    /**
     * This Method is executed whenever something is typed in 'Search by user' EditText. It checks
     * for the typed characters in the maker of different checker tasks and updates the list
     * accordingly. In case it is empty or blank it simply sets the checkerTaskList to the
     * fetchedCheckerTaskList to save the overload of checking every checkerTask.
     * @param text String?
     */
    private fun filter(text: String) {
        if (text.isEmpty()) {
            updateRecyclerViewWithNewList(fetchedCheckerTaskList)
        } else {
            val filteredList = mutableListOf<CheckerTask>()
            for (checkerTask in fetchedCheckerTaskList) {
                if (checkerTask.maker.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(checkerTask)
                }
            }
            checkerTaskListAdapter.submitList(filteredList)
            updateRecyclerViewWithNewList(filteredList)
        }
    }

    /**
     * This Method is executed whenever checker tasks are checked
     * or unchecked in the batch-processing mode.
     * @param view View
     * @param position Int
     */
    override fun onItemSelectedOrDeselcted(view: View, position: Int) {
        val task = checkerTaskList[position]
        if ((view as CheckBox).isChecked) {
            task.selectedFlag = true
            checkerTaskListAdapter.notifyItemChanged(position)
            selectedCheckerTaskList.add(task)
            tv_no_of_selected_tasks.text = selectedCheckerTaskList.size.toString()
        } else {
            task.selectedFlag = false
            checkerTaskListAdapter.notifyItemChanged(position)
            selectedCheckerTaskList.remove(task)
            tv_no_of_selected_tasks.text = selectedCheckerTaskList.size.toString()
        }
    }

    /**
     * This Method is being used to get the filtered list by applying different types of search
     * filters.
     * @param fromDate Timestamp?
     * @param toDate Timestamp?
     * @param action String
     * @param entity String
     * @param resourceId String
     * @return MutableList<CheckerTask>
     */
    private fun getFilteredList(fromDate: Timestamp?, toDate: Timestamp,
                                action: String, entity: String,
                                resourceId: String)
            : MutableList<CheckerTask> {
        val filteredList = mutableListOf<CheckerTask>()
        val ALL = "ALL"

        if (resourceId.isNotEmpty()) {
            // If resource id is available there is no need to check for other filter options
            for (checkerTask in fetchedCheckerTaskList) {

                if (resourceId == checkerTask.resourceId) {
                    filteredList.add(checkerTask)
                }
            }
            return filteredList
        } else {
            // Resource Id is not available.
            if (fromDate == null) {
                // From Date is not available
                if (action == ALL && entity == ALL) {
                    // No need to check for Action and Entity
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (!checkerTask.getTimeStamp().after(toDate)) {
                            filteredList.add(checkerTask)
                        }
                    }
                    return filteredList
                } else if (action == ALL) {
                    // Entity has a specific value
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().before(toDate)) {
                            if (entity.equals(checkerTask.entity, true)) {
                                filteredList.add(checkerTask)
                            }

                        }
                    }
                    return filteredList
                } else if (entity == ALL) {
                    // Action has a specific value
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().before(toDate)) {
                            if (action.equals(checkerTask.action, true)) {
                                filteredList.add(checkerTask)
                            }
                        }
                    }
                    return filteredList
                } else {
                    // Both Action and Entity have specific values
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().before(toDate)) {
                            if (action.equals(checkerTask.action, true) &&
                                    entity.equals(checkerTask.entity, true)) {
                                filteredList.add(checkerTask)
                            }
                        }
                    }
                    return filteredList
                }
            } else {
                // Both dates are available
                if (action == ALL && entity == ALL) {
                    // No need to check for Action and Entity
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().after(fromDate)
                                && checkerTask.getTimeStamp().before(toDate)) {
                            filteredList.add(checkerTask)
                        }
                    }
                    return filteredList
                } else if (action == ALL) {
                    // Entity has a specific value
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().after(fromDate)
                                && checkerTask.getTimeStamp().before(toDate)) {
                            if (entity.equals(checkerTask.entity, true)) {
                                filteredList.add(checkerTask)
                            }

                        }
                    }
                    return filteredList
                } else if (entity == ALL) {
                    // Action has a specific value
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().after(fromDate)
                                && checkerTask.getTimeStamp().before(toDate)) {
                            if (action.equals(checkerTask.action, true)) {
                                filteredList.add(checkerTask)
                            }
                        }
                    }
                    return filteredList
                } else {
                    // Both Action and Entity have specific values
                    for (checkerTask in fetchedCheckerTaskList) {
                        if (checkerTask.getTimeStamp().after(fromDate)
                                && checkerTask.getTimeStamp().before(toDate)) {
                            if (action.equals(checkerTask.action, true) &&
                                    entity.equals(checkerTask.entity, true)) {
                                filteredList.add(checkerTask)
                            }
                        }
                    }
                    return filteredList
                }
            }
        }
    }

    /**
     * This method takes care of filtering the list and updating the RecyclerView on the basis of
     * the values of the search filters sent by the Dialog Fragment.
     * @param fromDate Timestamp?
     * @param toDate Timestamp?
     * @param action String
     * @param entity String
     * @param resourceId String
     */
    override fun sendInput(fromDate: Timestamp?, toDate: Timestamp,
                           action: String, entity: String, resourceId: String) {
        val filteredList = getFilteredList(fromDate, toDate, action, entity, resourceId)
        updateRecyclerViewWithNewList(filteredList)
    }

    /**
     * This method takes updating the 'checkerTaskList' and the RecyclerView as per the
     * requirements.
     * @param updatedList List<CheckerTask>
     */
    private fun updateRecyclerViewWithNewList(updatedList: List<CheckerTask>) {
        checkerTaskList.clear()
        checkerTaskList.addAll(updatedList)
        checkerTaskListAdapter.submitList(checkerTaskList)
    }
}
