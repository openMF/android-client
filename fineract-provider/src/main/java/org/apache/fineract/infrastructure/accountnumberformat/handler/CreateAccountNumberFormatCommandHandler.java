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
package org.apache.fineract.infrastructure.accountnumberformat.handler;

import javax.transaction.Transactional;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.accountnumberformat.service.AccountNumberFormatWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "ACCOUNTNUMBERFORMAT", action = "CREATE")
public class CreateAccountNumberFormatCommandHandler implements NewCommandSourceHandler {

    private final AccountNumberFormatWritePlatformService accountNumberFormatWritePlatformService;

    @Autowired
    public CreateAccountNumberFormatCommandHandler(final AccountNumberFormatWritePlatformService accountNumberFormatWritePlatformService) {
        this.accountNumberFormatWritePlatformService = accountNumberFormatWritePlatformService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.accountNumberFormatWritePlatformService.createAccountNumberFormat(command);
    }

}
