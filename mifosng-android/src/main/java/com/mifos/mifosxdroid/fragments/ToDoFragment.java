package com.mifos.mifosxdroid.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    public static String TAG="ToDoFragment";
    public static ToDoListAdapter toDoListAdapter;

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

        list = ToDo.listAll(ToDo.class);
        toDoListAdapter = new ToDoListAdapter(getActivity(),list);
        lv_todo_list.setAdapter(toDoListAdapter);

        lv_todo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                
                list=ToDo.listAll(ToDo.class);
                ToDoFragment.toDoListAdapter.setMyList(list);

                ToDo toDo = list.get(i);

                Log.d(" date : " +  toDo.getDateOfTask(),"task : " + toDo.getTaskToBeDone());
                task = toDo.getTaskToBeDone();
                date = toDo.getDateOfTask();

                mCallback.onTaskSelected(task, date, toDo.getId());
                Log.d("ToDo table position ", "" + toDo.getId());

                return false;
            }

        });

        return rootView;
    }


    public interface onToDoFragmentSelectedListener{
        public void onTaskSelected(String task ,String date,Long position);
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

