package alex.task_manager.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;

import alex.task_manager.R;
import alex.task_manager.models.TaskForm;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.utils.TimestampUtils;

public class CreateTaskActivity  extends AppCompatActivity {

    private UserDbService userDbService;
    private TasksDbService tasksDbService;

    private EditText taskTitleEditText, taskDescriptionEditText;
    private TextView calendarTextView, alarmTimeTextView;

    Calendar dateCalendar = Calendar.getInstance();
    Calendar timeCalendar = Calendar.getInstance();

    private boolean editing = false;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskTitleEditText = findViewById(R.id.editTextTitle);
        taskDescriptionEditText = findViewById(R.id.editTextDescription);
        calendarTextView = findViewById(R.id.dateTextTitle);
        alarmTimeTextView = findViewById(R.id.alarmText);
        Button createTaskButton = findViewById(R.id.createTaskButton);

        userDbService = UserDbService.getInstance(this.getApplicationContext());
        tasksDbService = TasksDbService.getInstance(this.getApplicationContext());

        initCalendar();
        initButtonHandlers();

        resetDeadlineTime();
        resetAlarmTime();

        // recieving data
        Intent intent = getIntent();
        id = intent.getIntExtra(TasksDbService.LOCAL_ID_COLUMN, -1);
        if (id != -1) {
            editing = true;
            TaskForm.Builder taskBuilder = new TaskForm.Builder();
            fillFieldsWithTask(
                    taskBuilder.buildOneInstance(tasksDbService.getTaskModelCursorById(id))
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fillFieldsWithTask(TaskForm task) {
        taskTitleEditText.setText(task.getName());
        taskDescriptionEditText.setText(task.getAbout());
        if (task.getDeadline() != null) {
            dateCalendar.setTime(task.getDeadline());

            updateDeadlineTextView();
        }

        if (task.getNotificationTime() != null) {
            timeCalendar.setTime(task.getNotificationTime());

            updetaAlarmTextView();
        }
    }

    public void createTask(){
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            taskTitleEditText.setError(getString(R.string.CreatingTaskActivity__err__nameEmpty));
            taskTitleEditText.requestFocus();
            return;
        }
        if ((timeCalendar.get(Calendar.HOUR_OF_DAY) != 12
                || timeCalendar.get(Calendar.MINUTE) != 0)
                && !(dateCalendar.isSet(Calendar.YEAR)
                && dateCalendar.isSet(Calendar.MONTH)
                && dateCalendar.isSet(Calendar.DAY_OF_MONTH))
         ) {
            //ToDo: check this
            calendarTextView.setError(getString(R.string.CreatingTaskActivity__err__timeWithoutDate));
            return;
        }

        Timestamp notificationTime = null;
        Timestamp deadline = null;

        if (dateCalendar.isSet(Calendar.YEAR)
                && dateCalendar.isSet(Calendar.MONTH)
                && dateCalendar.isSet(Calendar.DAY_OF_MONTH)
        ) {
            deadline = new Timestamp(dateCalendar.getTime().getTime());

            if (timeCalendar.isSet(Calendar.HOUR_OF_DAY) && timeCalendar.isSet(Calendar.MINUTE)) {
                notificationTime = new Timestamp(timeCalendar.getTime().getTime());
            }
        }

        TaskForm task = new TaskForm(
                0,
                title,
                description,
                deadline,
                notificationTime
        );

        if (!editing) {
            tasksDbService.createTask(task);
            Toast.makeText(
                    CreateTaskActivity.this,
                    getResources().getText(R.string.CreatingTaskActivity__infoMsg__successCreating),
                    Toast.LENGTH_LONG
            ).show();
        } else {
            tasksDbService.updateTask(id, task);
            Toast.makeText(
                    CreateTaskActivity.this,
                    getResources().getText(R.string.CreatingTaskActivity__infoMsg__successEditing),
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    private void startTimeAlarmDialog() {
        new TimePickerDialog(
                CreateTaskActivity.this,
                timeDialogListener,
                timeCalendar.get(Calendar.HOUR_OF_DAY),
                timeCalendar.get(Calendar.MINUTE),
                true
            ).show();
    }

    private void startCalendarDialog() {
        if (dateCalendar.isSet(Calendar.YEAR)
                && dateCalendar.isSet(Calendar.MONTH)
                && dateCalendar.isSet(Calendar.DAY_OF_MONTH)
        ) {
            new DatePickerDialog(
                    CreateTaskActivity.this,
                    calendarDialogListener,
                    dateCalendar.get(Calendar.YEAR),
                    dateCalendar.get(Calendar.MONTH),
                    dateCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        } else {
            Calendar tmpCalendar = Calendar.getInstance();
            new DatePickerDialog(
                    CreateTaskActivity.this,
                    calendarDialogListener,
                    tmpCalendar.get(Calendar.YEAR),
                    tmpCalendar.get(Calendar.MONTH),
                    tmpCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        }
    }

    TimePickerDialog.OnTimeSetListener timeDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeCalendar.set(Calendar.MINUTE, minute);
            updetaAlarmTextView();
        }
    };

    DatePickerDialog.OnDateSetListener calendarDialogListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDeadlineTextView();
        }
    };

    private void updetaAlarmTextView() {
        if (!(dateCalendar.isSet(Calendar.YEAR)
              && dateCalendar.isSet(Calendar.MONTH)
              && dateCalendar.isSet(Calendar.DAY_OF_MONTH))
            && timeCalendar.get(Calendar.HOUR_OF_DAY) == 12
            && timeCalendar.get(Calendar.MINUTE) == 0) {
            alarmTimeTextView.setText("");
        } else {
            alarmTimeTextView.setText(TimestampUtils.calendarToString(
                    timeCalendar,
                    TimestampUtils.USER_FRIENDLY_TIME_FORMAT
            ));
        }
    }

    private void updateDeadlineTextView() {
        if (dateCalendar.isSet(Calendar.YEAR)
                && dateCalendar.isSet(Calendar.MONTH)
                && dateCalendar.isSet(Calendar.DAY_OF_MONTH)
        ) {
            calendarTextView.setText(TimestampUtils.calendarToString(
                    dateCalendar,
                    TimestampUtils.USER_FRIENDLY_DATE_FORMAT
            ));
        } else {
            calendarTextView.setText("");
        }
    }

    private void initCalendar() {
        dateCalendar.clear();
    }

    private void initButtonHandlers() {

        View createTaskButton = findViewById(R.id.createTaskButton);
        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        View resetTimeAlarmBtn = findViewById(R.id.resetTimeAlarmBtn);
        resetTimeAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAlarmTime();
            }
        });

        View resetTimeDeadlineBtn = findViewById(R.id.resetTimeDeadlineBtn);
        resetTimeDeadlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDeadlineTime();
            }
        });

        View todayBtn = findViewById(R.id.todayBtn);
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToday();

                updateDeadlineTextView();
            }
        });

        View tomorrowBtn = findViewById(R.id.tomorrowBtn);
        tomorrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToday();

                dateCalendar.add(Calendar.DAY_OF_YEAR, 1);

                updateDeadlineTextView();
            }
        });

        View nextWeekBtn = findViewById(R.id.nextWeekBtn);
        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToday();

                dateCalendar.add(Calendar.DAY_OF_YEAR, 7);

                updateDeadlineTextView();
            }
        });

        View setDateTimeBtn = findViewById(R.id.setDateBtn);
        setDateTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalendarDialog();
            }
        });

        View setTimeAlarmBtn = findViewById(R.id.setTimeAlarmBtn);
        setTimeAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeAlarmDialog();
            }
        });
    }

    private void resetAlarmTime() {
        timeCalendar.set(Calendar.HOUR_OF_DAY, 12);
        timeCalendar.set(Calendar.MINUTE, 0);

        updetaAlarmTextView();
    }

    private void resetDeadlineTime() {
        dateCalendar.clear(Calendar.YEAR);
        dateCalendar.clear(Calendar.MONTH);
        dateCalendar.clear(Calendar.DAY_OF_MONTH);

        updateDeadlineTextView();
        updetaAlarmTextView();
    }

    private void setToday() {
        dateCalendar = Calendar.getInstance();
    }
}
