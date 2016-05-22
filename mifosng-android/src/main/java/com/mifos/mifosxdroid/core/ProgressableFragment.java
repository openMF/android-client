package com.mifos.mifosxdroid.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.mifos.mifosxdroid.R;

/**
 * A {@link MifosBaseFragment} that provides progress viewing functionality with a ViewFlipper.
 */
public class ProgressableFragment extends MifosBaseFragment {
    public static final int VIEW_FLIPPER_PROGRESS = 0;
    public static final int VIEW_FLIPPER_CONTENT = 1;

    private ViewFlipper viewFlipper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFlipper = (ViewFlipper) view.findViewById(R.id.view_flipper);

        if (viewFlipper == null) {
            throw new NullPointerException("Are you sure your Fragment has a ViewFlipper with id " +
                    "\"view_flipper\"?");
        }
    }

    /**
     * Switches the {@link ViewFlipper} either to the {@link ProgressBar} or to the content.
     *
     * @param show true to show {@link ProgressBar}, false for content.
     */
    public void showProgress(boolean show) {
        try {
            int childToFlipTo = show ? VIEW_FLIPPER_PROGRESS : VIEW_FLIPPER_CONTENT;
            if (viewFlipper.getDisplayedChild() != childToFlipTo) {
                viewFlipper.setDisplayedChild(childToFlipTo);
            }
        } catch (NullPointerException e) {
            Log.w(getClass().getSimpleName(), "Couldn't show/hide progress bar. Are you sure your" +
                    " Fragment contains a ViewFlipper with ID \"view_flipper\"?");
        }
    }
}
