package com.example.hw8;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewPager viewPager;
    private List<View> listViews;
    private ListView list;
    private ImageView[] titles;
    private ImageView tabalbum, tabpost;
    private int offset = 0;
    private int lineWidth;
    private int current_index = 0;
    private ImageView cursorIv;
    private String id;
    private String type;
    private static final int TAB_COUNT = 2;
    private static final int TAB_ALBUM= 0;
    private static final int TAB_POST = 1;
    private String name;
    private String picture;
    private MyPost myPost;
    private MyAlbum myAlbum;
    private ExpandableListView expandableListView;
    private SharedPreferences sp;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private boolean canPresentShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences("hw8", Context.MODE_PRIVATE);
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        String caller = intent.getStringExtra("caller");
        if (caller.equals("Display")) {
            id = intent.getStringExtra(DisplayResult.EXTRA_USERID);
            type = intent.getStringExtra(DisplayResult.EXTRA_TYPE);
        }else if (caller.equals("Main")){
            id = intent.getStringExtra(MainActivity.EXTRA_USERID);
            type = intent.getStringExtra(MainActivity.EXTRA_TYPE);
        }

        initUI();
        initImageView();
        initVPager();

        //register fb sdk
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, shareCallback);

        String tempUrl = "http://sample-env-1.zhxcb8ruey.us-east-1.elasticbeanstalk.com/?id=" + id + "&type=" + type;
        DetailActivity.Mytask my = new DetailActivity.Mytask();
        my.execute(tempUrl);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("Facebook", "Canceled");
            Toast.makeText(DetailActivity.this, "You canceled this post!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("Facebook", String.format("Error: %s",error.toString()));
            Toast.makeText(DetailActivity.this, "Error post!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Toast.makeText(DetailActivity.this, "You shared this post!", Toast.LENGTH_SHORT).show();
            Log.d("HelloFacebook", "Success!");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.detail_settings);
        if (sp.contains(id)){
            item.setTitle("Remove from Favorites");
        }else{
            item.setTitle("Add to Favorites");
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }
    private void setOverflowIconVisible(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.d("OverflowIconVisible", e.getMessage());
                }
            }
        }
    }


    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.detail_Pager);
        cursorIv = (ImageView) findViewById(R.id.iv_tab_bottom_img_detail);
        tabalbum = (ImageView) findViewById(R.id.tvalbum);
        tabpost = (ImageView) findViewById(R.id.tvpost);

        tabalbum.setOnClickListener(this);
        tabpost.setOnClickListener(this);
    }
    /**
     * 初始化底部下划线
     */
    private void initImageView() {
        // 获取图片宽度
        lineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.ic_orange_line_detail).getWidth();
        // Android提供的DisplayMetrics可以很方便的获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels; // 获取分辨率宽度
        offset = (screenW / TAB_COUNT - lineWidth) / 2;  // 计算偏移值
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        // 设置下划线初始位置
        cursorIv.setImageMatrix(matrix);
    }

    /**
     * 初始化ViewPager并添加监听事件
     */
    private void initVPager() {
        listViews = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.tab_album, null));
        listViews.add(mInflater.inflate(R.layout.tab_post, null));
        viewPager.setAdapter(new DetailActivity.MyPagerAdapter(listViews));
        viewPager.setCurrentItem(0);
        titles = new ImageView[]{tabalbum, tabpost};
        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = offset * 2 + lineWidth;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 下划线开始移动前的位置
                float fromX = one * current_index;
                // 下划线移动完毕后的位置
                float toX = one * position;
                Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(100);
                // 给图片添加动画
                cursorIv.startAnimation(animation);
                // 当前Tab的字体变成红色
                //titles[position].setTextColor(Color.BLACK);
                //titles[current_index].setTextColor(Color.BLACK);
                current_index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvalbum:
                // 避免重复加载
                if (viewPager.getCurrentItem() != TAB_ALBUM) {
                    drawAlbum();
                    viewPager.setCurrentItem(TAB_ALBUM);
                }
                break;
            case R.id.tvpost:
                if (viewPager.getCurrentItem() != TAB_POST) {
                    drawPost();
                    viewPager.setCurrentItem(TAB_POST);
                }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.detail_settings:
                if (sp.contains(id)){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove(id);
                    editor.commit();
                    item.setTitle("Add to Favorites");
                    Toast.makeText(DetailActivity.this, "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor = sp.edit();
                    String tempString = name + "###" + picture + "###" + type;
                    editor.putString(id, tempString);
                    editor.commit();
                    item.setTitle("Remove from Favorites");
                    Toast.makeText(DetailActivity.this, "Added to Favorites!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.detail_shares:
                Toast.makeText(DetailActivity.this, "sharing " + name, Toast.LENGTH_SHORT).show();
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(name)
                        .setContentDescription("FB SEARCH FROM USC CSCI571...")
                        .setImageUrl(Uri.parse(picture))
                        .setContentUrl(Uri.parse("http://cs-server.usc.edu:45678/"))
                        .build();
                shareDialog.show(linkContent);



                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class Mytask extends AsyncTask<String, Integer, String> {
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params){

            StringBuffer buffer = new StringBuffer();

            try{
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();
                    BufferedReader reader =  new BufferedReader(new InputStreamReader(is));
                    String s = "";
                    if ((s = reader.readLine()) != null){
                        buffer.append(s);
                    }
                    is.close();
                    reader.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String r){
            super.onPostExecute(r);
            try {

                JSONObject rawData = new JSONObject(r.substring(1, r.length() - 1));
                name = rawData.getString("name");
                picture = rawData.getJSONObject("picture").getJSONObject("data").getString("url");
                JSONObject post;
                if (rawData.has("posts")){
                    post = rawData.getJSONObject("posts");
                }else{
                    post = null;
                }

                myPost = new MyPost(name, picture, post);

                JSONObject album;
                if (rawData.has("albums")){
                    album = rawData.getJSONObject("albums");
                }else{
                    album = null;
                }
                myAlbum = new MyAlbum(album);
                drawAlbum();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void drawPost(){
        list = (ListView) findViewById(R.id.post_list);
        TextView temp = (TextView) findViewById(R.id.no_detail);
        if (this.myPost.list == null){
            temp.setText("No posts available to display");
            temp.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        PostAdapter adapter = new PostAdapter(DetailActivity.this, this.myPost.name, this.myPost.url, this.myPost.list);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
    }
    private void drawAlbum(){
        expandableListView = (ExpandableListView)findViewById(R.id.expendlist);
        TextView temp = (TextView) findViewById(R.id.no_detail);
        if (this.myAlbum.list == null){
            temp.setText("No albums available to display");
            temp.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        ExtendAdapter myAdapter = new ExtendAdapter(DetailActivity.this, myAlbum);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0, count = expandableListView.getExpandableListAdapter().getGroupCount(); i < count; i++) {
                    if (groupPosition != i) {// 关闭其他分组
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
        expandableListView.setAdapter(myAdapter);
        expandableListView.setVisibility(View.VISIBLE);

    }

}
