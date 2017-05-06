package com.error404.memome.Activities;

import android.annotation.TargetApi;
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
import android.widget.Toast;

import com.error404.memome.Adapters.MemoAdapter;
import com.error404.memome.DB.DAO;
import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;

import java.util.ArrayList;
//snellire oncreate
public class FavoriteMemoActivity extends AppCompatActivity {
    private DAO dao;
    private ListView myListView;
    private ArrayList<Memo> memoList = new ArrayList<Memo>();
    private MemoAdapter mem;
    private static Handler handler = null;
    private static Runnable run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");
        setContentView(R.layout.activity_favorite_memo);
        openDB();
        initializeGuiAndListener();
    }
    public void initializeGuiAndListener(){
        //Inizializza le View e i rekativi listener
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.favoriteActivityTitle);
        memoList = dao.loadAllFavoriteMemo();
        mem = new MemoAdapter(this, R.layout.raw_layout_favorite, memoList);
        myListView = (ListView) findViewById(R.id.listOfNotes);
        myListView.setAdapter(mem);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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
                final LinearLayout wrongPassword = (LinearLayout) formElementsView
                        .findViewById(R.id.layoutWrongPassword);
                String decryptedFromDB = Encrypt.decryption(dao.loadMemoById(idMemo).getPassword(), nameEditText.getText().toString());
                System.out.println(decryptedFromDB);
                if (nameEditText.getText().toString().equals(decryptedFromDB) && !nameEditText.getText().toString().equals(Values.EMPTY_STRING)) {
                    Intent myIntent = new Intent(FavoriteMemoActivity.this, ShowMemoActivity.class);
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
    @Override
    public void onResume(){
        super.onResume();
        memoList = dao.loadAllFavoriteMemo();
        mem = new MemoAdapter(this, R.layout.raw_layout_favorite, memoList);
        myListView.setAdapter(mem);
    }
    @Override
    public void onBackPressed() {//chiudi l'activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
