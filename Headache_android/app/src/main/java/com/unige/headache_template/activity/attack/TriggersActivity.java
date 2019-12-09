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

public class TriggersActivity extends AppCompatActivity {
    List<String> triggers = new ArrayList<>();
    List<Integer> img = new ArrayList<>();
    private RecyclerView recyclerView;
    private SymptomAdapter sAdapter;
    boolean[] symptomClicked = new boolean[10];
    private static final String TAG = TriggersActivity.class.getName();
    String new_triggers="[";
    User patient=null;
    Attack attack,new_attack=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        this.patient=(User) getIntent().getSerializableExtra("patient");
        this.attack=(Attack) getIntent().getSerializableExtra("attack");
        this.new_attack=(Attack) getIntent().getSerializableExtra("new_attack");

        populateSymptoms();

        if(new_attack!=null){
            Log.d(TAG, "-----New attack");
            sAdapter = new SymptomAdapter(triggers,img);
        }else{
            sAdapter = new SymptomAdapter(triggers,img,attack.getTriggers());
            for(int i=0;i<attack.getTriggers().length;i++){
                int p=triggers.indexOf(attack.getTriggers()[i]);
                symptomClicked[p]=true;
            }
        }

        //change title of the page
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(" Triggers ?");

        recyclerView = (RecyclerView) findViewById(R.id.symptoms_recycler_viewer);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) sLayoutManager).getOrientation()));
        recyclerView.setAdapter(sAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String symptom = triggers.get(position);
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
        triggers.add("Stress");
        triggers.add("Auditory");
        triggers.add("Fatigue");
        triggers.add("Fasting");
        triggers.add("Hormonal");
        triggers.add("Sleep");
        triggers.add("Weather");
        triggers.add("Visual");
        triggers.add("Olfactory");
        triggers.add("Alcohol");

        img.add(R.drawable.stress);
        img.add(R.drawable.auditory);
        img.add(R.drawable.fatigue);
        img.add(R.drawable.fasting);
        img.add(R.drawable.hormonal);
        img.add(R.drawable.sleep);
        img.add(R.drawable.weather);
        img.add(R.drawable.visual);
        img.add(R.drawable.olfactory);
        img.add(R.drawable.alcohol);
    }

    public void saveOnClick(View v){

        List<String> my_trigg=new ArrayList<>();

        for(int i=0;i<symptomClicked.length;i++){
            if(symptomClicked[i]){
                my_trigg.add(triggers.get(i));
            }
        }


        if(new_attack!=null){
            //create new attack and tap next
            new_attack.setTriggers(my_trigg.toArray(new String[my_trigg.size()]));


            Intent i =new Intent(this,PositionActivity.class);
            i.putExtra("new_attack",new_attack);
            i.putExtra("patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
            this.attack.setTriggers(my_trigg.toArray(new String[my_trigg.size()]));


            int j=0;
            for(j=0;j<my_trigg.size()-1;j++){
                new_triggers+="\""+my_trigg.get(j)+"\",";
            }
            new_triggers+="\""+my_trigg.get(j)+"\"]";

            //save in database
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.updateAttack(attack.getAttack_id(),new_triggers,"triggers");
                        try {
                            call.execute();
                            Log.d(TAG, "attack's triggers are updated in database: "+new_triggers);
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