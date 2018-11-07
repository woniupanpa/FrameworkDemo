package com.example.admin.frameworkdemo.DrawLayout;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.admin.frameworkdemo.R;

/**
 * 侧拉菜单Demo
 * @author wangsong
 *
 */
public class DrawlayoutMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RelativeLayout rightLayout;
    private List<ContentModel> list;
    private ContentAdapter adapter;
    private ImageView leftMenu, rightMenu;
    private ListView listView;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawlayout_activity_main);
        getSupportActionBar().hide();

        leftMenu = (ImageView) findViewById(R.id.leftmenu);
        rightMenu = (ImageView) findViewById(R.id.rightmenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        rightLayout = (RelativeLayout) findViewById(R.id.right);
        listView = (ListView) findViewById(R.id.left_listview);
        fm = getSupportFragmentManager();

        initData();
        adapter = new ContentAdapter(this, list);
        listView.setAdapter(adapter);
        leftMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FragmentTransaction bt = fm.beginTransaction();
                switch ((int) id) {
                    case 1:
                        bt.replace(R.id.content, new NewsFragment());
                        break;
                    case 2:
                        bt.replace(R.id.content, new SubscriptionFragment());
                        break;

                    default:
                        break;
                }
                bt.commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        rightMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        rightLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
    }

    private void initData() {
        list = new ArrayList<ContentModel>();

        list.add(new ContentModel(R.drawable.doctoradvice2, "新闻", 1));
        list.add(new ContentModel(R.drawable.infusion_selected, "订阅", 2));
        list.add(new ContentModel(R.drawable.mypatient_selected, "图片", 3));
        list.add(new ContentModel(R.drawable.mywork_selected, "视频", 4));
        list.add(new ContentModel(R.drawable.nursingcareplan2, "跟帖", 5));
        list.add(new ContentModel(R.drawable.personal_selected, "投票", 6));
    }

}
