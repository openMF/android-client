/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.di

import com.mifos.core.domain.useCases.ActivateCenterUseCase
import com.mifos.core.domain.useCases.ActivateClientUseCase
import com.mifos.core.domain.useCases.ActivateGroupUseCase
import com.mifos.core.domain.useCases.ActivateSavingsUseCase
import com.mifos.core.domain.useCases.AddClientPinpointLocationUseCase
import com.mifos.core.domain.useCases.AddDataTableEntryUseCase
import com.mifos.core.domain.useCases.ApproveCheckerUseCase
import com.mifos.core.domain.useCases.ApproveSavingsApplicationUseCase
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.CreateClientIdentifierUseCase
import com.mifos.core.domain.useCases.CreateDocumentUseCase
import com.mifos.core.domain.useCases.CreateGroupLoansAccountUseCase
import com.mifos.core.domain.useCases.CreateLoanAccountUseCase
import com.mifos.core.domain.useCases.CreateLoanChargesUseCase
import com.mifos.core.domain.useCases.CreateSavingsAccountUseCase
import com.mifos.core.domain.useCases.DeleteCheckerUseCase
import com.mifos.core.domain.useCases.DeleteClientAddressPinpointUseCase
import com.mifos.core.domain.useCases.DeleteDataTableEntryUseCase
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.domain.useCases.DownloadDocumentUseCase
import com.mifos.core.domain.useCases.FetchCenterDetailsUseCase
import com.mifos.core.domain.useCases.FetchCollectionSheetUseCase
import com.mifos.core.domain.useCases.FetchGroupsAssociatedWithCenterUseCase
import com.mifos.core.domain.useCases.FetchProductiveCollectionSheetUseCase
import com.mifos.core.domain.useCases.GetAllChargesV2UseCase
import com.mifos.core.domain.useCases.GetAllChargesV3UseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetCenterDetailsUseCase
import com.mifos.core.domain.useCases.GetCentersInOfficeUseCase
import com.mifos.core.domain.useCases.GetCheckerInboxBadgesUseCase
import com.mifos.core.domain.useCases.GetCheckerTasksUseCase
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.domain.useCases.GetClientIdentifierTemplateUseCase
import com.mifos.core.domain.useCases.GetClientPinpointLocationsUseCase
import com.mifos.core.domain.useCases.GetClientSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.GetDataTableInfoUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.domain.useCases.GetGroupLoansAccountTemplateUseCase
import com.mifos.core.domain.useCases.GetGroupSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.GetGroupsByCenterUseCase
import com.mifos.core.domain.useCases.GetGroupsByOfficeUseCase
import com.mifos.core.domain.useCases.GetIndividualCollectionSheetUseCase
import com.mifos.core.domain.useCases.GetListOfLoanChargesUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.domain.useCases.GetReportCategoryUseCase
import com.mifos.core.domain.useCases.GetReportFullParameterListUseCase
import com.mifos.core.domain.useCases.GetReportParameterDetailsUseCase
import com.mifos.core.domain.useCases.GetRunReportOfficesUseCase
import com.mifos.core.domain.useCases.GetRunReportProductUseCase
import com.mifos.core.domain.useCases.GetRunReportWithQueryUseCase
import com.mifos.core.domain.useCases.GetStaffInOfficeUseCase
import com.mifos.core.domain.useCases.GetUserPathTrackingUseCase
import com.mifos.core.domain.useCases.GroupsListPagingDataSource
import com.mifos.core.domain.useCases.LoadSavingsAccountsAndTemplateUseCase
import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.RejectCheckerUseCase
import com.mifos.core.domain.useCases.RemoveDocumentUseCase
import com.mifos.core.domain.useCases.SaveIndividualCollectionSheetUseCase
import com.mifos.core.domain.useCases.SubmitCollectionSheetUseCase
import com.mifos.core.domain.useCases.SubmitProductiveSheetUseCase
import com.mifos.core.domain.useCases.UpdateClientPinpointUseCase
import com.mifos.core.domain.useCases.UploadClientImageUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.domain.useCases.ValidateServerApiPathUseCase
import com.mifos.core.domain.useCases.ValidateServerEndPointUseCase
import com.mifos.core.domain.useCases.ValidateServerPortUseCase
import com.mifos.core.domain.useCases.ValidateServerProtocolUseCase
import com.mifos.core.domain.useCases.ValidateServerTenantUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    single<ActivateCenterUseCase> { ActivateCenterUseCase(get()) }
    single<ActivateClientUseCase> { ActivateClientUseCase(get()) }
    single<ActivateGroupUseCase> { ActivateGroupUseCase(get()) }
    single<ActivateSavingsUseCase> { ActivateSavingsUseCase(get()) }
    single<AddClientPinpointLocationUseCase> { AddClientPinpointLocationUseCase(get()) }
    single<AddDataTableEntryUseCase> { AddDataTableEntryUseCase(get()) }
    single<ApproveCheckerUseCase> { ApproveCheckerUseCase(get()) }
    single<ApproveSavingsApplicationUseCase> { ApproveSavingsApplicationUseCase((get())) }
    single<CreateChargesUseCase> { CreateChargesUseCase(get()) }
    single<CreateClientIdentifierUseCase> { CreateClientIdentifierUseCase(get()) }
    single<CreateDocumentUseCase> { CreateDocumentUseCase(get()) }
    single<CreateGroupLoansAccountUseCase> { CreateGroupLoansAccountUseCase(get()) }
    single<CreateLoanAccountUseCase> { CreateLoanAccountUseCase(get()) }
    single<CreateLoanChargesUseCase> { CreateLoanChargesUseCase(get()) }
    single<CreateSavingsAccountUseCase> { CreateSavingsAccountUseCase(get()) }
    single<DeleteCheckerUseCase> { DeleteCheckerUseCase(get()) }
    single<DeleteClientAddressPinpointUseCase> { DeleteClientAddressPinpointUseCase(get()) }
    single<DeleteDataTableEntryUseCase> { DeleteDataTableEntryUseCase(get()) }
    single<DeleteIdentifierUseCase> { DeleteIdentifierUseCase(get()) }
    single<DownloadDocumentUseCase> { DownloadDocumentUseCase(get()) }
    single<FetchCenterDetailsUseCase> { FetchCenterDetailsUseCase(get()) }
    single<FetchCollectionSheetUseCase> { FetchCollectionSheetUseCase(get()) }
    single<FetchGroupsAssociatedWithCenterUseCase> { FetchGroupsAssociatedWithCenterUseCase(get()) }
    single { FetchProductiveCollectionSheetUseCase(get()) }
    single<GetAllChargesV2UseCase> { GetAllChargesV2UseCase(get()) }
    single<GetAllChargesV3UseCase> { GetAllChargesV3UseCase(get()) }
    single<GetAllLoanUseCase> { GetAllLoanUseCase(get()) }
    single<GetCenterDetailsUseCase> { GetCenterDetailsUseCase(get()) }
    single<GetCentersInOfficeUseCase> { GetCentersInOfficeUseCase(get()) }
    single<GetCheckerInboxBadgesUseCase> { GetCheckerInboxBadgesUseCase(get()) }
    single<GetCheckerTasksUseCase> { GetCheckerTasksUseCase(get()) }
    single<GetClientDetailsUseCase> { GetClientDetailsUseCase(get()) }
    single<GetClientIdentifierTemplateUseCase> { GetClientIdentifierTemplateUseCase(get()) }
    single<GetClientPinpointLocationsUseCase> { GetClientPinpointLocationsUseCase(get()) }
    single<GetClientSavingsAccountTemplateByProductUseCase> {
        GetClientSavingsAccountTemplateByProductUseCase(
            get(),
        )
    }
    single<GetDataTableInfoUseCase> { GetDataTableInfoUseCase(get()) }
    single<GetDocumentsListUseCase> { GetDocumentsListUseCase(get()) }
    single<GetGroupLoansAccountTemplateUseCase> { GetGroupLoansAccountTemplateUseCase(get()) }
    single<GetGroupSavingsAccountTemplateByProductUseCase> {
        GetGroupSavingsAccountTemplateByProductUseCase(
            get(),
        )
    }
    single<GetGroupsByCenterUseCase> { GetGroupsByCenterUseCase() }
    single<GetGroupsByOfficeUseCase> { GetGroupsByOfficeUseCase(get()) }
    single<GetIndividualCollectionSheetUseCase> { GetIndividualCollectionSheetUseCase(get()) }
    single<GetListOfLoanChargesUseCase> { GetListOfLoanChargesUseCase(get()) }
    single<GetLoansAccountTemplateUseCase> { GetLoansAccountTemplateUseCase(get()) }
    single<GetReportCategoryUseCase> { GetReportCategoryUseCase(get()) }
    single<GetReportFullParameterListUseCase> { GetReportFullParameterListUseCase(get()) }
    single<GetReportParameterDetailsUseCase> { GetReportParameterDetailsUseCase(get()) }
    single<GetRunReportOfficesUseCase> { GetRunReportOfficesUseCase(get()) }
    single<GetRunReportProductUseCase> { GetRunReportProductUseCase(get()) }
    single<GetRunReportWithQueryUseCase> { GetRunReportWithQueryUseCase(get()) }
    single<GetStaffInOfficeUseCase> { GetStaffInOfficeUseCase(get()) }
    single<GetUserPathTrackingUseCase> { GetUserPathTrackingUseCase(get()) }
    single<GroupsListPagingDataSource> { GroupsListPagingDataSource(get(), get()) } // todo provide limit
    single<LoadSavingsAccountsAndTemplateUseCase> { LoadSavingsAccountsAndTemplateUseCase(get()) }
    single<RejectCheckerUseCase> { RejectCheckerUseCase(get()) }
    single<RemoveDocumentUseCase> { RemoveDocumentUseCase(get()) }
    single<SaveIndividualCollectionSheetUseCase> { SaveIndividualCollectionSheetUseCase(get()) }
    single<SubmitCollectionSheetUseCase> { SubmitCollectionSheetUseCase(get()) }
    single<SubmitProductiveSheetUseCase> { SubmitProductiveSheetUseCase(get()) }
    single<UpdateClientPinpointUseCase> { UpdateClientPinpointUseCase(get()) }
    single<UploadClientImageUseCase> { UploadClientImageUseCase(get()) }
    single<UsernameValidationUseCase> { UsernameValidationUseCase() }
    single<PasswordValidationUseCase> { PasswordValidationUseCase() }
    single<LoginUseCase> { LoginUseCase(get()) }
    single<ValidateServerProtocolUseCase> { ValidateServerProtocolUseCase() }
    single<ValidateServerApiPathUseCase> { ValidateServerApiPathUseCase() }
    single<ValidateServerEndPointUseCase> { ValidateServerEndPointUseCase() }
    single<ValidateServerPortUseCase> { ValidateServerPortUseCase() }
    single<ValidateServerTenantUseCase> { ValidateServerTenantUseCase() }
}
