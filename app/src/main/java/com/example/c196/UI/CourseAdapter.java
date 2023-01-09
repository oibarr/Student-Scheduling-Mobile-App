package com.example.c196.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c196.R;
import com.example.c196.entities.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> mCourses;
    private final Context context;
    private final LayoutInflater mInflater;

    public CourseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView courseItemView;

        private CourseViewHolder(View itemView) {
            super(itemView);
            courseItemView = itemView.findViewById(R.id.courseNameText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Course currentCourse = mCourses.get(position);
                    Intent intent = new Intent(context, CourseDetails.class);
                    intent.putExtra("id", currentCourse.getCourseID());
                    intent.putExtra("name", currentCourse.getCourseName());
                    intent.putExtra("status", currentCourse.getCourseStatus());
                    intent.putExtra("start", currentCourse.getCourseStart());
                    intent.putExtra("end", currentCourse.getCourseEnd());
                    intent.putExtra("note", currentCourse.getCourseNote());
                    intent.putExtra("termID", currentCourse.getTermID());
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.course_list_item, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder holder, int position) {
        if (mCourses != null) {
            Course currentCourse = mCourses.get(position);
            holder.courseItemView.setText(currentCourse.getCourseName());
        } else {
            holder.courseItemView.setText("No Course Name");
        }
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public void setCourse(List<Course> courses) {
        mCourses = courses;
        notifyDataSetChanged();
    }
}
