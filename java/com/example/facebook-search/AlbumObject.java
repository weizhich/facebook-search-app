package com.example.hw8;

/**
 * Created by weizh on 2017/4/20.
 */

public class AlbumObject {
    public String title;
    public String photo1;
    public String photo2;
    public AlbumObject(String t){
        title = t;
        photo1 = null;
        photo2 = null;
    }
    public AlbumObject(String t, String p1){
        title = t;
        photo1 = p1;
        photo2 = null;
    }
    public AlbumObject(String t,String p1, String p2){
        title = t;
        photo1 = p1;
        photo2 = p2;
    }
}
