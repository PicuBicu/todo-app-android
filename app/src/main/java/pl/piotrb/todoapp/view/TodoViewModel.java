package pl.piotrb.todoapp.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;
import pl.piotrb.todoapp.repository.TodoRepository;

public class TodoViewModel extends AndroidViewModel {
    private final TodoRepository todoRepository;
    private final LiveData<List<Todo>> todoList;

    public TodoViewModel(@NonNull Application application) {
        super(application);
        todoRepository = new TodoRepository(application);
        todoList = todoRepository.getAllTodos();
    }

    public long insert(Todo todo) {
        return todoRepository.insert(todo);
    }

    public void update(Todo todo) {
        todoRepository.update(todo);
    }

    public void delete(Todo todo) {
        todoRepository.delete(todo);
    }

    public LiveData<Todo> getById(int id) {
        return todoRepository.getById(id);
    }

    public LiveData<List<Todo>> getAllTodos() {
        return todoList;
    }
}
