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
package org.apache.fineract.organisation.workingdays.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WorkingDaysApiConstants {

    public static final String WORKING_DAYS_RESOURCE_NAME = "workingdays";

    // request parameters
    public static final String recurrence = "recurrence";

    public static final String repayment_rescheduling_enum = "repaymentRescheduleType";

    public static final String idParamName = "id";

    public static final String rescheduleRepaymentTemplate = "rescheduleRepaymentTemplate";
    public static final String localeParamName = "locale";
    public static final String extendTermForDailyRepayments = "extendTermForDailyRepayments";



    public static final Set<String> WORKING_DAYS_CREATE_OR_UPDATE_REQUEST_DATA_PARAMETERS =new HashSet<>(Arrays.asList(
            recurrence,repayment_rescheduling_enum,localeParamName,extendTermForDailyRepayments
    ));
    public static final Set<String> WORKING_DAYS_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName,
            recurrence,repayment_rescheduling_enum,extendTermForDailyRepayments
    ));

    public static final Set<String> WORKING_DAYS_TEMPLATE_PARAMETERS = new HashSet<>(Arrays.asList(rescheduleRepaymentTemplate));
}
