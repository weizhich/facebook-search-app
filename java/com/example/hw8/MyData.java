package com.example.hw8;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MyData{
    public ArrayList<FBObject> user;
    public ArrayList<FBObject> page;
    public ArrayList<FBObject> event;
    public ArrayList<FBObject> place;
    public ArrayList<FBObject> group;
    public String userNext;
    public String userPrevious;
    public String pageNext;
    public String pagePrevious;
    public String eventNext;
    public String eventPrevious;
    public String placeNext;
    public String placePrevious;
    public String groupNext;
    public String groupPrevious;


    public MyData(Activity a){
        user = new ArrayList<FBObject>();
        page = new ArrayList<FBObject>();
        event = new ArrayList<FBObject>();
        place = new ArrayList<FBObject>();
        group = new ArrayList<FBObject>();
        SharedPreferences sp = a.getSharedPreferences("hw8", Context.MODE_PRIVATE);
        Map<String, ?> content = sp.getAll();
        for(Map.Entry<String, ?>  entry : content.entrySet()){
            String id = entry.getKey();
            String str = (String) entry.getValue();
            String[] temp = str.split("###");
            String name = temp[0];
            String url = temp[1];
            String type = temp[2];
            if (type.equals("users"))
                user.add(new FBObject(id, name, url));
            else if (type.equals("pages"))
                page.add(new FBObject(id, name, url));
            else if (type.equals("events"))
                event.add(new FBObject(id, name, url));
            else if (type.equals("places"))
                place.add(new FBObject(id, name, url));
            else if (type.equals("groups"))
                group.add(new FBObject(id, name, url));
        }

    }
    public MyData(JSONObject data){
        //construct user data
        try {
            JSONObject userData = data.getJSONObject("user");
            JSONArray arr = userData.getJSONArray("data");
            if (arr.length() == 0){
                this.user = null;
                this.userNext = null;
                this.userPrevious = null;
            }else{
                this.user = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.user.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (userData.has("paging")){
                    JSONObject tempPaging = userData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.userPrevious = tempPaging.getString("previous");
                    }else{
                        this.userPrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.userNext = tempPaging.getString("next");
                    }else{
                        this.userNext = null;
                    }
                }else{
                    this.userNext = null;
                    this.userPrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //construct page data
        try {
            JSONObject pageData = data.getJSONObject("page");
            JSONArray arr = pageData.getJSONArray("data");
            if (arr.length() == 0){
                this.page = null;
                this.pageNext = null;
                this.pagePrevious = null;
            }else{
                this.page = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.page.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (pageData.has("paging")){
                    JSONObject tempPaging = pageData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.pagePrevious = tempPaging.getString("previous");
                    }else{
                        this.pagePrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.pageNext = tempPaging.getString("next");
                    }else{
                        this.pageNext = null;
                    }
                }else{
                    this.pageNext = null;
                    this.pagePrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //construct event data
        try {
            JSONObject eventData = data.getJSONObject("event");
            JSONArray arr = eventData.getJSONArray("data");
            if (arr.length() == 0){
                this.event = null;
                this.eventNext = null;
                this.eventPrevious = null;
            }else{
                this.event = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.event.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (eventData.has("paging")){
                    JSONObject tempPaging = eventData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.eventPrevious = tempPaging.getString("previous");
                    }else{
                        this.eventPrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.eventNext = tempPaging.getString("next");
                    }else{
                        this.eventNext = null;
                    }
                }else{
                    this.eventNext = null;
                    this.eventPrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //construct place data
        try {
            JSONObject placeData = data.getJSONObject("place");
            JSONArray arr = placeData.getJSONArray("data");
            if (arr.length() == 0){
                this.place = null;
                this.placeNext = null;
                this.placePrevious = null;
            }else{
                this.place = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.place.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (placeData.has("paging")){
                    JSONObject tempPaging = placeData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.placePrevious = tempPaging.getString("previous");
                    }else{
                        this.placePrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.placeNext = tempPaging.getString("next");
                    }else{
                        this.placeNext = null;
                    }
                }else{
                    this.placeNext = null;
                    this.placePrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //construct group data
        try {
            JSONObject groupData = data.getJSONObject("group");
            JSONArray arr = groupData.getJSONArray("data");
            if (arr.length() == 0){
                this.group = null;
                this.groupNext = null;
                this.groupPrevious = null;
            }else{
                this.group = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.group.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (groupData.has("paging")){
                    JSONObject tempPaging = groupData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.groupPrevious = tempPaging.getString("previous");
                    }else{
                        this.groupPrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.groupNext = tempPaging.getString("next");
                    }else{
                        this.groupNext = null;
                    }
                }else{
                    this.groupNext = null;
                    this.groupPrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void changeUser(JSONObject userData){
        try {
            JSONArray arr = userData.getJSONArray("data");
            if (arr.length() == 0){
                this.user = null;
                this.userNext = null;
                this.userPrevious = null;
            }else{
                this.user = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.user.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (userData.has("paging")){
                    JSONObject tempPaging = userData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.userPrevious = tempPaging.getString("previous");
                    }else{
                        this.userPrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.userNext = tempPaging.getString("next");
                    }else{
                        this.userNext = null;
                    }
                }else{
                    this.userNext = null;
                    this.userPrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void changePage(JSONObject pageData){
        //construct page data
        try {
            JSONArray arr = pageData.getJSONArray("data");
            if (arr.length() == 0){
                this.page = null;
                this.pageNext = null;
                this.pagePrevious = null;
            }else{
                this.page = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.page.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (pageData.has("paging")){
                    JSONObject tempPaging = pageData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.pagePrevious = tempPaging.getString("previous");
                    }else{
                        this.pagePrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.pageNext = tempPaging.getString("next");
                    }else{
                        this.pageNext = null;
                    }
                }else{
                    this.pageNext = null;
                    this.pagePrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void changeEvent(JSONObject eventData) {
        //construct event data
        try {
            JSONArray arr = eventData.getJSONArray("data");
            if (arr.length() == 0){
                this.event = null;
                this.eventNext = null;
                this.eventPrevious = null;
            }else{
                this.event = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.event.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (eventData.has("paging")){
                    JSONObject tempPaging = eventData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.eventPrevious = tempPaging.getString("previous");
                    }else{
                        this.eventPrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.eventNext = tempPaging.getString("next");
                    }else{
                        this.eventNext = null;
                    }
                }else{
                    this.eventNext = null;
                    this.eventPrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void changePlace(JSONObject placeData) {
        //construct place data
        try {
            JSONArray arr = placeData.getJSONArray("data");
            if (arr.length() == 0){
                this.place = null;
                this.placeNext = null;
                this.placePrevious = null;
            }else{
                this.place = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.place.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (placeData.has("paging")){
                    JSONObject tempPaging = placeData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.placePrevious = tempPaging.getString("previous");
                    }else{
                        this.placePrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.placeNext = tempPaging.getString("next");
                    }else{
                        this.placeNext = null;
                    }
                }else{
                    this.placeNext = null;
                    this.placePrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void changeGroup(JSONObject groupData) {
        //construct group data
        try {
            JSONArray arr = groupData.getJSONArray("data");
            if (arr.length() == 0){
                this.group = null;
                this.groupNext = null;
                this.groupPrevious = null;
            }else{
                this.group = new ArrayList<FBObject>();
                for (int i = 0; i < arr.length(); i++){
                    JSONObject tempData = arr.getJSONObject(i);
                    String tempId = tempData.getString("id");
                    String tempName = tempData.getString("name");
                    String tempUrl = tempData.getJSONObject("picture").getJSONObject("data").getString("url");
                    this.group.add(new FBObject(tempId, tempName, tempUrl));
                }
                if (groupData.has("paging")){
                    JSONObject tempPaging = groupData.getJSONObject("paging");
                    if (tempPaging.has("previous")){
                        this.groupPrevious = tempPaging.getString("previous");
                    }else{
                        this.groupPrevious = null;
                    }
                    if (tempPaging.has("next")){
                        this.groupNext = tempPaging.getString("next");
                    }else{
                        this.groupNext = null;
                    }
                }else{
                    this.groupNext = null;
                    this.groupPrevious = null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}