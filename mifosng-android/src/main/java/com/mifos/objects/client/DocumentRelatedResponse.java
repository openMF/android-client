package com.mifos.objects.client;

/**
 * Created by Tarun on 14-08-17.
 */

public class DocumentRelatedResponse {

    private int resourceId;

    private String resourceIdentifier;

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }
}
