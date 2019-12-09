package com.unige.headache_template.activity.attack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import com.unige.headache_template.model.Attack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.unige.headache_template.R;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class DurationActivity extends AppCompatActivity implements
        View.OnClickListener {

    Button btnDatePicker_s, btnTimePicker_s,btnDatePicker_e, btnTimePicker_e;
    private static final String TAG = DurationActivity.class.getName();
    EditText txtDate_s, txtTime_s,txtDate_e, txtTime_e;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private User patient=null;
    private Attack attack,new_attack=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration);

        this.patient=(User) getIntent().getSerializableExtra("patient");
        this.attack=(Attack) getIntent().getSerializableExtra("attack");
        this.new_attack=(Attack) getIntent().getSerializableExtra("new_attack");

        btnDatePicker_s=findViewById(R.id.btn_date_start);
        btnTimePicker_s=findViewById(R.id.btn_time_start);
        txtDate_s=findViewById(R.id.in_date_start);
        txtTime_s=findViewById(R.id.in_time_start);

        btnDatePicker_s.setOnClickListener(this);
        btnTimePicker_s.setOnClickListener(this);

        btnDatePicker_e=findViewById(R.id.btn_date_end);
        btnTimePicker_e=findViewById(R.id.btn_time_end);
        txtDate_e=findViewById(R.id.in_date_end);
        txtTime_e=findViewById(R.id.in_time_end);

        btnDatePicker_e.setOnClickListener(this);
        btnTimePicker_e.setOnClickListener(this);

        Date my_date;

        if(new_attack!=null){
            Log.d(TAG, "-----New attack");
            my_date=new Date();
        }else {
            Log.d(TAG, "-----update attack");
            my_date = attack.getStartedDate();
        }
        //select data of this attack
        int h=my_date.getHours();
        int m=my_date.getMinutes();
        int M=my_date.getMonth()+1;
        int D=my_date.getDate();
        int Y=my_date.getYear()+1900;
        Log.d(TAG, "-----start attack"+D+"-"+M+"-"+Y+" "+h+":"+m);

        TextView start_time = findViewById(R.id.in_time_start);
        TextView start_date = findViewById(R.id.in_date_start);

        start_time.setText(h+":"+m);
        start_date.setText(D+"-"+M+"-"+Y);

        h=my_date.getHours();
        m=my_date.getMinutes();
        M=my_date.getMonth()+1;
        D=my_date.getDate();
        Y=my_date.getYear()+1900;

        Log.d(TAG, "-----stop attack"+D+"-"+M+"-"+Y+" "+h+":"+m);

        TextView end_time = findViewById(R.id.in_time_end);
        TextView end_date = findViewById(R.id.in_date_end);

        end_time.setText(h+":"+m);
        end_date.setText(D+"-"+M+"-"+Y);
    }

    @Override
    public void onClick(View v) {
        final View test = v;

        if (test == btnDatePicker_s || test == btnDatePicker_e) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            if (test == btnDatePicker_s) {
                                txtDate_s.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                            if (test == btnDatePicker_e) {
                                txtDate_e.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker_s || v == btnTimePicker_e) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (test == btnTimePicker_s) txtTime_s.setText(String.format("%02d:%02d", hourOfDay, minute));
                            if (test == btnTimePicker_e) txtTime_e.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    public void saveOnClick(View v){

        //start date
        TextView start_time_o = findViewById(R.id.in_time_start);
        TextView start_date_o = findViewById(R.id.in_date_start);
        String start=start_date_o.getText().toString()+" "+start_time_o.getText().toString()+":00";

        String new_start_date=parseDate("dd-MM-yyyy HH:mm:ss","yyyy-MM-dd HH:mm:ss",start);

        //end date
        TextView end_time_o = findViewById(R.id.in_time_end);
        TextView end_date_o = findViewById(R.id.in_date_end);
        String end=end_date_o.getText().toString()+" "+end_time_o.getText().toString()+":00";

        String new_end_date=parseDate("dd-MM-yyyy HH:mm:ss","yyyy-MM-dd HH:mm:ss",end);

        long duration = difference(new_end_date,new_start_date);

        Log.d(TAG, "-----Duration of attack  :"+duration);

        if(new_attack!=null){
            //create new attack and tap next
            new_attack.setStarted_at(new_start_date);
            new_attack.setStopped_at(new_end_date);
            new_attack.setDuration((int)duration);
            new_attack.setStatus("Stopped");

            Intent i =new Intent(this,SymptomsActivity.class);
            i.putExtra("new_attack",new_attack);
            i.putExtra("patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
           this.attack.setStarted_at(new_start_date);
           this.attack.setStopped_at(new_end_date);
           this.attack.setDuration((int)duration);
           this.attack.setStatus("Stopped");

            //save in database
            Thread b = new Thread() {
                    @Override
                    public void run() {
                        synchronized(this){
                            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                            Call<UserResponse<User>> call = apiService.updateAttackDate(attack.getAttack_id(),attack.getStatus(),attack.getDuration(),attack.getStarted_at(),attack.getStopped_at());
                            try {
                                call.execute();
                                Log.d(TAG, "---- attack's date are updated in database"+attack.getStarted_at());
                            } catch (Exception e) {
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

    private String parseDate(String OLD_FORMAT,String NEW_FORMAT, String date){

        try{
            String oldDateString=date;

            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            String newDateString= sdf.format(d);

            return newDateString;

        }catch(Exception e){
            Log.d(TAG, "---------------------Parsing error :"+e);
            return null;
        }
    }

    private long difference(String end , String start){

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date s = sdf.parse(start);
            Date e = sdf.parse(end);

            long diffInMillies = Math.abs(e.getTime() - s.getTime());
            long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return diff;

        }catch (Exception e){
            Log.d(TAG, "---- ---------------------------------Error in parsing date :"+e);
            return -1;
        }

    }
}