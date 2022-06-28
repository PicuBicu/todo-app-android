package pl.piotrb.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.databinding.ActivityMainBinding;
import pl.piotrb.todoapp.view.TodoViewModel;

public class MainActivity extends AppCompatActivity implements TodoListAdapter.OnTaskSelected {

    public final static String TODO_DATA = "TODO_DATA";
    private final static int ADD_TASK_REQUEST = 1;
    private final static int UPDATE_TASK_REQUEST = 2;

    private TodoViewModel todoViewModel;
    private final TodoListAdapter todoListAdapter = new TodoListAdapter(this);
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View view = binding.getRoot();
        setContentView(view);

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
            Intent data = new Intent(MainActivity.this, AddTodoActivity.class);
            startActivityForResult(data, ADD_TASK_REQUEST);
        });
        prepareDeleteSwipe(todoListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Todo todo = (Todo)data.getSerializableExtra(TODO_DATA);
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
                Toast.makeText(getApplicationContext(),"Usunięto zadanie " + taskOnPosition.title, Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(binding.recyclerView);
    }

    @Override
    public void selectTask(int position) {
        Intent updateData = new Intent(MainActivity.this, UpdateTodoActivity.class);
        updateData.putExtra(TODO_DATA, todoListAdapter.getTodoOnPosition(position));
        startActivityForResult(updateData, UPDATE_TASK_REQUEST);
    }
}