package alex.task_manager.adapters;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import alex.task_manager.R;
import alex.task_manager.activities.CreateTaskActivity;
import alex.task_manager.models.TaskModel;
import alex.task_manager.models.TaskViewModel;
import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.services.DbServices.Mappers.taskViewModelMapper;
import static alex.task_manager.utils.TimestampUtils.timestampToString;

public class TasksRvCursorAdapter extends CursorRecyclerViewAdapter<TasksRvCursorAdapter.TaskViewHolder>{

    public TasksRvCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox TaskTitle;
        private TextView descriptionTextView;
        private TextView authorTextView;
        private TextView deadlineTextView;
        private ImageView taskEditBtn;
        private int id;

        public TaskViewHolder(View itemView) {
            super(itemView);
            TaskTitle = itemView.findViewById(R.id.task_title);
            descriptionTextView = itemView.findViewById(R.id.task_description);
            authorTextView = itemView.findViewById(R.id.taskAuthor);
            deadlineTextView = itemView.findViewById(R.id.task_deadline);
            taskEditBtn = itemView.findViewById(R.id.task_edit_btn);

            taskEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CreateTaskActivity.class);
                    intent.putExtra("task_id", id);
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void bind(TaskViewModel task) {

            TaskTitle.setText(task.getCaption());
            descriptionTextView.setText(task.getAbout());
            authorTextView.setText(task.getAuthor());
            deadlineTextView.setText(timestampToString(task.getTime(),TimestampUtils.USER_FRIENDLY_DATE_FORMAT));
            id = task.getId();
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_panel, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder viewHolder, Cursor cursor) {
        TaskViewModel myListItem = taskViewModelMapper(cursor);
        viewHolder.bind(myListItem);
    }
}
