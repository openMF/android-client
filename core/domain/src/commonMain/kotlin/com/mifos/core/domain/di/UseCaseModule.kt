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
import com.mifos.core.domain.useCases.AddNoteUseCase
import com.mifos.core.domain.useCases.ApproveCheckerUseCase
import com.mifos.core.domain.useCases.ApproveSavingsApplicationUseCase
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.CreateClientIdentifierUseCase
import com.mifos.core.domain.useCases.CreateGroupLoansAccountUseCase
import com.mifos.core.domain.useCases.CreateLoanAccountUseCase
import com.mifos.core.domain.useCases.CreateLoanChargesUseCase
import com.mifos.core.domain.useCases.CreateSavingsAccountUseCase
import com.mifos.core.domain.useCases.CreateSignatureUseCase
import com.mifos.core.domain.useCases.DeleteCheckerUseCase
import com.mifos.core.domain.useCases.DeleteClientAddressPinpointUseCase
import com.mifos.core.domain.useCases.DeleteDataTableEntryUseCase
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.domain.useCases.DeleteNoteUseCase
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
import com.mifos.core.domain.useCases.GetClientTemplateUseCase
import com.mifos.core.domain.useCases.GetDataTableInfoUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.domain.useCases.GetGroupDetailsUseCase
import com.mifos.core.domain.useCases.GetGroupLoansAccountTemplateUseCase
import com.mifos.core.domain.useCases.GetGroupSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.GetGroupsByCenterUseCase
import com.mifos.core.domain.useCases.GetGroupsByOfficeUseCase
import com.mifos.core.domain.useCases.GetIndividualCollectionSheetUseCase
import com.mifos.core.domain.useCases.GetListOfLoanChargesUseCase
import com.mifos.core.domain.useCases.GetLoanAndLoanRepaymentUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.domain.useCases.GetReportCategoryUseCase
import com.mifos.core.domain.useCases.GetReportFullParameterListUseCase
import com.mifos.core.domain.useCases.GetReportParameterDetailsUseCase
import com.mifos.core.domain.useCases.GetRunReportOfficesUseCase
import com.mifos.core.domain.useCases.GetRunReportProductUseCase
import com.mifos.core.domain.useCases.GetRunReportWithQueryUseCase
import com.mifos.core.domain.useCases.GetSavingsAccountAndTemplateUseCase
import com.mifos.core.domain.useCases.GetStaffInOfficeUseCase
import com.mifos.core.domain.useCases.GetUserPathTrackingUseCase
import com.mifos.core.domain.useCases.GroupsListPagingDataSource
import com.mifos.core.domain.useCases.LoadSavingsAccountsAndTemplateUseCase
import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.RejectCheckerUseCase
import com.mifos.core.domain.useCases.RemoveDocumentUseCase
import com.mifos.core.domain.useCases.SaveIndividualCollectionSheetUseCase
import com.mifos.core.domain.useCases.ServerConfigValidatorUseCase
import com.mifos.core.domain.useCases.SubmitCollectionSheetUseCase
import com.mifos.core.domain.useCases.SubmitProductiveSheetUseCase
import com.mifos.core.domain.useCases.UpdateClientPinpointUseCase
import com.mifos.core.domain.useCases.UpdateNoteUseCase
import com.mifos.core.domain.useCases.UpdateSignatureUseCase
import com.mifos.core.domain.useCases.UploadClientImageUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.domain.useCases.ValidateServerApiPathUseCase
import com.mifos.core.domain.useCases.ValidateServerEndPointUseCase
import com.mifos.core.domain.useCases.ValidateServerPortUseCase
import com.mifos.core.domain.useCases.ValidateServerProtocolUseCase
import com.mifos.core.domain.useCases.ValidateServerTenantUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val UseCaseModule = module {
    factoryOf(::ActivateCenterUseCase)
    factoryOf(::ActivateClientUseCase)
    factoryOf(::ActivateGroupUseCase)
    factoryOf(::ActivateSavingsUseCase)
    factoryOf(::AddClientPinpointLocationUseCase)
    factoryOf(::AddDataTableEntryUseCase)
    factoryOf(::ApproveCheckerUseCase)
    factoryOf(::ApproveSavingsApplicationUseCase)
    factoryOf(::CreateChargesUseCase)
    factoryOf(::CreateClientIdentifierUseCase)
    factoryOf(::CreateGroupLoansAccountUseCase)
    factoryOf(::CreateLoanAccountUseCase)
    factoryOf(::CreateLoanChargesUseCase)
    factoryOf(::CreateSavingsAccountUseCase)
    factoryOf(::GetClientTemplateUseCase)
    factoryOf(::DeleteCheckerUseCase)
    factoryOf(::DeleteClientAddressPinpointUseCase)
    factoryOf(::DeleteDataTableEntryUseCase)
    factoryOf(::DeleteIdentifierUseCase)
    factoryOf(::DownloadDocumentUseCase)
    factoryOf(::FetchCenterDetailsUseCase)
    factoryOf(::FetchCollectionSheetUseCase)
    factoryOf(::FetchGroupsAssociatedWithCenterUseCase)
    factoryOf(::FetchProductiveCollectionSheetUseCase)
    factoryOf(::GetAllChargesV2UseCase)
    factoryOf(::GetAllChargesV3UseCase)
    factoryOf(::GetAllLoanUseCase)
    factoryOf(::GetCenterDetailsUseCase)
    factoryOf(::GetCentersInOfficeUseCase)
    factoryOf(::GetCheckerInboxBadgesUseCase)
    factoryOf(::GetCheckerTasksUseCase)
    factoryOf(::GetClientDetailsUseCase)
    factoryOf(::GetClientIdentifierTemplateUseCase)
    factoryOf(::GetClientPinpointLocationsUseCase)
    factoryOf(::GetClientSavingsAccountTemplateByProductUseCase)
    factoryOf(::GetDataTableInfoUseCase)
    factoryOf(::GetDocumentsListUseCase)
    factoryOf(::GetGroupLoansAccountTemplateUseCase)
    factoryOf(::GetGroupSavingsAccountTemplateByProductUseCase)
    factoryOf(::GetGroupsByCenterUseCase)
    factoryOf(::GetGroupsByOfficeUseCase)
    factoryOf(::GetIndividualCollectionSheetUseCase)
    factoryOf(::GetListOfLoanChargesUseCase)
    factoryOf(::GetLoansAccountTemplateUseCase)
    factoryOf(::GetReportCategoryUseCase)
    factoryOf(::GetReportFullParameterListUseCase)
    factoryOf(::GetReportParameterDetailsUseCase)
    factoryOf(::GetRunReportOfficesUseCase)
    factoryOf(::ServerConfigValidatorUseCase)
    factoryOf(::GetRunReportProductUseCase)
    factoryOf(::GetRunReportWithQueryUseCase)
    factoryOf(::GetStaffInOfficeUseCase)
    factoryOf(::GetUserPathTrackingUseCase)
    factoryOf(::GroupsListPagingDataSource)
    factoryOf(::LoadSavingsAccountsAndTemplateUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::PasswordValidationUseCase)
    factoryOf(::RejectCheckerUseCase)
    factoryOf(::RemoveDocumentUseCase)
    factoryOf(::SaveIndividualCollectionSheetUseCase)
    factoryOf(::SubmitCollectionSheetUseCase)
    factoryOf(::SubmitProductiveSheetUseCase)
    factoryOf(::UpdateClientPinpointUseCase)
    factoryOf(::UploadClientImageUseCase)
    factoryOf(::UsernameValidationUseCase)
    factoryOf(::ValidateServerProtocolUseCase)
    factoryOf(::ValidateServerApiPathUseCase)
    factoryOf(::ValidateServerEndPointUseCase)
    factoryOf(::ValidateServerPortUseCase)
    factoryOf(::ValidateServerTenantUseCase)
    factoryOf(::GetGroupDetailsUseCase)
    factoryOf(::GetLoanAndLoanRepaymentUseCase)
    factoryOf(::GetSavingsAccountAndTemplateUseCase)
    factoryOf(::AddNoteUseCase)
    factoryOf(::UpdateNoteUseCase)
    factoryOf(::DeleteNoteUseCase)
    factoryOf(::CreateSignatureUseCase)
    factoryOf(::UpdateSignatureUseCase)
}
