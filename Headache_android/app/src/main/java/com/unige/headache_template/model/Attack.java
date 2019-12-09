package com.unige.headache_template.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Attack  implements Serializable {


    @SerializedName("attack_id")
    private Integer attack_id;

    @SerializedName("status")
    private String status;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("intensity")
    private String intensity;

    @SerializedName("patient_id")
    private Integer patient_id;

    @SerializedName("started_at")
    private String started_at;

    @SerializedName("stopped_at")
    private String stopped_at;

    @SerializedName("notes")
    private String notes;

    @SerializedName("symptoms")
    @Expose
    private String[] symptoms = new String[10];

    @SerializedName("auraSymptoms")
    private String[] auraSymptoms = new String[10];

    @SerializedName("triggers")
    private String[] triggers = new String[10];

    @SerializedName("medications")
    private String[] medications = new String[10];

    @SerializedName("positions")
    private String[] positions = new String[10];

    public Attack() {
            }


    public Attack(Integer attack_id, String status, Integer duration, String intensity, Integer patient_id, String started_at, String stopped_at, String[] symptoms, String[] auraSymptoms, String[] triggers, String[] medications, String[] positions) {
        this.attack_id = attack_id;
        this.status = status;
        this.duration = duration;
        this.intensity = intensity;
        this.patient_id = patient_id;
        this.started_at = started_at;
        this.stopped_at = stopped_at;
        this.symptoms = symptoms;
        this.auraSymptoms = auraSymptoms;
        this.triggers = triggers;
        this.medications = medications;
        this.positions = positions;
    }

    public Integer getAttack_id() {
        return attack_id;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public Integer getPatient_id() {
        return patient_id;
    }

    public String getStarted_at() {
        return started_at;
    }

    public String getStopped_at() {
        return stopped_at;
    }

    public String[] getSymptoms() {
        return symptoms;
    }

    public String[] getAuraSymptoms() {
        return auraSymptoms;
    }

    public String[] getTriggers() {
        return triggers;
    }

    public String[] getMedications() {
        return medications;
    }

    public String[] getPositions() {
        return positions;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setAttack_id(Integer attack_id) {
        this.attack_id = attack_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public void setPatient_id(Integer patient_id) {
        this.patient_id = patient_id;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public void setStopped_at(String stopped_at) {
        this.stopped_at = stopped_at;
    }

    public void setSymptoms(String[] symptoms) {
        this.symptoms = symptoms;
    }

    public void setAuraSymptoms(String[] auraSymptoms) {
        this.auraSymptoms = auraSymptoms;
    }

    public void setTriggers(String[] triggers) {
        this.triggers = triggers;
    }

    public void setMedications(String[] medications) {
        this.medications = medications;
    }

    public void setPositions(String[] positions) {
        this.positions = positions;
    }

    public Date getStartedDate(){
        try{
            Date result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(this.started_at);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Date getStopedDate(){
        try{
            Date result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(this.stopped_at);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "  \"status\":\"" + status + '\"' +
                ", \"duration\":" + duration +
                ", \"intensity\":\"" + intensity + '\"' +
                ", \"patient_id\":" + patient_id +
                ", \"started_at\":\"" + started_at + '\"' +
                ", \"stopped_at\":\"" + stopped_at + '\"' +
                ", \"notes\":\"" + notes + '\"' +
                ", \"symptoms\":" + ArrayToString(symptoms) +
                ", \"auraSymptoms\":" + ArrayToString(auraSymptoms) +
                ", \"triggers\":" +ArrayToString(triggers) +
                ", \"medications\":" + ArrayToString(medications) +
                ", \"positions\":" + ArrayToString(positions) +
                '}';
    }

    public String ArrayToString(String [] array){

        String result="[";
        int j=0;

        for(j=0;j<array.length-1;j++){
            result+="\""+array[j]+"\",";
        }
        result+="\""+array[j]+"\"]";

        return result;
    }
}

