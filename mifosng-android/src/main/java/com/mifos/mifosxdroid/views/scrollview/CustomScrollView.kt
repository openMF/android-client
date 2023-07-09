package com.mifos.mifosxdroid.views.scrollview

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class CustomScrollView : ScrollView {
    private var scrollChangeListener: ScrollChangeListener? = null
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (scrollChangeListener != null) {
            scrollChangeListener!!.onScrollChanged(l, t, oldl, oldt)
        }
    }

    fun setScrollChangeListener(scrollChangeListener: ScrollChangeListener?) {
        this.scrollChangeListener = scrollChangeListener
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }
}