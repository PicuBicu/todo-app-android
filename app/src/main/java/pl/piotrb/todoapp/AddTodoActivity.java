package pl.piotrb.todoapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
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
                        binding.activityAddDateTextView.setText(date.getTime().toString());
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
        binding.activityAddShowDateButton.setOnClickListener((v) -> {
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
                        binding.activityAddFileTextView.setText(pathName);
                    } catch (IOException e) {
                        Toast.makeText(this, "Wystąpił problem w trakcie zapisu załącznika", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }
        });

        binding.activityAddAttachmentButton.setOnClickListener(v -> {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
            startForResult.launch(intent);
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close);
        setTitle("Dodaj zadanie");
    }

    public static void copy(InputStream in, File destination) throws IOException {
        try (OutputStream out = new FileOutputStream(destination)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
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
        todo.attachmentPath = binding.activityAddFileTextView.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(MainActivity.TODO_DATA, todo);
        setResult(RESULT_OK, intent);
        finish();

    }
}