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

import com.example.c196.Database.Repository;
import com.example.c196.R;
import com.example.c196.entities.Assessment;
import com.example.c196.entities.Course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssessmentDetails extends AppCompatActivity {
    Repository repository;

    EditText editName;
    EditText editType;
    EditText editStart;
    EditText editEnd;
    EditText editCourse;

    int id;
    int courseID;

    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    final Calendar calendarStart = Calendar.getInstance();

    String formatter = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter, Locale.getDefault());

    Assessment assessment;
    Assessment currentAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        id = getIntent().getIntExtra("id", -1);

        editName = findViewById(R.id.assessmentName);
        editName.setText(getIntent().getStringExtra("name"));

        editType = findViewById(R.id.assessmentType);
        editType.setText(getIntent().getStringExtra("type"));

        editStart = findViewById(R.id.assessmentStartDate);
        editStart.setText(getIntent().getStringExtra("start"));

        editEnd = findViewById(R.id.assessmentEndDate);
        editEnd.setText(getIntent().getStringExtra("end"));

        editCourse = findViewById(R.id.associatedCourseName);
        courseID = getIntent().getIntExtra("courseID", -1);

        repository = new Repository(getApplication());

        ArrayList<String> typeArray = new ArrayList<>();
        typeArray.add("Performance Assessment");
        typeArray.add("Objective Assessment");

        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        ArrayAdapter<String> typeArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeArray);
        typeSpinner.setAdapter(typeArrayAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeSpinner.setPrompt(typeArrayAdapter.getItem(i));
                editType.setText(typeArrayAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                typeSpinner.setPrompt("No Selection");
            }
        });

        Spinner courseSpinner = findViewById(R.id.courseSpinner);
        ArrayAdapter<Course> courseArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, repository.getAllCourses());
        courseSpinner.setAdapter(courseArrayAdapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                courseSpinner.setPrompt(courseArrayAdapter.getItem(i).toString());
                editCourse.setText(courseArrayAdapter.getItem(i).toString());
                courseID = courseArrayAdapter.getItem(i).getCourseID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                courseSpinner.setPrompt("No Selection");
            }
        });

        Button button = findViewById(R.id.saveAssessment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == -1) {
                    assessment = new Assessment(0, editName.getText().toString(), editType.getText().toString(), editStart.getText().toString(), editEnd.getText().toString(), courseID);
                    repository.insert(assessment);
                } else {
                    assessment = new Assessment(id, editName.getText().toString(), editType.getText().toString(), editStart.getText().toString(), editEnd.getText().toString(), courseID);
                    repository.update(assessment);
                }
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
                new DatePickerDialog(AssessmentDetails.this, startDate, calendarStart.get(Calendar.YEAR),
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
                new DatePickerDialog(AssessmentDetails.this, endDate, calendarStart.get(Calendar.YEAR),
                        calendarStart.get(Calendar.MONTH),
                        calendarStart.get(Calendar.DAY_OF_MONTH)).show();
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
        getMenuInflater().inflate(R.menu.menu_assessment_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.notifyStart:
                Date startTriggerDate = null;
                String startDate = editStart.getText().toString();
                String assessmentName = editName.getText().toString();
                try{
                    startTriggerDate = simpleDateFormat.parse(startDate);
                } catch (ParseException e){
                    e.printStackTrace();
                }

                Long dateTrigger = startTriggerDate.getTime();

                Intent intent = new Intent(AssessmentDetails.this, MyReceiver.class);
                intent.putExtra("trigger", assessmentName + " will start on " + startDate);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(AssessmentDetails.this, ++MainActivity.alertID, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateTrigger, pendingIntent);

                return true;

            case R.id.notifyEnd:
                Date endTriggerDate = null;
                String endDate = editEnd.getText().toString();
                assessmentName = editName.getText().toString();

                try{
                    endTriggerDate = simpleDateFormat.parse(endDate);
                } catch (ParseException e){
                    e.printStackTrace();
                }

                dateTrigger = endTriggerDate.getTime();

                intent = new Intent(AssessmentDetails.this, MyReceiver.class);
                intent.putExtra("trigger", assessmentName + " will end on " + endDate);

                pendingIntent = PendingIntent.getBroadcast(AssessmentDetails.this, ++MainActivity.alertID, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateTrigger, pendingIntent);

                return true;

            case R.id.deleteAssessment:

                for(Assessment a : repository.getAllAssessments()) {
                    if (a.getAssessmentID() == id){
                        currentAssessment = a;
                        repository.delete(currentAssessment);
                        Toast.makeText(AssessmentDetails.this, currentAssessment.getAssessmentName() + " deleted successfully", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(AssessmentDetails.this, currentAssessment.getAssessmentName() +" could not be deleted", Toast.LENGTH_LONG).show();
                    }
                }
        }
        return super.onOptionsItemSelected(menuItem);
    }




}