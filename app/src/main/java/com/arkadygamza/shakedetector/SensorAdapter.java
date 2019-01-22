package com.arkadygamza.shakedetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder>  {
    private List<Sensor> sensors=new ArrayList<>();
    private LayoutInflater mIflater;
    private Context mContext;
    public SensorAdapter(Context context, List<Sensor> sensorList) {
    this.mContext=context;
    this.sensors=sensorList;
    this.mIflater=LayoutInflater.from(mContext);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view=mIflater.inflate(R.layout.adapter_item,viewGroup,false);
      view.setOnClickListener((View.OnClickListener) mContext);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    final Sensor sensor=sensors.get(i);
    String[] finalSensorName = sensors.get(i).getName().toString().split(" ");
    viewHolder.name.setText("Name: " + finalSensorName[1]);
    viewHolder.power.setText("Power: " + String.valueOf(sensor.getPower()));
    viewHolder.max.setText("Maximum: " + String.valueOf( sensor.getMaximumRange()));
    viewHolder.resolution.setText("Resolition: " + String.valueOf(sensor.getResolution()));
    viewHolder.mMinDelay.setText("mMinDelay: " +String.valueOf(sensor.getMinDelay()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewHolder.mMinDelay.setText("mMinDelay: " +String.valueOf(sensor.getFifoReservedEventCount()));
        }
    }
    @Override
    public int getItemCount() {
        return sensors.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements SensorEventListener {
        TextView name;
        TextView type;
        TextView vendor;
        TextView max;
        TextView resolution;
        TextView textViewX;
        TextView ed;
        TextView power;
        TextView mMinDelay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.name);
            //vendor=(TextView)itemView.findViewById(R.id.vendor);
            mMinDelay =(TextView) itemView.findViewById(R.id.mMinDelay);
            max=(TextView) itemView.findViewById(R.id.max);
            resolution=(TextView) itemView.findViewById(R.id.resolution);
            power=(TextView) itemView.findViewById(R.id.power);
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
