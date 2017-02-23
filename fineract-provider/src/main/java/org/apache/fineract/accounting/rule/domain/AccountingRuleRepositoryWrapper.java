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
package org.apache.fineract.accounting.rule.domain;

import org.apache.fineract.accounting.rule.exception.AccountingRuleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link AccountingRuleRepository} .
 * </p>
 */
@Service
public class AccountingRuleRepositoryWrapper {

    private final AccountingRuleRepository repository;

    @Autowired
    public AccountingRuleRepositoryWrapper(final AccountingRuleRepository repository) {
        this.repository = repository;
    }

    public AccountingRule findOneWithNotFoundDetection(final Long id) {
        final AccountingRule accountingRule = this.repository.findOne(id);
        if (accountingRule == null) { throw new AccountingRuleNotFoundException(id); }
        return accountingRule;
    }

}