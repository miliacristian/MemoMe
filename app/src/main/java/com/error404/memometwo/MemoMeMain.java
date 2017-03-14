package com.error404.memometwo;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MemoMeMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DAO dao;
    private ListView myListView;
    private ArrayList<Memo> memoAdapter = new ArrayList<Memo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_me_main);

        //Cambia il colore di sfondo, da mettere in show memo e modify memo

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dao=new DAO(this);
        dao.open();
        memoAdapter=dao.loadAllMemo();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MemoMeMain.this,activity_modifyOrAdd.class);
                Bundle b=new Bundle();
                b.putInt("key",-1);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MemoAdapter mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);//adapter deve essere un arraylist di memo
        myListView = (ListView) findViewById(R.id.listOfNotes);//id della list view nella prima activity
        myListView.setAdapter(mem);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent=new Intent(MemoMeMain.this,ShowMemo.class);
                Bundle bun=new Bundle();
                bun.putInt("key",position);
                myIntent.putExtras(bun);
                if(memoAdapter.get(position).getEncryption()==1){
                    //alert dialog che prende in input la password e la verifica
                    startActivity(myIntent);
                }
                else {
                    //vado alla nuova activity
                    startActivity(myIntent);
                }
            }
        });
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
        getMenuInflater().inflate(R.menu.memo_me_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {//ordina per titolo
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("title");
                finish();
                startActivity(getIntent());
            }
        } else if (id == R.id.nav_gallery) {//ordinamento data creazione
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("yeardatecreation,monthdatecreation,daydatecreation");
                finish();
                startActivity(getIntent());
            }

        } else if (id == R.id.nav_slideshow) {//ordinamento ultima modifica
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("yearlastmodify,monthlastmodify,daylastmodify");
                finish();
                startActivity(getIntent());
            }
        } else if (id == R.id.nav_manage) {//ordinamento colore
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("color");
                finish();
                startActivity(getIntent());
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

