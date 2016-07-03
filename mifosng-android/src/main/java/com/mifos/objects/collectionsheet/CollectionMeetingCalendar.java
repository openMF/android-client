/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 16/07/14.
 */
public class CollectionMeetingCalendar implements Parcelable {

    private Integer id;
    private Integer calendarInstanceId;
    private Integer entityId;
    private EntityType entityType;
    private String title;
    private List<Integer> startDate = new ArrayList<Integer>();
    private Integer duration;
    private EntityType type;
    private Boolean repeating;
    private String recurrence;

    @SerializedName("frequency")
    private EntityType frequency;

    private Integer interval;

    @SerializedName("repeatsOnNthDayOfMonth")
    private EntityType repeatsOnDay;

    private Integer firstReminder;
    private Integer secondReminder;
    private List<List<Integer>> recurringDates = new ArrayList<>();
    private List<List<Integer>> nextTenRecurringDates = new ArrayList<>();
    private String humanReadable;
    private List<Integer> recentEligibleMeetingDate = new ArrayList<Integer>();
    private List<Integer> createdDate = new ArrayList<Integer>();
    private List<Integer> lastUpdatedDate = new ArrayList<Integer>();
    private Integer createdByUserId;
    private String createdByUsername;
    private Integer lastUpdatedByUserId;
    private String lastUpdatedByUsername;

    public void setFrequency(EntityType frequency) {
        this.frequency = frequency;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.calendarInstanceId);
        dest.writeValue(this.entityId);
        dest.writeParcelable(this.entityType, flags);
        dest.writeString(this.title);
        dest.writeList(this.startDate);
        dest.writeValue(this.duration);
        dest.writeParcelable(this.type, flags);
        dest.writeValue(this.repeating);
        dest.writeString(this.recurrence);
        dest.writeParcelable(this.frequency, flags);
        dest.writeValue(this.interval);
        dest.writeParcelable(this.repeatsOnDay, flags);
        dest.writeValue(this.firstReminder);
        dest.writeValue(this.secondReminder);
        dest.writeList(this.recurringDates);
        dest.writeList(this.nextTenRecurringDates);
        dest.writeString(this.humanReadable);
        dest.writeList(this.recentEligibleMeetingDate);
        dest.writeList(this.createdDate);
        dest.writeList(this.lastUpdatedDate);
        dest.writeValue(this.createdByUserId);
        dest.writeString(this.createdByUsername);
        dest.writeValue(this.lastUpdatedByUserId);
        dest.writeString(this.lastUpdatedByUsername);
    }

    public CollectionMeetingCalendar() {
    }

    protected CollectionMeetingCalendar(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.calendarInstanceId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.entityId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.entityType = in.readParcelable(EntityType.class.getClassLoader());
        this.title = in.readString();
        this.startDate = new ArrayList<Integer>();
        in.readList(this.startDate, Integer.class.getClassLoader());
        this.duration = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = in.readParcelable(EntityType.class.getClassLoader());
        this.repeating = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.recurrence = in.readString();
        this.frequency = in.readParcelable(EntityType.class.getClassLoader());
        this.interval = (Integer) in.readValue(Integer.class.getClassLoader());
        this.repeatsOnDay = in.readParcelable(EntityType.class.getClassLoader());
        this.firstReminder = (Integer) in.readValue(Integer.class.getClassLoader());
        this.secondReminder = (Integer) in.readValue(Integer.class.getClassLoader());
        this.recurringDates = new ArrayList<List<Integer>>();
        this.nextTenRecurringDates = new ArrayList<List<Integer>>();
        this.humanReadable = in.readString();
        this.recentEligibleMeetingDate = new ArrayList<Integer>();
        in.readList(this.recentEligibleMeetingDate, Integer.class.getClassLoader());
        this.createdDate = new ArrayList<Integer>();
        in.readList(this.createdDate, Integer.class.getClassLoader());
        this.lastUpdatedDate = new ArrayList<Integer>();
        in.readList(this.lastUpdatedDate, Integer.class.getClassLoader());
        this.createdByUserId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createdByUsername = in.readString();
        this.lastUpdatedByUserId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lastUpdatedByUsername = in.readString();
    }

    public static final Parcelable.Creator<CollectionMeetingCalendar> CREATOR = new Parcelable
            .Creator<CollectionMeetingCalendar>() {
        @Override
        public CollectionMeetingCalendar createFromParcel(Parcel source) {
            return new CollectionMeetingCalendar(source);
        }

        @Override
        public CollectionMeetingCalendar[] newArray(int size) {
            return new CollectionMeetingCalendar[size];
        }
    };
}
