package com.example.hw8;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DisplayResult extends AppCompatActivity implements View.OnClickListener {
    private static MyData data;
    private ListView list;
    private List<View> listViews;
    private ImageView cursorIv;
    private ImageView tabuser, tabpage, tabevent, tabplace, tabgroup;
    private ImageView[] titles;
    private ViewPager viewPager;
    private int offset = 0;
    private int lineWidth;
    private int current_index = 0;
    private static final int TAB_COUNT = 5;
    private static final int TAB_USER= 0;
    private static final int TAB_PAGE = 1;
    private static final int TAB_EVNET = 2;
    private static final int TAB_PLACE = 3;
    private static final int TAB_GROUP = 4;
    public final static String EXTRA_USERID = "com.example.hw8.USERID";
    public final static String EXTRA_TYPE = "com.example.hw8.TYPE";
    private String preSearch = null;

    public DisplayResult() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        initUI();
        initImageView();
        initVPager();

        Intent intent = getIntent();
        String tempUrl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Mytask my = new Mytask();
        my.execute(tempUrl);
        /*
        try {
            JSONObject rawData = new JSONObject(message);
            this.data = new MyData(rawData);
            drawUser();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (preSearch == null)
            return;
        else if (preSearch.equals("user"))
            drawUser();
        else if (preSearch.equals("page"))
            drawPage();
        else if (preSearch.equals("event"))
            drawEvent();
        else if (preSearch.equals("place"))
            drawPlace();
        else if (preSearch.equals("group"))
            drawGroup();
    }

    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        cursorIv = (ImageView) findViewById(R.id.iv_tab_bottom_img);
        tabuser = (ImageView) findViewById(R.id.tvuser);
        tabpage = (ImageView) findViewById(R.id.tvpage);
        tabevent = (ImageView) findViewById(R.id.tvevent);
        tabplace = (ImageView) findViewById(R.id.tvplace);
        tabgroup = (ImageView) findViewById(R.id.tvgroup);

        tabuser.setOnClickListener(this);
        tabpage.setOnClickListener(this);
        tabevent.setOnClickListener(this);
        tabplace.setOnClickListener(this);
        tabgroup.setOnClickListener(this);

    }

    /**
     * 初始化底部下划线
     */
    private void initImageView() {
        // 获取图片宽度
        lineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.ic_orange_line).getWidth();
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
        listViews.add(mInflater.inflate(R.layout.tab_user, null));
        listViews.add(mInflater.inflate(R.layout.tab_page, null));
        listViews.add(mInflater.inflate(R.layout.tab_event, null));
        listViews.add(mInflater.inflate(R.layout.tab_place, null));
        listViews.add(mInflater.inflate(R.layout.tab_group, null));
        viewPager.setAdapter(new MyPagerAdapter(listViews));
        viewPager.setCurrentItem(0);
        titles = new ImageView[]{tabuser, tabpage, tabevent,tabplace,tabgroup};
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
            case R.id.tvuser:
                // 避免重复加载
                if (viewPager.getCurrentItem() != TAB_USER) {
                    drawUser();
                    viewPager.setCurrentItem(TAB_USER);
                }
                break;
            case R.id.tvpage:
                if (viewPager.getCurrentItem() != TAB_PAGE) {
                    drawPage();
                    viewPager.setCurrentItem(TAB_PAGE);
                }
                break;
            case R.id.tvevent:
                if (viewPager.getCurrentItem() != TAB_EVNET) {
                    drawEvent();
                    viewPager.setCurrentItem(TAB_EVNET);
                }
                break;
            case R.id.tvplace:
                if (viewPager.getCurrentItem() != TAB_PLACE) {
                    drawPlace();
                    viewPager.setCurrentItem(TAB_PLACE);
                }
                break;
            case R.id.tvgroup:
                if (viewPager.getCurrentItem() != TAB_GROUP) {
                    drawGroup();
                    viewPager.setCurrentItem(TAB_GROUP);
                }
                break;
        }
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
                data = new MyData(rawData);
                drawUser();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawUser(){
        list = (ListView) findViewById(R.id.user_list);
        TextView temp = (TextView) findViewById(R.id.no_result);
        if (this.data.user == null){
            temp.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        LazyAdapter adapter = new LazyAdapter(DisplayResult.this, this.data.user);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        //set previous and next
        Button pre = (Button) findViewById(R.id.button_pre);
        Button next = (Button) findViewById(R.id.button_next);
        if (this.data.userPrevious == null){
            pre.setEnabled(false);
        }else{
            pre.setEnabled(true);
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("user");
                    pt.execute(data.userPrevious);
                }
            });
        }
        if (this.data.userNext == null){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("user");
                    pt.execute(data.userNext);
                }
            });
        }
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(DisplayResult.this, DetailActivity.class);
                String id = data.user.get(arg2).id;
                intent.putExtra("caller", "Display");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "users");
                preSearch = "user";
                startActivity(intent);
            }
        });
    }
    private void drawPage(){
        list = (ListView) findViewById(R.id.page_list);
        TextView temp = (TextView) findViewById(R.id.no_result);
        if (this.data.page == null){
            temp.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        LazyAdapter adapter = new LazyAdapter(DisplayResult.this, this.data.page);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        //set previous and next
        Button pre = (Button) findViewById(R.id.button_pre);
        Button next = (Button) findViewById(R.id.button_next);
        if (this.data.pagePrevious == null){
            pre.setEnabled(false);
        }else{
            pre.setEnabled(true);
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("page");
                    pt.execute(data.pagePrevious);
                }
            });
        }
        if (this.data.pageNext == null){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("page");
                    pt.execute(data.pageNext);
                }
            });
        }
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(DisplayResult.this, DetailActivity.class);
                String id = data.page.get(arg2).id;
                intent.putExtra("caller", "Display");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "pages");
                preSearch = "page";
                startActivity(intent);
            }
        });
    }
    private void drawEvent(){
        list = (ListView) findViewById(R.id.event_list);
        TextView temp = (TextView) findViewById(R.id.no_result);
        if (this.data.event == null){
            temp.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        LazyAdapter adapter = new LazyAdapter(DisplayResult.this, this.data.event);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        //set previous and next
        Button pre = (Button) findViewById(R.id.button_pre);
        Button next = (Button) findViewById(R.id.button_next);
        if (this.data.eventPrevious == null){
            pre.setEnabled(false);
        }else{
            pre.setEnabled(true);
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("event");
                    pt.execute(data.eventPrevious);
                }
            });
        }
        if (this.data.eventNext == null){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("event");
                    pt.execute(data.eventNext);
                }
            });
        }
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(DisplayResult.this, DetailActivity.class);
                String id = data.event.get(arg2).id;
                intent.putExtra("caller", "Display");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "events");
                preSearch = "event";
                startActivity(intent);
            }
        });
    }
    private void drawPlace(){
        list = (ListView) findViewById(R.id.place_list);
        TextView temp = (TextView) findViewById(R.id.no_result);
        if (this.data.place == null){
            temp.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        LazyAdapter adapter = new LazyAdapter(DisplayResult.this, this.data.place);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        //set previous and next
        Button pre = (Button) findViewById(R.id.button_pre);
        Button next = (Button) findViewById(R.id.button_next);
        if (this.data.placePrevious == null){
            pre.setEnabled(false);
        }else{
            pre.setEnabled(true);
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("place");
                    pt.execute(data.placePrevious);
                }
            });
        }
        if (this.data.placeNext == null){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("place");
                    pt.execute(data.placeNext);
                }
            });
        }

        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(DisplayResult.this, DetailActivity.class);
                String id = data.place.get(arg2).id;
                intent.putExtra("caller", "Display");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "places");
                preSearch = "place";
                startActivity(intent);
            }
        });
    }
    private void drawGroup(){
        list = (ListView) findViewById(R.id.group_list);
        TextView temp = (TextView) findViewById(R.id.no_result);
        if (this.data.group == null){
            temp.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
            return;
        }
        temp.setVisibility(View.GONE);
        LazyAdapter adapter = new LazyAdapter(DisplayResult.this, this.data.group);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        //set previous and next
        Button pre = (Button) findViewById(R.id.button_pre);
        Button next = (Button) findViewById(R.id.button_next);
        if (this.data.groupPrevious == null){
            pre.setEnabled(false);
        }else{
            pre.setEnabled(true);
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("group");
                    pt.execute(data.groupPrevious);
                }
            });
        }
        if (this.data.groupNext == null){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagingTask pt = new PagingTask("group");
                    pt.execute(data.groupNext);
                }
            });
        }

        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(DisplayResult.this, DetailActivity.class);
                String id = data.group.get(arg2).id;
                intent.putExtra("caller", "Display");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "groups");
                preSearch = "group";
                startActivity(intent);
            }
        });
    }
    private class PagingTask extends AsyncTask<String, Integer, String> {
        private String type;
        public PagingTask(String t){
            type = t;
        }
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
                JSONObject rawData = new JSONObject(r);
                if (this.type.equals("user")) {
                    data.changeUser(rawData);
                    drawUser();
                }else if(this.type.equals("page")){
                    data.changePage(rawData);
                    drawPage();
                }else if(this.type.equals("event")){
                    data.changeEvent(rawData);
                    drawEvent();
                }else if(this.type.equals("place")){
                    data.changePlace(rawData);
                    drawPlace();
                }else if(this.type.equals("group")){
                    data.changeGroup(rawData);
                    drawGroup();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
