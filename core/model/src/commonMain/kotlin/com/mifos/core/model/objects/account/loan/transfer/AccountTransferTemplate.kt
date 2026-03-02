/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Data class representing the template for account transfers
 */
@Parcelize
@Serializable
data class AccountTransferTemplate(
    val fromOfficeOptions: List<OfficeOption> = emptyList(),
    val fromClientOptions: List<ClientOption> = emptyList(),
    val fromAccountTypeOptions: List<AccountTypeOption> = emptyList(),
    val fromAccountOptions: List<AccountOption> = emptyList(),
    val toOfficeOptions: List<OfficeOption> = emptyList(),
    val toClientOptions: List<ClientOption> = emptyList(),
    val toAccountTypeOptions: List<AccountTypeOption> = emptyList(),
    val toAccountOptions: List<AccountOption> = emptyList(),
    val currencyOptions: List<CurrencyOption> = emptyList(),
) : Parcelable

@Parcelize
@Serializable
data class OfficeOption(
    val id: Int,
    val name: String,
    val nameDecorated: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class ClientOption(
    val id: Int,
    val displayName: String,
    val officeId: Int? = null,
    val officeName: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class AccountTypeOption(
    val id: Int,
    val code: String,
    val value: String,
) : Parcelable

@Parcelize
@Serializable
data class AccountOption(
    val id: Int,
    val accountNo: String,
    val clientId: Int? = null,
    val clientName: String? = null,
    val officeId: Int? = null,
    val officeName: String? = null,
    val accountType: AccountType? = null,
    val currency: Currency? = null,
) : Parcelable

@Parcelize
@Serializable
data class AccountType(
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class Currency(
    val code: String,
    val name: String? = null,
    val decimalPlaces: Int? = null,
    val inMultiplesOf: Int? = null,
    val displaySymbol: String? = null,
    val nameCode: String? = null,
    val displayLabel: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class CurrencyOption(
    val code: String,
    val name: String? = null,
    val decimalPlaces: Int? = null,
    val inMultiplesOf: Int? = null,
    val displaySymbol: String? = null,
) : Parcelable
