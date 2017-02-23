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
package org.apache.fineract.portfolio.savings.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

/**
 * A {@link RuntimeException} thrown when update not allowed.
 */
public class TransactionUpdateNotAllowedException extends AbstractPlatformServiceUnavailableException {

    public TransactionUpdateNotAllowedException(final Long savingsId, final Long transactionId) {
        super("error.msg.saving.account.trasaction.update.notallowed",
                "Savings Account transaction update not allowed with savings identifier " + savingsId + " and trasaction identifier "
                        + transactionId, savingsId, transactionId);
    }
}