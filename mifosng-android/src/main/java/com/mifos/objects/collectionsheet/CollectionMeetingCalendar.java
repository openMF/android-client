/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.collectionsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ishankhanna on 16/07/14.
 */
public class CollectionMeetingCalendar {

    private Integer calendarInstanceId;
    private Integer createdByUserId;
    private String createdByUsername;
    private List<Integer> createdDate = new ArrayList<Integer>();
    private Integer duration;
    private Integer entityId;
    private EntityType entityType;
    private Integer firstReminder;
    private CollectionFrequency frequency;
    private String  humanReadable;
    private Integer id;
    private Integer interval;
    private Integer lastUpdatedByUserId;
    private String  lastUpdatedByUsername;
    private List<Integer> lastUpdatedDate = new ArrayList<Integer>();
    private List<List<Integer>> nextTenRecurringDates = new ArrayList<List<Integer>>();
    private List<Integer> recentEligibleMeetingDate = new ArrayList<Integer>();
    private String recurrence;
    private List<List<Integer>> recurringDates = new ArrayList<List<Integer>>();
    private Boolean repeating;
    private EntityType repeatsOnDay;
    private Integer secondReminder;
    private List<Integer> startDate = new ArrayList<Integer>();
    private String title;
    private EntityType type;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getCalendarInstanceId() {
        return calendarInstanceId;
    }

    public void setCalendarInstanceId(Integer calendarInstanceId) {
        this.calendarInstanceId = calendarInstanceId;
    }

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public List<Integer> getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(List<Integer> createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Integer getFirstReminder() {
        return firstReminder;
    }

    public void setFirstReminder(Integer firstReminder) {
        this.firstReminder = firstReminder;
    }

    public CollectionFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(CollectionFrequency frequency) {
        this.frequency = frequency;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    public void setHumanReadable(String humanReadable) {
        this.humanReadable = humanReadable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getLastUpdatedByUserId() {
        return lastUpdatedByUserId;
    }

    public void setLastUpdatedByUserId(Integer lastUpdatedByUserId) {
        this.lastUpdatedByUserId = lastUpdatedByUserId;
    }

    public String getLastUpdatedByUsername() {
        return lastUpdatedByUsername;
    }

    public void setLastUpdatedByUsername(String lastUpdatedByUsername) {
        this.lastUpdatedByUsername = lastUpdatedByUsername;
    }

    public List<Integer> getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(List<Integer> lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<List<Integer>> getNextTenRecurringDates() {
        return nextTenRecurringDates;
    }

    public void setNextTenRecurringDates(List<List<Integer>> nextTenRecurringDates) {
        this.nextTenRecurringDates = nextTenRecurringDates;
    }

    public List<Integer> getRecentEligibleMeetingDate() {
        return recentEligibleMeetingDate;
    }

    public void setRecentEligibleMeetingDate(List<Integer> recentEligibleMeetingDate) {
        this.recentEligibleMeetingDate = recentEligibleMeetingDate;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public List<List<Integer>> getRecurringDates() {
        return recurringDates;
    }

    public void setRecurringDates(List<List<Integer>> recurringDates) {
        this.recurringDates = recurringDates;
    }

    public Boolean getRepeating() {
        return repeating;
    }

    public void setRepeating(Boolean repeating) {
        this.repeating = repeating;
    }

    public EntityType getRepeatsOnDay() {
        return repeatsOnDay;
    }

    public void setRepeatsOnDay(EntityType repeatsOnDay) {
        this.repeatsOnDay = repeatsOnDay;
    }

    public Integer getSecondReminder() {
        return secondReminder;
    }

    public void setSecondReminder(Integer secondReminder) {
        this.secondReminder = secondReminder;
    }

    public List<Integer> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<Integer> startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
