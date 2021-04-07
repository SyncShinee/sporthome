/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sporthome.demo.R;

public class recyclerAdapter extends RecyclerView.Adapter{
    @NonNull


    private DemoInfo[] demos;
    private Context context;

    public recyclerAdapter(Context context, DemoInfo[] demoinfo) {

        this.demos = demoinfo;
        this.context = context;
    }

    class myholder extends RecyclerView.ViewHolder{

        private ImageView [] imgsss = new ImageView[8];
        private TextView [] texsss = new TextView[8];
        public myholder(View itemView) {
            super(itemView);
            for (int index = 0; index < demos.length; index = index + 1) {
               imgsss[index] =  itemView.findViewById(R.id.iv);
               texsss[index] =  itemView.findViewById(R.id.content);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        myholder holder = new myholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_info_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((myholder)holder).imgsss[position].setImageResource(R.drawable.sport);
        ((myholder)holder).texsss[position].setText(demos[position].desc);

    }

    @Override
    public int getItemCount() {
        return demos.length;
    }
}
