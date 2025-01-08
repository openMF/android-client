/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.zipmodels

import com.mifos.core.entity.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.entity.templates.savings.SavingsAccountTransactionTemplate

/**
 * Created by Rajan Maurya on 21/08/16.
 */
class SavingsAccountAndTransactionTemplate {
    var savingsAccountWithAssociations: SavingsAccountWithAssociations? = null
    var savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate? = null

    constructor()
    constructor(
        savingsAccountWithAssociations: SavingsAccountWithAssociations?,
        savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate?,
    ) {
        this.savingsAccountWithAssociations = savingsAccountWithAssociations
        this.savingsAccountTransactionTemplate = savingsAccountTransactionTemplate
    }

    override fun toString(): String {
        return "SavingsAccountAndTransactionTemplate{" +
            "savingsAccountWithAssociations=" + savingsAccountWithAssociations +
            ", savingsAccountTransactionTemplate=" + savingsAccountTransactionTemplate +
            '}'
    }
}
