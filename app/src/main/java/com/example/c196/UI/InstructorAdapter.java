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
import com.example.c196.entities.Instructor;

import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {
    private List<Instructor> mInstructors;
    private final Context context;
    private final LayoutInflater mInflater;

    public InstructorAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    class InstructorViewHolder extends RecyclerView.ViewHolder {
        private final TextView instructorItemView;

        private InstructorViewHolder(View itemView) {
            super(itemView);
            instructorItemView = itemView.findViewById(R.id.instructorNameText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Instructor currentInstructor = mInstructors.get(position);
                    Intent intent = new Intent(context, InstructorDetails.class);
                    intent.putExtra("id", currentInstructor.getInstructorID());
                    intent.putExtra("name", currentInstructor.getInstructorName());
                    intent.putExtra("phone", currentInstructor.getInstructorPhone());
                    intent.putExtra("email", currentInstructor.getInstructorEmail());
                    intent.putExtra("associatedCourseID", currentInstructor.getAssociatedCourseID());
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.instructor_list_item, parent, false);
        return new InstructorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        if (mInstructors != null) {
            Instructor currentInstructor = mInstructors.get(position);
            String name = currentInstructor.getInstructorName();
            holder.instructorItemView.setText(name);
        } else {
            holder.instructorItemView.setText("No Instructor Name");
        }
    }

    @Override
    public int getItemCount() {
        return mInstructors.size();
    }

    public void setInstructor(List<Instructor> instructors) {
        mInstructors = instructors;
        notifyDataSetChanged();
    }

}
