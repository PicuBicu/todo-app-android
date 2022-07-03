package pl.piotrb.todoapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings extends ContextWrapper {

    private static Settings INSTANCE;
    private final static String PREF_KEY_CATEGORY = "CATEGORY";
    private final static String PREF_KEY_TIME = "TIME";
    private final static String PREF_KEY_SORTING_ORDER = "SORTING_ORDER";
    private final static String PREF_KEY_SET_OF_CATEGORIES = "SET_OF_CATEGORIES";
    private final static String PREF_KEY_DISPLAYING_DONE_TASKS = "DISPLAYING_DONE_TASKS";

    public boolean isSortingDescending = true;
    public String categoryName = "";
    public List<String> categories;
    public List<Integer> times;
    public String selectedTimeInMinutes = "";
    public boolean hideDoneTasks = false;

    public Settings(Context base) {
        super(base);
        categories = new ArrayList<>();
        times = new ArrayList<>();
        categories.add("");
        times.add(5);
        times.add(10);
        times.add(15);
    }

    public static Settings getInstance(Context base) {
        if (INSTANCE == null) {
            INSTANCE = new Settings(base);
        }
        return INSTANCE;
    }

    public void saveSettings() {
        Log.i("APP", "Saving settings");
        SharedPreferences sharedPreferences = getSharedPreferences("GLOBAL_PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_CATEGORY, categoryName);
        editor.putString(PREF_KEY_TIME, selectedTimeInMinutes);
        editor.putBoolean(PREF_KEY_SORTING_ORDER, isSortingDescending);
        editor.putBoolean(PREF_KEY_DISPLAYING_DONE_TASKS, hideDoneTasks);
        editor.putStringSet(PREF_KEY_SET_OF_CATEGORIES, new HashSet<>(categories));
        editor.apply();
    }

    public void readSettings() {
        Log.i("APP", "Loading settings");
        SharedPreferences sharedPreferences = getSharedPreferences("GLOBAL_PREFERENCES", MODE_PRIVATE);
        categoryName = sharedPreferences.getString(PREF_KEY_CATEGORY, "");
        selectedTimeInMinutes = sharedPreferences.getString(PREF_KEY_TIME, "");
        isSortingDescending = sharedPreferences.getBoolean(PREF_KEY_SORTING_ORDER, false);
        Set<String> setOfCategories = new HashSet<>();
        setOfCategories = sharedPreferences.getStringSet(PREF_KEY_SET_OF_CATEGORIES, setOfCategories);
        categories = new ArrayList<>(setOfCategories);
        hideDoneTasks = sharedPreferences.getBoolean(PREF_KEY_DISPLAYING_DONE_TASKS, false);
    }

}
