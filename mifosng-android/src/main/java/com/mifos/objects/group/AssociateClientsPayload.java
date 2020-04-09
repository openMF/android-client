package com.mifos.objects.group;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AssociateClientsPayload {

    @SerializedName("clientMembers")
    ArrayList<Integer> clientMembers;

    public ArrayList<Integer> getClientMembers() {
        return clientMembers;
    }

    public void setClientMembers(ArrayList<Integer> clientMembers) {
        this.clientMembers = clientMembers;
    }
}
