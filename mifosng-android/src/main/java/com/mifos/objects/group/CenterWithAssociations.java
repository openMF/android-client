/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import com.mifos.objects.Status;
import com.mifos.objects.Timeline;
import com.mifos.objects.collectionsheet.CollectionMeetingCalendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ishankhanna on 28/06/14.
 */
public class CenterWithAssociations {

    private Integer id;
    private String name;
    private String externalId;
    private Integer officeId;
    private String officeName;
    private Integer staffId;
    private String staffName;
    private String hierarchy;
    private Status status;
    private Boolean active;
    private List<Integer> activationDate = new ArrayList<Integer>();
    private Timeline timeline;
    private List<Group> groupMembers = new ArrayList<Group>();
    private CollectionMeetingCalendar collectionMeetingCalendar = new CollectionMeetingCalendar();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public List<Group> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Group> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public CollectionMeetingCalendar getCollectionMeetingCalendar() {
        return collectionMeetingCalendar;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
