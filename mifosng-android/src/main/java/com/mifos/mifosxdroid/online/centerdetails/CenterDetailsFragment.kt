package com.mifos.mifosxdroid.online.centerdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentCenterDetailsBinding
import com.mifos.utils.Constants
import com.mifos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@AndroidEntryPoint
class CenterDetailsFragment : MifosBaseFragment() {

    private lateinit var binding: FragmentCenterDetailsBinding

    private lateinit var viewModel: CenterDetailsViewModel

    private var centerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            centerId = requireArguments().getInt(Constants.CENTER_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCenterDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CenterDetailsViewModel::class.java]
        viewModel.loadCentersGroupAndMeeting(centerId)
        viewModel.loadSummaryInfo(centerId)

        viewModel.centerDetailsUiState.observe(viewLifecycleOwner) {
            when (it) {
                is CenterDetailsUiState.ShowCenterDetails -> {
                    showProgressbar(false)
                    showCenterDetails(it.centerWithAssociations)
                }

                is CenterDetailsUiState.ShowErrorMessage -> {
                    showProgressbar(false)
                    showErrorMessage(it.message)
                }

                is CenterDetailsUiState.ShowMeetingDetails -> {
                    showProgressbar(false)
                    showMeetingDetails(it.centerWithAssociations)
                }

                is CenterDetailsUiState.ShowProgressbar -> showProgressbar(it.state)
                is CenterDetailsUiState.ShowSummaryInfo -> {
                    showProgressbar(false)
                    showSummaryInfo(it.centerInfo)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActivateCenter.setOnClickListener {
            onClickActivateCenter()
        }
    }


    private fun onClickActivateCenter() {
        val action = CenterDetailsFragmentDirections.actionCenterDetailsFragmentToActivateFragment(
            centerId,
            Constants.ACTIVATE_CENTER
        )
        findNavController().navigate(action)
    }

    private fun showProgressbar(show: Boolean) {
        if (show) {
            binding.rlCenter.visibility = View.GONE
            showMifosProgressBar()
        } else {
            binding.rlCenter.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    private fun showCenterDetails(centerWithAssociations: CenterWithAssociations?) {
        centerWithAssociations?.name?.let { setToolbarTitle(it) }
        if (centerWithAssociations?.activationDate?.isNotEmpty() == true) {
            if (centerWithAssociations.staffName != null) {
                binding.tvStaffName.text = centerWithAssociations.staffName
            } else {
                binding.tvStaffName.setText(R.string.no_staff)
            }
            binding.tvCenterActivationDate.text =
                Utils.getStringOfDate(centerWithAssociations.activationDate)
        }
    }

    private fun showMeetingDetails(centerWithAssociations: CenterWithAssociations?) {
        if (!centerWithAssociations!!.active!!) {
            binding.llBottomPanel.visibility = View.VISIBLE
            showErrorMessage(R.string.error_center_inactive)
        }
        if (centerWithAssociations.collectionMeetingCalendar.calendarInstanceId == null) {
            binding.tvMeetingDate.text = getString(R.string.unassigned)
            if (view != null) {
                requireView().findViewById<View>(R.id.row_meeting_frequency).visibility = View.GONE
            }
        } else {
            binding.tvMeetingDate.text = Utils.getStringOfDate(
                centerWithAssociations
                    .collectionMeetingCalendar.nextTenRecurringDates[0]
            )
            if (view != null) {
                requireView().findViewById<View>(R.id.row_meeting_frequency).visibility =
                    View.VISIBLE
                binding.tvMeetingFrequency.text = centerWithAssociations.collectionMeetingCalendar
                    .humanReadable
            }
        }
    }

    private fun showSummaryInfo(centerInfos: List<CenterInfo>) {
        val centerInfo = centerInfos[0]
        binding.tvActiveClients.text = centerInfo.activeClients.toString()
        binding.tvActiveGroupLoans.text = centerInfo.activeGroupLoans.toString()
        binding.tvActiveClientLoans.text = centerInfo.activeClientLoans.toString()
        binding.tvActiveClientBorrowers.text = centerInfo.activeClientBorrowers.toString()
        binding.tvActiveGroupBorrowers.text = centerInfo.activeGroupBorrowers.toString()
        binding.tvActiveOverdueClientLoans.text = centerInfo.overdueClientLoans.toString()
        binding.tvActiveOverdueGroupLoans.text = centerInfo.overdueGroupLoans.toString()
    }

    private fun showErrorMessage(message: Int) {
        Toaster.show(binding.root, message)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_center, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_savings_account -> addCenterSavingAccount(centerId)
            R.id.view_group_list -> loadGroupsOfCenter(centerId)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addCenterSavingAccount(centerId: Int) {
        val action =
            CenterDetailsFragmentDirections.actionCenterDetailsFragmentToSavingsAccountFragment(
                centerId,
                true
            )
        findNavController().navigate(action)
    }

    private fun loadGroupsOfCenter(centerId: Int) {
        val action =
            CenterDetailsFragmentDirections.actionCenterDetailsFragmentToGroupListFragment(centerId)
        findNavController().navigate(action)
    }

}