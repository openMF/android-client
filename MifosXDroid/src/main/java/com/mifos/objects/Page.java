package com.mifos.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class Page {

    private int totalFilteredRecords;
    private List<PageItem> pageItems = new ArrayList<PageItem>();


    public List<PageItem> getPageItems() {
        return pageItems;
    }
    public void setPageItems(List<PageItem> pageItems) {
        this.pageItems = pageItems;
    }


    public int getTotalFilteredRecords() {
        return totalFilteredRecords;
    }

    public void setTotalFilteredRecords(int totalFilteredRecords) {
        this.totalFilteredRecords = totalFilteredRecords;
    }


}
