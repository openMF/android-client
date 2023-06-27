package com.mifos.mifosxdroid.dialogfragments.searchdialog

import android.R
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import java.util.Locale

class SearchDialog(
    context: Context, private val mainList: ArrayList<String>,
    private val clickListener: AdapterView.OnItemClickListener
) : Dialog(context) {
    private lateinit var filterList: ArrayList<String>
    private var editText: EditText? = null
    private var listView: ListView? = null
    private var adapter: ArrayAdapter<String>? = null

    init {
        setUp()
    }

    private fun setUp() {
        filterList = ArrayList()
        filterList.addAll(mainList)
        setContentView(0)
        adapter = ArrayAdapter(
            context, R.layout.simple_list_item_1,
            R.id.text1, filterList
        )
        listView = findViewById(com.mifos.mifosxdroid.R.id.lv_items)
        editText = findViewById(com.mifos.mifosxdroid.R.id.et_drop_down_search)
        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                filterList.clear()
                val text = editable.toString().lowercase(Locale.getDefault())
                for (s in mainList) {
                    if (s.lowercase(Locale.getDefault()).contains(text)) {
                        filterList.add(s)
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
        listView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            for (pos in mainList.indices) {
                if (mainList[pos] == filterList[i]) {
                    clickListener.onItemClick(adapterView, view, pos, l)
                }
            }
            dismiss()
        }
        listView?.adapter = adapter
    }

    override fun setContentView(layoutResID: Int) {
        var layoutResID = layoutResID
        layoutResID = com.mifos.mifosxdroid.R.layout.search_list_dialog_layout
        super.setContentView(layoutResID)
    }

    @Deprecated("")
    override fun setContentView(view: View) {
        super.setContentView(view)
    }

    @Deprecated("")
    override fun addContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.addContentView(view, params)
    }
}