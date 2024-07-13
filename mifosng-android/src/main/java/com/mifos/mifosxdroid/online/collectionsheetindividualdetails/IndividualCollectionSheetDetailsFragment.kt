package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.feature.individual_collection_sheet.individual_collection_sheet_details.IndividualCollectionSheetDetailsScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by aksh on 20/6/18.
 */
@AndroidEntryPoint
class IndividualCollectionSheetDetailsFragment : MifosBaseFragment() {

    private val arg: IndividualCollectionSheetDetailsFragmentArgs by navArgs()

    private lateinit var sheet: IndividualCollectionSheet
    private lateinit var actualDisbursementDate: String
    private lateinit var transactionDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sheet = arg.sheet
        actualDisbursementDate = arg.actualDisbursementDate
        transactionDate = arg.transactionDate
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                IndividualCollectionSheetDetailsScreen(
                    sheet = sheet,
                    onBackPressed = {
                        findNavController().popBackStack()
                    },
                    submit = { position, payload, paymentTypeOptionsName, loansAndClientName, paymentTypeOptions, clientId ->
                        showPayment(
                            position,
                            payload,
                            paymentTypeOptionsName,
                            loansAndClientName,
                            paymentTypeOptions,
                            clientId
                        )
                    }
                )
            }
        }
    }

    private fun showPayment(
        position: Int,
        payload: IndividualCollectionSheetPayload,
        paymentTypeOptionsName: List<String>,
        loansAndClientName: LoanAndClientName,
        paymentTypeOptions: List<PaymentTypeOptions>,
        clientId: Int
    ) {
        val action =
            IndividualCollectionSheetDetailsFragmentDirections.actionIndividualCollectionSheetDetailsFragmentToPaymentDetailsFragment(
                position,
                payload,
                paymentTypeOptionsName.toTypedArray(),
                loansAndClientName,
                paymentTypeOptions.toTypedArray(),
                clientId
            )
        findNavController().navigate(action)
    }
}