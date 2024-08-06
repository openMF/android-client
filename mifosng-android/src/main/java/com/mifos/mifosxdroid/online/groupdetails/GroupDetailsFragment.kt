package com.mifos.mifosxdroid.online.groupdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.navigation.ClientListArgs
import com.mifos.feature.groups.group_details.GroupDetailsScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 2/27/2016.
 */
@AndroidEntryPoint
class GroupDetailsFragment : MifosBaseFragment() {

    private var groupId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            groupId = requireArguments().getInt(Constants.GROUP_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                GroupDetailsScreen(
                    groupId = groupId,
                    onBackPressed = {
                        requireActivity().onBackPressed()
                    },
                    addLoanAccount = {
                        addGroupLoanAccount(it)
                    },
                    addSavingsAccount = {
                        addGroupSavingsAccount(it)
                    },
                    documents = {
                        loadDocuments(it)
                    },
                    groupClients = {
                        loadGroupClients(it)
                    },
                    moreGroupInfo = {
                        loadGroupDataTables(it)
                    },
                    notes = {
                        loadNotes(it)
                    },
                    loanAccountSelected = {
                        loadLoanAccountSummary(it)
                    },
                    savingsAccountSelected = { savingsId, depositType ->
                        loadSavingsAccountSummary(savingsId, depositType)
                    },
                    activateGroup = {
                        onClickActivateGroup(it)
                    }
                )
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

    private fun onClickActivateGroup(groupId: Int) {
        val action = GroupDetailsFragmentDirections.actionGroupDetailsFragmentToActivateFragment(
            groupId,
            Constants.ACTIVATE_GROUP
        )
        findNavController().navigate(action)
    }

    private fun loadDocuments(groupId: Int) {
        val action =
            GroupDetailsFragmentDirections.actionGroupDetailsFragmentToDocumentListFragment(
                groupId,
                Constants.ENTITY_TYPE_GROUPS
            )
        findNavController().navigate(action)
    }

    private fun loadNotes(groupId: Int) {
        val action = GroupDetailsFragmentDirections.actionGroupDetailsFragmentToNoteFragment(
            groupId,
            Constants.ENTITY_TYPE_GROUPS
        )
        findNavController().navigate(action)
    }

    private fun addGroupSavingsAccount(groupId: Int) {
        val action =
            GroupDetailsFragmentDirections.actionGroupDetailsFragmentToSavingsAccountFragment(
                groupId,
                true
            )
        findNavController().navigate(action)
    }

    private fun addGroupLoanAccount(groupId: Int) {
        val action =
            GroupDetailsFragmentDirections.actionGroupDetailsFragmentToGroupLoanAccountFragment(
                groupId
            )
        findNavController().navigate(action)
    }

    private fun loadGroupDataTables(groupId: Int) {
        val action = GroupDetailsFragmentDirections.actionGroupDetailsFragmentToDataTableFragment(
            Constants.DATA_TABLE_NAME_GROUP,
            groupId
        )
        findNavController().navigate(action)
    }

    private fun loadGroupClients(clients: List<Client>) {
        val action = GroupDetailsFragmentDirections.actionGroupDetailsFragmentToClientListFragment(
            ClientListArgs(clients, true)
        )
        findNavController().navigate(action)
    }

    private fun loadLoanAccountSummary(loanAccountNumber: Int) {
        val action =
            GroupDetailsFragmentDirections.actionGroupDetailsFragmentToLoanAccountSummaryFragment(
                loanAccountNumber,
                true
            )
        findNavController().navigate(action)
    }

    private fun loadSavingsAccountSummary(savingsAccountNumber: Int, accountType: DepositType?) {
        val action = accountType?.let {
            GroupDetailsFragmentDirections.actionGroupDetailsFragmentToSavingsAccountSummaryFragment(
                savingsAccountNumber,
                it, true
            )
        }
        action?.let { findNavController().navigate(it) }
    }
}