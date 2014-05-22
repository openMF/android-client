package com.mifos.services.data;


import com.google.gson.Gson;

public class Changes
{
    public String locale;
    public String dateFormat;
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
