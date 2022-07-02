package pl.piotrb.todoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import pl.piotrb.todoapp.databinding.ActivityPreferencesBinding;

public class PreferencesActivity extends AppCompatActivity {

    private static final String ACTIVITY_TITLE = "Ustawienia";
    private final Settings settings = Settings.getInstance(this);
    private List<String> categories = new ArrayList<>();
    private List<Integer> times = new ArrayList<>();
    private ActivityPreferencesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadFromSettings();
        initCategoriesSpinner(categories);
        initTimesSpinner(times);
        setTitle(ACTIVITY_TITLE);
        binding.preferencesSubmitCategory.setOnClickListener(view -> {
            String category = binding.preferencesAddCategory.getText().toString();
            if (!category.isEmpty()) {
                categories.add(category);
                binding.preferencesAddCategory.setText("");
            }
        });
    }

    private void initCategoriesSpinner(List<String> categories) {
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        binding.preferencesSelectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.preferencesSelectedCategory.setText(categories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                binding.preferencesSelectedCategory.setText("");
            }
        });
        binding.preferencesSelectCategory.setAdapter(adapter);
    }

    public void initTimesSpinner(List<Integer> times) {
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        binding.preferencesSelectTimeForNotifications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.preferencesSelectedTime.setText(times.get(i) + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                binding.preferencesSelectedTime.setText("");
            }
        });
        binding.preferencesSelectTimeForNotifications.setAdapter(adapter);
    }

    public void saveToSettings() {
        settings.categoryName = binding.preferencesSelectedCategory.getText().toString();
        settings.selectedTimeInMinutes = binding.preferencesSelectedTime.getText().toString();
        settings.isSortingAscending = binding.preferencesIsSortingDescending.isChecked();
        settings.hideDoneTasks = binding.preferencesHideDoneTasks.isChecked();
        settings.categories = categories;
        setResult(RESULT_OK, null);
        finish();
    }

    public void loadFromSettings() {
        binding.preferencesSelectedCategory.setText(settings.categoryName);
        binding.preferencesSelectedTime.setText(settings.selectedTimeInMinutes);
        binding.preferencesIsSortingDescending.setChecked(settings.isSortingAscending);
        binding.preferencesHideDoneTasks.setChecked(settings.hideDoneTasks);
        categories = settings.categories;
        times = settings.times;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_update_todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.add_todo_menu_save_todo_button:
                saveToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}