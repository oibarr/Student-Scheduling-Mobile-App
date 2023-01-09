package com.example.c196.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c196.Database.Repository;
import com.example.c196.R;
import com.example.c196.entities.Instructor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InstructorList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_list);
        RecyclerView recyclerView = findViewById(R.id.instructorRecyclerView);

        final InstructorAdapter instructorAdapter = new InstructorAdapter(this);
        recyclerView.setAdapter(instructorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = new Repository(getApplication());

        List<Instructor> allInstructors = repository.getAllInstructors();
        instructorAdapter.setInstructor(allInstructors);

        FloatingActionButton floatingActionButton = findViewById(R.id.addInstructor);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InstructorList.this, InstructorDetails.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Instructor> allInstructors = repository.getAllInstructors();
        RecyclerView recyclerView = findViewById(R.id.instructorRecyclerView);

        final InstructorAdapter instructorAdapter = new InstructorAdapter(this);
        recyclerView.setAdapter(instructorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        instructorAdapter.setInstructor(allInstructors);
    }
}