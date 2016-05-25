package com.mifos.objects.templates.savings;

import com.mifos.objects.InterestType;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

/**
 * Created by rajan on 13/3/16.
 */
public class AccountOptions {

    private Integer id;
    private String name;
    private Integer glCode;
    private Boolean disabled;
    private Boolean manualEntriesAllowed;
    private InterestType type;
    private InterestType usage;
    private String nameDecorated;
    private TagId tagId;

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

    public Integer getGlCode() {
        return glCode;
    }

    public void setGlCode(Integer glCode) {
        this.glCode = glCode;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getManualEntriesAllowed() {
        return manualEntriesAllowed;
    }

    public void setManualEntriesAllowed(Boolean manualEntriesAllowed) {
        this.manualEntriesAllowed = manualEntriesAllowed;
    }

    public InterestType getType() {
        return type;
    }

    public void setType(InterestType type) {
        this.type = type;
    }

    public InterestType getUsage() {
        return usage;
    }

    public void setUsage(InterestType usage) {
        this.usage = usage;
    }

    public String getNameDecorated() {
        return nameDecorated;
    }

    public void setNameDecorated(String nameDecorated) {
        this.nameDecorated = nameDecorated;
    }

    public TagId getTagId() {
        return tagId;
    }

    public void setTagId(TagId tagId) {
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "AccountOptions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", glCode=" + glCode +
                ", disabled=" + disabled +
                ", manualEntriesAllowed=" + manualEntriesAllowed +
                ", type=" + type +
                ", usage=" + usage +
                ", nameDecorated='" + nameDecorated + '\'' +
                ", tagId=" + tagId +
                '}';
    }

    public class TagId {

        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

    }
}
