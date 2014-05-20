package com.mifos.objects.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class Page {

    private int totalFilteredRecords;
    private List<Client> pageItems = new ArrayList<Client>();


    public List<Client> getPageItems() {
        return pageItems;
    }
    public void setPageItems(List<Client> pageItems) {
        this.pageItems = pageItems;
    }


    public int getTotalFilteredRecords() {
        return totalFilteredRecords;
    }

    public void setTotalFilteredRecords(int totalFilteredRecords) {
        this.totalFilteredRecords = totalFilteredRecords;
    }


}
