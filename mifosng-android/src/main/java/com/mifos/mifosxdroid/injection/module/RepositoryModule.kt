package com.mifos.mifosxdroid.injection.module

import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.data.repository_imp.NoteRepositoryImp
import com.mifos.core.network.DataManager
import com.mifos.core.network.datamanager.DataManagerAuth
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.network.datamanager.DataManagerSearch
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.mifosxdroid.activity.login.LoginRepository
import com.mifos.mifosxdroid.activity.login.LoginRepositoryImp
import com.mifos.mifosxdroid.activity.pinpointclient.PinPointClientRepository
import com.mifos.mifosxdroid.activity.pinpointclient.PinPointClientRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogRepository
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogRepository
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogRepository
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.identifierdialog.IdentifierDialogRepository
import com.mifos.mifosxdroid.dialogfragments.identifierdialog.IdentifierDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogRepository
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.synccenterdialog.SyncCentersDialogRepository
import com.mifos.mifosxdroid.dialogfragments.synccenterdialog.SyncCentersDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogRepository
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog.SyncGroupsDialogRepository
import com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog.SyncGroupsDialogRepositoryImp
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogRepository
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogRepositoryImp
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardRepository
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardRepositoryImp
import com.mifos.mifosxdroid.offline.synccenterpayloads.SyncCenterPayloadsRepository
import com.mifos.mifosxdroid.offline.synccenterpayloads.SyncCenterPayloadsRepositoryImp
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsRepository
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsRepositoryImp
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsRepository
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsRepositoryImp
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionRepository
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionRepositoryImp
import com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction.SyncSavingsAccountTransactionRepository
import com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction.SyncSavingsAccountTransactionRepositoryImp
import com.mifos.mifosxdroid.online.activate.ActivateRepository
import com.mifos.mifosxdroid.online.activate.ActivateRepositoryImp
import com.mifos.mifosxdroid.online.centerlist.CenterListRepository
import com.mifos.mifosxdroid.online.centerlist.CenterListRepositoryImp
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeRepository
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeRepositoryImp
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsRepository
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsRepositoryImp
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersRepository
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersRepositoryImp
import com.mifos.mifosxdroid.online.clientlist.ClientListRepository
import com.mifos.mifosxdroid.online.clientlist.ClientListRepositoryImp
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetRepository
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetRepositoryImp
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.IndividualCollectionSheetDetailsRepository
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.IndividualCollectionSheetDetailsRepositoryImp
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientRepository
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientRepositoryImp
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupRepository
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupRepositoryImp
import com.mifos.mifosxdroid.online.datatable.DataTableRepository
import com.mifos.mifosxdroid.online.datatable.DataTableRepositoryImp
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataRepository
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataRepositoryImp
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListRepository
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListRepositoryImp
import com.mifos.mifosxdroid.online.documentlist.DocumentListRepository
import com.mifos.mifosxdroid.online.documentlist.DocumentListRepositoryImp
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetRepository
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetRepositoryImp
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsRepository
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsRepositoryImp
import com.mifos.mifosxdroid.online.grouplist.GroupListRepository
import com.mifos.mifosxdroid.online.grouplist.GroupListRepositoryImp
import com.mifos.mifosxdroid.online.grouploanaccount.GroupLoanAccountRepository
import com.mifos.mifosxdroid.online.grouploanaccount.GroupLoanAccountRepositoryImp
import com.mifos.mifosxdroid.online.groupslist.GroupsListRepository
import com.mifos.mifosxdroid.online.groupslist.GroupsListRepositoryImp
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountRepository
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountRepositoryImp
import com.mifos.mifosxdroid.online.loanaccountapproval.LoanAccountApprovalRepository
import com.mifos.mifosxdroid.online.loanaccountapproval.LoanAccountApprovalRepositoryImp
import com.mifos.mifosxdroid.online.loanaccountdisbursement.LoanAccountDisbursementRepository
import com.mifos.mifosxdroid.online.loanaccountdisbursement.LoanAccountDisbursementRepositoryImp
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryRepository
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryRepositoryImp
import com.mifos.mifosxdroid.online.loancharge.LoanChargeRepository
import com.mifos.mifosxdroid.online.loancharge.LoanChargeRepositoryImp
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentRepository
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentRepositoryImp
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleRepository
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleRepositoryImp
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsRepository
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsRepositoryImp
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailRepository
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailRepositoryImp
import com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryRepository
import com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryRepositoryImp
import com.mifos.mifosxdroid.online.savingaccounttransaction.SavingsAccountTransactionRepository
import com.mifos.mifosxdroid.online.savingaccounttransaction.SavingsAccountTransactionRepositoryImp
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountRepository
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountRepositoryImp
import com.mifos.mifosxdroid.online.savingsaccountactivate.SavingsAccountActivateRepository
import com.mifos.mifosxdroid.online.savingsaccountactivate.SavingsAccountActivateRepositoryImp
import com.mifos.mifosxdroid.online.savingsaccountapproval.SavingsAccountApprovalRepository
import com.mifos.mifosxdroid.online.savingsaccountapproval.SavingsAccountApprovalRepositoryImp
import com.mifos.mifosxdroid.online.search.SearchRepository
import com.mifos.mifosxdroid.online.search.SearchRepositoryImp
import com.mifos.mifosxdroid.online.sign.SignatureRepository
import com.mifos.mifosxdroid.online.sign.SignatureRepositoryImp
import com.mifos.mifosxdroid.online.surveylist.SurveyListRepository
import com.mifos.mifosxdroid.online.surveylist.SurveyListRepositoryImp
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitRepository
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Aditya Gupta on 06/08/23.
 */

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun providesLoginRepository(dataManagerAuth: DataManagerAuth): LoginRepository {
        return LoginRepositoryImp(dataManagerAuth)
    }

    @Provides
    fun providesSearchRepository(dataManagerSearch: DataManagerSearch): SearchRepository {
        return SearchRepositoryImp(dataManagerSearch)
    }

    @Provides
    fun providesCenterListRepository(dataManagerCenter: DataManagerCenter): CenterListRepository {
        return CenterListRepositoryImp(dataManagerCenter)
    }

    @Provides
    fun providesGroupListRepository(dataManager: DataManager): GroupListRepository {
        return GroupListRepositoryImp(dataManager)
    }

    @Provides
    fun providesClientDetailsRepository(dataManagerClient: DataManagerClient): ClientDetailsRepository {
        return ClientDetailsRepositoryImp(dataManagerClient)
    }

    @Provides
    fun providesGroupDetailsRepository(dataManagerGroups: DataManagerGroups): GroupDetailsRepository {
        return GroupDetailsRepositoryImp(dataManagerGroups)
    }

    @Provides
    fun providesActivateRepository(
        dataManagerClient: DataManagerClient,
        dataManagerCenter: DataManagerCenter,
        dataManagerGroups: DataManagerGroups
    ): ActivateRepository {
        return ActivateRepositoryImp(dataManagerClient, dataManagerCenter, dataManagerGroups)
    }

    @Provides
    fun providesClientListRepository(dataManagerClient: DataManagerClient): ClientListRepository {
        return ClientListRepositoryImp(dataManagerClient)
    }

    @Provides
    fun providesGroupsListRepository(dataManagerGroups: DataManagerGroups): GroupsListRepository {
        return GroupsListRepositoryImp(dataManagerGroups)
    }

    @Provides
    fun providesClientChargeRepository(dataManagerCharge: DataManagerCharge): ClientChargeRepository {
        return ClientChargeRepositoryImp(dataManagerCharge)
    }

    @Provides
    fun providesLoanAccountSummary(dataManagerLoan: DataManagerLoan): LoanAccountSummaryRepository {
        return LoanAccountSummaryRepositoryImp(dataManagerLoan)
    }

    @Provides
    fun providesSavingsAccountSummaryRepository(dataManagerSavings: DataManagerSavings): SavingsAccountSummaryRepository {
        return SavingsAccountSummaryRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesDataTableRepository(dataManagerDataTable: DataManagerDataTable): DataTableRepository {
        return DataTableRepositoryImp(dataManagerDataTable)
    }

    @Provides
    fun providesPinPointClientRepository(dataManagerClient: DataManagerClient): PinPointClientRepository {
        return PinPointClientRepositoryImp(dataManagerClient)
    }

    @Provides
    fun providesDocumentListRepository(dataManagerDocument: DataManagerDocument): DocumentListRepository {
        return DocumentListRepositoryImp(dataManagerDocument)
    }

    @Provides
    fun providesNoteRepository(dataManagerNote: DataManagerNote): NoteRepository {
        return NoteRepositoryImp(dataManagerNote)
    }

    @Provides
    fun providesSavingAccountRepository(dataManagerSavings: DataManagerSavings): SavingsAccountRepository {
        return SavingsAccountRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesLoanAccountRepository(dataManagerLoan: DataManagerLoan): LoanAccountRepository {
        return LoanAccountRepositoryImp(dataManagerLoan)
    }

    @Provides
    fun providesSignatureRepository(dataManagerDocument: DataManagerDocument): SignatureRepository {
        return SignatureRepositoryImp(dataManagerDocument)
    }

    @Provides
    fun providesClientIdentifiersRepository(dataManagerClient: DataManagerClient): ClientIdentifiersRepository {
        return ClientIdentifiersRepositoryImp(dataManagerClient)
    }

    @Provides
    fun providesSurveyListRepository(dataManagerSurveys: DataManagerSurveys): SurveyListRepository {
        return SurveyListRepositoryImp(dataManagerSurveys)
    }

    @Provides
    fun providesLoanChargeRepository(dataManager: DataManager): LoanChargeRepository {
        return LoanChargeRepositoryImp(dataManager)
    }

    @Provides
    fun providesLoanAccountApprovalRepository(dataManager: DataManager): LoanAccountApprovalRepository {
        return LoanAccountApprovalRepositoryImp(dataManager)
    }

    @Provides
    fun providesLoanAccountDisbursementRepository(dataManagerLoan: DataManagerLoan): LoanAccountDisbursementRepository {
        return LoanAccountDisbursementRepositoryImp(dataManagerLoan)
    }

    @Provides
    fun providesLoanRepaymentRepository(dataManagerLoan: DataManagerLoan): LoanRepaymentRepository {
        return LoanRepaymentRepositoryImp(dataManagerLoan)
    }

    @Provides
    fun providesCollectionSheetRepository(dataManager: DataManager): CollectionSheetRepository {
        return CollectionSheetRepositoryImp(dataManager)
    }

    @Provides
    fun providesIndividualCollectionSheetDetailsRepository(dataManagerCollection: DataManagerCollectionSheet): IndividualCollectionSheetDetailsRepository {
        return IndividualCollectionSheetDetailsRepositoryImp(dataManagerCollection)
    }

    @Provides
    fun providesCreateNewClientRepository(
        dataManagerClient: DataManagerClient,
        dataManagerOffices: DataManagerOffices,
        dataManagerStaff: DataManagerStaff
    ): CreateNewClientRepository {
        return CreateNewClientRepositoryImp(dataManagerClient, dataManagerOffices, dataManagerStaff)
    }

    @Provides
    fun providesCreateNewGroupRepository(
        dataManagerOffices: DataManagerOffices, dataManagerGroups: DataManagerGroups
    ): CreateNewGroupRepository {
        return CreateNewGroupRepositoryImp(dataManagerOffices, dataManagerGroups)
    }

    @Provides
    fun providesDataTableDataRepository(dataManagerDataTable: DataManagerDataTable): DataTableDataRepository {
        return DataTableDataRepositoryImp(dataManagerDataTable)
    }

    @Provides
    fun providesDataTableListRepository(
        dataManagerLoan: DataManagerLoan,
        dataManager: DataManager,
        dataManagerClient: DataManagerClient
    ): DataTableListRepository {
        return DataTableListRepositoryImp(dataManagerLoan, dataManager, dataManagerClient)
    }

    @Provides
    fun providesGenerateCollectionSheetRepository(
        dataManager: DataManager,
        collectionDataManager: DataManagerCollectionSheet
    ): GenerateCollectionSheetRepository {
        return GenerateCollectionSheetRepositoryImp(dataManager, collectionDataManager)
    }

    @Provides
    fun providesGroupLoanAccountRepository(dataManager: DataManager): GroupLoanAccountRepository {
        return GroupLoanAccountRepositoryImp(dataManager)
    }

    @Provides
    fun providesReportDetailRepository(dataManager: DataManagerRunReport): ReportDetailRepository {
        return ReportDetailRepositoryImp(dataManager)
    }

    @Provides
    fun providesLoanRepaymentScheduleRepository(dataManager: DataManager): LoanRepaymentScheduleRepository {
        return LoanRepaymentScheduleRepositoryImp(dataManager)
    }

    @Provides
    fun providesLoanTransactionsRepository(dataManager: DataManager): LoanTransactionsRepository {
        return LoanTransactionsRepositoryImp(dataManager)
    }

    @Provides
    fun providesSavingsAccountTransactionRepository(dataManagerSavings: DataManagerSavings): SavingsAccountTransactionRepository {
        return SavingsAccountTransactionRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesSavingsAccountActivateRepository(dataManagerSavings: DataManagerSavings): SavingsAccountActivateRepository {
        return SavingsAccountActivateRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesSavingsAccountApprovalRepository(dataManagerSavings: DataManagerSavings): SavingsAccountApprovalRepository {
        return SavingsAccountApprovalRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesSurveySubmitRepository(dataManagerSurveys: DataManagerSurveys): SurveySubmitRepository {
        return SurveySubmitRepositoryImp(dataManagerSurveys)
    }

    @Provides
    fun providesChargeDialogRepository(dataManager: DataManager): ChargeDialogRepository {
        return ChargeDialogRepositoryImp(dataManager)
    }

    @Provides
    fun providesDataTableRowDialogRepository(dataManagerDataTable: DataManagerDataTable): DataTableRowDialogRepository {
        return DataTableRowDialogRepositoryImp(dataManagerDataTable)
    }

    @Provides
    fun providesDocumentDialogRepository(dataManagerDocument: DataManagerDocument): DocumentDialogRepository {
        return DocumentDialogRepositoryImp(dataManagerDocument)
    }

    @Provides
    fun providesIdentifierDialogRepository(dataManagerClient: DataManagerClient): IdentifierDialogRepository {
        return IdentifierDialogRepositoryImp(dataManagerClient)
    }

    @Provides
    fun providesLoanChargeDialogRepository(dataManager: DataManager): LoanChargeDialogRepository {
        return LoanChargeDialogRepositoryImp(dataManager)
    }

    @Provides
    fun providesSyncSurveysDialogRepository(dataManagerSurvey: DataManagerSurveys): SyncSurveysDialogRepository {
        return SyncSurveysDialogRepositoryImp(dataManagerSurvey)
    }

    @Provides
    fun providesSyncGroupsDialogRepository(
        dataManagerGroups: DataManagerGroups,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
        dataManagerClient: DataManagerClient
    ): SyncGroupsDialogRepository {
        return SyncGroupsDialogRepositoryImp(
            dataManagerGroups,
            dataManagerLoan,
            dataManagerSavings,
            dataManagerClient
        )
    }

    @Provides
    fun providesSyncClientsDialogRepository(
        dataManagerClient: DataManagerClient,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings
    ): SyncClientsDialogRepository {
        return SyncClientsDialogRepositoryImp(
            dataManagerClient,
            dataManagerLoan,
            dataManagerSavings
        )
    }

    @Provides
    fun providesSyncCentersDialogRepository(
        dataManagerCenter: DataManagerCenter,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
        dataManagerGroups: DataManagerGroups,
        dataManagerClient: DataManagerClient
    ): SyncCentersDialogRepository {
        return SyncCentersDialogRepositoryImp(
            dataManagerCenter,
            dataManagerLoan,
            dataManagerSavings,
            dataManagerGroups,
            dataManagerClient
        )
    }

    @Provides
    fun providesOfflineDashboardRepository(
        dataManagerClient: DataManagerClient,
        dataManagerGroups: DataManagerGroups,
        dataManagerCenter: DataManagerCenter,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings
    ): OfflineDashboardRepository {
        return OfflineDashboardRepositoryImp(
            dataManagerClient,
            dataManagerGroups,
            dataManagerCenter,
            dataManagerLoan,
            dataManagerSavings
        )
    }

    @Provides
    fun providesSyncCenterPayloadsRepository(dataManagerCenter: DataManagerCenter): SyncCenterPayloadsRepository {
        return SyncCenterPayloadsRepositoryImp(dataManagerCenter)
    }

    @Provides
    fun providesSyncSavingsAccountTransactionRepository(
        dataManagerSavings: DataManagerSavings,
        dataManagerLoan: DataManagerLoan
    ): SyncSavingsAccountTransactionRepository {
        return SyncSavingsAccountTransactionRepositoryImp(dataManagerSavings, dataManagerLoan)
    }

    @Provides
    fun providesSyncLoanRepaymentTransactionRepository(dataManagerLoan: DataManagerLoan): SyncLoanRepaymentTransactionRepository {
        return SyncLoanRepaymentTransactionRepositoryImp(dataManagerLoan)
    }

    @Provides
    fun providesSyncGroupPayloadsRepository(dataManagerGroups: DataManagerGroups): SyncGroupPayloadsRepository {
        return SyncGroupPayloadsRepositoryImp(dataManagerGroups)
    }

    @Provides
    fun providesSyncClientPayloadsRepository(dataManagerClient: DataManagerClient): SyncClientPayloadsRepository {
        return SyncClientPayloadsRepositoryImp(dataManagerClient)
    }
}