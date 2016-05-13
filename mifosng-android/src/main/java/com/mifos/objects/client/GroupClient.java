package com.mifos.objects.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 14/4/16.
 */
public class GroupClient<T> {
    private List<T> clientMembers = new ArrayList<T>();

    public List<T> getClientMembers() {
        return clientMembers;
    }

    public void setClientMembers(List<T> clientMembers) {
        this.clientMembers = clientMembers;
    }
}
