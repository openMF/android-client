/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.zipmodels

import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate

/**
 *
 * Created by Rajan Maurya on 08/08/16.
 */
class LoanAndLoanRepayment {
    var loanWithAssociations: LoanWithAssociations? = null
    var loanRepaymentTemplate: LoanRepaymentTemplate? = null

    constructor()
    constructor(
        loanWithAssociations: LoanWithAssociations?,
        loanRepaymentTemplate: LoanRepaymentTemplate?,
    ) {
        this.loanWithAssociations = loanWithAssociations
        this.loanRepaymentTemplate = loanRepaymentTemplate
    }

    override fun toString(): String {
        return "LoanAndLoanRepayment{" +
            "loanWithAssociations=" + loanWithAssociations +
            ", loanRepaymentTemplate=" + loanRepaymentTemplate +
            '}'
    }
}
