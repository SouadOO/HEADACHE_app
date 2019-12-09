package com.unige.headache_template.activity.calendar;


import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.unige.headache_template.R;
import com.unige.headache_template.activity.attack.AttackDetailsActivity;
import com.unige.headache_template.activity.patient.PatientListActivity;
import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.ManyUsersResponse;
import com.unige.headache_template.model.User;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;


public class BasicActivity extends BaseActivity {

    private User patient;
    private Attack[] attacks;
    private static final String TAG = PatientListActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.patient=(User) getIntent().getSerializableExtra("Current_patient");
        Toast.makeText(BasicActivity.this, "patient "+this.patient.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        List<WeekViewEvent> events = loadDateFromJson( newYear ,  newMonth);
        return events;
    }


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        Intent i =new Intent(this,AttackDetailsActivity.class);
        int position = (int)event.getId()-1;
        i.putExtra("Current_attack",attacks[position]);
        startActivity(i);
    }

    private List<WeekViewEvent> loadDateFromJson(int  newYear , int  newMonth){

        List<WeekViewEvent> events=new ArrayList<WeekViewEvent> ();

        //this thread is for loading data
        Thread b = new Thread() {
            @Override
            public void run() {
                synchronized(this){
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<ManyUsersResponse<Attack>> call = apiService.getAllPatientAttacks(patient.getId());
                    try {
                        attacks = call.execute().body().getUsers();
                        Log.d(TAG, "finnish loading data");
                    } catch (Exception e) {
                        Log.e(TAG, "Error in >>call.execute to load attacks " + e);
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
            Log.d(TAG, "finish loading data "+attacks.length+" attacks");

            Log.d(TAG, "Start reading data!");
            for (int i = 0; i < this.attacks.length; i++) {
                Date start_date = attacks[i].getStartedDate();
                Date stop_date = attacks[i].getStopedDate();

                if( (start_date.getMonth()==(newMonth-1)) && ((start_date.getYear()+1900)==newYear) ){
                    int hour_s=start_date.getHours();
                    int minute_s=start_date.getMinutes();
                    int hour_e=stop_date.getHours();
                    int minute_e=stop_date.getMinutes();

                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, hour_s);
                    startTime.set(Calendar.MINUTE, minute_s);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);

                    Log.d(TAG, "--------------------------"+hour_s+"--"+minute_s+"--"+hour_e+"--"+minute_e);

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR, Math.abs(hour_e-hour_s));
                    endTime.set(Calendar.MINUTE, minute_e);
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);
                }
            }
        }
        return  events;
    }
}