package com.unige.headache_template.activity.attack;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.unige.headache_template.R;

import java.util.Arrays;
import java.util.List;

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.CustomViewHolder> {
    private List<String> symptoms;
    private List<Integer> img;
    private List<String> selected=null;
    private static final String TAG = SymptomAdapter.class.getName();


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView symptom;
        public ImageView symptoms_img;

        public CustomViewHolder(View view) {
            super(view);
            symptom = (TextView) view.findViewById(R.id.symptom);
            symptoms_img = (ImageView) view.findViewById(R.id.symptomPicture);
        }
    }

    public SymptomAdapter(List<String> symptoms,List<Integer> img) {
        this.symptoms = symptoms;
        this.img=img;
    }

    public SymptomAdapter(List<String> symptoms,List<Integer> img,String[] selected ){
        this.symptoms = symptoms;
        this.img=img;
        if (selected.length>0)
            this.selected=Arrays.asList(selected);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.symptoms, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        String symptom = symptoms.get(position);
        holder.symptom.setText(symptom);
        holder.symptoms_img.setImageResource(img.get(position));
        Log.d(TAG, "--------------------In SymptomsAdapter"+symptom+this.selected);

        if(this.selected!=null) {
            if (this.selected.contains(symptom)) {
                holder.symptom.setTextColor(Color.rgb(128, 0, 0));
                holder.symptoms_img.setBackgroundColor(Color.rgb(200, 200, 200));
            }
        }
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

}