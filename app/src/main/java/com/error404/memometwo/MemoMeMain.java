package com.error404.memometwo;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MemoMeMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DAO dao;
    private ListView myListView;
    private ArrayList<Memo> memoAdapter = new ArrayList<Memo>();
    private MemoAdapter mem;
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
        FloatingActionButton buttonCreateMemo = (FloatingActionButton) findViewById(R.id.fab);
        buttonCreateMemo.setOnClickListener(new View.OnClickListener() {
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

        mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);//adapter deve essere un arraylist di memo
        myListView = (ListView) findViewById(R.id.listOfNotes);//id della list view nella prima activity
        myListView.setAdapter(mem);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent myIntent=new Intent(MemoMeMain.this,ShowMemo.class);
                Bundle bun=new Bundle();
                bun.putInt("key",position);
                myIntent.putExtras(bun);
                if(memoAdapter.get(position).getEncryption()==1){
                    //alert dialog che prende in input la password e la verifica
                    String password;
                    //Inizio alert
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View formElementsView = inflater.inflate(R.layout.password_layout,
                            null, false);

                    final EditText nameEditText = (EditText) formElementsView
                            .findViewById(R.id.nameEditText);

                    //alert dialog
                    new AlertDialog.Builder(MemoMeMain.this).setView(formElementsView)
                            .setTitle("Insert Password")
                            .setIcon(R.mipmap.lock_finale)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @TargetApi(11)
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Memo currentMemo = dao.loadMemoById(position);

                                            String cifratedPassword = "" +Encrypt.encryption(nameEditText.getText().toString(), nameEditText.getText().toString());
                                            System.out.println(cifratedPassword);
                                            System.out.println(memoAdapter.get(position).getPassword());
                                            String passFromDB = "" +memoAdapter.get(position).getPassword();
                                            if(cifratedPassword.equals(passFromDB)) {
                                                Intent myIntent=new Intent(MemoMeMain.this,ShowMemo.class);
                                                Bundle bun=new Bundle();
                                                bun.putInt("key",position);
                                                bun.putString("password", nameEditText.getText().toString());
                                                myIntent.putExtras(bun);
                                                startActivity(myIntent);
                                                dialog.cancel();
                                            }else{
                                                Toast.makeText(MemoMeMain.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                            }
                                            //showToast(toastString);


                                        }


                                    }
                            )
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @TargetApi(11)
                                public void onClick(DialogInterface dialog, int id) {
                                    //showToast(" is not awesome for you. :(");
                                    dialog.cancel();
                                }
                            })
                            .show();

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
                //finish();
                //startActivity(getIntent());
                memoAdapter=dao.loadAllMemo();
                mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
                myListView.setAdapter(mem);
            }
        } else if (id == R.id.nav_gallery) {//ordinamento data creazione
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("yeardatecreation,monthdatecreation,daydatecreation");
                memoAdapter=dao.loadAllMemo();
                mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
                myListView.setAdapter(mem);
                //finish();
                //startActivity(getIntent());
            }

        } else if (id == R.id.nav_slideshow) {//ordinamento ultima modifica
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("yearlastmodify,monthlastmodify,daylastmodify");
                memoAdapter=dao.loadAllMemo();
                mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
                myListView.setAdapter(mem);
                //finish();
                //startActivity(getIntent());
            }
        } else if (id == R.id.nav_manage) {//ordinamento colore
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("color");
                memoAdapter=dao.loadAllMemo();
                mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
                myListView.setAdapter(mem);
                //finish();
                //startActivity(getIntent());
            }
        }
        else if (id == R.id.nav_delete_all){
            if(dao!=null) {
                //dao.open(); è necessaria??
                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete")
                        .setIcon(R.mipmap.delete_finale)

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                deleteAllMemo();
                                dialog.dismiss();
                            }

                        })



                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .show();

                //finish();
                //startActivity(getIntent());
            }
            //Toast.makeText(this, "delete all", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_emoji){
            if(dao!=null) {
                //dao.open(); è necessaria??
                dao.updateSort("emoji");
                memoAdapter=dao.loadAllMemo();
                mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
                myListView.setAdapter(mem);
                //finish();
                //startActivity(getIntent());
            }
            //ordina per emoji
            //Toast.makeText(this, "delete all", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();
        memoAdapter=dao.loadAllMemo();
        mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
        myListView.setAdapter(mem);
    }

    public void deleteAllMemo(){
        dao.deleteAllMemoNotEncrypted();
        memoAdapter=dao.loadAllMemo();
        mem = new MemoAdapter(this, R.layout.rawlayout,memoAdapter);
        myListView.setAdapter(mem);
    }
}

