package com.mifos.mifosxdroid.online.checkerinbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mifos.core.common.utils.Constants
import com.mifos.feature.checker_inbox_task.checker_inbox.ui.CheckerInboxScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.dialogfragments.checkertaskfilterdialog.CheckerTaskFilterDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp

@AndroidEntryPoint
class CheckerInboxFragment : MifosBaseFragment(),CheckerTaskFilterDialogFragment.OnInputSelected {
//    CheckerTaskListAdapter.CheckerTaskBadgeProcessMode


//    override fun onItemLongPress(position: Int) {
//        binding.viewFlipper.showNext()
//        if (inBadgeProcessingMode) {
//            binding.tvNoOfSelectedTasks.text = "0"
//            selectedCheckerTaskList.clear()
//            inBadgeProcessingMode = false
//            checkerTaskListAdapter.notifyDataSetChanged()
//        } else {
//            inBadgeProcessingMode = true
//            checkerTaskListAdapter.notifyDataSetChanged()
//        }
//    }

    //    override fun isInBadgeProcessingMode(): Boolean {
//        return inBadgeProcessingMode
//    }


//    /** This list is being used to keep track of selected items in case of Batch processing of
//    tasks.*/
//    private var selectedCheckerTaskList = mutableListOf<CheckerTask>()
//
//    private var adapterPosition: Int = -1
//    var inBadgeProcessingMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                CheckerInboxScreen(onBackPressed = {
                    requireActivity().onBackPressed()
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

//    /**
//     * This Method is executed whenever 'status' LiveData is updated. It can have 6 different
//     * values according to the result of the network call. Depending upon the 'status' value
//     * further execution is performed. For example, when the value of 'status' is
//     * Status.APPROVE_SUCCESS, it means that Approving of a checker task has been
//     * performed successfully. Now we can do further execution like updating of recycler view and
//     * other things according to the needs.
//     * @param status Status?
//     */
//    private fun handleStatus(status: Status?) {
//        when (status) {
//            Status.APPROVE_SUCCESS -> {
//                // Check if the tasks are being approved using batch processing mode
//                if (selectedCheckerTaskList.isNotEmpty()) {
//                    val task = selectedCheckerTaskList.removeAt(0)
//                    checkerTaskList.remove(task)
//                    fetchedCheckerTaskList.remove(task)
//
//                    // Check if more tasks are available for batch processing
//                    if (selectedCheckerTaskList.isNotEmpty()) {
//                        viewModel.approveCheckerEntry(
//                            selectedCheckerTaskList[0].id
//                        )
//                    } else {
//                        // No more tasks are available for batch processing
//                        hideMifosProgressBar()
//                        binding.tvNoOfSelectedTasks.text = "0"
//                        inBadgeProcessingMode = false
//                        checkerTaskListAdapter.submitList(checkerTaskList)
//                        binding.viewFlipper.showNext()
//                    }
//                } else {
//                    // Single Entry Approved (without batch processing)
//                    hideMifosProgressBar()
//                    updateRecyclerViewAfterOperation(
//                        adapterPosition,
//                        "APPROVED"
//                    )
//                }
//            }
//
//            Status.REJECT_SUCCESS -> {
//                // Check if the tasks are being rejected using batch processing mode
//                if (selectedCheckerTaskList.isNotEmpty()) {
//                    val task = selectedCheckerTaskList.removeAt(0)
//                    checkerTaskList.remove(task)
//                    fetchedCheckerTaskList.remove(task)
//
//                    // Check if more tasks are available for batch processing
//                    if (selectedCheckerTaskList.isNotEmpty()) {
//                        viewModel.rejectCheckerEntry(
//                            selectedCheckerTaskList[0].id
//                        )
//                    } else {
//                        // No more tasks are available for batch processing
//                        hideMifosProgressBar()
//                        binding.tvNoOfSelectedTasks.text = "0"
//                        inBadgeProcessingMode = false
//                        checkerTaskListAdapter.submitList(checkerTaskList)
//                        binding.viewFlipper.showNext()
//                    }
//                } else {
//                    // Single Entry Rejected (without batch processing)
//                    hideMifosProgressBar()
//                    updateRecyclerViewAfterOperation(
//                        adapterPosition,
//                        "REJECTED"
//                    )
//                }
//            }
//
//            Status.DELETE_SUCCESS -> {
//                // Check if the tasks are being deleted using batch processing mode
//                if (selectedCheckerTaskList.isNotEmpty()) {
//                    val task = selectedCheckerTaskList.removeAt(0)
//                    checkerTaskList.remove(task)
//                    fetchedCheckerTaskList.remove(task)
//
//                    // Check if more tasks are available for batch processing
//                    if (selectedCheckerTaskList.isNotEmpty()) {
//                        viewModel.deleteCheckerEntry(
//                            selectedCheckerTaskList[0].id
//                        )
//                    } else {
//                        // No more tasks are available for batch processing
//                        hideMifosProgressBar()
//                        binding.tvNoOfSelectedTasks.text = "0"
//                        inBadgeProcessingMode = false
//                        checkerTaskListAdapter.submitList(checkerTaskList)
//                        binding.viewFlipper.showNext()
//                    }
//                } else {
//                    // Single Entry Deleted (without batch processing)
//                    hideMifosProgressBar()
//                    updateRecyclerViewAfterOperation(
//                        adapterPosition,
//                        "DELETED"
//                    )
//                }
//            }
//
//            Status.APPROVE_ERROR -> {
//                if (selectedCheckerTaskList.isNotEmpty()) {
//                    selectedCheckerTaskList.removeAt(0)
//                    if (selectedCheckerTaskList.isNotEmpty()) {
//                        viewModel.approveCheckerEntry(
//                            selectedCheckerTaskList[0].id
//                        )
//                    } else {
//                        hideMifosProgressBar()
//                        binding.tvNoOfSelectedTasks.text = "0"
//                        inBadgeProcessingMode = false
//                        checkerTaskListAdapter.submitList(checkerTaskList)
//                        binding.viewFlipper.showNext()
//                    }
//                } else {
//                    hideMifosProgressBar()
//                    showNetworkError()
//                }
//            }
//
//            Status.REJECT_ERROR -> {
//                if (selectedCheckerTaskList.isNotEmpty()) {
//                    selectedCheckerTaskList.removeAt(0)
//                    if (selectedCheckerTaskList.isNotEmpty()) {
//                        viewModel.rejectCheckerEntry(
//                            selectedCheckerTaskList[0].id
//                        )
//                    } else {
//                        hideMifosProgressBar()
//                        binding.tvNoOfSelectedTasks.text = "0"
//                        inBadgeProcessingMode = false
//                        checkerTaskListAdapter.submitList(checkerTaskList)
//                        binding.viewFlipper.showNext()
//                    }
//                } else {
//                    showNetworkError()
//                }
//            }
//
//            Status.DELETE_ERROR -> {
//                if (selectedCheckerTaskList.isNotEmpty()) {
//                    selectedCheckerTaskList.removeAt(0)
//                    if (selectedCheckerTaskList.isNotEmpty()) {
//                        viewModel.deleteCheckerEntry(
//                            selectedCheckerTaskList[0].id
//                        )
//                    } else {
//                        hideMifosProgressBar()
//                        binding.tvNoOfSelectedTasks.text = "0"
//                        inBadgeProcessingMode = false
//                        checkerTaskListAdapter.submitList(checkerTaskList)
//                        binding.viewFlipper.showNext()
//                    }
//                } else {
//                    showNetworkError()
//                }
//            }
//
//            else -> {}
//        }
//    }
//
    private fun setUpOnClickListeners() {
        val dialogSearchFilter = CheckerTaskFilterDialogFragment()
        dialogSearchFilter.setTargetFragment(this, Constants.DIALOG_FRAGMENT)
        dialogSearchFilter.show(parentFragmentManager, "DialogSearchFilter")
    }
//
//        binding.ivDeselectAll.setOnClickListener {
//            binding.viewFlipper.showNext()
//            binding.tvNoOfSelectedTasks.text = "0"
//            selectedCheckerTaskList.clear()
//            inBadgeProcessingMode = false
//            checkerTaskListAdapter.notifyDataSetChanged()
//        }
//    }

//
//    /**
//     * This Method is executed whenever checker tasks are checked
//     * or unchecked in the batch-processing mode.
//     * @param view View
//     * @param position Int
//     */
//    override fun onItemSelectedOrDeselcted(view: View, position: Int) {
//        val task = checkerTaskList[position]
//        if ((view as CheckBox).isChecked) {
//            task.selectedFlag = true
//            checkerTaskListAdapter.notifyItemChanged(position)
//            selectedCheckerTaskList.add(task)
//            binding.tvNoOfSelectedTasks.text = selectedCheckerTaskList.size.toString()
//        } else {
//            task.selectedFlag = false
//            checkerTaskListAdapter.notifyItemChanged(position)
//            selectedCheckerTaskList.remove(task)
//            binding.tvNoOfSelectedTasks.text = selectedCheckerTaskList.size.toString()
//        }
//    }


    /**
     * This method takes care of filtering the list and updating the RecyclerView on the basis of
     * the values of the search filters sent by the Dialog Fragment.
     * @param fromDate Timestamp?
     * @param toDate Timestamp?
     * @param action String
     * @param entity String
     * @param resourceId String
     */
    override fun sendInput(
        fromDate: Timestamp?, toDate: Timestamp?, action: String, entity: String,
        resourceId: String
    ) {
//        val filteredList = getFilteredList(fromDate, toDate, action, entity, resourceId)
//        updateRecyclerViewWithNewList(filteredList)
    }
}