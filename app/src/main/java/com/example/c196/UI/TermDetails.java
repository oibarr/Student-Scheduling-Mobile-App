package com.example.c196.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c196.Database.Repository;
import com.example.c196.R;
import com.example.c196.entities.Course;
import com.example.c196.entities.Term;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TermDetails extends AppCompatActivity {
    Repository repository;

    EditText editName;
    EditText editStart;
    EditText editEnd;

    int id;

    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    final Calendar calendarStart = Calendar.getInstance();

    String formatter = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter, Locale.getDefault());

    Term term;
    Term currentTerm;

    public void filterCourses(CourseAdapter courseAdapter) {
        List<Course> filteredCourses = new ArrayList<>();
        for (Course c : repository.getAllCourses()) {
            if (c.getTermID() == id) filteredCourses.add(c);
        }
        courseAdapter.setCourse(filteredCourses);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);

        id = getIntent().getIntExtra("id", -1);

        editName = findViewById(R.id.termName);
        editName.setText(getIntent().getStringExtra("name"));

        editStart = findViewById(R.id.startDate);
        editStart.setText(getIntent().getStringExtra("start"));

        editEnd = findViewById(R.id.endDate);
        editEnd.setText(getIntent().getStringExtra("end"));

        repository = new Repository(getApplication());

        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = new Repository(getApplication());

        filterCourses(courseAdapter);

        Button save = findViewById(R.id.saveTerm);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == -1) {
                    term = new Term(0, editName.getText().toString(), editStart.getText().toString(), editEnd.getText().toString());
                    repository.insert(term);
                } else {
                    term = new Term(id, editName.getText().toString(), editStart.getText().toString(), editEnd.getText().toString());
                    repository.update(term);
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
                new DatePickerDialog(TermDetails.this, startDate, calendarStart.get(Calendar.YEAR),
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
                new DatePickerDialog(TermDetails.this, endDate, calendarStart.get(Calendar.YEAR),
                        calendarStart.get(Calendar.MONTH),
                        calendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button courses = findViewById(R.id.courseButton);
        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermDetails.this, CourseDetails.class);
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
        getMenuInflater().inflate(R.menu.menu_term_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.deleteTerm:

                for (Term t : repository.getAllTerms()) {
                    if (t.getTermID() == id) currentTerm = t;
                }

                int numCourses = 0;
                for (Course c : repository.getAllCourses()) {
                    if (c.getTermID() == id) {
                        numCourses++;
                    }
                }

                if (numCourses == 0) {
                    repository.delete(currentTerm);
                    Toast.makeText(TermDetails.this, currentTerm.getTermName() + " deleted successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TermDetails.this, "Cannot delete a term with associated courses", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterCourses(courseAdapter);
    }
}