package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.ToDo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ADMIN on 08-Jul-15.
 */
public class ToDoListAdapter extends BaseAdapter {

    private List<ToDo> toDoList = new ArrayList<>();
    private LayoutInflater layoutInflater;


    public ToDoListAdapter(){

    }

    public ToDoListAdapter(Context context,List<ToDo> toDoList){

        layoutInflater=LayoutInflater.from(context);
        this.toDoList=toDoList;

    }

    @Override
    public int getCount() {

        return this.toDoList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.toDoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view==null){
            view =layoutInflater.inflate(R.layout.row_todo_list_item,viewGroup,false);
            viewHolder= new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_task.setText(toDoList.get(i).getTaskToBeDone());
        viewHolder.tv_task_date.setText(toDoList.get(i).getDateOfTask());

        return view;
    }

    public static class ViewHolder{
        @InjectView(R.id.tv_task)
        TextView tv_task;
        @InjectView(R.id.tv_task_date)
        TextView tv_task_date;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }


    }
}
