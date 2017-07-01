package com.mifos.utils;

import android.os.Handler;

/**
 * Created by mayankjindal on 18/07/17.
 */

public class ForegroundChecker {
    public static final long CHECK_DELAY = 500;
    public static final int MIN_BACKGROUND_THRESHOLD = 60;
    public static final String TAG = ForegroundChecker.class.getName();

    public interface Listener {
        public void onBecameForeground();
    }

    private static ForegroundChecker instance;

    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private Listener listener;
    private Runnable check;
    private long backgroundTimeStart;

    public static ForegroundChecker init() {
        if (instance == null) {
            instance = new ForegroundChecker();
        }
        return instance;
    }

    public static ForegroundChecker get() {
        return instance;
    }

    private ForegroundChecker() {
        backgroundTimeStart = -1;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(Listener listener) {
        this.listener = listener;
    }

    public void onActivityResumed() {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {

            int secondsInBackground = (int) ((System.currentTimeMillis() - backgroundTimeStart) /
                    1000);

            if (backgroundTimeStart != -1 && secondsInBackground >= MIN_BACKGROUND_THRESHOLD &&
                    listener != null && PrefManager.getPassCodeStatus()) {
                listener.onBecameForeground();
            }

        }
    }

    public void onActivityPaused() {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    backgroundTimeStart = System.currentTimeMillis();

                }
            }
        }, CHECK_DELAY);
    }
}
