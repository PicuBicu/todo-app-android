package pl.piotrb.todoapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityMainBinding;
import pl.piotrb.todoapp.view.TodoViewModel;

public class MainActivity extends AppCompatActivity implements TodoListAdapter.OnTaskSelected {

    public static final String CHANNEL_ID = "channel1";
    public final static String TODO_DATA = "TODO_DATA";
    private final static int ADD_TASK_REQUEST = 1;
    private final static int UPDATE_TASK_REQUEST = 2;
    private static final int NOTIFICATION_ID = 1;
    public final Settings settings = Settings.getInstance(this);
    private final TodoListAdapter todoListAdapter = new TodoListAdapter(this);
    private ActivityResultLauncher<Intent> startForPreferences;
    private TodoViewModel todoViewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setContentView(binding.getRoot());

        initNotificationChannel();

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        binding.recyclerView.setAdapter(todoListAdapter);
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        todoViewModel.getAllTodos().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                Log.i("APP", "View model data has changed");
                Log.i("APP", "Todos list " + todos.toString());
                List<Todo> collected = todos
                        .stream()
                        .filter(todo -> settings.categoryName.length() > 0 && settings.categoryName.equals(todo.category))
                        .filter(todo -> settings.hideDoneTasks && !todo.isFinished)
                        .collect(Collectors.toList());
                if (!settings.isSortingAscending) {
                    collected.sort(new Comparator<Todo>() {
                        @Override
                        public int compare(Todo o1, Todo o2) {
                            return o2.creationDate.compareTo(o1.creationDate);
                        }
                    });
                }
                todoListAdapter.setTodoList(collected);
            }
        });

        binding.activityMainAddTodoButton.setOnClickListener(v -> {
            Intent data = new Intent(MainActivity.this, AddUpdateTodoActivity.class);
            startActivityForResult(data, ADD_TASK_REQUEST);
        });
        prepareDeleteSwipe(todoListAdapter);

        settings.readSettings();

        startForPreferences = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Log.i("APP", "Prepare for saving settings");
                settings.saveSettings();
                Log.i("APP", "Settings saved");
                Log.i("APP", "Settings " + settings);
            }
        });
    }

    private void initNotificationChannel() {
        String channelName = "Todo notifications channel";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    private void scheduleNotification(Todo todo) {
        if (todo.isNotificationsEnabled) {
            Intent intent = new Intent(getApplicationContext(), Notification.class);
            intent.putExtra(MainActivity.TODO_DATA, todo);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            long timeInMillis = todo.deadlineDate.getTime();

            AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            scheduler.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == ADD_TASK_REQUEST) {
            Todo todo = (Todo) data.getSerializableExtra(TODO_DATA);
            todoViewModel.insert(todo);
            scheduleNotification(todo);
        } else if (data != null && requestCode == UPDATE_TASK_REQUEST) {
            Todo todo = (Todo) data.getSerializableExtra(TODO_DATA);
            todoViewModel.update(todo);
            scheduleNotification(todo);
        }

    }

    private void prepareDeleteSwipe(TodoListAdapter adapter) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int taskPosition = viewHolder.getAdapterPosition();
                Todo taskOnPosition = adapter.getTodoOnPosition(taskPosition);
                todoViewModel.delete(taskOnPosition);
                Toast.makeText(getApplicationContext(), "Usunięto zadanie " + taskOnPosition.title, Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(binding.recyclerView);
    }

    @Override
    public void selectTask(View view, int position) {
        Todo selectedTodoData = todoListAdapter.getTodoOnPosition(position);
        switch (view.getId()) {
            case R.id.todo_item_mark_as_done:
                CheckBox button = (CheckBox) view;
                markTodoAsDone(selectedTodoData, button.isChecked());
                break;
            case R.id.todo_item_attachment_button:
                openAttachment(selectedTodoData);
                break;
            default:
                Intent updateData = new Intent(MainActivity.this, AddUpdateTodoActivity.class);
                updateData.putExtra(TODO_DATA, selectedTodoData);
                startActivityForResult(updateData, UPDATE_TASK_REQUEST);
                break;
        }
    }

    private void markTodoAsDone(Todo task, boolean isFinished) {
        task.isFinished = isFinished;
        todoViewModel.update(task);
    }

    private void openAttachment(Todo task) {
        File file = new File(task.attachmentPath);
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        String mimeType = getContentResolver().getType(uri);
        Intent fileViewIntent = new Intent(Intent.ACTION_VIEW);
        fileViewIntent.setDataAndType(uri, mimeType);
        fileViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(fileViewIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.main_menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                todoListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_notifications_button: {
                Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
                startForPreferences.launch(preferencesIntent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}