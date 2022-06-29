package pl.piotrb.todoapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.piotrb.todoapp.database.models.Todo;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoItemHolder> {

    private List<Todo> todoList = new ArrayList<>();
    private OnTaskSelected onTaskSelected;

    public TodoListAdapter(OnTaskSelected onTaskSelected) {
        this.onTaskSelected = onTaskSelected;
    }

    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);

        return new TodoItemHolder(itemView, onTaskSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoItemHolder holder, int position) {
        Todo currentTodo = todoList.get(position);
        holder.title.setText(currentTodo.title);
        holder.description.setText(currentTodo.description);
        holder.deadlineDate.setText(currentTodo.deadlineDate.toString());
        holder.markAsDoneButton.setChecked(currentTodo.isFinished);
        if (currentTodo.attachmentPath.length() > 0) {
            holder.attachmentButton.setVisibility(View.VISIBLE);
        } else {
            holder.attachmentButton.setVisibility(View.GONE);
        }
        if (currentTodo.deadlineDate.before(new Date())) {
            holder.deadlineDate.setTextColor(Color.parseColor("#ff0000"));
        } else {
            holder.deadlineDate.setTextColor(Color.parseColor("#00ff00"));
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Todo getTodoOnPosition(int position) {
        return todoList.get(position);
    }

    public static class TodoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final TextView description;
        private final TextView deadlineDate;
        private final CheckBox markAsDoneButton;
        private final ImageView attachmentButton;
        private OnTaskSelected onTaskSelected;

        public TodoItemHolder(View itemView, OnTaskSelected onTaskSelected) {
            super(itemView);
            this.onTaskSelected = onTaskSelected;
            title = itemView.findViewById(R.id.todo_item_title);
            description = itemView.findViewById(R.id.todo_item_description);
            deadlineDate = itemView.findViewById(R.id.todo_item_deadline_date);
            attachmentButton = itemView.findViewById(R.id.todo_item_attachment_button);
            markAsDoneButton = itemView.findViewById(R.id.todo_item_mark_as_done);
            itemView.setOnClickListener(this);
            markAsDoneButton.setOnClickListener(this);
            attachmentButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onTaskSelected != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                onTaskSelected.selectTask(view, getAdapterPosition());
            }
        }
    }

    public interface OnTaskSelected {
        void selectTask(View view, int position);
    }

}
