package com.example.c196.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c196.Database.Repository;
import com.example.c196.R;
import com.example.c196.entities.Assessment;
import com.example.c196.entities.Course;
import com.example.c196.entities.Instructor;
import com.example.c196.entities.Term;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseDetails extends AppCompatActivity {

    Repository repository;

    EditText editName;
    EditText editStatus;
    EditText editStart;
    EditText editEnd;
    EditText editNote;
    EditText editTerm;

    int id;
    int termID;

    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    final Calendar calendarStart = Calendar.getInstance();

    String formatter = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter, Locale.getDefault());

    Course course;
    Course currentCourse;

    public void filterAssessments(AssessmentAdapter assessmentAdapter) {
        List<Assessment> filteredAssessments = new ArrayList<>();
        for (Assessment a : repository.getAllAssessments()) {
            if (a.getCourseID() == id) filteredAssessments.add(a);
        }
        assessmentAdapter.setAssessment(filteredAssessments);
    }

    public void filterInstructors(InstructorAdapter instructorAdapter){
        List<Instructor> filteredInstructors = new ArrayList<>();
        for (Instructor i : repository.getAllInstructors()) {
            if (i.getAssociatedCourseID() == id) filteredInstructors.add(i);
        }

        instructorAdapter.setInstructor(filteredInstructors);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        RecyclerView recyclerView = findViewById(R.id.assessmentRecyclerView);
        RecyclerView instructorRecyclerView = findViewById(R.id.instructorRecyclerView);

        id = getIntent().getIntExtra("id", -1);

        editName = findViewById(R.id.courseName);
        editName.setText(getIntent().getStringExtra("name"));

        editStatus = findViewById(R.id.courseStatus);
        editStatus.setText(getIntent().getStringExtra("status"));

        editStart = findViewById(R.id.courseStartDate);
        editStart.setText(getIntent().getStringExtra("start"));

        editEnd = findViewById(R.id.courseEndDate);
        editEnd.setText(getIntent().getStringExtra("end"));

        editNote = findViewById(R.id.courseNote);
        editNote.setText(getIntent().getStringExtra("note"));

        editTerm = findViewById(R.id.termName);
        termID = getIntent().getIntExtra("termID", -1);

        repository = new Repository(getApplication());

        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final InstructorAdapter instructorAdapter = new InstructorAdapter(this);
        instructorRecyclerView.setAdapter(instructorAdapter);
        instructorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = new Repository(getApplication());

        filterAssessments(assessmentAdapter);
        filterInstructors(instructorAdapter);

        ArrayList<String> statusArray = new ArrayList<>();
        statusArray.add("In Progress");
        statusArray.add("Completed");
        statusArray.add("Dropped");
        statusArray.add("Plan to Take");

        Spinner statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<String> statusArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        statusSpinner.setAdapter(statusArrayAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusSpinner.setPrompt(statusArrayAdapter.getItem(i));
                editStatus.setText(statusArrayAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                statusSpinner.setPrompt("No Selection");
            }
        });

        Spinner spinner = findViewById(R.id.termSpinner);
        ArrayAdapter<Term> termArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, repository.getAllTerms());
        spinner.setAdapter(termArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner.setPrompt(termArrayAdapter.getItem(i).toString());
                editTerm.setText(termArrayAdapter.getItem(i).toString());
                termID = termArrayAdapter.getItem(i).getTermID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setPrompt("No Selection");
            }
        });

        Button save = findViewById(R.id.saveCourse);
        save.setOnClickListener(view -> {
            if (id == -1) {
                course = new Course(0, editName.getText().toString(), editStatus.toString(), editStart.getText().toString(), editEnd.getText().toString(), editNote.getText().toString(), termID);
                repository.insert(course);

            } else {
                course = new Course(id, editName.getText().toString(), editStatus.toString(), editStart.getText().toString(), editEnd.getText().toString(), editNote.getText().toString(), termID);
                repository.update(course);
            }
        });

        startDate = (datePicker, year, monthOfYear, dayOfMonth) -> {
            calendarStart.set(Calendar.YEAR, year);
            calendarStart.set(Calendar.MONTH, monthOfYear);
            calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateStart();
        };

        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CourseDetails.this, startDate, calendarStart.get(Calendar.YEAR),
                        calendarStart.get(Calendar.MONTH),
                        calendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate = (datePicker, year, monthOfYear, dayOfMonth) -> {
            calendarStart.set(Calendar.YEAR, year);
            calendarStart.set(Calendar.MONTH, monthOfYear);
            calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEnd();
        };

        editEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CourseDetails.this, endDate, calendarStart.get(Calendar.YEAR),
                        calendarStart.get(Calendar.MONTH),
                        calendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button instructors = findViewById(R.id.instructorButton);
        instructors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetails.this, InstructorDetails.class);
                startActivity(intent);
            }
        });

        Button assessments = findViewById(R.id.assessmentButton);
        assessments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetails.this, AssessmentDetails.class);
                startActivity(intent);
            }
        });
    }

    private void updateStart() {
        editStart.setText(simpleDateFormat.format(calendarStart.getTime()));
    }

    private void updateEnd() {
        editEnd.setText(simpleDateFormat.format(calendarStart.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.shareNotes:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
                sendIntent.putExtra(Intent.EXTRA_TITLE, "Note Share");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;

            case R.id.notifyStart:
                Date startTriggerDate = null;
                String startDate = editStart.getText().toString();
                String courseName = editName.getText().toString();
                try{
                    startTriggerDate = simpleDateFormat.parse(startDate);
                } catch (ParseException e){
                    e.printStackTrace();
                }

                Long dateTrigger = startTriggerDate.getTime();

                Intent intent = new Intent(CourseDetails.this, MyReceiver.class);
                intent.putExtra("trigger", courseName + " will start on " + startDate);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(CourseDetails.this, ++MainActivity.alertID, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateTrigger, pendingIntent);

                return true;

            case R.id.notifyEnd:
                Date endTriggerDate = null;
                String endDate = editEnd.getText().toString();
                courseName = editName.getText().toString();

                try{
                    endTriggerDate = simpleDateFormat.parse(endDate);
                } catch (ParseException e){
                    e.printStackTrace();
                }

                dateTrigger = endTriggerDate.getTime();

                intent = new Intent(CourseDetails.this, MyReceiver.class);
                intent.putExtra("trigger", courseName + " will end on " + endDate);

                pendingIntent = PendingIntent.getBroadcast(CourseDetails.this, ++MainActivity.alertID, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateTrigger, pendingIntent);

                return true;

            case R.id.deleteCourse:

                for(Course c : repository.getAllCourses()) {
                    if (c.getCourseID() == id){
                        currentCourse = c;
                        repository.delete(currentCourse);
                        Toast.makeText(CourseDetails.this, currentCourse.getCourseName() + " deleted successfully", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(CourseDetails.this, currentCourse.getCourseName() +" could not be deleted", Toast.LENGTH_LONG).show();
                    }
                }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.assessmentRecyclerView);
        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterAssessments(assessmentAdapter);

        RecyclerView instructorRecyclerView = findViewById(R.id.instructorRecyclerView);
        final InstructorAdapter instructorAdapter = new InstructorAdapter(this);
        instructorRecyclerView.setAdapter(instructorAdapter);
        instructorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterInstructors(instructorAdapter);
    }

}
