/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.charge.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * {@link AbstractPlatformDomainRuleException} thrown when loan charge does not
 * exist.
 */
public class LoanChargeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanChargeNotFoundException(final Long id) {
        super("error.msg.loanCharge.id.invalid", "Loan charge with identifier " + id + " does not exist", id);
    }

    public LoanChargeNotFoundException(final Long id, final Long loanId) {
        super("error.msg.loanCharge.id.invalid.for.given.loan", "Loan charge with identifier " + id + " does not exist for loan " + loanId,
                id, loanId);
    }
}