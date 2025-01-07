/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.clients

import com.mifos.core.entity.accounts.ClientAccounts
import com.mifos.core.entity.accounts.loan.LoanAccount
import com.mifos.core.entity.accounts.loan.LoanType
import com.mifos.core.entity.accounts.savings.Currency
import com.mifos.core.entity.accounts.savings.DepositType
import com.mifos.core.entity.accounts.savings.SavingsAccount
import com.mifos.core.entity.accounts.savings.Status
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetClientsClientIdAccountsResponse
import org.openapitools.client.models.GetClientsLoanAccounts
import org.openapitools.client.models.GetClientsLoanAccountsStatus
import org.openapitools.client.models.GetClientsLoanAccountsType
import org.openapitools.client.models.GetClientsSavingsAccounts
import org.openapitools.client.models.GetClientsSavingsAccountsCurrency
import org.openapitools.client.models.GetClientsSavingsAccountsDepositType
import org.openapitools.client.models.GetClientsSavingsAccountsStatus

/**
 * Created by Aditya Gupta on 30/08/23.
 */

object GetClientsClientIdAccountMapper :
    AbstractMapper<GetClientsClientIdAccountsResponse, ClientAccounts>() {

    override fun mapFromEntity(entity: GetClientsClientIdAccountsResponse): ClientAccounts {
        return ClientAccounts().apply {
            savingsAccounts = entity.savingsAccounts?.map {
                SavingsAccount().apply {
                    id = it.id?.toInt()
                    accountNo = it.accountNo
                    productId = it.productId?.toInt()
                    productName = it.productName
                    depositType = DepositType().apply {
                        id = it.depositType?.id?.toInt()
                        code = it.depositType?.code
                        value = it.depositType?.value
                    }
                    status = Status().apply {
                        id = it.status?.id?.toInt()
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
                    id = it.id?.toInt()
                    accountNo = it.accountNo
                    externalId = it.externalId.toString()
                    productId = it.productId?.toInt()
                    productName = it.productName
                    status = com.mifos.core.entity.accounts.loan.Status().apply {
                        id = it.status?.id?.toInt()
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
                        id = it.loanType?.id?.toInt()
                        code = it.loanType?.code
                        value = it.loanType?.description
                    }
                    loanCycle = it.loanCycle
                }
            }!!
        }
    }

    override fun mapToEntity(domainModel: ClientAccounts): GetClientsClientIdAccountsResponse {
        return GetClientsClientIdAccountsResponse(
            savingsAccounts = domainModel.savingsAccounts.map {
                GetClientsSavingsAccounts(
                    id = it.id?.toLong(),
                    accountNo = it.accountNo,
                    productId = it.productId?.toLong(),
                    productName = it.productName,
                    depositType = GetClientsSavingsAccountsDepositType(
                        id = it.depositType?.id?.toLong(),
                        code = it.depositType?.code,
                        value = it.depositType?.value,
                    ),
                    status = GetClientsSavingsAccountsStatus(
                        id = it.status?.id?.toLong(),
                        code = it.status?.code,
                        value = it.status?.value,
                        submittedAndPendingApproval = it.status?.submittedAndPendingApproval,
                        approved = it.status?.approved,
                        rejected = it.status?.rejected,
                        withdrawnByApplicant = it.status?.withdrawnByApplicant,
                        active = it.status?.active,
                        closed = it.status?.closed,
                    ),
                    currency = GetClientsSavingsAccountsCurrency(
                        code = it.currency!!.code,
                        name = it.currency!!.name,
                        nameCode = it.currency!!.nameCode,
                        decimalPlaces = it.currency!!.decimalPlaces,
                        displaySymbol = it.currency!!.displaySymbol,
                    ),
                )
            }.toSet(),
            loanAccounts = domainModel.loanAccounts.map {
                GetClientsLoanAccounts(
                    id = it.id?.toLong(),
                    accountNo = it.accountNo,
                    externalId = it.externalId,
                    productId = it.productId?.toLong(),
                    productName = it.productName,
                    status = GetClientsLoanAccountsStatus(
                        id = it.status?.id?.toLong(),
                        code = it.status?.code,
                        description = it.status?.value,
                        pendingApproval = it.status?.pendingApproval,
                        waitingForDisbursal = it.status?.waitingForDisbursal,
                        active = it.status?.active,
                        closedObligationsMet = it.status?.closedObligationsMet,
                        closedWrittenOff = it.status?.closedWrittenOff,
                        closedRescheduled = it.status?.closedRescheduled,
                        closed = it.status?.closed,
                        overpaid = it.status?.overpaid,
                    ),
                    loanType = GetClientsLoanAccountsType(
                        id = it.loanType?.id?.toLong(),
                        code = it.loanType?.code,
                        description = it.loanType?.value,
                    ),
                    loanCycle = it.loanCycle,
                )
            }.toSet(),
        )
    }
}
