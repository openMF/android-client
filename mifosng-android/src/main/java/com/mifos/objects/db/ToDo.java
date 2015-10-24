package com.mifos.objects.db;

import android.util.Log;
import com.orm.SugarRecord;

public class ToDo extends SugarRecord<ToDo> {

    private String taskToBeDone;
    private String dateOfTask;

    public ToDo(){

        Log.d("ToDO class" ,"entered");
    }

    public ToDo(String taskToBeDone,String dateOfTask){

        this.taskToBeDone = taskToBeDone;
        this.dateOfTask = dateOfTask;
    }

    public String getDateOfTask() {
        return dateOfTask;
    }

    public void setDateOfTask(String dateOfTask) {
        this.dateOfTask = dateOfTask;
    }

    public String getTaskToBeDone() {
        return taskToBeDone;
    }

    public void setTaskToBeDone(String taskToBeDone) {
        this.taskToBeDone = taskToBeDone;
    }
}
