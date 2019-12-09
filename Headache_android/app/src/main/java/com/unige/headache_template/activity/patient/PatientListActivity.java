package com.unige.headache_template.activity.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.unige.headache_template.R;
import com.unige.headache_template.activity.attack.RecyclerTouchListener;
import com.unige.headache_template.activity.calendar.BasicActivity;
import com.unige.headache_template.activity.login.LoginActivity;
import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.ManyUsersResponse;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListActivity extends AppCompatActivity {


    private static final String TAG = PatientListActivity.class.getName();
    private RecyclerView recyclerView;
    private PatientListAdapter plAdapter;
    private User doctor=null;
    List<User> patients = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the doctors (current user) information
        this.doctor=(User) getIntent().getSerializableExtra("Current_User");
        Toast.makeText(PatientListActivity.this, "Hi "+this.doctor.getName(), Toast.LENGTH_LONG).show();

        populatePatientDetails();


        //Add patients list
        setContentView(R.layout.activity_patient_list);
        recyclerView = (RecyclerView) findViewById(R.id.patient_recycler_viewer);
        plAdapter = new PatientListAdapter(patients);
        RecyclerView.LayoutManager plLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(plLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) plLayoutManager).getOrientation()));
        recyclerView.setAdapter(plAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i =new Intent(PatientListActivity.this,PatientCalendarActivity.class);
                i.putExtra("Current_patient",patients.get(position));
                i.putExtra("Current_user",doctor);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
                Intent i =new Intent(PatientListActivity.this,PatientDetailsActivity.class);
                i.putExtra("patient",patients.get(position));
                startActivity(i);
            }
        }));

    }

    private void populatePatientDetails(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ManyUsersResponse<User>> call = apiService.getAllMyPatients(this.doctor.getId());
        call.enqueue(new Callback<ManyUsersResponse<User>>(){
            @Override
            public void onResponse(Call<ManyUsersResponse<User>>call, Response<ManyUsersResponse<User>> response) {

                if(response.isSuccessful()) {
                    User[] users = response.body().getUsers();
                    Boolean error= response.body().getError();
                    String msg= response.body().getMessage();
                    Log.d(TAG, "------------------------load patients "+msg);
                    setPatients(users);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PatientListActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d(TAG,e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<ManyUsersResponse<User>>call, Throwable t) {
                // Log error here since request failed
                Log.d(TAG, "----------"+t.toString());
                Toast.makeText(PatientListActivity.this, "Please try later !", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void setPatients(User[] users) {
        Log.d(TAG, "------------------------add patients to the list "+users.length);
        for(int i=0;i<users.length;i++){
            users[i].setUser_type("P");
            patients.add(users[i]);
            Log.d(TAG, "-----------"+i+"--------------"+patients.get(i).getName());
        }


    }



}
