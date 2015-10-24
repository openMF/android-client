package com.mifos.mifosxdroid;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mifos.mifosxdroid.dialogfragments.ToDoDialogFragment;
import com.mifos.mifosxdroid.fragments.ToDoFragment;


public class ToDoActivity extends ActionBarActivity implements ToDoFragment.onToDoFragmentSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_task);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ToDoFragment toDoFragment = ToDoFragment.newInstance();
        fragmentTransaction.replace(R.id.to_do_container,toDoFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_do_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.mItem_add_task :
                Toast.makeText(this,"Add TASK",Toast.LENGTH_LONG).show();
                showDialogFragment();

        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialogFragment(){

        DialogFragment toDoDialogFragment = ToDoDialogFragment.newInstance();
        toDoDialogFragment.show(this.getSupportFragmentManager(),"ToDoDiaologFragment");

    }

    @Override
    public void onTaskSelected(String task, String date,Long position) {
        DialogFragment dialogFragment = ToDoDialogFragment.newInstance(task,date,position);
        dialogFragment.show(this.getSupportFragmentManager(),"ToDoDiaologFragment");


    }

}
