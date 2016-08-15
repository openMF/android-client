/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CollectionMeetingCalendar {
    private int calendarInstanceId;
    private long calendarId;
    private int entityId;
    private EntityType entityType;
    private String title;
    private String description;
    private String location;
    private MeetingDate meetingCalendarDate;
    private boolean repeating;
    private String recurrence;

    private List<Integer> startDate = new ArrayList<Integer>();


    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public MeetingDate getMeetingCalendarDate() {
        return meetingCalendarDate;
    }

    public void setMeetingCalendarDate(MeetingDate meetingCalendarDate) {
        this.meetingCalendarDate = meetingCalendarDate;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }


    public void setCalendarInstanceId(int calendarInstanceId) {
        this.calendarInstanceId = calendarInstanceId;
    }


    public List<Integer> getStartDate() {
        return startDate;
    }


    public void setStartDate(List<Integer> startDate) {
        this.startDate = startDate;
    }

    public int getCalendarInstanceId() {
        return calendarInstanceId;
    }

    public void setCalendarInstanceId(Integer calendarInstanceId) {
        this.calendarInstanceId = calendarInstanceId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}



