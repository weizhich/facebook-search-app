package com.example.hw8;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by weizh on 2017/4/20.
 */

public class MyAlbum {
    public ArrayList<AlbumObject> list;
    public MyAlbum(JSONObject data) throws JSONException {
        if (data == null){
            list = null;
            return;
        }
        JSONArray arr = data.getJSONArray("data");
        if (arr.length() == 0){
         list = null;
        }else{
            list = new ArrayList<AlbumObject>();
            for (int i = 0; i < arr.length(); i++){
                JSONObject tempData = arr.getJSONObject(i);
                String name = tempData.getString("name");
                if (!tempData.has("photos")){
                    list.add(new AlbumObject(name));
                }else{
                    JSONArray pArr = tempData.getJSONObject("photos").getJSONArray("data");
                    if (pArr.length() == 0){
                        list.add(new AlbumObject(name));
                    }else if(pArr.length() == 1){
                        list.add(new AlbumObject(name, pArr.getJSONObject(0).getString("picture")));
                    }else{
                        list.add(new AlbumObject(name, pArr.getJSONObject(0).getString("picture"), pArr.getJSONObject(1).getString("picture")));
                    }
                }
            }
        }
    }
}
