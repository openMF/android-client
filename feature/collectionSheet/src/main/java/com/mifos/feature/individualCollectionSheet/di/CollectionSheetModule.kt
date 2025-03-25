package com.mifos.feature.individualCollectionSheet.di

import org.koin.dsl.module
import com.mifos.feature.individualCollectionSheet.generateCollectionSheet.GenerateCollectionSheetViewModel
import com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails.IndividualCollectionSheetDetailsViewModel
import com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet.ui.NewIndividualCollectionSheetViewModel
import com.mifos.feature.individualCollectionSheet.paymentDetails.PaymentDetailsViewModel
import com.mifos.feature.individualCollectionSheet.savedIndividualCollectionSheet.ui.SavedIndividualCollectionSheetViewModel
import org.koin.core.module.dsl.viewModelOf

val CollectionSheetModule = module {
    viewModelOf(::GenerateCollectionSheetViewModel)
    viewModelOf(::IndividualCollectionSheetDetailsViewModel)
    viewModelOf(::NewIndividualCollectionSheetViewModel)
    viewModelOf(::PaymentDetailsViewModel)
    viewModelOf(::SavedIndividualCollectionSheetViewModel)
}
 