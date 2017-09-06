package com.example.hw8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Trace;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public final static String EXTRA_MESSAGE = "com.example.hw8.MESSAGE";
    private ListView list;
    private static MyData data;
    private ViewPager viewPager;
    private ImageView cursorIv;
    private List<View> listViews;
    private ImageView[] titles;
    private ImageView tabuser, tabpage, tabevent, tabplace, tabgroup;
    private int lineWidth;
    private int offset = 0;
    private int current_index = 0;
    private static final int TAB_COUNT = 5;
    private static final int TAB_USER= 0;
    private static final int TAB_PAGE = 1;
    private static final int TAB_EVNET = 2;
    private static final int TAB_PLACE = 3;
    private static final int TAB_GROUP = 4;
    private String preSearch = null;
    public final static String EXTRA_USERID = "com.example.hw8.USERID";
    public final static String EXTRA_TYPE = "com.example.hw8.TYPE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        data = new MyData(MainActivity.this);
        initUI();
        initImageView();
        initVPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawUser();
    }
    @Override
    protected void onResume(){
        super.onResume();
        data = new MyData(MainActivity.this);
        int tab = viewPager.getCurrentItem();
        if (tab == TAB_USER){
            drawUser();
        }else if (tab == TAB_PAGE){
            drawPage();
        }else if (tab == TAB_EVNET){
            drawEvent();
        }else if (tab == TAB_PLACE){
            drawPlace();
        }else if (tab == TAB_GROUP){
            drawGroup();
        }
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
        viewPager.setAdapter(new MainActivity.MyPagerAdapter(listViews));
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast toast = Toast.makeText(MainActivity.this, "这是Add to Favorites", Toast.LENGTH_SHORT);
            toast.show();
        }else if (id == R.id.action_shares){
            Toast toast = Toast.makeText(MainActivity.this, "这是Share", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            LinearLayout home = (LinearLayout)findViewById(R.id.search_layout);
            LinearLayout fav = (LinearLayout)findViewById(R.id.favorite_layout);
            home.setVisibility(View.VISIBLE);
            fav.setVisibility(View.GONE);
        } else if (id == R.id.nav_gallery) {
            LinearLayout home = (LinearLayout)findViewById(R.id.search_layout);
            LinearLayout fav = (LinearLayout)findViewById(R.id.favorite_layout);
            home.setVisibility(View.GONE);
            fav.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, AboutMe.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void searchFB(View view){
        EditText editText = (EditText) findViewById(R.id.main_text);
        String message = editText.getText().toString();
        if (message == null || message.equals("")){
            Toast toast = Toast.makeText(MainActivity.this, "Empty input", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String tempUrl = "http://sample-env-1.zhxcb8ruey.us-east-1.elasticbeanstalk.com/?value=" + message;
        Intent intent = new Intent(MainActivity.this, DisplayResult.class);
        intent.putExtra(EXTRA_MESSAGE, tempUrl);
        startActivity(intent);
        //Mytask my = new Mytask();
        //my.execute(tempUrl);

    }
    public void clearFB(View view){
        EditText editText = (EditText) findViewById(R.id.main_text);
        editText.setText("");
    }
    private void drawUser(){
        View v = listViews.get(0);
        list = (ListView)  v.findViewById(R.id.user_list);
        LazyAdapter adapter = new LazyAdapter(MainActivity.this, this.data.user);
        list.setAdapter(adapter);

        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String id = data.user.get(arg2).id;
                intent.putExtra("caller", "Main");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "users");
                preSearch = "user";
                startActivity(intent);
            }
        });
    }
    private void drawPage(){
        View v = listViews.get(1);
        list = (ListView)  v.findViewById(R.id.page_list);
        LazyAdapter adapter = new LazyAdapter(MainActivity.this, this.data.page);
        list.setAdapter(adapter);
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String id = data.page.get(arg2).id;
                intent.putExtra("caller", "Main");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "pages");
                preSearch = "page";
                startActivity(intent);
            }
        });
    }
    private void drawEvent(){
        View v = listViews.get(2);
        list = (ListView)  v.findViewById(R.id.event_list);
        LazyAdapter adapter = new LazyAdapter(MainActivity.this, this.data.event);
        list.setAdapter(adapter);
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String id = data.event.get(arg2).id;
                intent.putExtra("caller", "Main");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "events");
                preSearch = "event";
                startActivity(intent);
            }
        });
    }
    private void drawPlace(){
        View v = listViews.get(3);
        list = (ListView)  v.findViewById(R.id.place_list);
        LazyAdapter adapter = new LazyAdapter(MainActivity.this, this.data.place);
        list.setAdapter(adapter);
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String id = data.place.get(arg2).id;
                intent.putExtra("caller", "Main");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "places");
                preSearch = "place";
                startActivity(intent);
            }
        });
    }
    private void drawGroup(){
        View v = listViews.get(4);
        list = (ListView)  v.findViewById(R.id.group_list);
        LazyAdapter adapter = new LazyAdapter(MainActivity.this, this.data.group);
        list.setAdapter(adapter);
        //add click activity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String id = data.group.get(arg2).id;
                intent.putExtra("caller", "Main");
                intent.putExtra(EXTRA_USERID, id);
                intent.putExtra(EXTRA_TYPE, "groups");
                preSearch = "group";
                startActivity(intent);
            }
        });
    }

}
