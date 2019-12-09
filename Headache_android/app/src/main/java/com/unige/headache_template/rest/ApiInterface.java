package com.unige.headache_template.rest;

import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.ManyUsersResponse;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiInterface {
    @FormUrlEncoded
    @POST("user/login")
    Call<UserResponse<User>> checkAccess(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @PUT("user/update")
    Call<UserResponse<User>> changePassword (@Field("email") String email, @Field("password") String password);

    @GET("doctor/getAll")
    Call<ManyUsersResponse<User>> getAllDoctors ();

    @FormUrlEncoded
    @POST("user/register")
    Call<UserResponse<User>> AddUser(@Field("email") String email,@Field("password") String pass1,@Field("user_type") String type,@Field("name") String name,@Field("sex") String gender,@Field("age") Integer age ,@Field("my_doctor_id") Integer id);


    @GET("doctor/myPatients/{id}")
    Call<ManyUsersResponse<User>> getAllMyPatients (@Path("id") Integer id);

    @GET("patient/all_attacks/{id}")
    Call<ManyUsersResponse<Attack>> getAllPatientAttacks (@Path("id") int id);

    @FormUrlEncoded
    @PUT("patient/updateNote/{id}")
    Call<UserResponse<User>> addnotes (@Path("id") Integer id,
                                       @Field("note") String note);


    @FormUrlEncoded
    @PUT("patient/updateIntensity/{id}")
    Call<UserResponse<User>> updateAttackIntensity (@Path("id") Integer id,
                                                    @Field("intensity") String intensity);

    @FormUrlEncoded
    @PUT("patient/updateAttackDate/{id}")
    Call<UserResponse<User>> updateAttackDate (@Path("id") Integer id,
                                               @Field("status") String status,
                                               @Field("duration") Integer duration,
                                               @Field("started_at") String getStarted_at,
                                               @Field("stopped_at") String Stopped_at
    );

    @FormUrlEncoded
    @PUT("patient/updateAttack/{id}")
    Call<UserResponse<User>> updateAttack (@Path("id") Integer id,
                                           @Field("fields") String fields,
                                           @Field("field_name") String field_name
    );

    @FormUrlEncoded
    @PUT("patient/addAttack")
    Call<UserResponse<User>> saveAttack (@Field ("attack") String attack);
}