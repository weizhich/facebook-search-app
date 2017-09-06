package com.example.hw8;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by weizh on 2017/4/20.
 */

public class MyPost {
    public String name;
    public String url;
    public ArrayList<PostObject> list;
    public MyPost(String n, String u, JSONObject data) throws JSONException {
        name = n;
        url = u;
        if (data == null){
            list = null;
            return;
        }
        JSONArray arr = data.getJSONArray("data");
        if (arr.length() == 0){
            list = null;
        }else{
            list = new ArrayList<PostObject>();
            for (int i = 0; i < arr.length(); i++){
                JSONObject tempData = arr.getJSONObject(i);
                if (tempData.has("message")){
                    String tempMessage = tempData.getString("message");
                    String tempTime = tempData.getString("created_time");
                    list.add(new PostObject(tempTime, tempMessage));
                }
            }
        }
    }
}
