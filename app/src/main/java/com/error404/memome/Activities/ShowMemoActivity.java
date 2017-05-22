package com.error404.memome.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.error404.memome.DB.DAO;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;

public class ShowMemoActivity extends AppCompatActivity {
    private DAO dao;
    private TextView emojitxt;
    private TextView txtViewTitle;
    private TextView txtViewNota;
    private int color;
    private int id;
    private int emoji;
    private String password;
    private static Activity refer;
    private Bundle bundleState=null;
    private static Handler handler = null;
    private static Runnable run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//apri il database,gestisici il bundle
        // e inizializza i componenti grafici in base alla memo cliccata

        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){//memorizza il bundle savedInstanceState nell'attributo bundlestate
            bundleState=savedInstanceState;
        }
        setContentView(R.layout.activity_show_memo);
        refer = this;
        openDB();
        handleBundleFromPreviousActivity();
        initializeGuiAndListener();
    }
    public void initializeGuiAndListener(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.showMemoTitle);
        Memo m=dao.loadMemoById(id);//carica memo corrispondente all'id
        //imposta colore titolo emoji e testo della nota caricata
        color=m.getColor();
        emoji=m.getEmoji();
        emojitxt = (TextView) findViewById(R.id.emojitxt);
        txtViewTitle = (TextView) findViewById(R.id.txtViewTitle);
        txtViewNota = (TextView) findViewById(R.id.txtViewNota);
        setColorOnTitleAndText();
        if (m.isEncrypted()){//se nota cifrata decifra la nota

                dao.decryptText(m, password);
        }
        txtViewNota.setText(m.getText());
        txtViewTitle.setText(m.getTitle());
        emojitxt.setText(Memo.getEmojiByUnicode(emoji));
        //bottone modifica
        FloatingActionButton buttonModifyOrAdd = (FloatingActionButton) findViewById(R.id.fabShow);
        buttonModifyOrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//vai alla modifyordaddactivity
                Intent intent = new Intent(ShowMemoActivity.this, ModifyOrAddActivity.class);
                Bundle b = new Bundle();
                b.putInt(Values.BUNDLE_KEY, id);//passa alla nuova activity id e password
                b.putString(Values.PASSWORD, password);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        if (color != R.color.white){//imposta colore actionbar
            int actionColor = Memo.darkerColor(color);
            ActionBar bar = getSupportActionBar();
            bar.setBackgroundDrawable(new ColorDrawable(actionColor));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(actionColor);
        }
    }
    public void handleBundleFromPreviousActivity(){//metodo per gestire il bundle proveniente dalla MainActivity
        //(ho valore id e valore password dentro il bundle)
        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        id = bun.getInt(Values.BUNDLE_KEY);//ottieni il valore id
        if(bundleState!=null){
            password=bundleState.getString(Values.PASSWORD);//ripristina la password dal bundlestate se ho chiamato 2 volte oncreate
        }
        else {
            password = bun.getString(Values.PASSWORD);//memorizza la password dal bundle usato dalla MainActivity
        }
        return;
    }
    public void openDB(){//metodo per aprire il db
        dao = new DAO(this);
        dao.open();
        return;
    }
    public void deleteThisMemo(){//metodo per eliminare la nota corrente
        dao.deleteMemoByIdFromDB(id);
        finish();
    }

    public static Activity getInstance(){//metodo per ottenere il riferimento all'activity corrente
        return refer;
    }

    public void setColorOnTitleAndText(){//metodo per impostare il colore di background all'activity
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
    }
    public boolean isEncrypted(){//metodo per verificare se la nota è cifrata
        return dao.isEncrypted(id);
    }

    public boolean isFavorite(){//metodo per verificare se la nota è preferita
        return dao.isFavorite(id);
    }

    public void insertEncryptToPasswordAndText(String password){//metodo per inserire la cifratura sulla password e sul testo

        this.password = password;
        dao.addEncryptionToPasswordAndText(id,password);
    }

    public void deleteEncryptionToPasswordAndText(String password){//metodo per eliminare la cifratura sulla password e sul testo
        dao.deleteEncryptionToPasswordAndText(id,password);
    }
    public void addToFavorites(){//metodo per aggiungere la nota ai preferti
        dao.addToFavorites(id);
        return;
    }
    public void deleteFromFavorites(){//metodo per eliminare la nota ai preferti
        dao.deleteFromFavorites(id);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//metodo per gestire le icone dell'activity ShowMemoActivity
        // in base al valore degli attributi favorite e encryption
        MenuInflater inflater = getMenuInflater();
        if(isFavorite()) {
            inflater.inflate(R.menu.menu_favorite, menu);
        }else{
        inflater.inflate(R.menu.menu_not_favorite, menu);}
        if (isEncrypted()){
            inflater.inflate(R.menu.show_memo_decode, menu);
        }else{
            inflater.inflate(R.menu.show_memo_encode, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete://mostra alert dialog e elimina la nota
                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.confirmDelete)
                        .setIcon(R.mipmap.delete_finale)
                        .setPositiveButton(R.string.btnDelete, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                deleteThisMemo();
                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;

            case R.id.action_encode://mostra alert dialog e cifra la nota
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.encode_layout,
                        null, false);
                final EditText editPassword = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);
                final EditText confirmPassword = (EditText) formElementsView
                        .findViewById(R.id.nameEditText2);

                //GUI alertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMemoActivity.this);
                builder.setTitle(R.string.insertPsw);
                builder.setIcon(R.mipmap.lock_finale);
                builder.setView(formElementsView);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id){}
                        });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {//chiudi alert Dialog
                        dialog.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();//mostra alertDialog
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {//sul click del bottone ok..
                    @Override
                    public void onClick(View v)
                    {   //GUI per gestire errori
                        final LinearLayout wrongPassword = (LinearLayout) formElementsView
                                .findViewById(R.id.layoutWrongPassword);
                        final TextView alertText = (TextView) formElementsView
                                .findViewById(R.id.textView2);
                        String alertMessage = Values.EMPTY_STRING;
                        //verifiche dopo l'inserimento della password
                        if (editPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                            //password coincidono
                           if (TextUtils.isEmpty(editPassword.getText())){
                               //password vuota non ammessa
                                alertMessage = getResources().getString(R.string.emptyPass);
                           }else{
                               //effettivamente cifro la nota
                               insertEncryptToPasswordAndText(editPassword.getText().toString());
                               dialog.cancel();
                               invalidateOptionsMenu();
                           }
                        }
                        else {
                            alertMessage = getResources().getString(R.string.pswMatch);
                        }
                        if (!alertMessage.equals(Values.EMPTY_STRING)){
                            //in caso di errori, fa apparire la view di errore con il tipo di errore avvenuto (stringa vuota, o password diverse)
                            alertText.setText(alertMessage);
                            wrongPassword.setVisibility(View.VISIBLE);
                            editPassword.setText(Values.EMPTY_STRING);
                            confirmPassword.setText(Values.EMPTY_STRING);
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
                return true;
            case R.id.action_decode://se cifrata elimina la cifratura sulla nota
                AlertDialog myQuittingDialogB =new AlertDialog.Builder(this)
                        .setTitle(R.string.decode)
                        .setMessage(R.string.decodeText)
                        .setIcon(R.mipmap.unlock_finale)

                        .setPositiveButton(R.string.decode, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteEncryptionToPasswordAndText(password);
                                invalidateOptionsMenu();
                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            case android.R.id.home://torna indietro all'activity MainActivity
                finish();
                return true;
            case R.id.action_favorite://se era preferita eliminala dai preferiti
                deleteFromFavorites();
                invalidateOptionsMenu();
                return true;
            case R.id.action_not_favorite://se non era preferita aggiungila ai preferiti
                addToFavorites();
                invalidateOptionsMenu();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onDestroy() {//chiudi il database nel momento della distruzione dell'activity
        super.onDestroy();
        dao.close();
    }
    @Override
    public void onSaveInstanceState(Bundle keepState){//Mantiene lo stato dell'activity mettendo nel bundle la password.
        //una  nuova chiamata ripristina lo stato dell'activity
        super.onSaveInstanceState(keepState);
        keepState.putString(Values.PASSWORD,password);
        return;
    }
}