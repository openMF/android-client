package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.accounts.CenterAccounts
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.client.Client
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncCentersDialogRepositoryImp @Inject constructor(
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings,
    private val dataManagerGroups: DataManagerGroups,
    private val dataManagerClient: DataManagerClient
) : SyncCentersDialogRepository {

    override fun syncCenterAccounts(centerId: Int): Observable<CenterAccounts> {
        return dataManagerCenter.syncCenterAccounts(centerId)
    }

    override fun syncLoanById(loanId: Int): Observable<LoanWithAssociations> {
        return dataManagerLoan.syncLoanById(loanId)
    }

    override fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate> {
        return dataManagerLoan.syncLoanRepaymentTemplate(loanId)
    }

    override fun getCenterWithAssociations(centerId: Int): Observable<CenterWithAssociations> {
        return dataManagerCenter.getCenterWithAssociations(centerId)
    }

    override fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations> {
        return dataManagerGroups.getGroupWithAssociations(groupId)
    }

    override fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts> {
        return dataManagerGroups.syncGroupAccounts(groupId)
    }

    override fun syncClientAccounts(clientId: Int): Observable<ClientAccounts> {
        return dataManagerClient.syncClientAccounts(clientId)
    }

    override fun syncGroupInDatabase(group: Group): Observable<Group> {
        return dataManagerGroups.syncGroupInDatabase(group)
    }

    override fun syncClientInDatabase(client: Client): Observable<Client> {
        return dataManagerClient.syncClientInDatabase(client)
    }

    override fun syncCenterInDatabase(center: Center): Observable<Center> {
        return dataManagerCenter.syncCenterInDatabase(center)
    }

    override fun syncSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?
    ): Observable<SavingsAccountWithAssociations> {
        return dataManagerSavings.syncSavingsAccount(type, savingsAccountId, association)
    }

    override fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate> {
        return dataManagerSavings.syncSavingsAccountTransactionTemplate(
            savingsAccountType,
            savingsAccountId,
            transactionType
        )
    }
}