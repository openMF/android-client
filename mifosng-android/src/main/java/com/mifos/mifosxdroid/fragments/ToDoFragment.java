package com.mifos.mifosxdroid.fragments;

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

    List<ToDo> list = new ArrayList<>();

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
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(getActivity(),list);
        lv_todo_list.setAdapter(toDoListAdapter);

        lv_todo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });

        return rootView;
    }


}

