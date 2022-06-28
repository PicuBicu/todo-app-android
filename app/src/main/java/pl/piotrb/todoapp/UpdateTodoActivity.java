package pl.piotrb.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityUpdateTodoBinding;

public class UpdateTodoActivity extends AppCompatActivity {

    private ActivityUpdateTodoBinding binding;
    private Todo todo;
    private Calendar date;
    private boolean hasDateBeenSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTodoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close);
        Intent updateData = getIntent();
        if (updateData.hasExtra(MainActivity.TODO_DATA)) {
            todo = (Todo)updateData.getSerializableExtra(MainActivity.TODO_DATA);
            setTitle("Aktualizuj zadanie");
        }
        binding.activityUpdateShowDate.setOnClickListener(v -> showDateTimePicker());
        prepareUI();
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v("APP", "The choosen one " + date.getTime());
                        binding.activityUpdateDeadlineDate.setText(date.getTime().toString());
                        hasDateBeenSet = true;
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.add_todo_menu_save_todo_button:
                updateTodo();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void updateTodo() {
        todo.title = binding.activityUpdateTodoTitle.getText() + "";
        todo.description = binding.activityUpdateTodoDescription.getText() + "";
        Calendar currentTime = Calendar.getInstance();
        todo.creationDate = currentTime.getTime();
        todo.deadlineDate = hasDateBeenSet ? date.getTime() : todo.deadlineDate;
        todo.isNotificationsEnabled = binding.activityUpdateTodoEnableNotificationsSwitch.isChecked();
        todo.attachmentPath = "";
        Intent intent = new Intent();
        intent.putExtra(MainActivity.TODO_DATA, todo);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void prepareUI() {
        binding.activityUpdateCreationDate.setText(todo.creationDate.toString());
        binding.activityUpdateTodoTitle.setText(todo.title);
        binding.activityUpdateTodoDescription.setText(todo.description);
        binding.activityUpdateTodoEnableNotificationsSwitch.setChecked(todo.isNotificationsEnabled);
        binding.activityUpdateDeadlineDate.setText(todo.deadlineDate.toString());
    }
}