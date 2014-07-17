/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.group;

import com.mifos.objects.Status;
import com.mifos.objects.Timeline;
import com.mifos.objects.client.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ishankhanna on 29/06/14.
 */
public class GroupWithAssociations {

    List<Integer> activationDate = new ArrayList<Integer>();
    Boolean active;
    String hierarchy;
    Integer id;
    String name;
    Integer officeId;
    String officeName;
    Integer staffId;
    String staffName;
    Status status;
    Timeline timeline;
    List<Client> clientMembers = new ArrayList<Client>();
    Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public List<Client> getClientMembers() {
        return clientMembers;
    }

    public void setClientMembers(List<Client> clientMembers) {
        this.clientMembers = clientMembers;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
