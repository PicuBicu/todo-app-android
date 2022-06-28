package pl.piotrb.todoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityMainBinding;
import pl.piotrb.todoapp.view.TodoViewModel;

public class MainActivity extends AppCompatActivity {

    public final static String TODO_DATA = "TODO_DATA";
    private final static int ADD_TASK_REQUEST = 1;
    private final static int UPDATE_TASK_REQUEST = 2;
    private TodoViewModel todoViewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View view = binding.getRoot();
        setContentView(view);

        final TodoListAdapter todoListAdapter = new TodoListAdapter();
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
            openAddNewActivity(v);
        });
    }

    public void openAddNewActivity(View view) {
        Intent data = new Intent(MainActivity.this, AddTodoActivity.class);
        startActivityForResult(data, ADD_TASK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Todo todo = (Todo)data.getSerializableExtra(TODO_DATA);
        switch (requestCode) {
            case ADD_TASK_REQUEST:
                saveTodo(todo);
                break;
            case UPDATE_TASK_REQUEST:
                break;
        }
    }

    private void saveTodo(Todo todo) {
        Log.i("APP", "Todo data " + todo.toString());
        todoViewModel.insert(todo);
    }
}