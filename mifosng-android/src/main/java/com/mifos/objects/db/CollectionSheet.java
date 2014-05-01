package com.mifos.objects.db;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class CollectionSheet extends SugarRecord<CollectionSheet> {

    public int[] dueDate;

    @Ignore
    public List<MifosGroup> groups;

//	     public LoanProduct loanProducts[];
//	     public AttendanceTypeOptions attendanceTypeOptions[];

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
