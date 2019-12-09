package com.unige.headache_template.activity.patient;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import com.unige.headache_template.R;
import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.ManyUsersResponse;
import com.unige.headache_template.model.User;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import retrofit2.Call;

public class PatientDetailsActivity extends AppCompatActivity {

    private User patient;
    private Attack lastAttack=null;
    private static final String TAG = PatientDetailsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        Intent intent = getIntent();

        TextView profileFullName = findViewById(R.id.profileFullName);
        TextView profileUserName = findViewById(R.id.profileDetails);

        this.patient= (User) intent.getSerializableExtra("patient");
        profileFullName.setText(patient.getName());



        Thread b = new Thread() {
            @Override
            public void run() {
                synchronized(this){
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<ManyUsersResponse<Attack>> call = apiService.getAllPatientAttacks(patient.getId());
                    try {
                        lastAttack = call.execute().body().getUsers()[0];
                        Log.d(TAG, "finnish loading data");
                    } catch (Exception e) {
                        Log.e(TAG, "Error in >>call.execute to load attacks " +e);
                    }
                    notify();
                }
            }
        };

        b.start();

        //this thread is for loading data into calendar
        synchronized(b){
            try{
                Log.d(TAG, "Waiting for loading data to complete...");
                b.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            if(this.lastAttack!=null){

                TextView date = findViewById(R.id.lastAttack);
                TextView duration = findViewById(R.id.duration);
                TextView intensity = findViewById(R.id.intensity);

                int h=lastAttack.getDuration()/60;
                int m=lastAttack.getDuration()%60;

                date.setText(lastAttack.getStarted_at());
                duration.setText(new String(h+"h"+m+"m"));
                intensity.setText(lastAttack.getIntensity());
            }
        }
        }

    public void onInspectDiaryClick(View View){
        Intent i =new Intent(this,PatientCalendarActivity.class);
        i.putExtra("Current_patient",this.patient);
        i.putExtra("user_type","D");
        startActivity(i);
    }
}