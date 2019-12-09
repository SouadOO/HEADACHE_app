package com.unige.headache_template.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    private Integer id;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("user_type")
    private String user_type;

    @SerializedName("name")
    private String name;

    @SerializedName("sex")
    private String sex;

    @SerializedName("age")
    private String age=null;

    @SerializedName("my_doctor_id")
    private String doctor_id=null;


    public User(){}

    //for Doctor user
    public User(Integer id, String name, String email){
        this.email=email;
        this.name=name;
        this.id=id;
    }


    //for doctor user
    public User(Integer id, String email, String password, String user_type, String name, String sex, String age, String doctor_id) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.user_type = user_type;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.doctor_id = doctor_id;
    }

    //getters
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getUser_type() {
        return user_type;
    }
    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSex() {
        return sex;
    }
    public String getAge() {
        return age;
    }
    public String getDoctor_id() {
        return doctor_id;
    }

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", user_type='" + user_type + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", doctor_id='" + doctor_id + '\'' +
                '}';
    }
}
