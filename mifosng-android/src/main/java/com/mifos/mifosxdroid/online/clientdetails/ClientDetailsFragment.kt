/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientdetails

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
import com.mifos.core.objects.client.Charges
import com.mifos.feature.client.clientDetails.ui.ClientDetailsScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDetailsFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                ClientDetailsScreen(
                    onBackPressed = {
                        requireActivity().onBackPressed()
                    },
                    addLoanAccount = {
                        addLoanAccount(it)
                    },
                    addSavingsAccount = {
                        addSavingsAccount(it)
                    },
                    charges = {
                        loadClientCharges(it)
                    },
                    documents = {
                        loadDocuments(it)
                    },
                    identifiers = {
                        loadIdentifiers(it)
                    },
                    moreClientInfo = { loadClientDataTables(it) },
                    notes = {
                        loadNotes(it)
                    },
                    pinpointLocation = {
                        loadPinPoint(it)
                    },
                    survey = {
                        loadSurveys(it)
                    },
                    uploadSignature = {
                        loadSignUpload(it)
                    },
                    loanAccountSelected = {
                        loadLoanAccountSummary(it)
                    },
                    savingsAccountSelected = { accountNo, deposit ->
                        loadSavingsAccountSummary(
                            savingsAccountNumber = accountNo,
                            accountType = deposit
                        )
                    },
                    activateClient = {
                        activateClient(it)
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

    private fun loadDocuments(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToDocumentListFragment(
                clientId,
                Constants.ENTITY_TYPE_CLIENTS
            )
        findNavController().navigate(action)
    }

    private fun loadNotes(clientId: Int) {
        val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToNoteFragment(
            clientId,
            Constants.ENTITY_TYPE_CLIENTS
        )
        findNavController().navigate(action)
    }

    private fun loadClientCharges(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToClientChargeFragment(
                clientId,
                listOf<Charges>().toTypedArray()
            )
        findNavController().navigate(action)
    }

    private fun loadIdentifiers(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToClientIdentifiersFragment(
                clientId
            )
        findNavController().navigate(action)
    }

    private fun loadSurveys(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSurveyListFragment(clientId)
        findNavController().navigate(action)
    }

    private fun addSavingsAccount(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSavingsAccountFragment(
                clientId,
                false
            )
        findNavController().navigate(action)
    }

    private fun addLoanAccount(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToLoanAccountFragment(
                clientId
            )
        findNavController().navigate(action)
    }

    private fun activateClient(clientId: Int) {
        val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToActivateFragment(
            clientId,
            Constants.ACTIVATE_CLIENT
        )
        findNavController().navigate(action)
    }

    private fun loadClientDataTables(clientId: Int) {
        val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToDataTableFragment(
            Constants.DATA_TABLE_NAME_CLIENT,
            clientId
        )
        findNavController().navigate(action)
    }

    private fun loadSignUpload(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSignatureFragment(clientId)
        findNavController().navigate(action)
    }

    private fun loadPinPoint(clientId: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToPinpointClientActivity(
                clientId
            )
        findNavController().navigate(action)
    }

    private fun loadLoanAccountSummary(loanAccountNumber: Int) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToLoanAccountSummaryFragment(
                loanAccountNumber,
                true
            )
        findNavController().navigate(action)
    }

    private fun loadSavingsAccountSummary(savingsAccountNumber: Int, accountType: DepositType) {
        val action =
            ClientDetailsFragmentDirections.actionClientDetailsFragmentToSavingsAccountSummaryFragment(
                savingsAccountNumber,
                accountType,
                true
            )
        findNavController().navigate(action)
    }
}