package pl.piotrb.todoapp.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import pl.piotrb.todoapp.database.converter.DateConverter;
import pl.piotrb.todoapp.database.dao.TodoDao;
import pl.piotrb.todoapp.database.models.Todo;

@Database(entities = {Todo.class}, version = 1)
@TypeConverters(value = {DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "todo-database";
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        Log.i("APP", "Database init");
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract TodoDao todoDao();

}
