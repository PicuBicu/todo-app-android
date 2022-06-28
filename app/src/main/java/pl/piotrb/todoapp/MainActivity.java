package pl.piotrb.todoapp;

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
                Log.i("APP", "Doszło do zmiany w view model");
                Log.i("APP", todos.toString());
                todoListAdapter.setTodoList(todos);
            }
        });
    }

    public void openAddNewActivity(View view) {
        startActivity(new Intent(this, AddTodoActivity.class));
    }
}