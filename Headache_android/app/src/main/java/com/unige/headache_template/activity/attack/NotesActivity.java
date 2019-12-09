package com.unige.headache_template.activity.attack;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.unige.headache_template.R;
import com.unige.headache_template.activity.login.LoginActivity;
import com.unige.headache_template.activity.patient.PatientListActivity;
import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.ManyUsersResponse;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesActivity extends AppCompatActivity {

    private Attack attack;
    private String notes_v;
    private static final String TAG = NotesActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.attack=(Attack) getIntent().getSerializableExtra("Current_attack");
        setContentView(R.layout.activity_notes);

        if(!attack.getNotes().equals("")) {
            EditText notes_o = (EditText) findViewById(R.id.text);
            notes_o.setText(attack.getNotes());
        }
    }

    public void saveNotesOnClick(View View){
        //save notes of attack
        EditText notes_o = (EditText) findViewById(R.id.text);
        this.notes_v= notes_o.getText().toString();

        if(!this.notes_v.equals("")){
            //this thread is for loading data
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.addnotes(attack.getAttack_id(),notes_v);
                        try {
                            call.execute();
                            Log.d(TAG, "attack's note are updated");
                        } catch (Exception e) {
                            Log.e(TAG, "Error in >>call.execute to save attack's note " + e);
                        }
                        notify();
                    }
                }
            };
            b.start();
        }

        EditText notes_n= (EditText) findViewById(R.id.text);
        attack.setNotes(notes_n.getText().toString());
        Intent i =new Intent(this,AttackDetailsActivity.class);
        i.putExtra("Current_attack",attack);
        i.putExtra("Current_user",new User());
        startActivity(i);
    }
}