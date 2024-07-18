package com.mifos.feature.individual_collection_sheet.individual_collection_sheet_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.SaveIndividualCollectionSheetUseCase
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.ClientCollectionSheet
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.feature.collection_sheet.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Observable
import javax.inject.Inject

@HiltViewModel
class IndividualCollectionSheetDetailsViewModel @Inject constructor(
    private val saveIndividualCollectionSheetUseCase: SaveIndividualCollectionSheetUseCase
) : ViewModel() {

    private val _individualCollectionSheetDetailsUiState =
        MutableStateFlow<IndividualCollectionSheetDetailsUiState>(
            IndividualCollectionSheetDetailsUiState.Empty
        )
    val individualCollectionSheetDetailsUiState =
        _individualCollectionSheetDetailsUiState.asStateFlow()

    fun submitIndividualCollectionSheet(payload: IndividualCollectionSheetPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            saveIndividualCollectionSheetUseCase(payload).collect { result ->
                when (result) {
                    is Resource.Error -> _individualCollectionSheetDetailsUiState.value =
                        IndividualCollectionSheetDetailsUiState.Error(R.string.feature_collection_sheet_failed_to_save_collection_sheet)

                    is Resource.Loading -> _individualCollectionSheetDetailsUiState.value =
                        IndividualCollectionSheetDetailsUiState.Loading

                    is Resource.Success -> _individualCollectionSheetDetailsUiState.value =
                        IndividualCollectionSheetDetailsUiState.SavedSuccessfully
                }
            }
        }

    fun filterLoanAndClientNames(clientCollectionSheets: List<ClientCollectionSheet>): List<LoanAndClientName> {
        val loansAndClientNames: MutableList<LoanAndClientName> = ArrayList()
        Observable.from(clientCollectionSheets)
            .subscribe { clientCollectionSheet ->
                clientCollectionSheet.loans?.let {
                    for (loanCollectionSheet in it) {
                        loansAndClientNames.add(
                            LoanAndClientName(
                                loanCollectionSheet,
                                clientCollectionSheet.clientName,
                                clientCollectionSheet.clientId
                            )
                        )
                    }
                }
            }
        return loansAndClientNames
    }
}