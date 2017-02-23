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
package org.apache.fineract.portfolio.group.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.joda.time.LocalDate;

/**
 * Immutable data object representing groups.
 */
public class CenterData {

    private final Long id;
    private String accountNo;
    private final String name;
    private final String externalId;
    private final Long officeId;
    private final String officeName;
    private final Long staffId;
    private final String staffName;
    private final String hierarchy;

    private final EnumOptionData status;
    @SuppressWarnings("unused")
    private final boolean active;
    private final LocalDate activationDate;

    private final GroupTimelineData timeline;
    // associations
    private final Collection<GroupGeneralData> groupMembers;

    // template
    private final Collection<GroupGeneralData> groupMembersOptions;
    private final CalendarData collectionMeetingCalendar;
    private final Collection<CodeValueData> closureReasons;
    private final Collection<OfficeData> officeOptions;
    private final Collection<StaffData> staffOptions;

    public static CenterData template(final Long officeId, final String accountNo, final LocalDate activationDate, final Collection<OfficeData> officeOptions,
            final Collection<StaffData> staffOptions, final Collection<GroupGeneralData> groupMembersOptions) {
        final CalendarData collectionMeetingCalendar = null;
        final Collection<CodeValueData> closureReasons = null;
        final GroupTimelineData timeline = null;
        return new CenterData(null, accountNo, null, null, null, activationDate, officeId, null, null, null, null, null, officeOptions, staffOptions,
                groupMembersOptions, collectionMeetingCalendar, closureReasons, timeline);
    }

    public static CenterData withTemplate(final CenterData templateCenter, final CenterData center) {
        return new CenterData(center.id, center.accountNo, center.name, center.externalId, center.status, center.activationDate, center.officeId,
                center.officeName, center.staffId, center.staffName, center.hierarchy, center.groupMembers, templateCenter.officeOptions,
                templateCenter.staffOptions, templateCenter.groupMembersOptions, templateCenter.collectionMeetingCalendar,
                templateCenter.closureReasons, center.timeline);
    }

    public static CenterData instance(final Long id, final String accountNo, final String name, final String externalId, final EnumOptionData status,
            final LocalDate activationDate, final Long officeId, final String officeName, final Long staffId, final String staffName,
            final String hierarchy, final GroupTimelineData timeline, final CalendarData collectionMeetingCalendar) {

        final Collection<GroupGeneralData> groupMembers = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<GroupGeneralData> groupMembersOptions = null;
        final Collection<CodeValueData> closureReasons = null;

        return new CenterData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, staffId, staffName, hierarchy,
                groupMembers, officeOptions, staffOptions, groupMembersOptions, collectionMeetingCalendar, closureReasons, timeline);
    }

    public static CenterData withAssociations(final CenterData centerData, final Collection<GroupGeneralData> groupMembers,
            final CalendarData collectionMeetingCalendar) {
        return new CenterData(centerData.id, centerData.accountNo, centerData.name, centerData.externalId, centerData.status, centerData.activationDate,
                centerData.officeId, centerData.officeName, centerData.staffId, centerData.staffName, centerData.hierarchy, groupMembers,
                centerData.officeOptions, centerData.staffOptions, centerData.groupMembersOptions, collectionMeetingCalendar,
                centerData.closureReasons, centerData.timeline);
    }

    public static CenterData withClosureReasons(final Collection<CodeValueData> closureReasons) {
        final Long id = null;
        final String accountNo = null;
        final String name = null;
        final String externalId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final Long officeId = null;
        final String officeName = null;
        final Long staffId = null;
        final String staffName = null;
        final String hierarchy = null;
        final Collection<GroupGeneralData> groupMembers = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<GroupGeneralData> groupMembersOptions = null;
        final CalendarData collectionMeetingCalendar = null;
        final GroupTimelineData timeline = null;
        return new CenterData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, staffId, staffName, hierarchy,
                groupMembers, officeOptions, staffOptions, groupMembersOptions, collectionMeetingCalendar, closureReasons, timeline);
    }

    private CenterData(final Long id, final String accountNo, final String name, final String externalId, final EnumOptionData status,
            final LocalDate activationDate, final Long officeId, final String officeName, final Long staffId, final String staffName,
            final String hierarchy, final Collection<GroupGeneralData> groupMembers, final Collection<OfficeData> officeOptions,
            final Collection<StaffData> staffOptions, final Collection<GroupGeneralData> groupMembersOptions,
            final CalendarData collectionMeetingCalendar, final Collection<CodeValueData> closureReasons, final GroupTimelineData timeline) {
        this.id = id;
        this.accountNo = accountNo;
        this.name = name;
        this.externalId = externalId;
        this.status = status;
        if (status != null) {
            this.active = status.getId().equals(300L);
        } else {
            this.active = false;
        }
        this.activationDate = activationDate;
        this.officeId = officeId;
        this.officeName = officeName;
        this.staffId = staffId;
        this.staffName = staffName;
        this.hierarchy = hierarchy;

        this.groupMembers = groupMembers;

        //
        this.officeOptions = officeOptions;
        this.staffOptions = staffOptions;
        this.groupMembersOptions = groupMembersOptions;
        this.collectionMeetingCalendar = collectionMeetingCalendar;
        this.closureReasons = closureReasons;
        this.timeline = timeline;
    }

    public Long officeId() {
        return this.officeId;
    }

    public Long staffId() {
        return this.staffId;
    }

    public Long getId() {
        return this.id;
    }
    
    public String getAccountNo(){
    	return this.accountNo;
    }

    public String getName() {
        return this.name;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public CalendarData getCollectionMeetingCalendar() {
        return collectionMeetingCalendar;
    }

    public String getStaffName() {
        return this.staffName;
    }
}