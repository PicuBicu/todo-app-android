package pl.piotrb.todoapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityAddTodoBinding;

public class AddUpdateTodoActivity extends AppCompatActivity {

    private ActivityAddTodoBinding binding;
    private Calendar selectedDate;
    private Todo todo;
    private boolean hasDateBeenSet = false;

    public static void copy(InputStream in, File destination) throws IOException {
        try (OutputStream out = new FileOutputStream(destination)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        selectedDate = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDate.set(Calendar.MINUTE, minute);
                        Log.v("APP", "The choosen one " + selectedDate.getTime());
                        binding.activityAddUpdateTodoDeadlineDate.setText(selectedDate.getTime().toString());
                        hasDateBeenSet = true;
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void prepareUI() {
        if (todo != null) {
            binding.activityAddUpdateTodoCreationDate.setVisibility(View.VISIBLE);
            binding.activityAddUpdateTodoCreationDate.setText(todo.creationDate.toString());
            binding.activityAddUpdateTodoTitle.setText(todo.title);
            binding.activityAddUpdateTodoDescription.setText(todo.description);
            binding.activityAddUpdateEnableNotifications.setChecked(todo.isNotificationsEnabled);
            binding.activityAddUpdateTodoDeadlineDate.setText(todo.deadlineDate.toString());
            binding.activityAddUpdateTodoAttachmentPath.setText(todo.attachmentPath);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityAddTodoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close);

        Intent data = getIntent();
        if (data.hasExtra(MainActivity.TODO_DATA)) {
            setTitle("Aktualizuj zadanie");
            todo = (Todo)data.getSerializableExtra(MainActivity.TODO_DATA);
            prepareUI();
        } else {
            setTitle("Dodaj zadanie");
        }

        binding.activityAddUpdateShowDateDialog.setOnClickListener((v) -> {
            showDateTimePicker();
        });

        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    try {
                        File sourceFile = getFileName(uri);
                        File destinationFolder = new File(getApplicationContext().getFilesDir(), "attachments");
                        if (!destinationFolder.exists()) {
                            destinationFolder.mkdir();
                        }
                        File copiedFile = new File(destinationFolder, String.valueOf(sourceFile));
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        copiedFile.createNewFile();
                        copy(inputStream, copiedFile);
                        String pathName = copiedFile.toPath().toString();
                        binding.activityAddUpdateTodoAttachmentPath.setText(pathName);
                    } catch (IOException e) {
                        Toast.makeText(this, "Wystąpił problem w trakcie zapisu załącznika", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.activityAddUpdateShowAttachmentDialog.setOnClickListener(v -> {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
            startForResult.launch(intent);
        });


    }

    private File getFileName(Uri uri) {
        Cursor returnCursor = getContentResolver()
                .query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        try {
            returnCursor.moveToFirst();
            return new File(returnCursor.getString(nameIndex));
        } finally {
            returnCursor.close();
        }
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
                saveTodo();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void saveTodo() {
        if (todo != null) {
            todo = new Todo();
            Calendar currentTime = Calendar.getInstance();
            todo.creationDate = currentTime.getTime();
            todo.isFinished = false;
        }

        todo.isNotificationsEnabled = binding.activityAddUpdateEnableNotifications.isChecked();
        todo.attachmentPath = binding.activityAddUpdateTodoAttachmentPath.getText().toString();
        todo.title = binding.activityAddUpdateTodoTitle.getText() + "";
        todo.description = binding.activityAddUpdateTodoDescription.getText() + "";
        todo.deadlineDate = hasDateBeenSet ? selectedDate.getTime() : todo.deadlineDate;

        Intent intent = new Intent();
        intent.putExtra(MainActivity.TODO_DATA, todo);
        setResult(RESULT_OK, intent);
        finish();
    }
}