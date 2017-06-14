package com.error404.memome.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
    private ListView memoListView;
    private ArrayList<Memo> memoList = new ArrayList<Memo>();
    private MemoAdapter mem;
    private static Context context;
    private SearchView searchView;
    private boolean doubleBackPressed;
    private static Handler handler = null;
    private static Runnable run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//apri il DB e inizializza GUI e tutti i listener
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_me_main);
        openDB();
        initializeGuiAndListener();
    }
    public void openDB(){//apri il DB
        dao = new DAO(this);
        dao.open();
        return;
    }
    public void initializeGuiAndListener(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context=getApplicationContext();
        openDB();
        setSupportActionBar(toolbar);
        memoList = dao.loadAllMemo();//memorizza tutte le memo in memoList
        FloatingActionButton buttonCreateMemo = (FloatingActionButton) findViewById(R.id.fab);
        buttonCreateMemo.setOnClickListener(new View.OnClickListener() {//sul click del bottone "crea memo" vai alla modify Activity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModifyOrAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Values.BUNDLE_KEY, Values.NO_ID);//inserisci nel bundle l'id della memo
                intent.putExtras(bundle);
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
        //inizializza listView
        mem = new MemoAdapter(this, R.layout.rawlayout, memoList);
        memoListView = (ListView) findViewById(R.id.listOfNotes);
        memoListView.setAdapter(mem);
        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//sul click di una nota vai alla showMemo caricando la nota cliccata
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                goToShowMemoActivity(memoList.get(position).getId());
            }
        });
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
        return;
    }
    public void goToShowMemoActivity(int id){//metodo per andare alla ShowMemo activity
        Memo m;
        m=dao.loadMemoById(id);//carica la memo
        if (m.getEncryption() == Values.TRUE) {//verifica se è cifrata
            alertEncrypted(id);
            return;
        }else {
                Intent myIntent = new Intent(MainActivity.this, ShowMemoActivity.class);
                Bundle bun = new Bundle();
                bun.putInt(Values.BUNDLE_KEY,id);//inserisci nel bundle l'id della memo e vai alla ShowMemo activity
                myIntent.putExtras(bun);
                startActivity(myIntent);
                return;
            }
        }

    public void deleteAllMemoNotEncrypted() {//metodo per cancellare tutte le note non cifrate
        dao.deleteAllMemoNotEncrypted();
    }
    public static Context getIstanceContext(){
        return context;
    }

    public void updateSortAndGUI(String type) {//metodo per aggiornare la GUI dell'activity
        dao.updateSort(type);//aggiorna il DB
        memoList = dao.loadAllMemo();//carica tutte le memo
        mem = new MemoAdapter(this, R.layout.rawlayout, memoList);//istanzaia memoAdapter e impostalo
        memoListView.setAdapter(mem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//apri la searchView
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memo_me_main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//imposta il comportamento del listener
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {//ogni volta che cambia il testo ricalcola la lista di memo filtrate
                final ArrayList<Memo> filteredMemos=getFilteredMemos(newText);
                mem= new MemoAdapter(MainActivity.this, R.layout.rawlayout,filteredMemos);
                memoListView.setAdapter(mem);
                memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//rimposta l'azione di default del listener
                        goToShowMemoActivity(filteredMemos.get(position).getId());
                    }
                });
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//metodo per gestire il drawer laterale
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();//memorizza l'opzione scelta
        if (id == R.id.sort_title) {//ordina per titolo, chiudi il drawer e aggiorna la GUI
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.TITLE);
            }
        } else if (id == R.id.sort_creation) {//ordinamento data creazione, chiudi il drawer e aggiorna la GUI
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.SORT_CREATION);
            }
        }else if(id==R.id.nav_favorites){
            Intent myIntent=new Intent(MainActivity.this,FavoriteMemoActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.sort_modify) {//ordinamento ultima modifica, chiudi il drawer e aggiorna la GUI
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.SORT_LAST_MODIFY);
            }
        } else if (id == R.id.sort_color) {//ordinamento colore, chiudi il drawer e aggiorna la GUI
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.COLOR);
            }
        } else if (id == R.id.nav_delete_all) {//elimina tutte le note non cifrate, chiudi il drawer e aggiorna la GUI
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                deleteAllNotEncryptedAlert();
            }
        } else if (id == R.id.sort_emoji) {//ordina emoji, chiudi il drawer e aggiorna la GUI
            if (dao != null) {
                drawer.closeDrawer(GravityCompat.START);
                updateSortAndGUI(Values.EMOJI);
            }
            //ordina per emoji
        } else if (id == R.id.nav_about){//apri alert about e chiudi il drawer
            drawer.closeDrawer(GravityCompat.START);
            alertAbout();
        }
        return true;
    }

    private void alertAbout(){//metodo per visualizzare l'alert about
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.infoTitle)
                .setMessage(R.string.infoText)
                .setIcon(R.mipmap.info_icon)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {//chiudi l'alert sul click del pulsante ok
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }


                        }
                )
                .show();
    }

    public void alertEncrypted(int id) {
        //alert dialog che gestisce la richiesta e controllo di password per l'accesso a note cifrate
        final int idMemo=id;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.password_layout,
                null, false);

        final EditText editPassword = (EditText) formElementsView
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
                    public void onClick(DialogInterface dialog, int id){}
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
            //onClick del pulsante di conferma (OK)
            @Override
            public void onClick(View v)
            {
                final LinearLayout wrongPassword = (LinearLayout) formElementsView
                        .findViewById(R.id.layoutWrongPassword);
                String decryptedFromDB = Encrypt.decryption(dao.loadMemoById(idMemo).getPassword(), editPassword.getText().toString());
                System.out.println(decryptedFromDB);
                if (editPassword.getText().toString().equals(decryptedFromDB) && !editPassword.getText().toString().equals(Values.EMPTY_STRING)) {
                    //se la password immessa è corretta, procedo verso la show memo
                    Intent myIntent = new Intent(MainActivity.this, ShowMemoActivity.class);
                    Bundle bun = new Bundle();
                    bun.putInt(Values.BUNDLE_KEY,idMemo);
                    bun.putString(Values.PASSWORD,editPassword.getText().toString());
                    myIntent.putExtras(bun);
                    startActivity(myIntent);
                    dialog.cancel();
                } else {
                    //password errata, quindi faccio apparire una scritta che avverte di tale errore, gestita con handler
                    //di modo che scompaia dopo ALERT_TIME_OUT secondi
                    wrongPassword.setVisibility(View.VISIBLE);
                    editPassword.setText(Values.EMPTY_STRING);
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

    public void deleteAllNotEncryptedAlert() {//metodo che gestisce l'alert per eliminare tutte le memo non cifrate
        //GUI alert (inizio)
        AlertDialog deleteNotEncryptedAlert = new AlertDialog.Builder(this)
                .setTitle(R.string.deleteAllNotEncoded)
                .setMessage(R.string.confirmDeleteAll)
                .setIcon(R.mipmap.delete_finale)
                //GUI alert(fine)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteAllMemoNotEncrypted();//elimina tutte le note non cifrate
                        updateSortAndGUI(Values.ONLYUPDATEGUI);//aggiorna la GUI
                        dialog.dismiss();//chiudi l'alert
                    }

                })


                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {//chiudi l'alert e non fare niente
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
    public ArrayList<Memo> getFilteredMemos(String newText){//metodo per ottenere una lista di memo filtrate che nel titolo contengono la stringa newText
        ArrayList<Memo> filteredMemos=new ArrayList<Memo>();//lista memo filtrate
        ArrayList<Memo> allMemo=dao.loadAllMemo();//lista di tutte le memo
        if(newText.equals(Values.EMPTY_STRING)){//se la stringa è vuota allora prendi tutte le note
            filteredMemos=allMemo;
            return filteredMemos;
        }
        for(int i=0;i<allMemo.size();i++){//cicla tutta lista di note e per ognuna verifica che il titolo contenga newText poi aggiungila alla lista memo filtrate
            if(allMemo.get(i).getTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredMemos.add(allMemo.get(i));
            }
        }
        return filteredMemos;
    }
    @Override
    public void onBackPressed() {//sul primo click del tasto indietro appare un toast "clicca ancora indietro per uscire"
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(searchView!=null && !searchView.isIconified()){//se la searchView è ancora aperta allora chiudila
            searchView.onActionViewCollapsed();
            memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//rimposta l'azione di default sul listener della listView
                    goToShowMemoActivity(memoList.get(position).getId());
                }
            });
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {//se il drawer è aperto chiudilo
            drawer.closeDrawer(GravityCompat.START);
        }
        else {//se in EXIT_TIMEOUT secondi si clicca sul tasto indietro 2 volte si chiude l'activity,altrimenti si rimane nell'activity
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
        updateSortAndGUI(Values.ONLYUPDATEGUI);//aggiorna GUI
        if(searchView!=null && !searchView.isIconified()){//se la searchView è ancora aperta allora chiudila
            searchView.onActionViewCollapsed();
            memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//rimposta l'azione di default sul listener della listView
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
