package com.unige.headache_template.activity.attack;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.unige.headache_template.R;
import com.unige.headache_template.model.Attack;
import com.unige.headache_template.model.User;
import com.unige.headache_template.model.UserResponse;
import com.unige.headache_template.rest.ApiClient;
import com.unige.headache_template.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SymptomsActivity extends AppCompatActivity {
    List<String> symptoms = new ArrayList<>();
    List<Integer> img = new ArrayList<>();

    private static final String TAG = SymptomsActivity.class.getName();
    private RecyclerView recyclerView;
    private SymptomAdapter sAdapter;
    boolean[] symptomClicked = new boolean[10];
    String new_symptoms="[";
    User patient=null;
    Attack attack,new_attack=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        populateSymptoms();

        this.patient=(User) getIntent().getSerializableExtra("patient");
        this.attack=(Attack) getIntent().getSerializableExtra("attack");
        this.new_attack=(Attack) getIntent().getSerializableExtra("new_attack");


        if(new_attack!=null){
            Log.d(TAG, "-----New attack");
            sAdapter = new SymptomAdapter(symptoms,img);
        }else{
            sAdapter = new SymptomAdapter(symptoms,img,attack.getSymptoms());
            for(int i=0;i<attack.getSymptoms().length;i++){
                int p=symptoms.indexOf(attack.getSymptoms()[i]);
                Log.d(TAG, "-----selected attack"+p+attack.getSymptoms()[i]);
                symptomClicked[p]=true;
            }
        }

        //change title of the page
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(" Symptoms ?");

        recyclerView = (RecyclerView) findViewById(R.id.symptoms_recycler_viewer);

        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) sLayoutManager).getOrientation()));
        recyclerView.setAdapter(sAdapter);



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String symptom = symptoms.get(position);
                symptomClicked[position] = !symptomClicked[position];
                ImageView imageView = (ImageView) view.findViewById(R.id.symptomPicture);
                TextView textView = (TextView) view.findViewById(R.id.symptom);
                if (symptomClicked[position] ) {
                    imageView.setBackgroundColor(Color.rgb(200, 200, 200));
                    textView.setTextColor(Color.rgb(95,158,160));
                }else {
                    imageView.setBackgroundColor(Color.rgb(255,255,255));
                    textView.setTextColor(Color.rgb(8,8,8));
                }
                Toast.makeText(getApplicationContext(), symptom, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                //Nothing
            }
        }));


        sAdapter.notifyDataSetChanged();
    }

    private void populateSymptoms() {
        symptoms.add("Nausea");
        symptoms.add("Photophobia");
        symptoms.add("Phonophobia");
        symptoms.add("Anxiety");
        symptoms.add("Mood alterations");
        symptoms.add("Fatigue");
        symptoms.add("Vomiting");
        symptoms.add("Throbbing pain");
        symptoms.add("Pulsating pain");
        symptoms.add("Pounding pain");

        img.add(R.drawable.nausea);
        img.add(R.drawable.photophobia);
        img.add(R.drawable.phonophobia);
        img.add(R.drawable.anxiety);
        img.add(R.drawable.mood_alternations);
        img.add(R.drawable.fatigue);
        img.add(R.drawable.vomiting);
        img.add(R.drawable.throbbing_pain);
        img.add(R.drawable.pulsating_pain);
        img.add(R.drawable.pounding_pain);
    }


    public void saveOnClick(View v){

        List<String> my_sym=new ArrayList<>();

        for(int i=0;i<symptomClicked.length;i++){
            if(symptomClicked[i]){
                my_sym.add(symptoms.get(i));
            }
        }

        if(new_attack!=null){
            //create new attack and tap next
            new_attack.setSymptoms(my_sym.toArray(new String[my_sym.size()]));

            Intent i =new Intent(this,AuraSymptomsActivity.class);
            i.putExtra("new_attack",new_attack);
            i.putExtra("patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
            this.attack.setSymptoms(my_sym.toArray(new String[my_sym.size()]));

            int j=0;
            for(j=0;j<my_sym.size()-1;j++){
                new_symptoms=new_symptoms+"\""+my_sym.get(j)+"\",";
            }
            new_symptoms=new_symptoms+"\""+my_sym.get(j)+"\"]";

            //save in database
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.updateAttack(attack.getAttack_id(),new_symptoms,"symptoms");
                        try {
                            call.execute();
                            Log.d(TAG, "-------------attack's Symptoms are updated in database"+new_symptoms);
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

}