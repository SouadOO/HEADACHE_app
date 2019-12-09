package com.unige.headache_template.activity.attack;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.unige.headache_template.R;
import com.unige.headache_template.activity.patient.PatientCalendarActivity;
import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

import retrofit2.Call;

public class MedicationActivity extends AppCompatActivity {
    List<String> medications = new ArrayList<>();
    List<Integer> img = new ArrayList<>();
    private RecyclerView recyclerView;
    private SymptomAdapter sAdapter;
    boolean[] symptomClicked = new boolean[10];
    private static final String TAG = MedicationActivity.class.getName();
    String new_medications="[";
    User patient=null;
    Attack attack,new_attack=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        populateSymptoms();

        this.patient=(User) getIntent().getSerializableExtra("patient");
        this.attack=(Attack) getIntent().getSerializableExtra("attack");
        this.new_attack=(Attack) getIntent().getSerializableExtra("new_attack");



        if(new_attack!=null){
            Log.d(TAG, "-----New attack");
            sAdapter = new SymptomAdapter(medications,img);
        }else {
            sAdapter = new SymptomAdapter(medications,img,attack.getMedications());
            for(int i=0;i<attack.getMedications().length;i++){
                int p=medications.indexOf(attack.getMedications()[i]);
                symptomClicked[p]=true;
            }
        }

        //change title of the page
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(" Used Medications ?");

        recyclerView = (RecyclerView) findViewById(R.id.symptoms_recycler_viewer);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) sLayoutManager).getOrientation()));
        recyclerView.setAdapter(sAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String medication = medications.get(position);
                symptomClicked[position] = !symptomClicked[position];
                ImageView imageView = (ImageView) view.findViewById(R.id.symptomPicture);
                TextView textView = (TextView) view.findViewById(R.id.symptom);
                if (symptomClicked[position] ) {
                    imageView.setBackgroundColor(Color.rgb(200, 200, 200));
                    textView.setTextColor(Color.rgb(95,158,160));
                }else {
                    imageView.setBackgroundColor(Color.rgb(255,255,255));
                    textView.setTextColor(Color.rgb(8,8,8));
                }
                Toast.makeText(getApplicationContext(), medication, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                //Nothing
            }
        }));


        sAdapter.notifyDataSetChanged();

    }

    private void populateSymptoms() {
        medications.add("Pain killers");
        medications.add("Drugs");

        img.add(R.drawable.pain_killer);
        img.add(R.drawable.drugs);
    }

    public void saveOnClick(View v){

        List<String> my_medi=new ArrayList<>();

        for(int i=0;i<symptomClicked.length;i++){
            if(symptomClicked[i]){
                my_medi.add(medications.get(i));
            }
        }

        if(new_attack!=null){
            //create new attack and tap next
           new_attack.setMedications(my_medi.toArray(new String[my_medi.size()]));


            new_attack.setPatient_id(patient.getId());

            //save new attack

            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.saveAttack(new_attack.toString());
                        try {
                            call.execute();
                            Log.d(TAG, "Add new attack to database :"+new_attack);
                        } catch (Exception e) {
                            Log.e(TAG, "Error in >>call.execute to save new attack " + e);
                        }
                        notify();
                    } }
            };
            b.start();


            Intent i =new Intent(this, PatientCalendarActivity.class);
            i.putExtra("Current_patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
            this.attack.setMedications(my_medi.toArray(new String[my_medi.size()]));

            int j=0;
            for(j=0;j<my_medi.size()-1;j++){
                new_medications+="\""+my_medi.get(j)+"\",";
            }
            new_medications+="\""+my_medi.get(j)+"\"]";


            //save in database
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        //Call<UserResponse<User>> call = apiService.updateAttack(attack.getAttack_id(),new_medications,"medications");
                        try {
                            //call.execute();
                            Log.d(TAG, "attack's medications are updated in database :"+new_medications);
                        } catch (Exception e) {
                            Log.e(TAG, new_medications);
                            Log.e(TAG, "Error in >>call.execute to save attack's note " + e);
                        }
                        notify();
                    } }
            };
            b.start();

            Intent i =new Intent(this,AttackDetailsActivity.class);
            i.putExtra("Current_attack",this.attack);
            i.putExtra("Current_patient",this.patient);
            startActivity(i);
        }

    }



}

