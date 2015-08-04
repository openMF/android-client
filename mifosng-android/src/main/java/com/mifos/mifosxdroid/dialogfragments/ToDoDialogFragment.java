package com.mifos.mifosxdroid.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ToDoListAdapter;
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
    @InjectView(R.id.bt_save_task)
    Button bt_save_task;

    View rootView;
    String task;
    String et_task;
    String et_date;
    String date_of_task;
    //String bt_text="SAVE TASK";
    List<ToDo> list_object = new ArrayList<>();
    ToDoListAdapter toDoListAdapter = new ToDoListAdapter();
    int position;
    int flag =1;


    public static ToDoDialogFragment newInstance(){

        ToDoDialogFragment toDoDialogFragment = new ToDoDialogFragment();

        return toDoDialogFragment;
    }

    public static ToDoDialogFragment newInstance(@Nullable String task, @Nullable String date,int position){

        ToDoDialogFragment toDoDialogFragment = new ToDoDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("task", task);
        bundle.putString("date", date);
        bundle.putInt("position",position);

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
            position=getArguments().getInt("position");
            Log.d(" task  " + task, "Date " + date_of_task);
            list_object=ToDo.listAll(ToDo.class);
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

        bt_save_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(bt_save_task.getText()!=null){
                if(bt_save_task.getText().toString().equals("SAVE TASK")){
                    saveDataToTable();
                    toDoListAdapter.notifyDataSetChanged();
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


        return rootView;

    }

    public void setEditTextValue(String task,String date,int position){
        bt_save_task.setText("DELETE TASK");
        et_enter_task.setText(task);
        et_enter_date.setText(date);
        task=et_enter_task.getEditableText().toString();
        date=et_enter_date.getEditableText().toString();
        Log.d("Position variable", String.valueOf(position + 1));
        //deleteListItem(position);

        /*if(toDo!=null){
            toDo.setDateOfTask(date);
            toDo.setTaskToBeDone(task);
            toDo.save();
            toDoListAdapter.notifyDataSetChanged();
             }
        else {
            Toast.makeText(getActivity(), " null object", Toast.LENGTH_LONG).show();
        }*/



    }
    public void deleteListItem(int position){
         //
        Integer a = Integer.valueOf(position+1);
        Toast.makeText(getActivity(),"Entered deleteListItem method",Toast.LENGTH_SHORT).show();
        ToDo toDo = ToDo.findById(ToDo.class,a.longValue());
        Toast.makeText(getActivity(),String.valueOf(position+1),Toast.LENGTH_SHORT).show();

        List<ToDo> todo1 = ToDo.listAll(ToDo.class);
        for(int i=0;i<todo1.size();i++){

            Log.d(" Task " + i +": " + todo1.get(i).getTaskToBeDone(),"position of task" + todo1.get(i).getId());
        }


        if(toDo!=null){

        Toast.makeText(getActivity(),toDo.getTaskToBeDone(),Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(),"Entered If",Toast.LENGTH_SHORT).show();
        toDo.delete();

            List<ToDo> todo2 = ToDo.listAll(ToDo.class);
            for(int i=0;i<todo2.size();i++){

                Log.d(" inside IF Task " + i +": " + todo2.get(i).getTaskToBeDone(),"position of task" + todo2.get(i).getId());
            }

            ToDoDialogFragment.this.dismiss();}
        else {
            Toast.makeText(getActivity(),"ToDo Null",Toast.LENGTH_SHORT).show();
        }

    }

    public void saveDataToTable(){
        task = et_enter_task.getEditableText().toString();
        date_of_task=et_enter_date.getEditableText().toString();
        ToDo toDo = new ToDo(task,date_of_task);
        toDo.save();

    }


  }
