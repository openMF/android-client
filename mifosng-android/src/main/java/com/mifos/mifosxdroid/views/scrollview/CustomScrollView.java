package com.mifos.mifosxdroid.views.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    private ScrollChangeListener scrollChangeListener;


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollChangeListener != null) {
            scrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        this.scrollChangeListener = scrollChangeListener;
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
