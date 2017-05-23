package com.error404.memome.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.error404.memome.Adapters.MemoAdapter;
import com.error404.memome.DB.DAO;
import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;

import java.util.ArrayList;
public class FavoriteMemoActivity extends AppCompatActivity {
    private DAO dao;
    private ListView memoListView;
    private ArrayList<Memo> memoList = new ArrayList<Memo>();
    private MemoAdapter mem;
    private static Handler handler = null;
    private static Runnable run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//al momento della creazione:
        // apri il database;
        // inizializza i listener;
        //  carica tutte le note preferite presenti nel DB;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_memo);
        openDB();
        initializeGuiAndListener();
    }
    public void initializeGuiAndListener(){
        //Inizializza le View e i relativi listener
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.favoriteActivityTitle);
        memoList = dao.loadAllFavoriteMemo();//carica note preferite
        mem = new MemoAdapter(this, R.layout.raw_layout_favorite, memoList);
        memoListView = (ListView) findViewById(R.id.listOfNotes);
        memoListView.setAdapter(mem);
        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //sul click di un elemento della lista vai alla ShowMemoActivity
                goToShowMemoActivity(memoList.get(position).getId());
            }
        });
    }
    public void openDB(){
        //Apre il Database
        dao = new DAO(this);
        dao.open();
        return;
    }
    public void goToShowMemoActivity(int id){
        //Se la nota è criptata, viene richiesta la password con un alert dialog, altrimenti va all'activity ShowMemo
       // mettendo nel bundle l'id'
        Memo m;
        m=dao.loadMemoById(id);
        if (m.getEncryption() == Values.TRUE) {
            alertEncrypted(id);
        }else {
            Intent myIntent = new Intent(FavoriteMemoActivity.this, ShowMemoActivity.class);
            Bundle bun = new Bundle();
            bun.putInt(Values.BUNDLE_KEY,id);
            myIntent.putExtras(bun);
            startActivity(myIntent);
        }
    }
    public void alertEncrypted(int id) {
        //AlertDialog che gestisce la richiesta di password per note cifrate, e in caso di password corretta, lancia l'activity ShowMemo
        final int idMemo=id;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.password_layout,
                null, false);

        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteMemoActivity.this);
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
                //al momento del click decifra la password e vedi se la password inserita è corretta
                final LinearLayout wrongPassword = (LinearLayout) formElementsView
                        .findViewById(R.id.layoutWrongPassword);
                String decryptedFromDB = Encrypt.decryption(dao.loadMemoById(idMemo).getPassword(), nameEditText.getText().toString());
                //decifra la password della memo con la password inserita nella EditText
                System.out.println(decryptedFromDB);
                if (nameEditText.getText().toString().equals(decryptedFromDB) && !nameEditText.getText().toString().equals(Values.EMPTY_STRING)) {
                    //Se la password decifrata è uguale alla password inserita allora passa all'activity ShowMemo,altrimenti rimani nell'activity
                    Intent myIntent = new Intent(FavoriteMemoActivity.this, ShowMemoActivity.class);
                    Bundle bun = new Bundle();
                    bun.putInt(Values.BUNDLE_KEY,idMemo);//inserisce dentro il bundle id Memo
                    bun.putString(Values.PASSWORD, nameEditText.getText().toString());//inserisce dentro il bundle la password
                    myIntent.putExtras(bun);
                    startActivity(myIntent);
                    dialog.cancel();
                } else {//passwsord errata
                    wrongPassword.setVisibility(View.VISIBLE);//imposta la visibilità del layout a "visible" e mostra un messaggio di errore
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
    @Override
    public void onResume(){//ricarica tutte le memo preferite.Questo metodo garantisce che nella visualizzazione delle memo le note siano sempre aggiornate
    //e quindi non ci siano discordanze tra memo visualizzate e valori nel database
        super.onResume();
        memoList = dao.loadAllFavoriteMemo();
        mem = new MemoAdapter(this, R.layout.raw_layout_favorite, memoList);
        memoListView.setAdapter(mem);
    }
    @Override
    public void onBackPressed() {//chiudi l'activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //ritorna all'activity precedente
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {//chiudi il database nel momento della distruzione dell'activity
        super.onDestroy();
        dao.close();

    }
}
