package pl.piotrb.todoapp.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "todos")
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String title;
    public String description;
    @ColumnInfo(name = "creation_date")
    public Date creationDate;
    @ColumnInfo(name = "deadline_date")
    public Date deadlineDate;
    @ColumnInfo(name = "is_finished")
    public boolean isFinished;
    @ColumnInfo(name = "is_notifications_enabled")
    public boolean isNotificationsEnabled;
    @ColumnInfo(name = "attachment_path")
    public String attachmentPath;
    public String category;

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", deadlineDate=" + deadlineDate +
                ", isFinished=" + isFinished +
                ", isNotificationsEnabled=" + isNotificationsEnabled +
                ", attachmentPath='" + attachmentPath + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
