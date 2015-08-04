package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ToDoListAdapter;
import com.mifos.objects.db.ToDo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ToDoFragment extends Fragment{

    @InjectView(R.id.lv_todo_list)
    ListView lv_todo_list;

    View rootView;
    onToDoFragmentSelectedListener mCallback;

    List<ToDo> list = new ArrayList<>();
    String task;
    String date;

    public static ToDoFragment newInstance() {
        ToDoFragment toDoFragment = new ToDoFragment();
        return toDoFragment;
    }

    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_to_do, null);
        ButterKnife.inject(this, rootView);

       /* long l =2;
        ToDo toDo = ToDo.findById(ToDo.class,l);
        toDo.delete();
*/
        list = ToDo.listAll(ToDo.class);
        final ToDoListAdapter toDoListAdapter = new ToDoListAdapter(getActivity(),list);
        lv_todo_list.setAdapter(toDoListAdapter);

        lv_todo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                task = list.get(i).getTaskToBeDone();
                date = list.get(i).getDateOfTask();
                mCallback.onTaskSelected(task, date, i);
                return false;
            }


        });

        return rootView;
    }


    public interface onToDoFragmentSelectedListener{
        public void onTaskSelected(String task ,String date,int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (onToDoFragmentSelectedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " should implement onToDoFragmentSelectedListener" );
        }

    }
}

