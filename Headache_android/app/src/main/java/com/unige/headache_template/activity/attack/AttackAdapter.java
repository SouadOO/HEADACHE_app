package com.unige.headache_template.activity.attack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import com.unige.headache_template.model.Attack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import com.unige.headache_template.R;
import com.unige.headache_template.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackAdapter extends RecyclerView.Adapter<AttackAdapter.CustomViewHolder> {
    List<String> symptoms=new ArrayList<>();
    List<String> symptomDetails=new ArrayList<>();
    Attack attack;
    User patient,user=null;
    Intent i=null;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public Button symptomDetailsButton;
        public TextView symptomDetails;

        public CustomViewHolder(View view) {
            super(view);
            symptomDetailsButton = (Button) view.findViewById(R.id.symptom_details_button);
            symptomDetails = (TextView) view.findViewById(R.id.symptom_details);
        }
    }

    public AttackAdapter(Attack attack,User patient,User user) {
        this.symptoms.add("Duration");
        this.symptoms.add("Symptoms");
        this.symptoms.add("Aura Symptoms");
        this.symptoms.add("Triggers");
        this.symptoms.add("Position");
        this.symptoms.add("Intensity");
        this.symptoms.add("Medication");
        if(attack.getDuration()!=null)
            this.symptomDetails.add(attack.getDuration().toString());
        if(attack.getSymptoms()!=null)
            this.symptomDetails.add(Arrays.toString(attack.getSymptoms()).replaceAll("[\\[\\]\"]", ""));
        if(attack.getAuraSymptoms()!=null)
            this.symptomDetails.add(Arrays.toString(attack.getAuraSymptoms()).replaceAll("[\\[\\]\"]", ""));
        if(attack.getTriggers()!=null)
            this.symptomDetails.add(Arrays.toString(attack.getTriggers()).replaceAll("[\\[\\]\"]", ""));
        if(attack.getPositions()!=null)
            this.symptomDetails.add(Arrays.toString(attack.getPositions()).replaceAll("[\\[\\]\"]", ""));
        if(attack.getIntensity()!=null)
            this.symptomDetails.add(attack.getIntensity());
        if(attack.getMedications()!=null)
            this.symptomDetails.add(Arrays.toString(attack.getMedications()).replaceAll("[\\[\\]\"]", ""));
        this.attack=attack;
        this.patient=patient;
        this.user=user;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attack_details, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        AppCompatActivity[] arr = new AppCompatActivity[7];
        final int pos = position;
        String symptom = symptoms.get(position);
        String symptomDetail = symptomDetails.get(position);
        holder.symptomDetailsButton.setText(symptom);
        holder.symptomDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (pos) {
                    case 0:
                        i=new  Intent(view.getContext(),DurationActivity.class);
                        break;
                    case 1:
                        i=new  Intent(view.getContext(),SymptomsActivity.class);
                        break;
                    case 2:
                        i=new  Intent(view.getContext(),AuraSymptomsActivity.class);
                        break;
                    case 3:
                        i=new  Intent(view.getContext(),TriggersActivity.class);
                        break;
                    case 4:
                        i=new  Intent(view.getContext(),PositionActivity.class);
                        break;
                    case 5:
                        i=new  Intent(view.getContext(),IntensityActivity.class);
                        break;
                    case 6:
                        i=new  Intent(view.getContext(),MedicationActivity.class);
                        break;
                }
                i.putExtra("attack",attack);
                i.putExtra("patient",patient);
                if(user==null)
                    view.getContext().startActivity(i);
            }
        });
        holder.symptomDetails.setText(symptomDetail);
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

}