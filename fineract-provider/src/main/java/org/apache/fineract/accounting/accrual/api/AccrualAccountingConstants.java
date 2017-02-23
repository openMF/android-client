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
package org.apache.fineract.accounting.accrual.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AccrualAccountingConstants {

    public static final String accrueTillParamName = "tillDate";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    
    public static final String PERIODIC_ACCRUAL_ACCOUNTING_RESOURCE_NAME = "periodicaccrual";
    public static final String PERIODIC_ACCRUAL_ACCOUNTING_EXECUTION_ERROR_CODE = "execution.failed";

    public static final Set<String> LOAN_PERIODIC_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(accrueTillParamName,
            localeParamName, dateFormatParamName));
}
