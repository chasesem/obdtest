package zeolite.com.obd1;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;

import zeolite.com.obd1.adapter.TabFragmentPagerAdapter;
import zeolite.com.obd1.view.fragment.ConnectFragment;
import zeolite.com.obd1.view.fragment.DashBoardFragment;
import zeolite.com.obd1.view.fragment.MeFragment;

import zeolite.com.obd1.view.fragment.RecordFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ConnectFragment.OnHeadlineSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> listFragments;
    private Toolbar toolbar;

    DashBoardFragment dashBoardFragment;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AVAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, MainActivity.class);
        PushService.subscribe(this, "public", MainActivity.class);

        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });




         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Bluetooth connection status: normal", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
        initData();
    }


    private void initData() {

        listFragments = new ArrayList<Fragment>();
        ConnectFragment connectFragment= new ConnectFragment();
         dashBoardFragment=new DashBoardFragment();
        RecordFragment recordFragment=new RecordFragment();
        MeFragment meFragment=new MeFragment();
        listFragments.add(connectFragment);
        listFragments.add(dashBoardFragment);
        listFragments.add(recordFragment);
        listFragments.add(meFragment);


        TabFragmentPagerAdapter pagerAdapter=new TabFragmentPagerAdapter(getSupportFragmentManager(),listFragments,this);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }
        viewPager.setCurrentItem(1);
    }

    private void initView() {
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
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
        if (id == R.id.trouble_code) {
//            startActivity(new Intent(this, TroubleCodesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onArticleSelected(int speed, int rmp,  int gas, int water,int fuelPressure,int oilLevel,int temperature,String fault) {


        if (dashBoardFragment != null) {

            dashBoardFragment.updateResultView(speed,rmp,gas,water,fuelPressure,oilLevel,temperature, fault);
        } else {

        }

    }
}
