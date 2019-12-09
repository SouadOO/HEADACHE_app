package com.unige.headache_template.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.unige.headache_template.R;
import com.unige.headache_template.activity.patient.PatientCalendarActivity;
import com.unige.headache_template.activity.patient.PatientListActivity;
import com.unige.headache_template.activity.register.RegisterActivity;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
    }


    public void onLoginClick(View View){

        //get information from form
            EditText email_o = (EditText) findViewById(R.id.editTextEmail);
            EditText password_o = (EditText) findViewById(R.id.editTextPassword);
            String email_v= email_o.getText().toString();
            String password_v= password_o.getText().toString();

            if(isEmailValid(email_v)){

                //check the the access in database
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<UserResponse<User>> call = apiService.checkAccess(email_v,password_v);
                call.enqueue(new Callback<UserResponse<User>>(){
                    @Override
                    public void onResponse(Call<UserResponse<User>>call, Response<UserResponse<User>> response) {

                        if(response.isSuccessful()) {
                            User user = response.body().getUser();
                            Boolean error= response.body().getError();
                            String msg= response.body().getMessage();
                            if(user.getUser_type().equals("P")){
                                //Log.d(TAG, "-------You are a Patient "+user.getName()+"=--"+user.getAge());
                                Intent i =new Intent(LoginActivity.this, PatientCalendarActivity.class);
                                i.putExtra("Current_patient",user);
                                startActivity(i);
                            }else if (user.getUser_type().equals("D")){
                                //Log.d(TAG, "-------You are a doctor "+user.getName()+"=--"+user.getAge());
                                Intent i =new Intent(LoginActivity.this,PatientListActivity.class);
                                i.putExtra("Current_User",user);
                                startActivity(i);
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(LoginActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.d(TAG,e.getMessage());
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<UserResponse<User>>call, Throwable t) {
                        // Log error here since request failed
                        Log.d(TAG, t.toString());
                        Toast.makeText(LoginActivity.this, "Please try later !", Toast.LENGTH_LONG).show();
                    }
                });

            }else{
                Toast.makeText(LoginActivity.this, "Email not Valid !!!", Toast.LENGTH_LONG).show();
            }

    }


    public void onRegisterClick(View View){
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }


    public void onForgotClick(View View){
        startActivity(new Intent(this, ForgotActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}


