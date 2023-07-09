package com.mifos.mifosxdroid.injection.component

import com.mifos.mifosxdroid.activity.pathtracking.PathTrackingActivity
import com.mifos.mifosxdroid.activity.pinpointclient.PinpointClientActivity
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogFragment
import com.mifos.mifosxdroid.dialogfragments.checkertaskfilterdialog.CheckerTaskFilterDialogFragment
import com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog.CollectionSheetDialogFragment
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogFragment
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogFragment
import com.mifos.mifosxdroid.dialogfragments.identifierdialog.IdentifierDialogFragment
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogFragment
import com.mifos.mifosxdroid.dialogfragments.synccenterdialog.SyncCentersDialogFragment
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogFragment
import com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog.SyncGroupsDialogFragment
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment
import com.mifos.mifosxdroid.injection.PerActivity
import com.mifos.mifosxdroid.injection.module.ActivityModule
import com.mifos.mifosxdroid.login.LoginActivity
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment
import com.mifos.mifosxdroid.offline.synccenterpayloads.SyncCenterPayloadsFragment
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsFragment
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsFragment
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionFragment
import com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction.SyncSavingsAccountTransactionFragment
import com.mifos.mifosxdroid.online.activate.ActivateFragment
import com.mifos.mifosxdroid.online.centerdetails.CenterDetailsFragment
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxFragment
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxPendingTasksActivity
import com.mifos.mifosxdroid.online.checkerinbox.CheckerInboxTasksFragment
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetFragment
import com.mifos.mifosxdroid.online.collectionsheetindividual.IndividualCollectionSheetFragment
import com.mifos.mifosxdroid.online.collectionsheetindividual.NewIndividualCollectionSheetFragment
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.IndividualCollectionSheetDetailsFragment
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.PaymentDetailsFragment
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment
import com.mifos.mifosxdroid.online.datatable.DataTableFragment
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataFragment
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsFragment
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment
import com.mifos.mifosxdroid.online.grouploanaccount.GroupLoanAccountFragment
import com.mifos.mifosxdroid.online.groupslist.GroupsListFragment
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountFragment
import com.mifos.mifosxdroid.online.loanaccountapproval.LoanAccountApproval
import com.mifos.mifosxdroid.online.loanaccountdisbursement.LoanAccountDisbursementFragment
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryFragment
import com.mifos.mifosxdroid.online.loancharge.LoanChargeFragment
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentFragment
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleFragment
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsFragment
import com.mifos.mifosxdroid.online.note.NoteFragment
import com.mifos.mifosxdroid.online.runreports.report.ReportFragment
import com.mifos.mifosxdroid.online.runreports.reportcategory.ReportCategoryFragment
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment
import com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryFragment
import com.mifos.mifosxdroid.online.savingaccounttransaction.SavingsAccountTransactionFragment
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment
import com.mifos.mifosxdroid.online.savingsaccountactivate.SavingsAccountActivateFragment
import com.mifos.mifosxdroid.online.savingsaccountapproval.SavingsAccountApprovalFragment
import com.mifos.mifosxdroid.online.search.SearchFragment
import com.mifos.mifosxdroid.online.sign.SignatureFragment
import com.mifos.mifosxdroid.online.surveylist.SurveyListFragment
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import dagger.Component

/**
 * @author Rajan Maurya
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(passCodeActivity: PassCodeActivity)
    fun inject(centerListFragment: CenterListFragment)
    fun inject(clientChargeFragment: ClientChargeFragment)
    fun inject(clientListFragment: ClientListFragment)
    fun inject(clientIdentifiersFragment: ClientIdentifiersFragment)
    fun inject(clientSearchFragment: SearchFragment)
    fun inject(documentListFragment: DocumentListFragment)
    fun inject(groupListFragment: GroupListFragment)
    fun inject(generateCollectionSheetFragment: GenerateCollectionSheetFragment)
    fun inject(createNewCenterFragment: CreateNewCenterFragment)
    fun inject(createNewGroupFragment: CreateNewGroupFragment)
    fun inject(createNewClientFragment: CreateNewClientFragment)
    fun inject(dataTableDataFragment: DataTableDataFragment)
    fun inject(groupDetailsFragment: GroupDetailsFragment)
    fun inject(clientDetailsFragment: ClientDetailsFragment)
    fun inject(loanAccountSummaryFragment: LoanAccountSummaryFragment)
    fun inject(savingsAccountSummaryFragment: SavingsAccountSummaryFragment)
    fun inject(loanChargeFragment: LoanChargeFragment)
    fun inject(savingsAccountTransactionFragment: SavingsAccountTransactionFragment)
    fun inject(collectionSheetFragment: CollectionSheetFragment)
    fun inject(groupsListFragment: GroupsListFragment)
    fun inject(loanTransactionsFragment: LoanTransactionsFragment)
    fun inject(savingsAccountFragment: SavingsAccountFragment)
    fun inject(loanRepaymentFragment: LoanRepaymentFragment)
    fun inject(groupLoanAccountFragment: GroupLoanAccountFragment)
    fun inject(loanAccountFragment: LoanAccountFragment)
    fun inject(loanRepaymentScheduleFragment: LoanRepaymentScheduleFragment)
    fun inject(surveyListFragment: SurveyListFragment)
    fun inject(surveySubmitFragment: SurveySubmitFragment)
    fun inject(pinpointClientActivity: PinpointClientActivity)
    fun inject(chargeDialogFragment: ChargeDialogFragment)
    fun inject(dataTableRowDialogFragment: DataTableRowDialogFragment)
    fun inject(dataTableListFragment: DataTableListFragment)
    fun inject(documentDialogFragment: DocumentDialogFragment)
    fun inject(loanAccountApproval: LoanAccountApproval)
    fun inject(loanAccountDisbursement: LoanAccountDisbursementFragment)
    fun inject(loanChargeDialogFragment: LoanChargeDialogFragment)
    fun inject(savingsAccountApproval: SavingsAccountApprovalFragment)
    fun inject(syncPayloadsFragment: SyncClientPayloadsFragment)
    fun inject(syncGroupPayloadsFragment: SyncGroupPayloadsFragment)
    fun inject(syncCenterPayloadsFragment: SyncCenterPayloadsFragment)
    fun inject(offlineDashboardFragment: OfflineDashboardFragment)
    fun inject(syncLoanRepaymentTransactionFragment: SyncLoanRepaymentTransactionFragment)
    fun inject(syncClientsDialogFragment: SyncClientsDialogFragment)
    fun inject(syncSavingsAccountTransactionFragment: SyncSavingsAccountTransactionFragment)
    fun inject(syncGroupsDialogFragment: SyncGroupsDialogFragment)
    fun inject(syncCentersDialogFragment: SyncCentersDialogFragment)
    fun inject(syncSurveysDialogFragment: SyncSurveysDialogFragment)
    fun inject(identifierDialogFragment: IdentifierDialogFragment)
    fun inject(pathTrackingActivity: PathTrackingActivity)
    fun inject(centerDetailsFragment: CenterDetailsFragment)
    fun inject(activateClientFragment: ActivateFragment)
    fun inject(dataTableFragment: DataTableFragment)
    fun inject(noteFragment: NoteFragment)
    fun inject(savingsAccountActivateFragment: SavingsAccountActivateFragment)
    fun inject(signatureFragment: SignatureFragment)
    fun inject(individualCollectionSheetFragment: IndividualCollectionSheetFragment)
    fun inject(individualCollectionSheetFragment: NewIndividualCollectionSheetFragment)
    fun inject(individualCollectionSheetDetailsFragment: IndividualCollectionSheetDetailsFragment)
    fun inject(reportCategoryFragment: ReportCategoryFragment)
    fun inject(reportDetailFragment: ReportDetailFragment)
    fun inject(clientReportFragment: ReportFragment)
    fun inject(collectionSheetDialogFragment: CollectionSheetDialogFragment)
    fun inject(paymentDetailsFragment: PaymentDetailsFragment)
    fun inject(checkerInboxActivity: CheckerInboxPendingTasksActivity)
    fun inject(checkerInboxTasksFragment: CheckerInboxTasksFragment)
    fun inject(checkerInboxFragment: CheckerInboxFragment)
    fun inject(checkerTaskFilterDialogFragment: CheckerTaskFilterDialogFragment)
}