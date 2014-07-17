/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

/**
 * Created by ishankhanna on 02/07/14.
 */
public class Document {

    private int id;
    private String parentEntityType;
    private int parentEntityId;
    private String name;
    private String fileName;
    private long size;
    private String type;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentEntityType() {
        return parentEntityType;
    }

    public void setParentEntityType(String parentEntityType) {
        this.parentEntityType = parentEntityType;
    }

    public int getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(int parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
