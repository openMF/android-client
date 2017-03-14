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
package org.apache.fineract.portfolio.transfer.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransferApiConstants {

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    // request parameters
    public static final String idParamName = "id";
    public static final String destinationGroupIdParamName = "destinationGroupId";
    public static final String clients = "clients";
    public static final String inheritDestinationGroupLoanOfficer = "inheritDestinationGroupLoanOfficer";
    public static final String newStaffIdParamName = "staffId";
    public static final String transferActiveLoans = "transferActiveLoans";
    public static final String destinationOfficeIdParamName = "destinationOfficeId";
    public static final String note = "note";

    public static final Set<String> TRANSFER_CLIENTS_BETWEEN_GROUPS_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, destinationGroupIdParamName, clients, inheritDestinationGroupLoanOfficer, newStaffIdParamName,
            transferActiveLoans));

    public static final Set<String> PROPOSE_CLIENT_TRANSFER_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, destinationOfficeIdParamName, transferActiveLoans, note));

    public static final Set<String> ACCEPT_CLIENT_TRANSFER_DATA_PARAMETERS = new HashSet<>(Arrays.asList(newStaffIdParamName,
            destinationGroupIdParamName, note));

    public static final Set<String> PROPOSE_AND_ACCEPT_CLIENT_TRANSFER_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, destinationOfficeIdParamName, transferActiveLoans, newStaffIdParamName, destinationGroupIdParamName, note));

    public static final Set<String> REJECT_CLIENT_TRANSFER_DATA_PARAMETERS = new HashSet<>(Arrays.asList(note));

    public static final Set<String> WITHDRAW_CLIENT_TRANSFER_DATA_PARAMETERS = new HashSet<>(Arrays.asList(note));

}