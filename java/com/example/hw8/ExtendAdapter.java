package com.example.hw8;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by weizh on 2017/4/20.
 */

class ExtendAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private static LayoutInflater inflater = null;
    private MyAlbum myAlbum;
    public ExtendAdapter(Activity a, MyAlbum al){
        activity = a;
        myAlbum = al;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getGroupCount(){
        return myAlbum.list.size();
    }
    @Override
    public int getChildrenCount(int groupPosition){
        AlbumObject temp = myAlbum.list.get(groupPosition);
        if (temp.photo1 == null){
            return 0;
        }else if(temp.photo2 == null){
            return 1;
        }else
            return 2;
    }
    @Override
    public Object getGroup(int groupPosition){
        return myAlbum.list.get(groupPosition);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition){
        AlbumObject temp = myAlbum.list.get(groupPosition);
        if (childPosition == 0){
            return temp.photo1;
        }else{
            return temp.photo2;
        }
    }
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }
    @Override
    public boolean hasStableIds()
    {
        return true;
    }@Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.expendlist_group, null);
        TextView tile = (TextView)convertView.findViewById(R.id.group_txt);
        tile.setText(myAlbum.list.get(groupPosition).title);
        return convertView;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        if(convertView == null)
            convertView = inflater.inflate(R.layout.expendlist_item, null);

        AlbumObject my = myAlbum.list.get(groupPosition);
        LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.picture_layout);
        ll.removeAllViews();
        if (childPosition == 0){
            ImageView tempImg = new ImageView(activity);
            Glide.with(this.activity).load(my.photo1).into(tempImg);
            ll.addView(tempImg);
        }else {
            ImageView tempImg = new ImageView(activity);
            Glide.with(this.activity).load(my.photo2).into(tempImg);
            ll.addView(tempImg);
        }
        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

}