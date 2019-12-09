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

public class IntensityActivity extends AppCompatActivity {
    List<String> intensities = new ArrayList<>();
    List<Integer> img = new ArrayList<>();
    private RecyclerView recyclerView;
    private SymptomAdapter sAdapter;
    boolean[] symptomClicked = new boolean[10];
    private static final String TAG = IntensityActivity.class.getName();
    User patient=null;
    Attack attack,new_attack=null;
    String my_intensity ;
    int old_value;

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
            sAdapter = new SymptomAdapter(intensities,img);
        }else {
            sAdapter = new SymptomAdapter(intensities,img,new String[]{attack.getIntensity()});
            old_value=intensities.indexOf(attack.getIntensity());
            symptomClicked[old_value]=true;
        }

        //change title of the page
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(" Intensity ?");

        recyclerView = (RecyclerView) findViewById(R.id.symptoms_recycler_viewer);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) sLayoutManager).getOrientation()));
        recyclerView.setAdapter(sAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String intensity = intensities.get(position);

                if(position!=old_value) {
                    recyclerView.findViewHolderForAdapterPosition(old_value).itemView.performClick();
                }

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


                Toast.makeText(getApplicationContext(), intensity+old_value+position, Toast.LENGTH_SHORT).show();
                old_value=position;
            }

            @Override
            public void onLongClick(View view, int position) {
                //Nothing
            }
        }));


        sAdapter.notifyDataSetChanged();

    }

    private void populateSymptoms() {
        intensities.add("Mild");
        intensities.add("Moderate");
        intensities.add("Intense");

        img.add(R.drawable.medium);
        img.add(R.drawable.low);
        img.add(R.drawable.high);

    }

    public void saveOnClick(View v){

        my_intensity=intensities.get(old_value);

        if(new_attack!=null){
            //create new attack and tap next
            new_attack.setIntensity(my_intensity);

            Intent i =new Intent(this,MedicationActivity.class);
            i.putExtra("new_attack",new_attack);
            i.putExtra("patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
            this.attack.setIntensity(my_intensity);

            //save in database
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.updateAttackIntensity(attack.getAttack_id(),my_intensity);
                        try {
                            call.execute();
                            Log.d(TAG, "----attack's intensity are updated in database :"+my_intensity);
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
