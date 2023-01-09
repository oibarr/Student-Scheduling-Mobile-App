package com.example.c196.UI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.c196.Database.Repository;
import com.example.c196.R;
import com.example.c196.entities.Course;
import com.example.c196.entities.Instructor;

public class InstructorDetails extends AppCompatActivity {
    Repository repository;

    EditText editName;
    EditText editPhone;
    EditText editEmail;
    TextView editCourse;

    int id;
    int associatedCourseId;

    Instructor instructor;
    Instructor currentInstructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);

        id = getIntent().getIntExtra("id", -1);

        editName = findViewById(R.id.instructorName);
        editName.setText(getIntent().getStringExtra("name"));

        editPhone = findViewById(R.id.instructorPhone);
        editPhone.setText(getIntent().getStringExtra("phone"));

        editEmail = findViewById(R.id.instructorEmail);
        editEmail.setText(getIntent().getStringExtra("email"));

        editCourse = findViewById(R.id.associatedCourseNameText);
        associatedCourseId = getIntent().getIntExtra("associatedCourseID", -1);

        repository =  new Repository(getApplication());

        Spinner associatedCourseSpinner = findViewById(R.id.associatedCourseSpinner);
        ArrayAdapter<Course> associatedCourseArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, repository.getAllCourses());
        associatedCourseSpinner.setAdapter(associatedCourseArrayAdapter);
        associatedCourseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                associatedCourseSpinner.setPrompt(associatedCourseArrayAdapter.getItem(i).toString());
                editCourse.setText(associatedCourseArrayAdapter.getItem(i).toString());
                associatedCourseId = associatedCourseArrayAdapter.getItem(i).getCourseID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                associatedCourseSpinner.setPrompt("No Selection");
            }
        });

        Button save = findViewById(R.id.saveInstructor);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == -1) {
                    instructor = new Instructor(0, editName.getText().toString(), editPhone.getText().toString(), editEmail.getText().toString(), associatedCourseId);
                    repository.insert(instructor);
                } else {
                    instructor = new Instructor(id, editName.getText().toString(), editPhone.getText().toString(), editEmail.getText().toString(), associatedCourseId);
                    repository.update(instructor);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_instructor_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.deleteInstructor:

                for(Instructor i : repository.getAllInstructors()) {
                    if (i.getInstructorID() == id){
                        currentInstructor = i;
                        repository.delete(currentInstructor);
                        Toast.makeText(InstructorDetails.this, currentInstructor.getInstructorName() + " deleted successfully", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(InstructorDetails.this, currentInstructor.getInstructorName() +" could not be deleted", Toast.LENGTH_LONG).show();
                    }
                }
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
