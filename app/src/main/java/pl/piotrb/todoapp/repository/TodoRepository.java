package pl.piotrb.todoapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import pl.piotrb.todoapp.database.AppDatabase;
import pl.piotrb.todoapp.database.dao.TodoDao;
import pl.piotrb.todoapp.database.models.Todo;

public class TodoRepository {

    private TodoDao todoDao;
    private LiveData<List<Todo>> todoList;

    public TodoRepository(Application application) {
        todoDao = AppDatabase.getInstance(application).todoDao();
        todoList = todoDao.getAll();
    }

    public long insert(Todo todo) {
        return todoDao.insert(todo);
    }

    public void delete(Todo todo) {
        todoDao.delete(todo);
    }

    public void update(Todo todo) {
        todoDao.update(todo);
    }

    public LiveData<Todo> getById(long id) {
        return todoDao.findById(id);
    }

    public LiveData<List<Todo>> getAllTodos() {
        return todoList;
    }
}
