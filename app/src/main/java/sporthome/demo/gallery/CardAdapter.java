/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sporthome.demo.R;
import sporthome.demo.sportgym.GymActivity;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Integer> imgList = new ArrayList<>();
    private List<String> strList = new ArrayList<>();
    private List<String> tpyList = new ArrayList<>();
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private Context mContext;
    private List<String> disList;

    public CardAdapter(Context mContext, List<Integer> mList, List<String> sList, List<String> tList, List<String> disList) {
        this.imgList = mList;
        this.strList = sList;
        this.tpyList = tList;
        this.mContext = mContext;
        this.disList = disList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_info_item, parent, false);
        mCardAdapterHelper.onCreateViewHolder(parent, itemView);
        final ViewHolder holder = new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position= holder.getAdapterPosition();
                Intent intent = new Intent(mContext, GymActivity.class);
                intent.putExtra("id",strList.get(position));
                intent.putExtra("lat", 39.963175);
                intent.putExtra("lng", 116.400244);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        holder.mImageView.setImageResource(imgList.get(position));
        holder.mTextView.setText(strList.get(position));
        holder.mType.setText(tpyList.get(position));
        holder.mLoc.setText(disList.get(position));
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public final TextView mTextView;
        public final TextView mType;
        public final TextView mLoc;

        public ViewHolder(final View itemView) {
            //TODO
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv);
            mTextView = (TextView) itemView.findViewById(R.id.content);
            mType = (TextView) itemView.findViewById(R.id.types);
            mLoc = (TextView) itemView.findViewById(R.id.mLoc);
        }

    }
}
