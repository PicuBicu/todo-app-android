package pl.piotrb.todoapp;

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

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityAddTodoBinding;

public class AddTodoActivity extends AppCompatActivity {

    private ActivityAddTodoBinding binding;
    private Calendar date;

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
                        binding.dateTextView.setText(date.getTime().toString());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTodoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.showDateButton.setOnClickListener((v) -> {
            showDateTimePicker();
        });
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close);
        setTitle("Dodaj zadanie");
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
                saveTodo();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void saveTodo() {

        Todo todo = new Todo();
        todo.title = binding.activityAddTodoTitle.getText() + "";
        todo.description = binding.activityAddTodoDescription.getText() + "";
        Calendar currentTime = Calendar.getInstance();
        todo.creationDate = currentTime.getTime();
        todo.deadlineDate = date.getTime();
        todo.isFinished = false;
        todo.isNotificationsEnabled = binding.activityAddTodoEnableNotificationsSwitch.isChecked();
        todo.attachmentPath = "";

        Intent intent = new Intent();
        intent.putExtra(MainActivity.TODO_DATA, todo);
        setResult(RESULT_OK, intent);
        finish();

    }
}