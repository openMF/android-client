package com.mifos.mifosxdroid.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.fragments.ToDoFragment;
import com.mifos.objects.db.ToDo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ToDoDialogFragment extends DialogFragment {
public static final String TAG ="ToDoDialogFragment";

    @InjectView(R.id.et_enter_task)
    EditText et_enter_task;
    @InjectView(R.id.et_enter_date)
    EditText et_enter_date;
    @InjectView(R.id.bt_right)
    Button bt_right;
    @InjectView(R.id.bt_left)
    Button bt_left;

    View rootView;
    String task;
    String et_task;
    String et_date;
    String date_of_task;

    List<ToDo> list = new ArrayList<>();
    Long position;
    int flag =1;


    public static ToDoDialogFragment newInstance(){

        ToDoDialogFragment toDoDialogFragment = new ToDoDialogFragment();
        return toDoDialogFragment;
    }

    public static ToDoDialogFragment newInstance(@Nullable String task, @Nullable String date,Long position){

        ToDoDialogFragment toDoDialogFragment = new ToDoDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("task", task);
        bundle.putString("date", date);
        bundle.putLong("position", position);

        toDoDialogFragment.setArguments(bundle);

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

        if (getArguments() != null) {
            et_task= getArguments().getString("task");
            et_date=getArguments().getString("date");
            position=getArguments().getLong("position");

            flag =0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.dialogue_fragment_to_do, container, false);
        getDialog().setTitle("Add task");
        ButterKnife.inject(this, rootView);

        if(flag==0){
        setEditTextValue(et_task,et_date,position);
        }


        bt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(bt_right.getText()!=null){
                if(bt_right.getText().toString().equals("SAVE")){
                    saveDataToTable();

                    ToDoDialogFragment.this.dismiss();
                }
                else{
                    deleteListItem(position);
                }
             }
                else {
                    Toast.makeText(getActivity()," null object reference ",Toast.LENGTH_LONG).show();
                }
            }

        });

        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt_left.getText()!=null){
                    if(bt_left.getText().toString().equals("CANCEL")){

                        ToDoDialogFragment.this.dismiss();
                    }
                    else{
                       updateListItem(position);
                    }
                }
                else {
                    Toast.makeText(getActivity()," null object reference ",Toast.LENGTH_LONG).show();
                }
            }

        });


        return rootView;

    }

    public void setEditTextValue(String task,String date,Long position){
        bt_right.setText("DELETE");
        bt_left.setText("UPDATE");
        et_enter_task.setText(task);
        et_enter_date.setText(date);

    }

    public void updateListItem(Long position){
        task=et_enter_task.getText().toString();
        date_of_task=et_enter_date.getText().toString();
        ToDo toDo = ToDo.findById(ToDo.class, position);

        if(toDo!=null){
            toDo.setDateOfTask(date_of_task);
            toDo.setTaskToBeDone(task);
            toDo.save();
            Toast.makeText(getActivity(),"Updated Successfully",Toast.LENGTH_SHORT).show();
            notifyDataChanged();
        }
        else {
            Toast.makeText(getActivity(), " null object", Toast.LENGTH_LONG).show();
        }

    }
    public void deleteListItem(Long position){

        ToDo toDo = ToDo.findById(ToDo.class,position);

        if(toDo!=null)
        {
        toDo.delete();

        Toast.makeText(getActivity(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
        notifyDataChanged();
        }
        else {
            Toast.makeText(getActivity(),"ToDo Null",Toast.LENGTH_SHORT).show();
        }

    }

    public void saveDataToTable(){

        task = et_enter_task.getEditableText().toString();
        date_of_task=et_enter_date.getEditableText().toString();
        ToDo toDo = new ToDo(task,date_of_task);
        toDo.save();
        Toast.makeText(getActivity(),"Saved Successfully",Toast.LENGTH_SHORT).show();
        notifyDataChanged();

    }

    public void notifyDataChanged(){
        ToDoDialogFragment.this.dismiss();
        list=ToDo.listAll(ToDo.class);
        ToDoFragment.toDoListAdapter.setMyList(list);
    }

  }
