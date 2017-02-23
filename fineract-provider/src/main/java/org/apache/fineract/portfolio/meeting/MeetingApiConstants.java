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
package org.apache.fineract.portfolio.meeting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MeetingApiConstants {

    public static final String MEETING_RESOURCE_NAME = "meeting";

    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    // meetings parameters
    public static final String idParamName = "id";
    public static final String meetingDateParamName = "meetingDate";
    public static final String calendarIdParamName = "calendarId";
    public static final String clientsAttendanceParamName = "clientsAttendance";

    // attendance parameters
    public static final String clientIdParamName = "clientId";
    public static final String attendanceTypeParamName = "attendanceType";

    // attendance response parameters
    public static final String clientsAttendance = "clientsAttendance";

    // template response parameters
    public static final String clients = "clients";
    public static final String calendarData = "calendarData";
    public static final String attendanceTypeOptions = "attendanceTypeOptions";

    public static final Set<String> MEETING_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(meetingDateParamName,
            localeParamName, dateFormatParamName, calendarIdParamName, clientsAttendanceParamName));

    public static final Set<String> MEETING_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, meetingDateParamName,
            clientsAttendance, clients, calendarData, attendanceTypeOptions));

}