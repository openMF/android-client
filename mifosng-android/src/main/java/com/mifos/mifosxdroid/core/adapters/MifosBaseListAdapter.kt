/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.core.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import java.util.Collections

/**
 * @author fomenkoo
 */
abstract class MifosBaseListAdapter<T>(
    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    val context: Context, private var list: MutableList<T>?, private val layoutId: Int
) : BaseAdapter() {
    private val mLock = Any()

    /**
     * Handler for button elements in listview
     */
    var buttonListener = View.OnClickListener { v ->
        val listView = v.parent as ListView
        val position = listView.getPositionForView(v)
        listView.performItemClick(
            listView.getChildAt(position), position, listView
                .getItemIdAtPosition(position)
        )
    }
    var inflater: LayoutInflater? = null
        get() {
            if (field == null) {
                field = LayoutInflater.from(context)
            }
            return field
        }
        private set

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View? {
        return layout
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    fun add(`object`: T) {
        synchronized(mLock) { list?.add(`object`) }
        notifyDataSetChanged()
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    fun addAll(collection: Collection<T>?) {
        synchronized(mLock) {
            if (collection != null) {
                list?.addAll(collection)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    fun addAll(vararg items: T) {
        synchronized(mLock) { Collections.addAll(list, *items) }
        notifyDataSetChanged()
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    fun insert(`object`: T, index: Int) {
        synchronized(mLock) { list?.add(index, `object`) }
        notifyDataSetChanged()
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    fun remove(`object`: T) {
        synchronized(mLock) { list?.remove(`object`) }
        notifyDataSetChanged()
    }

    /**
     * Remove all elements from the list.
     */
    fun clear() {
        synchronized(mLock) { list?.clear() }
        notifyDataSetChanged()
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     * in this adapter.
     */
    fun sort(comparator: Comparator<in T>?) {
        synchronized(mLock) { Collections.sort(list, comparator) }
        notifyDataSetChanged()
    }

    /**
     * {@inheritDoc}
     */
    override fun getCount(): Int {
        return list?.size ?: 0
    }

    /**
     * {@inheritDoc}
     */
    override fun getItem(position: Int): T? {
        return list?.getOrNull(position)
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    fun getPosition(item: T): Int {
        return list?.indexOf(item) ?: -1
    }

    /**
     * {@inheritDoc}
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    val resources: Resources
        get() = context.resources

    fun getList(): List<T>? {
        return list
    }

    fun setList(list: MutableList<T>?) {
        synchronized(mLock) { this.list = list }
        notifyDataSetChanged()
    }

    val layout: View?
        get() = inflater?.inflate(layoutId, null)
}