package com.unige.headache_template.activity.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.unige.headache_template.R;
import com.unige.headache_template.activity.patient.PatientListActivity;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity  extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        changeStatusBarColor();
    }

    public void onChangeClick(final View view){

        //get information from form
        EditText email_o = (EditText) findViewById(R.id.editTextEmail);
        EditText password_n = (EditText) findViewById(R.id.editTextPassword);
        EditText password_n_r = (EditText) findViewById(R.id.editTextRepeatPassword);

        String email_v= email_o.getText().toString();
        String password_v=password_n.getText().toString();
        String password_v_r=password_n_r.getText().toString();

        if(password_v.equals(password_v_r) ){

            //check the the access in database
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserResponse<User>> call = apiService.changePassword(email_v,password_v);
            call.enqueue(new Callback<UserResponse<User>>(){
                @Override
                public void onResponse(Call<UserResponse<User>>call, Response<UserResponse<User>> response) {

                    if(response.isSuccessful()) {
                        User user = response.body().getUser();
                        Boolean error= response.body().getError();
                        String msg= response.body().getMessage();
                        Toast.makeText(ForgotActivity.this, msg, Toast.LENGTH_LONG).show();
                        if(!error)
                            onReturnClick(view);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(ForgotActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.d(TAG,e.getMessage());
                        }
                    }
                }
                @Override
                public void onFailure(Call<UserResponse<User>>call, Throwable t) {
                    // Log error here since request failed
                    Log.d(TAG, t.toString());
                    Toast.makeText(ForgotActivity.this, "message", Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Toast.makeText(ForgotActivity.this, "Passwords are not the same", Toast.LENGTH_LONG).show();
        }

    }

    public void onReturnClick(View view){
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }



}

