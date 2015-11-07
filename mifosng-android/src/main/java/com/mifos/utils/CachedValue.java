package com.mifos.utils;

import android.content.SharedPreferences;

/**
 * @author fomenkoo
 */
public class CachedValue<T> {
    private static final Object lock = new Object();

    private static SharedPreferences sharedPref;

    private SharedPreferences sp;

    private T value;
    private T defValue;
    private Class type;
    private String name;
    private boolean loaded = false;

    public CachedValue(String name, Class type) {
        this(name, null, null, type);
    }

    public CachedValue(String name, T defValue, Class type) {
        this(name, null, defValue, type);
    }

    public CachedValue(String name, T value, T defValue, Class type) {
        this.sp = sharedPref;
        this.name = name;
        this.type = type;
        this.loaded = value != null;
        this.value = value;
        this.defValue = defValue;
    }

    public void setValue(T value) {
        synchronized (lock) {
            loaded = true;
            write(this.value = value);
        }
    }

    public T getValue() {
        synchronized (lock) {
            if (!loaded) {
                this.value = load();
                loaded = true;
            }
            return this.value;
        }
    }

    public String getName() {
        return name;
    }

    private void write(T value) {
        SharedPreferences.Editor editor = sp.edit();

        if (value instanceof String) {

            editor.putString(name, (String) value);

        } else if (value instanceof Integer) {

            editor.putInt(name, (Integer) value);

        } else if (value instanceof Float) {

            editor.putFloat(name, (Float) value);

        } else if (value instanceof Long) {

            editor.putLong(name, (Long) value);

        } else if (value instanceof Boolean) {

            editor.putBoolean(name, (Boolean) value);

        }

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    private T load() {

        if (type == String.class) {

            return (T) sp.getString(name, (String) defValue);

        } else if (type == Integer.class) {

            return (T) Integer.valueOf(sp.getInt(name, (Integer) defValue));

        } else if (type == Float.class) {

            return (T) Float.valueOf(sp.getFloat(name, (Float) defValue));

        } else if (type == Long.class) {

            return (T) Long.valueOf(sp.getLong(name, (Long) defValue));

        } else if (type == Boolean.class) {

            return (T) Boolean.valueOf(sp.getBoolean(name, (Boolean) defValue));

        }

        return null;
    }

    public void delete() {
        synchronized (lock) {
            sp.edit().remove(name).commit();
            clear();
        }
    }

    public static void initialize(SharedPreferences sp) {
        CachedValue.sharedPref = sp;
    }

    public void setSharedPreferences(SharedPreferences sp) {
        this.sp = sp;
    }

    public void clear() {
        synchronized (lock) {
            loaded = false;
            this.value = null;
        }
    }

}
