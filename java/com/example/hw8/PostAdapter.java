package com.example.hw8;

import android.app.Activity;
import android.content.Context;
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

public class PostAdapter extends BaseAdapter {
    private Activity activity;
    private String name;
    private String picture;
    private ArrayList<PostObject> data;
    private static LayoutInflater inflater = null;

    public PostAdapter(Activity a, String n, String url, ArrayList<PostObject> d){
        activity = a;
        data = d;
        name = n;
        picture = url;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View vi  = convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.post_row, null);

        TextView title = (TextView)vi.findViewById(R.id.post_title); // 标题
        ImageView thumb_image = (ImageView)vi.findViewById(R.id.post_image); // 缩略图
        TextView post_time = (TextView)vi.findViewById(R.id.post_time);//时间
        TextView post_content = (TextView)vi.findViewById(R.id.post_content);//内容

        PostObject item = data.get(position);

        // 设置ListView的相关值
        title.setText(name);
        post_time.setText(item.time.substring(0, 10) + " " + item.time.substring(11, 19));
        post_content.setText(item.content);
        Glide.with(this.activity).load(this.picture).into(thumb_image);

        return vi;
    }
}
