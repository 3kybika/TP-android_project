package alex.myapplication.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import alex.myapplication.R;
import alex.myapplication.models.TaskModel;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox TaskTitle;
        private TextView descriptionTextView;
        private TextView authorTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            TaskTitle = itemView.findViewById(R.id.task_title);
            descriptionTextView = itemView.findViewById(R.id.task_description);
            authorTextView = itemView.findViewById(R.id.taskAuthor);
        }

        public void bind(TaskModel task) {

            TaskTitle.setText(task.getCaption());
            descriptionTextView.setText(task.getAbout());
            //ToDo - real author name
            authorTextView.setText(Integer.toString(task.getAuthorId()));
        }
    }

    private List<TaskModel> taskList = new ArrayList<>();

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_panel, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bind(taskList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setItems(Collection<TaskModel> tasks) {
        taskList.addAll(tasks);
        notifyDataSetChanged();
    }

    public void clearItems() {
        taskList.clear();
        notifyDataSetChanged();
    }
}
