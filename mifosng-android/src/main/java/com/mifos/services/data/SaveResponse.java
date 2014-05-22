package com.mifos.services.data;


import com.google.gson.Gson;

public class SaveResponse
{
    public int groupId;
    public int resourceId;
    public Changes changes;
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
