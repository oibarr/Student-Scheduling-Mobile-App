package com.example.c196.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.c196.Database.Repository;
import com.example.c196.R;
import com.example.c196.entities.Assessment;
import com.example.c196.entities.Course;
import com.example.c196.entities.Instructor;
import com.example.c196.entities.Term;

public class MainActivity extends AppCompatActivity {
    public static int alertID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.enterButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermList.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.addSampleData:
                Repository repository = new Repository(getApplication());
                repository.insert(new Term(1, "sampleTerm", "12/21/22", "12/31/22"));
                repository.insert(new Term(2, "sampleTerm2", "12/21/22", "12/31/22"));
                repository.insert(new Course(1, "sampleCourse", "Plan to Take", "12/21/22", "12/31/22", "sampleNote", 1));
                repository.insert(new Course(2, "sampleCourse2", "Plan to Take", "12/21/22", "12/31/22", "sampleNote2", 2));
                repository.insert(new Assessment(1, "sampleAssessment", "Performance Assessment", "12/21/22", "12/31/22", 1));
                repository.insert(new Assessment(2, "sampleAssessment2", "Performance Assessment", "12/21/22", "12/31/22", 2));
                repository.insert(new Instructor(1, "sampleInstructor", "(123) 456-7891", "sampleInstructor@wgu.edu", 1));
                repository.insert(new Instructor(2, "sampleInstructor2", "(123) 456-7891", "sampleInstructor2@wgu.edu", 2));
                Toast.makeText(this, "Sample data created successfully!", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}