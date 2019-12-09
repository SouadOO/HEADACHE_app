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

public class AuraSymptomsActivity extends AppCompatActivity {
    List<String> auraSymptoms = new ArrayList<>();
    List<Integer> img = new ArrayList<>();
    private RecyclerView recyclerView;
    private SymptomAdapter sAdapter;
    boolean[] symptomClicked = new boolean[5];
    private static final String TAG = AuraSymptomsActivity.class.getName();
    String new_aurasymptoms="[";
    User patient=null;
    Attack attack,new_attack=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);




        populateSymptoms();

        this.patient=(User) getIntent().getSerializableExtra("patient");
        this.attack= (Attack) getIntent().getSerializableExtra("attack");
        this.new_attack= (Attack) getIntent().getSerializableExtra("new_attack");



        if(new_attack!=null){
            Log.d(TAG, "-----New attack");
            sAdapter = new SymptomAdapter(auraSymptoms,img);
        }else{
            sAdapter = new SymptomAdapter(auraSymptoms,img,attack.getAuraSymptoms());
            for(int i=0;i<attack.getAuraSymptoms().length;i++){
                int p=auraSymptoms.indexOf(attack.getAuraSymptoms()[i]);
                symptomClicked[p]=true;
            }
        }

        //change title of the page
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("Aura Symptoms !!");

        recyclerView = (RecyclerView) findViewById(R.id.symptoms_recycler_viewer);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) sLayoutManager).getOrientation()));
        recyclerView.setAdapter(sAdapter);



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String symptom = auraSymptoms.get(position);
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
        auraSymptoms.add("Visual distribution");
        auraSymptoms.add("Sensitive symptoms");
        auraSymptoms.add("Aphasia");
        auraSymptoms.add("Weakness");
        auraSymptoms.add("Tingling near eyes");

        img.add(R.drawable.visual);
        img.add(R.drawable.sensitive_symptoms);
        img.add(R.drawable.aphasia);
        img.add(R.drawable.weakness);
        img.add(R.drawable.tingling_eyes);
    }


    public void saveOnClick(View v){

        List<String> my_sym=new ArrayList<>();

        for(int i=0;i<symptomClicked.length;i++){
            if(symptomClicked[i]){
                my_sym.add(auraSymptoms.get(i));
            }
        }

        if(new_attack!=null){
            //create new attack and tap next
            new_attack.setAuraSymptoms(my_sym.toArray(new String[my_sym.size()]));



            Intent i =new Intent(this,TriggersActivity.class);
            i.putExtra("new_attack",new_attack);
            i.putExtra("patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
            this.attack.setAuraSymptoms(my_sym.toArray(new String[my_sym.size()]));

            int j=0;
            for(j=0;j<my_sym.size()-1;j++){
                new_aurasymptoms=new_aurasymptoms+"\""+my_sym.get(j)+"\",";
            }
            new_aurasymptoms=new_aurasymptoms+"\""+my_sym.get(j)+"\"]";


            //save in database
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.updateAttack(attack.getAttack_id(),new_aurasymptoms,"aura");
                        try {
                            call.execute();
                            Log.d(TAG, "attack's auraSymptoms are updated in database :"+new_aurasymptoms);
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