package com.unige.headache_template.activity.register;

import android.content.Intent;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.unige.headache_template.activity.login.LoginActivity;
import com.unige.headache_template.R;
import com.unige.headache_template.activity.patient.PatientListActivity;
import com.unige.headache_template.model.ManyUsersResponse;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private int [] doctors_id=new int [20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_register);


        //check the the access in database
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ManyUsersResponse<User>> call = apiService.getAllDoctors();
        call.enqueue(new Callback<ManyUsersResponse<User>>() {
            @Override
            public void onResponse(Call<ManyUsersResponse<User>> call, Response<ManyUsersResponse<User>> response) {
                if (response.isSuccessful()) {
                    User[] users = response.body().getUsers();
                    setSpinner(users);
                }
            }

            @Override
            public void onFailure(Call<ManyUsersResponse<User>> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(RegisterActivity.this, "Please try later", Toast.LENGTH_LONG).show();
            }
        });


    }


    public void onNextClick(View view) {

        EditText name_o = (EditText) findViewById(R.id.editTextName);
        EditText email_o = (EditText) findViewById(R.id.editTextEmail);
        EditText pass1_o = (EditText) findViewById(R.id.editTextPassword);
        EditText pass2_o = (EditText) findViewById(R.id.editTextPassword2);
        EditText age_o = (EditText) findViewById(R.id.editTextAge);
        Spinner  doctor_o=(Spinner)  findViewById(R.id.static_spinner);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGender);

        if (! age_o.getText().toString().matches("")){

            String name = name_o.getText().toString();
            String email = email_o.getText().toString();
            String pass1 = pass1_o.getText().toString();
            String pass2 = pass2_o.getText().toString();
            Integer age = Integer.parseInt(age_o.getText().toString());
            Integer id =doctors_id[doctor_o.getSelectedItemPosition()];
            int selectedId=radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) findViewById(selectedId);
            String gender=radioButton.getText().toString().substring(0,1);
            String type= new String("P");

            if (isEmailValid(email)) {
                if (pass1.equals(pass2)) {

                    //Toast.makeText(RegisterActivity.this, email+pass1+type+name+gender+age+id, Toast.LENGTH_LONG).show();

                    //save user into data
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<UserResponse<User>> call = apiService.AddUser(email,pass1,type,name,gender,age,id);
                    call.enqueue(new Callback<UserResponse<User>>(){
                        @Override
                        public void onResponse(Call<UserResponse<User>>call, Response<UserResponse<User>> response) {
                            if(response.isSuccessful()) {
                                String msg= response.body().getMessage();
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                            }else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(RegisterActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.d(TAG,e.getMessage());
                                    Toast.makeText(RegisterActivity.this, "catch !", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<UserResponse<User>>call, Throwable t) {
                            // Log error here since request failed
                            Log.d(TAG, t.toString());
                            Toast.makeText(RegisterActivity.this, "Please try later !", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Toast.makeText(this, "Passwords are not same", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Email not valid !!", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You did not enter an age", Toast.LENGTH_LONG).show();
        }
    }


    public void onReturnLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    protected void setSpinner(User[] users){
        //select all doctors in database
        ArrayList<String> spinnerArray = new ArrayList<String>();


        Log.d(TAG, "List of doctors-------------- : " + users);
        for (int i = 0; i < users.length; i++) {
            spinnerArray.add("Doctor " + users[i].getName());
            doctors_id[i]=users[i].getId();
            Log.d(TAG, "doctor --------------------  : " + users[i].getName());
        }

        Spinner spinner = findViewById(R.id.static_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);

    }


    protected static boolean isNull(Object obj) {
        return obj == null;
    }
}

