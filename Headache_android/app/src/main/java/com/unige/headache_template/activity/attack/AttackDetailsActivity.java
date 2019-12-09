package com.unige.headache_template.activity.attack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import com.unige.headache_template.model.Attack;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;

import com.unige.headache_template.R;
import com.unige.headache_template.model.User;

public class AttackDetailsActivity extends AppCompatActivity {
    private static final String TAG = AttackDetailsActivity.class.getName();
    private Attack attack;
    private User patient,user=null;
    private String type="D";
    private RecyclerView recyclerView;
    private AttackAdapter aAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack_details);

        attack=(Attack) getIntent().getSerializableExtra("Current_attack");
        patient=(User) getIntent().getSerializableExtra("Current_patient");
        user=(User) getIntent().getSerializableExtra("Current_user");

        if(this.user==null) {
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        }else {
            findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }

        recyclerView = (RecyclerView) findViewById(R.id.attack_recycler_viewer);

        aAdapter = new AttackAdapter(attack,patient,user);
        RecyclerView.LayoutManager aLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(aLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), ((LinearLayoutManager) aLayoutManager).getOrientation()));
        recyclerView.setAdapter(aAdapter);


    }

    public void notesOnClick(View View){
        Intent i =new Intent(this,NotesActivity.class);
        i.putExtra("Current_attack",this.attack);
        startActivity(i);

    }
}