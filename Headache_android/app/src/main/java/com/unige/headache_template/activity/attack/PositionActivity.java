package com.unige.headache_template.activity.attack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class PositionActivity extends AppCompatActivity {

    boolean[] positionClicked = new boolean[12];
    ImageView frontTR,frontTL,frontMR,frontML,frontBR,frontBL,backTR,backTL,backMR,backML,backBR,backBL;
    List<String>  positions= Arrays.asList("frontTR","frontMR","frontBR","frontTL","frontML","frontBL","backTR","backMR","backBR","backTL","backML","backBL");
    List<int[]> images = new ArrayList<int[]>();

    private static final String TAG = PositionActivity.class.getName();
    User patient=null;
    Attack attack,new_attack=null;
    String new_positions="[";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        this.patient=(User) getIntent().getSerializableExtra("patient");
        this.attack=(Attack) getIntent().getSerializableExtra("attack");
        this.new_attack=(Attack) getIntent().getSerializableExtra("new_attack");

        populateImage();

        //change title of the page
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(" Position of pain ?");



        frontTR = (ImageView) findViewById(R.id.frontTopRight);
        frontMR = (ImageView) findViewById(R.id.frontMiddleRight);
        frontBR = (ImageView) findViewById(R.id.frontBottomRight);
        frontTL = (ImageView) findViewById(R.id.frontTopLeft);
        frontML = (ImageView) findViewById(R.id.frontMiddleLeft);
        frontBL = (ImageView) findViewById(R.id.frontBottomLeft);

        backTR = (ImageView) findViewById(R.id.backTopRight);
        backMR = (ImageView) findViewById(R.id.backMiddleRight);
        backBR = (ImageView) findViewById(R.id.backBottomRight);
        backTL = (ImageView) findViewById(R.id.backTopLeft);
        backML = (ImageView) findViewById(R.id.backMiddleLeft);
        backBL = (ImageView) findViewById(R.id.backBottomLeft);


        frontTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionClicked[0] = !positionClicked[0];
                frontTR.setImageResource(positionClicked[0] ? images.get(0)[0] : images.get(0)[1]);
            }
        });
        frontMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked MR",
                        Toast.LENGTH_LONG).show();
                positionClicked[1] = !positionClicked[1];
                frontMR.setImageResource(positionClicked[1] ?  images.get(1)[0] : images.get(1)[1]);
            }
        });
        frontBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked BR",
                        Toast.LENGTH_LONG).show();
                positionClicked[2] = !positionClicked[2];
                frontBR.setImageResource(positionClicked[2] ?  images.get(2)[0] : images.get(2)[1]);
            }
        });

        frontTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked TL",
                        Toast.LENGTH_LONG).show();
                positionClicked[3] = !positionClicked[3];
                frontTL.setImageResource(positionClicked[3] ?  images.get(3)[0] : images.get(3)[1]);
            }
        });
        frontML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked ML",
                        Toast.LENGTH_LONG).show();
                positionClicked[4] = !positionClicked[4];
                frontML.setImageResource(positionClicked[4] ?  images.get(4)[0] : images.get(4)[1]);
            }
        });
        frontBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked BL",
                        Toast.LENGTH_LONG).show();
                positionClicked[5] = !positionClicked[5];
                frontBL.setImageResource(positionClicked[5] ?  images.get(5)[0] : images.get(5)[1]);
            }
        });

        backTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked TR",
                        Toast.LENGTH_LONG).show();
                positionClicked[6] = !positionClicked[6];
                backTR.setImageResource(positionClicked[6] ? images.get(6)[0] : images.get(6)[1]);
            }
        });
        backMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked MR",
                        Toast.LENGTH_LONG).show();
                positionClicked[7] = !positionClicked[7];
                backMR.setImageResource(positionClicked[7] ?  images.get(7)[0] : images.get(7)[1]);
            }
        });
        backBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked BR",
                        Toast.LENGTH_LONG).show();
                positionClicked[8] = !positionClicked[8];
                backBR.setImageResource(positionClicked[8] ?  images.get(8)[0] : images.get(8)[1]);
            }
        });

        backTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked TL",
                        Toast.LENGTH_LONG).show();
                positionClicked[9] = !positionClicked[9];
                backTL.setImageResource(positionClicked[9] ?  images.get(9)[0] : images.get(9)[1]);
            }
        });
        backML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked ML",
                        Toast.LENGTH_LONG).show();
                positionClicked[10] = !positionClicked[10];
                backML.setImageResource(positionClicked[10] ?   images.get(10)[0] : images.get(10)[1]);
            }
        });
        backBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PositionActivity.this,
                        "You clicked BL",
                        Toast.LENGTH_LONG).show();
                positionClicked[11] = !positionClicked[11];
                backBL.setImageResource(positionClicked[11] ?  images.get(11)[0] : images.get(11)[1]);
            }
        });

        if(new_attack==null){
            Log.d(TAG, "-----Update attack Positions :");
            for(int i=0;i<attack.getPositions().length;i++){
                if(positions.contains(attack.getPositions()[i])) {
                    int position = positions.indexOf(attack.getPositions()[i]);
                    positionClicked[position] = true;
                    Log.d(TAG, "-----Update attack Positions :"+position+"--"+attack.getPositions()[i]);
                    switch (attack.getPositions()[i]){
                        case "frontTR":
                            frontTR.setImageResource(positionClicked[0] ? images.get(0)[0] : images.get(position)[1]);
                            break;
                        case "frontMR":
                            frontMR.setImageResource(positionClicked[1] ? images.get(1)[0] : images.get(position)[1]);
                            break;
                        case "frontBR":
                            frontBR.setImageResource(positionClicked[2] ? images.get(2)[0] : images.get(position)[1]);
                            break;
                        case "frontTL":
                            frontTL.setImageResource(positionClicked[3] ? images.get(3)[0] : images.get(position)[1]);
                            break;
                        case "frontML":
                            frontML.setImageResource(positionClicked[4] ? images.get(4)[0] : images.get(position)[1]);
                            break;
                        case "frontBL":
                            frontBL.setImageResource(positionClicked[5] ? images.get(5)[0] : images.get(position)[1]);
                            break;
                        case "backTR":
                            backTR.setImageResource(positionClicked[6] ? images.get(6)[0] : images.get(position)[1]);
                            break;
                        case "backMR":
                            backMR.setImageResource(positionClicked[7] ? images.get(7)[0] : images.get(position)[1]);
                            break;
                        case "backBR":
                            backBR.setImageResource(positionClicked[8] ? images.get(8)[0] : images.get(position)[1]);
                            break;
                        case "backTL":
                            backTL.setImageResource(positionClicked[9] ? images.get(9)[0] : images.get(position)[1]);
                            break;
                        case "backML":
                            backML.setImageResource(positionClicked[10] ? images.get(10)[0] : images.get(position)[1]);
                            break;
                        case "backBL":
                            backBL.setImageResource(positionClicked[11] ? images.get(11)[0] : images.get(position)[1]);
                            break;
                    }
                }
            }
        }

    }

    public void saveOnClick(View v){

        List<String> my_position=new ArrayList<>();

        for(int i=0;i<positionClicked.length;i++){
            if(positionClicked[i]){
                my_position.add(this.positions.get(i));
            }
        }

        if(new_attack!=null){
            //create new attack and tap next
            new_attack.setPositions(my_position.toArray(new String[my_position.size()]));

            Intent i =new Intent(this,IntensityActivity.class);
            i.putExtra("new_attack",new_attack);
            i.putExtra("patient",this.patient);
            startActivity(i);

        }else{
            //save changes and previous
            this.attack.setPositions(my_position.toArray(new String[my_position.size()]));

            int j=0;
            for(j=0;j<my_position.size()-1;j++){
                new_positions+="\""+my_position.get(j)+"\",";
            }
            new_positions+="\""+my_position.get(j)+"\"]";

            //save in database
            Thread b = new Thread() {
                @Override
                public void run() {
                    synchronized(this){
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<UserResponse<User>> call = apiService.updateAttack(attack.getAttack_id(),new_positions,"positions");
                        try {
                            call.execute();
                            Log.d(TAG, "attack's positions are updated in database :"+new_positions);
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

    private void populateImage(){
        int [] ftr={R.drawable.ftron,R.drawable.ftroff};
        int [] fmr={R.drawable.fmron,R.drawable.fmroff};
        int [] fbr={R.drawable.fbron,R.drawable.fbroff};
        int [] ftl={R.drawable.ftlon,R.drawable.ftloff};
        int [] fml={R.drawable.fmlon,R.drawable.fmloff};
        int [] fbl={R.drawable.fblon,R.drawable.fbloff};

        images.add(ftr);
        images.add(fmr);
        images.add(fbr);
        images.add(ftl);
        images.add(fml);
        images.add(fbl);

        int [] btr={R.drawable.btron,R.drawable.btroff};
        int [] bmr={R.drawable.bmron,R.drawable.bmroff};
        int [] bbr={R.drawable.bbron,R.drawable.bbroff};
        int [] btl={R.drawable.btlon,R.drawable.btloff};
        int [] bml={R.drawable.bmlon,R.drawable.bmloff};
        int [] bbl={R.drawable.bblon,R.drawable.bbloff};

        images.add(btr);
        images.add(bmr);
        images.add(bbr);
        images.add(btl);
        images.add(bml);
        images.add(bbl);
    }

}
