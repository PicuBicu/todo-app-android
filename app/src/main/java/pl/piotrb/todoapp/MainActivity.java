package pl.piotrb.todoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityMainBinding;
import pl.piotrb.todoapp.view.TodoViewModel;

public class MainActivity extends AppCompatActivity implements TodoListAdapter.OnTaskSelected {

    public final static String TODO_DATA = "TODO_DATA";
    private final static int ADD_TASK_REQUEST = 1;
    private final static int UPDATE_TASK_REQUEST = 2;
    private final TodoListAdapter todoListAdapter = new TodoListAdapter(this);
    private TodoViewModel todoViewModel;
    private ActivityMainBinding binding;

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Aby aplikacja działała poprawnie, wymagane jest ustawienie uprawnień w ustawieniach systemu", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }

    private boolean checkPermissions() {
        int writePermissionResult = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermissionResult = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return (writePermissionResult == PackageManager.PERMISSION_GRANTED && readPermissionResult == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View view = binding.getRoot();
        setContentView(view);

        if (checkPermissions()) {

            binding.recyclerView.setAdapter(todoListAdapter);
            todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
            todoViewModel.getAllTodos().observe(this, new Observer<List<Todo>>() {
                @Override
                public void onChanged(List<Todo> todos) {
                    Log.i("APP", "View model data has changed");
                    Log.i("APP", "Todos list " + todos.toString());
                    todoListAdapter.setTodoList(todos);
                }
            });
            binding.activityMainAddTodoButton.setOnClickListener(v -> {
                Intent data = new Intent(MainActivity.this, AddUpdateTodoActivity.class);
                startActivityForResult(data, ADD_TASK_REQUEST);
            });
            prepareDeleteSwipe(todoListAdapter);

        } else {
            requestPermission();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Todo todo = (Todo) data.getSerializableExtra(TODO_DATA);
        Log.i("APP", "Todo data " + todo.toString());
        switch (requestCode) {
            case ADD_TASK_REQUEST:
                saveTodo(todo);
                break;
            case UPDATE_TASK_REQUEST:
                updateTodo(todo);
                break;
        }
    }

    private void updateTodo(Todo todo) {
        todoViewModel.update(todo);
    }

    private void saveTodo(Todo todo) {
        todoViewModel.insert(todo);
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
    public void selectTask(int position) {
        Intent updateData = new Intent(MainActivity.this, AddUpdateTodoActivity.class);
        updateData.putExtra(TODO_DATA, todoListAdapter.getTodoOnPosition(position));
        startActivityForResult(updateData, UPDATE_TASK_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_notifications_button:
                return true;
            case R.id.main_menu_category_button:
                return true;
            case R.id.main_menu_tasks_button:
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}