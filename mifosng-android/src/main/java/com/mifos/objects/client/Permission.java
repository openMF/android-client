/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.client;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class Permission {

    private String grouping;
    private String code;
    private String entityName;
    private String actionName;
    private boolean selected;

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
