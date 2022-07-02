package pl.piotrb.todoapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todos")
    LiveData<List<Todo>> getAll();

    @Query("SELECT * FROM todos WHERE id = :id")
    LiveData<Todo> findById(Integer id);

    @Insert
    Long insert(Todo todo);

    @Delete
    void delete(Todo todo);

    @Update
    void update(Todo todo);
}
