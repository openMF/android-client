package com.mifos.core.network.mappers.clients

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.loan.LoanType
import com.mifos.core.objects.accounts.savings.Currency
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.accounts.savings.Status
import org.apache.fineract.client.models.GetClientsClientIdAccountsResponse
import org.apache.fineract.client.models.GetClientsLoanAccounts
import org.apache.fineract.client.models.GetClientsLoanAccountsStatus
import org.apache.fineract.client.models.GetClientsLoanAccountsType
import org.apache.fineract.client.models.GetClientsSavingsAccounts
import org.apache.fineract.client.models.GetClientsSavingsAccountsCurrency
import org.apache.fineract.client.models.GetClientsSavingsAccountsStatus
import org.mifos.core.data.AbstractMapper

/**
 * Created by Aditya Gupta on 30/08/23.
 */

object GetClientsClientIdAccountMapper :
    AbstractMapper<GetClientsClientIdAccountsResponse, ClientAccounts>() {

    override fun mapFromEntity(entity: GetClientsClientIdAccountsResponse): ClientAccounts {
        return ClientAccounts().apply {
            savingsAccounts = entity.savingsAccounts?.map {
                SavingsAccount().apply {
                    id = it.id
                    accountNo = it.accountNo
                    productId = it.productId
                    productName = it.productName
                    depositType = DepositType().apply {
                        id = it.depositType!!.id
                        code = it.depositType!!.code
                        value = it.depositType!!.value
                    }
                    status = Status().apply {
                        id = it.status?.id
                        code = it.status?.code
                        value = it.status?.value
                        submittedAndPendingApproval = it.status?.submittedAndPendingApproval
                        approved = it.status?.approved
                        rejected = it.status?.rejected
                        withdrawnByApplicant = it.status?.withdrawnByApplicant
                        active = it.status?.active
                        closed = it.status?.closed
                    }
                    currency = Currency().apply {
                        code = it.currency!!.code
                        name = it.currency!!.name
                        nameCode = it.currency!!.nameCode
                        decimalPlaces = it.currency!!.decimalPlaces
                        displaySymbol = it.currency!!.displaySymbol
                        displayLabel = it.currency!!.displayLabel
                    }
                }
            }!!
            loanAccounts = entity.loanAccounts?.map {
                LoanAccount().apply {
                    id = it.id
                    accountNo = it.accountNo
                    externalId = it.externalId.toString()
                    productId = it.productId
                    productName = it.productName
                    status = com.mifos.core.objects.accounts.loan.Status().apply {
                        id = it.status?.id
                        code = it.status?.code
                        value = it.status?.description
                        pendingApproval = it.status?.pendingApproval
                        waitingForDisbursal = it.status?.waitingForDisbursal
                        active = it.status?.active
                        closedObligationsMet = it.status?.closedObligationsMet
                        closedWrittenOff = it.status?.closedWrittenOff
                        closedRescheduled = it.status?.closedRescheduled
                        closed = it.status?.closed
                        overpaid = it.status?.overpaid
                    }
                    loanType = LoanType().apply {
                        id = it.loanType?.id
                        code = it.loanType?.code
                        value = it.loanType?.description
                    }
                    loanCycle = it.loanCycle
                }
            }!!
        }
    }

    override fun mapToEntity(domainModel: ClientAccounts): GetClientsClientIdAccountsResponse {
        return GetClientsClientIdAccountsResponse().apply {
            savingsAccounts = domainModel.savingsAccounts.map {
                GetClientsSavingsAccounts().apply {
                    id = it.id
                    accountNo = it.accountNo
                    productId = it.productId
                    productName = it.productName
                    status = GetClientsSavingsAccountsStatus().apply {
                        id = it.status?.id
                        code = it.status?.code
                        value = it.status?.value
                        submittedAndPendingApproval = it.status?.submittedAndPendingApproval
                        approved = it.status?.approved
                        rejected = it.status?.rejected
                        withdrawnByApplicant = it.status?.withdrawnByApplicant
                        active = it.status?.active
                        closed = it.status?.closed
                    }
                    currency = GetClientsSavingsAccountsCurrency().apply {
                        code = it.currency!!.code
                        name = it.currency!!.name
                        nameCode = it.currency!!.nameCode
                        decimalPlaces = it.currency!!.decimalPlaces
                        displaySymbol = it.currency!!.displaySymbol
                        displayLabel = it.currency!!.displayLabel
                    }
                }
            }
            loanAccounts = domainModel.loanAccounts.map {
                GetClientsLoanAccounts().apply {
                    id = it.id
                    accountNo = it.accountNo
                    externalId = it.externalId?.toInt()
                    productId = it.productId
                    productName = it.productName
                    status = GetClientsLoanAccountsStatus().apply {
                        id = it.status?.id
                        code = it.status?.code
                        description = it.status?.value
                        pendingApproval = it.status?.pendingApproval
                        waitingForDisbursal = it.status?.waitingForDisbursal
                        active = it.status?.active
                        closedObligationsMet = it.status?.closedObligationsMet
                        closedWrittenOff = it.status?.closedWrittenOff
                        closedRescheduled = it.status?.closedRescheduled
                        closed = it.status?.closed
                        overpaid = it.status?.overpaid
                    }
                    loanType = GetClientsLoanAccountsType().apply {
                        id = it.loanType?.id
                        code = it.loanType?.code
                        description = it.loanType?.value
                    }
                    loanCycle = it.loanCycle
                }
            }
        }
    }
}