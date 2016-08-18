package com.mifos.mifosxdroid.injection.component;

import com.mifos.mifosxdroid.activity.pinpointclient.PinpointClientActivity;
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogFragment;
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogFragment;
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogFragment;
import com.mifos.mifosxdroid.dialogfragments.loanaccountapproval.LoanAccountApproval;
import com.mifos.mifosxdroid.dialogfragments.loanaccountdisbursement.LoanAccountDisbursement;
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogFragment;
import com.mifos.mifosxdroid.dialogfragments.savingsaccountapproval.SavingsAccountApproval;
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogFragment;
import com.mifos.mifosxdroid.injection.PerActivity;
import com.mifos.mifosxdroid.injection.module.ActivityModule;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment;
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsFragment;
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsFragment;
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionFragment;
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment;
import com.mifos.mifosxdroid.online.clientchoose.ClientChooseFragment;
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment;
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment;
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchFragment;
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment;
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsFragment;
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment;
import com.mifos.mifosxdroid.online.grouploanaccount.GroupLoanAccountFragment;
import com.mifos.mifosxdroid.online.groupslist.GroupsListFragment;
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountFragment;
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryFragment;
import com.mifos.mifosxdroid.online.loancharge.LoanChargeFragment;
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentFragment;
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleFragment;
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsFragment;
import com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryFragment;
import com.mifos.mifosxdroid.online.savingaccounttransaction.SavingsAccountTransactionFragment;
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment;
import com.mifos.mifosxdroid.online.surveylist.SurveyListFragment;
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment;

import dagger.Component;

/**
 * @author Rajan Maurya
 *         This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

    void inject(CenterListFragment centerListFragment);

    void inject(ClientChargeFragment clientChargeFragment);

    void inject(ClientListFragment clientListFragment);

    void inject(ClientChooseFragment clientChooseFragment);

    void inject(ClientIdentifiersFragment clientIdentifiersFragment);

    void inject(ClientSearchFragment clientSearchFragment);

    void inject(DocumentListFragment documentListFragment);

    void inject(GroupListFragment groupListFragment);

    void inject(GenerateCollectionSheetFragment generateCollectionSheetFragment);

    void inject(CreateNewCenterFragment createNewCenterFragment);

    void inject(CreateNewGroupFragment createNewGroupFragment);

    void inject(CreateNewClientFragment createNewClientFragment);

    void inject(DataTableDataFragment dataTableDataFragment);

    void inject(GroupDetailsFragment groupDetailsFragment);

    void inject(ClientDetailsFragment clientDetailsFragment);

    void inject(LoanAccountSummaryFragment loanAccountSummaryFragment);

    void inject(SavingsAccountSummaryFragment savingsAccountSummaryFragment);

    void inject(LoanChargeFragment loanChargeFragment);

    void inject(SavingsAccountTransactionFragment savingsAccountTransactionFragment);

    void inject(CollectionSheetFragment collectionSheetFragment);

    void inject(GroupsListFragment groupsListFragment);

    void inject(LoanTransactionsFragment loanTransactionsFragment);

    void inject(SavingsAccountFragment savingsAccountFragment);

    void inject(LoanRepaymentFragment loanRepaymentFragment);

    void inject(GroupLoanAccountFragment groupLoanAccountFragment);

    void inject(LoanAccountFragment loanAccountFragment);

    void inject(LoanRepaymentScheduleFragment loanRepaymentScheduleFragment);

    void inject(SurveyListFragment surveyListFragment);

    void inject(SurveySubmitFragment surveySubmitFragment);

    void inject(PinpointClientActivity pinpointClientActivity);

    void inject(ChargeDialogFragment chargeDialogFragment);

    void inject(DataTableRowDialogFragment dataTableRowDialogFragment);

    void inject(DocumentDialogFragment documentDialogFragment);

    void inject(LoanAccountApproval loanAccountApproval);

    void inject(LoanAccountDisbursement loanAccountDisbursement);

    void inject(LoanChargeDialogFragment loanChargeDialogFragment);

    void inject(SavingsAccountApproval savingsAccountApproval);

    void inject(SyncClientPayloadsFragment syncPayloadsFragment);

    void inject(SyncGroupPayloadsFragment syncGroupPayloadsFragment);

    void inject(OfflineDashboardFragment offlineDashboardFragment);

    void inject(SyncLoanRepaymentTransactionFragment syncLoanRepaymentTransactionFragment);

    void inject(SyncClientsDialogFragment syncClientsDialogFragment);
}
