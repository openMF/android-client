package com.mifos.mifosxdroid.dialogfragments.searchdialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mifos.mifosxdroid.R;

import java.util.ArrayList;
import java.util.List;

public class SearchDialog extends Dialog {

    private List<String> mainList;
    private List<String> filterList;
    private AdapterView.OnItemClickListener clickListener;

    private EditText editText;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    public SearchDialog(@NonNull Context context, List<String> items,
                        AdapterView.OnItemClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
        this.mainList = items;
        setUp();
    }

    private void setUp() {
        filterList = new ArrayList<>();
        filterList.addAll(mainList);
        setContentView(0);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, filterList);
        listView = this.findViewById(R.id.lv_items);
        editText = this.findViewById(R.id.et_drop_down_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                filterList.clear();
                String text = editable.toString().toLowerCase();
                for (String s: mainList) {
                    if (s.toLowerCase().contains(text)) {
                        filterList.add(s);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int pos = 0; pos < mainList.size(); pos++) {
                    if (mainList.get(pos).equals(filterList.get(i))) {
                        clickListener.onItemClick(adapterView, view, pos, l);
                    }
                }
                dismiss();
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void setContentView(int layoutResID) {
        layoutResID = R.layout.search_list_dialog_layout;
        super.setContentView(layoutResID);
    }

    @Override
    @Deprecated
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
    }

    @Override
    @Deprecated
    public void addContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }
}
