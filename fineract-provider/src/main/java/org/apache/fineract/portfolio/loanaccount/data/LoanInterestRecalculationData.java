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
package org.apache.fineract.portfolio.loanaccount.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.joda.time.LocalDate;

public class LoanInterestRecalculationData {

    private final Long id;
    private final Long loanId;
    private final EnumOptionData interestRecalculationCompoundingType;
    private final EnumOptionData rescheduleStrategyType;
    @SuppressWarnings("unused")
    private final CalendarData calendarData;
    private final EnumOptionData recalculationRestFrequencyType;
    private final Integer recalculationRestFrequencyInterval;
    private final LocalDate recalculationRestFrequencyDate;
    private final EnumOptionData recalculationCompoundingFrequencyType;
    private final Integer recalculationCompoundingFrequencyInterval;
    private final LocalDate recalculationCompoundingFrequencyDate;
    @SuppressWarnings("unused")
    private final CalendarData compoundingCalendarData;

    public LoanInterestRecalculationData(final Long id, final Long loanId, final EnumOptionData interestRecalculationCompoundingType,
            final EnumOptionData rescheduleStrategyType, final CalendarData calendarData,
            final EnumOptionData recalculationRestFrequencyType, final Integer recalculationRestFrequencyInterval,
            final LocalDate recalculationRestFrequencyDate, final CalendarData compoundingCalendarData,
            final EnumOptionData recalculationCompoundingFrequencyType, final Integer recalculationCompoundingFrequencyInterval,
            final LocalDate recalculationCompoundingFrequencyDate) {
        this.id = id;
        this.loanId = loanId;
        this.interestRecalculationCompoundingType = interestRecalculationCompoundingType;
        this.rescheduleStrategyType = rescheduleStrategyType;
        this.calendarData = calendarData;
        this.recalculationRestFrequencyType = recalculationRestFrequencyType;
        this.recalculationRestFrequencyInterval = recalculationRestFrequencyInterval;
        this.recalculationRestFrequencyDate = recalculationRestFrequencyDate;
        this.recalculationCompoundingFrequencyType = recalculationCompoundingFrequencyType;
        this.recalculationCompoundingFrequencyInterval = recalculationCompoundingFrequencyInterval;
        this.recalculationCompoundingFrequencyDate = recalculationCompoundingFrequencyDate;
        this.compoundingCalendarData = compoundingCalendarData;
    }

    public static LoanInterestRecalculationData withCalendarData(final LoanInterestRecalculationData recalculationData,
            final CalendarData calendarData, CalendarData compoundingCalendarData) {
        return new LoanInterestRecalculationData(recalculationData.id, recalculationData.loanId,
                recalculationData.interestRecalculationCompoundingType, recalculationData.rescheduleStrategyType, calendarData,
                recalculationData.recalculationRestFrequencyType, recalculationData.recalculationRestFrequencyInterval,
                recalculationData.recalculationRestFrequencyDate, compoundingCalendarData,
                recalculationData.recalculationCompoundingFrequencyType, recalculationData.recalculationCompoundingFrequencyInterval,
                recalculationData.recalculationCompoundingFrequencyDate);
    }

    public Long getId() {
        return this.id;
    }

}
