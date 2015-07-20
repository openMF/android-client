package com.mifos.mifosxdroid.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ToDoListAdapter;
import com.mifos.objects.db.ToDo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ToDoDialogFragment extends DialogFragment {
public static final String TAG ="ToDoDialogFragment";

    //onToDoDialogSelectedListner mCallback;

    @InjectView(R.id.et_enter_task)
    EditText et_enter_task;
    @InjectView(R.id.et_enter_date)
    EditText et_enter_date;
    @InjectView(R.id.bt_save_task)
    Button bt_save_task;


    View rootView;
    String task;
    String date_of_task;
    List<ToDo> listToDo = new ArrayList<>();
    ToDoListAdapter toDoListAdapter = new ToDoListAdapter();
    int position;


    public static ToDoDialogFragment newInstance(){

        ToDoDialogFragment toDoDialogFragment = new ToDoDialogFragment();
        return toDoDialogFragment;
    }

    public ToDoDialogFragment(){
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.dialogue_fragment_to_do, container, false);
        getDialog().setTitle("Add task");
        ButterKnife.inject(this, rootView);


        bt_save_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataToTable();
                ToDoDialogFragment.this.dismiss();

            }
        });


        return rootView;

    }

    public List<ToDo> saveDataToTable(){
        task = et_enter_task.getEditableText().toString();
        date_of_task=et_enter_date.getEditableText().toString();
        ToDo toDo = new ToDo(task,date_of_task);
        toDo.save();
        toDoListAdapter.notifyDataSetChanged();
        return listToDo;
    }


  }
