package com.error404.memome.Activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.error404.memome.Adapters.MemoAdapter;
import com.error404.memome.DB.DAO;
import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DAO dao;
    private ListView myListView;
    private ArrayList<Memo> memoList = new ArrayList<Memo>();
    private MemoAdapter mem;
    private static Context context;
    private SearchView searchView;
    private boolean doubleBackPressed;
    private static Handler handler = null;
    private static Runnable run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");
        setContentView(R.layout.activity_memo_me_main);
        openDB();
        initializeGuiAndListener();
    }
    public void openDB(){
        dao = new DAO(this);
        dao.open();
        return;
    }
    public void initializeGuiAndListener(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context=getApplicationContext();
        openDB();
        setSupportActionBar(toolbar);
        memoList = dao.loadAllMemo();
        FloatingActionButton buttonCreateMemo = (FloatingActionButton) findViewById(R.id.fab);
        buttonCreateMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModifyOrAddActivity.class);
                Bundle b = new Bundle();
                b.putInt(Values.BUNDLE_KEY, Values.NO_ID);
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
        mem = new MemoAdapter(this, R.layout.rawlayout, memoList);
        myListView = (ListView) findViewById(R.id.listOfNotes);
        myListView.setAdapter(mem);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                goToShowMemoActivity(memoList.get(position).getId());
            }
        });
        return;
    }
    public void goToShowMemoActivity(int id){
        Memo m;
        m=dao.loadMemoById(id);
        if (m.getEncryption() == Values.TRUE) {
            alertEncrypted(id);
            return;
        }else {
                Intent myIntent = new Intent(MainActivity.this, ShowMemoActivity.class);
                Bundle bun = new Bundle();
                bun.putInt(Values.BUNDLE_KEY,id);
                myIntent.putExtras(bun);
                startActivity(myIntent);
                return;
            }
        }

    public void deleteAllMemo() {
        dao.deleteAllMemoNotEncrypted();
    }
    public static Context getIstanceContext(){
        return context;
    }

    public void updateSortAndGUI(String type) {
        dao.updateSort(type);
        memoList = dao.loadAllMemo();
        mem = new MemoAdapter(this, R.layout.rawlayout, memoList);
        myListView.setAdapter(mem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memo_me_main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final ArrayList<Memo> filteredMemos=getFilteredMemos(newText);
                mem= new MemoAdapter(MainActivity.this, R.layout.rawlayout,filteredMemos);
                myListView.setAdapter(mem);
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        goToShowMemoActivity(filteredMemos.get(position).getId());
                    }
                });
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        if (id == R.id.sort_title) {//ordina per titolo
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.TITLE);
            }
        } else if (id == R.id.sort_creation) {//ordinamento data creazione
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.SORT_CREATION);
            }
        }else if(id==R.id.nav_favorites){
            Intent myIntent=new Intent(MainActivity.this,FavoriteMemoActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.sort_modify) {//ordinamento ultima modifica
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.SORT_LAST_MODIFY);
            }
        } else if (id == R.id.sort_color) {//ordinamento colore
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.COLOR);
            }
        } else if (id == R.id.nav_delete_all) {
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                deleteAllAlert();
            }
        } else if (id == R.id.sort_emoji) {
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.EMOJI);
            }
            //ordina per emoji
        } else if (id == R.id.nav_about){
            drawer.closeDrawer(GravityCompat.START);
            alertAbout();
        }
        return true;
    }

    private void alertAbout(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.infoTitle)
                .setMessage(R.string.infoText)
                .setIcon(R.mipmap.info_icon)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }


                        }
                )
                .show();
    }

    public void alertEncrypted(int id) {
        /*final int idMemo=id;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.password_layout,
                null, false);

        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);
        new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                .setTitle(R.string.warningMemoEncoded)
                .setMessage(R.string.warningMemoEncodedText)
                .setIcon(R.mipmap.lock_finale)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String decryptedFromDB = Encrypt.decryption(dao.loadMemoById(idMemo).getPassword(), nameEditText.getText().toString());
                                if (//cifratedPassword.equals(passFromDB
                                        nameEditText.getText().toString().equals(decryptedFromDB)) {
                                    Intent myIntent = new Intent(MainActivity.this, ShowMemoActivity.class);
                                    Bundle bun = new Bundle();
                                    bun.putInt(Values.BUNDLE_KEY,idMemo);
                                    bun.putString(Values.PASSWORD, nameEditText.getText().toString());
                                    myIntent.putExtras(bun);
                                    startActivity(myIntent);
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.incorrectPsw, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();*/
        final int idMemo=id;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.password_layout,
                null, false);

        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.warningMemoEncoded)
                .setMessage(R.string.warningMemoEncodedText)
                .setIcon(R.mipmap.lock_finale);
        builder.setView(formElementsView);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final LinearLayout wrongPassword = (LinearLayout) formElementsView
                        .findViewById(R.id.layoutWrongPassword);
                String decryptedFromDB = Encrypt.decryption(dao.loadMemoById(idMemo).getPassword(), nameEditText.getText().toString());
                System.out.println(decryptedFromDB);
                if (nameEditText.getText().toString().equals(decryptedFromDB) && !nameEditText.getText().toString().equals(Values.EMPTY_STRING)) {
                    Intent myIntent = new Intent(MainActivity.this, ShowMemoActivity.class);
                    Bundle bun = new Bundle();
                    bun.putInt(Values.BUNDLE_KEY,idMemo);
                    bun.putString(Values.PASSWORD, nameEditText.getText().toString());
                    myIntent.putExtras(bun);
                    startActivity(myIntent);
                    dialog.cancel();
                } else {
                    //Toast.makeText(MainActivity.this, R.string.incorrectPsw, Toast.LENGTH_SHORT).show();

                    wrongPassword.setVisibility(View.VISIBLE);
                    nameEditText.setText(Values.EMPTY_STRING);
                    if(handler != null){
                        handler.removeCallbacks(run);
                    }
                    handler = new Handler();
                    run = new Runnable() {

                        @Override
                        public void run() {
                            wrongPassword.setVisibility(View.GONE);
                            handler = null;
                        }
                    };
                    handler.postDelayed(run , Values.ALERT_TIME_OUT);
                }
                }

        });
    }
    public void deleteAllAlert() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle(R.string.deleteAllNotEncoded)
                .setMessage(R.string.confirmDeleteAll)
                .setIcon(R.mipmap.delete_finale)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteAllMemo();
                        updateSortAndGUI(Values.ONLYUPDATEGUI);
                        dialog.dismiss();
                    }

                })


                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
    public ArrayList<Memo> getFilteredMemos(String newText){
        ArrayList<Memo> filteredMemos=new ArrayList<Memo>();
        ArrayList<Memo> allMemo=dao.loadAllMemo();
        if(newText.equals(Values.EMPTY_STRING)){
            filteredMemos=allMemo;
            return filteredMemos;
        }
        for(int i=0;i<allMemo.size();i++){
            if(allMemo.get(i).getTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredMemos.add(allMemo.get(i));
            }
        }
        return filteredMemos;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(searchView!=null && !searchView.isIconified()){
            searchView.onActionViewCollapsed();
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    goToShowMemoActivity(memoList.get(position).getId());
                }
            });
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackPressed) {
                super.onBackPressed();
                return;
            }
            this.doubleBackPressed = true;
            Toast.makeText(context, R.string.back_again, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackPressed = false;
                }
            },Values.EXIT_TIMEOUT);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        updateSortAndGUI(Values.ONLYUPDATEGUI);
        if(searchView!=null && !searchView.isIconified()){
            searchView.onActionViewCollapsed();
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    goToShowMemoActivity(memoList.get(position).getId());
                }
            });
            return;
        }
    }
    @Override
    public void onDestroy() {//chiudi il database nel momento della distruzione dell'activity
        super.onDestroy();
        dao.close();

    }
}
