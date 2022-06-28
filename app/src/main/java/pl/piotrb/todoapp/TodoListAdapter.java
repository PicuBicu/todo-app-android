package pl.piotrb.todoapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoItemHolder> {

    private List<Todo> todoList = new ArrayList<>();

    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new TodoItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoItemHolder holder, int position) {
        Log.i("APP", "xd");
        Todo currentTodo = todoList.get(position);
        holder.title.setText(currentTodo.title);
        holder.description.setText(currentTodo.description);
        holder.deadlineDate.setText(currentTodo.deadlineDate.toString());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoItemHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final TextView deadlineDate;

        public TodoItemHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_item_title);
            description = itemView.findViewById(R.id.todo_item_description);
            deadlineDate = itemView.findViewById(R.id.todo_item_deadline_date);
        }
    }

}
