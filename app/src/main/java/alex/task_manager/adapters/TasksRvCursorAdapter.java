package alex.task_manager.adapters;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import alex.task_manager.R;
import alex.task_manager.activities.CreateTaskActivity;
import alex.task_manager.models.TaskViewModel;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.utils.TimestampUtils.timestampToString;

public class TasksRvCursorAdapter extends CursorRecyclerViewAdapter<TasksRvCursorAdapter.TaskBaseViewHolder>{

    private int selectedItem = -1;

    private final static int TYPE_MINIMAL=1,
                             TYPE_DETAILED=2;

    private Context mContext;

    public TasksRvCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    abstract class TaskBaseViewHolder extends RecyclerView.ViewHolder {
        CheckBox TaskTitle;
        ImageView taskEditBtn;
        int id;
        TasksRvCursorAdapter adapter;
        TextView deadlineTextView;
        TextView authorTextView;
        boolean prevChecked;

        TaskBaseViewHolder(final View itemView, final TasksRvCursorAdapter adapter) {
            super(itemView);

            this.adapter = adapter;

            TaskTitle = itemView.findViewById(R.id.task_title);
            taskEditBtn = itemView.findViewById(R.id.task_edit_btn);
            deadlineTextView = itemView.findViewById(R.id.task_deadline);
            authorTextView = itemView.findViewById(R.id.taskAuthor);

            taskEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CreateTaskActivity.class);
                    intent.putExtra("task_id", id);
                    v.getContext().startActivity(intent);
                }
            });

            TaskTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (prevChecked != isChecked) {
                        prevChecked = isChecked;
                        TasksDbService.getInstance(buttonView.getContext()).setCompleted(id, isChecked);

                        adapter.updateCursor();
                    }
                }
            });
        }

        public void bind(TaskViewModel task) {

            TaskTitle.setText(task.getName());
            deadlineTextView.setText(task.getAbout());
            authorTextView.setText(task.getAuthor());
            deadlineTextView.setText(timestampToString(task.getDeadline(),TimestampUtils.USER_FRIENDLY_DATE_FORMAT));
            TaskTitle.setChecked(task.isComplited());
            id = task.getId();
        }

        TasksRvCursorAdapter getAdapter() {
            return adapter;
        }

        int getId() {
            return id;
        }
    }

    class TaskDetailedViewHolder extends TaskBaseViewHolder {
        TextView descriptionTextView;
        TextView authorTextView;
        TextView deadlineTextView;

        TaskDetailedViewHolder(View itemView, final TasksRvCursorAdapter adapter) {
            super(itemView, adapter);

            descriptionTextView = itemView.findViewById(R.id.task_description);
            authorTextView = itemView.findViewById(R.id.taskAuthor);
            deadlineTextView = itemView.findViewById(R.id.task_deadline);

            itemView.findViewById(R.id.clickable_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAdapter().setSelectedItem(-1);
                }
            });
        }

        public void bind(TaskViewModel task) {
            super.bind(task);
            descriptionTextView.setText(task.getAbout());
            authorTextView.setText(task.getAuthor());
            deadlineTextView.setText(timestampToString(task.getDeadline(),TimestampUtils.USER_FRIENDLY_DATE_FORMAT));
        }
    }

    class TasksMinimalViewHolder extends TaskBaseViewHolder {
        TasksMinimalViewHolder(View itemView, TasksRvCursorAdapter adapter) {
            super(itemView, adapter);

            itemView.findViewById(R.id.clickable_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAdapter().setSelectedItem(TasksMinimalViewHolder.this.getAdapterPosition());
                }
            });
        }
    }

    void setSelectedItem(int pos) {
        int prev = selectedItem;
        selectedItem = pos;

        if (prev != -1) {
            notifyItemChanged(prev);
        }

        if (pos != -1) {
            notifyItemChanged(pos);
        }
    }

    @Override
    public TaskBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskBaseViewHolder holder;
        View view;

        switch (viewType) {
            case TYPE_MINIMAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_panel_minimal, parent, false);
                holder = new TasksMinimalViewHolder(view, this);
                break;
            case TYPE_DETAILED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_panel_detailed, parent, false);
                holder = new TaskDetailedViewHolder(view, this);
                break;
            default:
                holder = null;
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(TaskBaseViewHolder viewHolder, Cursor cursor) {
        TaskViewModel myListItem = (new TaskViewModel.Builder()).buildCurrentInstance(cursor);
        viewHolder.bind(myListItem);
        int viewType=viewHolder.getItemViewType();

        switch (viewType) {
            case TYPE_MINIMAL:
                ((TasksMinimalViewHolder)viewHolder).bind(myListItem);
                break;
            case TYPE_DETAILED:
                ((TaskDetailedViewHolder)viewHolder).bind(myListItem);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == selectedItem ? TYPE_DETAILED : TYPE_MINIMAL;
    }

    void updateCursor() {
        changeCursor(TasksDbService.getInstance(mContext).getTaskModelCursorByPerformerId(UserDbService.getInstance(mContext).getCurrentUserId()));
    }

    public void removeItem(int position) {
        int id = (int)getItemId(position);
        TasksDbService.getInstance(mContext).removeTask(id);

        updateCursor();
        notifyItemRemoved(position);
    }

//    private void showUndoSnackbar() {
//        View view = mActivity.findViewById(R.id.coordinator_layout);
//        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
//                Snackbar.LENGTH_LONG);
//        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
//        snackbar.show();
//    }
//
//    private void undoDelete() {
//        mListItems.add(mRecentlyDeletedItemPosition,
//                mRecentlyDeletedItem);
//        notifyItemInserted(mRecentlyDeletedItemPosition);
//    }
}
