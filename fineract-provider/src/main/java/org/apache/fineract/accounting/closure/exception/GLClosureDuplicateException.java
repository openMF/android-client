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
package org.apache.fineract.accounting.closure.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.joda.time.LocalDate;

/**
 * A {@link RuntimeException} thrown when a GL Closure for a given date and
 * Office combination is already present
 */
public class GLClosureDuplicateException extends AbstractPlatformDomainRuleException {

    public GLClosureDuplicateException(final Long officeId, final LocalDate closureDate) {
        super("error.msg.glclosure.glcode.duplicate", "An accounting closure for branch with Id " + officeId
                + " already exists for the date " + closureDate, officeId, closureDate);
    }

}