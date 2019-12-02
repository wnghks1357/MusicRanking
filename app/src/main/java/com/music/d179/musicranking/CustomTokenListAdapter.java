package com.music.d179.musicranking;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomTokenListAdapter extends BaseAdapter {

    private ArrayList<String> rankingList = new ArrayList<String>();
    private ArrayList<Bitmap> imgList = new ArrayList<Bitmap>();

    public CustomTokenListAdapter(ArrayList<String> rankingList, ArrayList<Bitmap> imgList){
        this.rankingList = rankingList;
        this.imgList = imgList;

    }

    @Override
    public int getCount() {
        return rankingList.size();
    }

    @Override
    public String getItem(int position) {
        return rankingList.get(position);
    }

    public Bitmap getImage(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if( convertView == null) {
            convertView = View.inflate(context, R.layout.view_custom_item, null);
        }

        String rank = getItem(position);
        Bitmap logoImg = getImage(position);

        TextView rankTv = convertView.findViewById(R.id.rankTv);
        ImageView logoIv= convertView.findViewById(R.id.logoIv);


        rankTv.setText(rank);
        logoIv.setImageBitmap(logoImg);

        return convertView;
    }

    public void addItem(String rank, Bitmap img){
        rankingList.add(rank);
        imgList.add(img);
    }
}
