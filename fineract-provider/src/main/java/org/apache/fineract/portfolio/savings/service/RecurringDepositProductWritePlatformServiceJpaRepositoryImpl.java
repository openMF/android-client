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
package org.apache.fineract.portfolio.savings.service;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.accountingRuleParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.chargesParamName;

import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.interestratechart.service.InterestRateChartAssembler;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.data.DepositProductDataValidator;
import org.apache.fineract.portfolio.savings.domain.DepositProductAssembler;
import org.apache.fineract.portfolio.savings.domain.RecurringDepositProduct;
import org.apache.fineract.portfolio.savings.domain.RecurringDepositProductRepository;
import org.apache.fineract.portfolio.savings.exception.RecurringDepositProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecurringDepositProductWritePlatformServiceJpaRepositoryImpl implements RecurringDepositProductWritePlatformService {

    private final Logger logger;
    private final PlatformSecurityContext context;
    private final RecurringDepositProductRepository recurringDepositProductRepository;
    private final DepositProductDataValidator fromApiJsonDataValidator;
    private final DepositProductAssembler depositProductAssembler;
    private final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService;
    private final InterestRateChartAssembler chartAssembler;

    @Autowired
    public RecurringDepositProductWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final RecurringDepositProductRepository recurringDepositProductRepository,
            final DepositProductDataValidator fromApiJsonDataValidator, final DepositProductAssembler depositProductAssembler,
            final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService,
            final InterestRateChartAssembler chartAssembler) {
        this.context = context;
        this.recurringDepositProductRepository = recurringDepositProductRepository;
        this.fromApiJsonDataValidator = fromApiJsonDataValidator;
        this.depositProductAssembler = depositProductAssembler;
        this.logger = LoggerFactory.getLogger(RecurringDepositProductWritePlatformServiceJpaRepositoryImpl.class);
        this.accountMappingWritePlatformService = accountMappingWritePlatformService;
        this.chartAssembler = chartAssembler;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        try {
            this.fromApiJsonDataValidator.validateForRecurringDepositCreate(command.json());

            final RecurringDepositProduct product = this.depositProductAssembler.assembleRecurringDepositProduct(command);

            this.recurringDepositProductRepository.save(product);

            // save accounting mappings
            this.accountMappingWritePlatformService.createSavingProductToGLAccountMapping(product.getId(), command,
                    DepositAccountType.RECURRING_DEPOSIT);

            return new CommandProcessingResultBuilder() //
                    .withEntityId(product.getId()) //
                    .build();
        } catch (final DataAccessException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult update(final Long productId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();
            this.fromApiJsonDataValidator.validateForRecurringDepositUpdate(command.json());

            final RecurringDepositProduct product = this.recurringDepositProductRepository.findOne(productId);
            if (product == null) { throw new RecurringDepositProductNotFoundException(productId); }
            product.setHelpers(this.chartAssembler);

            final Map<String, Object> changes = product.update(command);

            if (changes.containsKey(chargesParamName)) {
                final Set<Charge> savingsProductCharges = this.depositProductAssembler.assembleListOfSavingsProductCharges(command, product
                        .currency().getCode());
                final boolean updated = product.update(savingsProductCharges);
                if (!updated) {
                    changes.remove(chargesParamName);
                }
            }

            // accounting related changes
            final boolean accountingTypeChanged = changes.containsKey(accountingRuleParamName);
            final Map<String, Object> accountingMappingChanges = this.accountMappingWritePlatformService
                    .updateSavingsProductToGLAccountMapping(product.getId(), command, accountingTypeChanged, product.getAccountingType(),
                            DepositAccountType.RECURRING_DEPOSIT);
            changes.putAll(accountingMappingChanges);

            if (!changes.isEmpty()) {
                this.recurringDepositProductRepository.save(product);
            }

            return new CommandProcessingResultBuilder() //
                    .withEntityId(product.getId()) //
                    .with(changes).build();
        } catch (final DataAccessException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult delete(final Long productId) {

        this.context.authenticatedUser();
        final RecurringDepositProduct product = this.recurringDepositProductRepository.findOne(productId);
        if (product == null) { throw new RecurringDepositProductNotFoundException(productId); }

        this.recurringDepositProductRepository.delete(product);

        return new CommandProcessingResultBuilder() //
                .withEntityId(product.getId()) //
                .build();
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataAccessException dae) {

        final Throwable realCause = dae.getMostSpecificCause();
        if (realCause.getMessage().contains("sp_unq_name")) {

            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.product.savings.duplicate.name", "Recurring Deposit product with name `"
                    + name + "` already exists", "name", name);
        } else if (realCause.getMessage().contains("sp_unq_short_name")) {

            final String shortName = command.stringValueOfParameterNamed("shortName");
            throw new PlatformDataIntegrityException("error.msg.product.savings.duplicate.short.name",
                    "Recurring Deposit product with short name `" + shortName + "` already exists", "shortName", shortName);
        }

        logAsErrorUnexpectedDataIntegrityException(dae);
        throw new PlatformDataIntegrityException("error.msg.savingsproduct.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataAccessException dae) {
        this.logger.error(dae.getMessage(), dae);
    }
}