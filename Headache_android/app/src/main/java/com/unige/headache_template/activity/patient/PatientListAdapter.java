package com.unige.headache_template.activity.patient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unige.headache_template.R;
import com.unige.headache_template.model.User;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.CustomViewHolder> {
    private List<User> patients;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView patientAge;
        public TextView patientGender;

        public CustomViewHolder(View view) {
            super(view);
            patientName = (TextView) view.findViewById(R.id.patientFirstName);
            patientAge = (TextView) view.findViewById(R.id.patientAge);
            patientGender = (TextView) view.findViewById(R.id.patientGender);
        }
    }

    public PatientListAdapter(List<User> patients) {
        this.patients = patients;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        User patient = patients.get(position);
        holder.patientName.setText(patient.getName());
        holder.patientAge.setText(patient.getAge());
        holder.patientGender.setText(patient.getSex());
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

}