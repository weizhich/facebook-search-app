package com.example.hw8;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by weizh on 2017/4/16.
 */

public class LazyAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<FBObject> data;
    private static LayoutInflater inflater = null;
    private SharedPreferences sp;

    public LazyAdapter(Activity a, ArrayList<FBObject> d){
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sp = a.getSharedPreferences("hw8", Context.MODE_PRIVATE);
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi  =convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);
        TextView title = (TextView)vi.findViewById(R.id.list_title); // 标题
        ImageView thumb_image = (ImageView)vi.findViewById(R.id.list_image); // 缩略图
        ImageView star = (ImageView)vi.findViewById(R.id.list_star);

        FBObject item = data.get(position);

        // 设置ListView的相关值
        title.setText(item.name);
        Glide.with(this.activity).load(item.url).into(thumb_image);
        if (sp.contains(item.id)){
            star.setImageResource(R.drawable.favorites_on);
        }else{
            star.setImageResource(R.mipmap.ic_star_border_black_24dp);
        }

        return vi;
    }
}
