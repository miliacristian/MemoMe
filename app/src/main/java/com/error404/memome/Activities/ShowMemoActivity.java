package com.error404.memome.Activities;

import android.annotation.TargetApi;
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
import android.widget.Toast;

import com.error404.memome.DB.DAO;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Utilities.Values;

//snellire oncreate
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
    private Toast emptyToast;
    private Toast matchTaost;
    private Bundle bundleState=null;
    private static Handler handler = null;
    private static Runnable run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//apri il database,gestisici il bundle
        // e inizializza i componenti grafici in base alla memo cliccata

        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){//memorizza il bundle nell'attributo bundlestate
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
        Memo m=dao.loadMemoById(id);
        color=m.getColor();
        emoji=m.getEmoji();
        emojitxt = (TextView) findViewById(R.id.emojitxt);
        txtViewTitle = (TextView) findViewById(R.id.txtViewTitle);
        txtViewNota = (TextView) findViewById(R.id.txtViewNota);
        setColorOnTitleAndText();
        System.out.println("la password è:"+password);
        if (m.isEncrypted()){
            //password = bun.getString(DAO.PASSWORD);
                dao.decryptText(m, password);
        }
        txtViewNota.setText(m.getText());
        txtViewTitle.setText(m.getTitle());
        emojitxt.setText(Memo.getEmojiByUnicode(emoji));
        FloatingActionButton buttonModifyOrAdd = (FloatingActionButton) findViewById(R.id.fabShow);
        buttonModifyOrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowMemoActivity.this, ModifyOrAddActivity.class);
                Bundle b = new Bundle();
                b.putInt(Values.BUNDLE_KEY, id);
                b.putString(Values.PASSWORD, password);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        if (color != R.color.white){
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
        // in base al valore degli attributi favorite,encryption
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
            case R.id.action_delete:
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

            case R.id.action_encode:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.encode_layout,
                        null, false);
                final EditText nameEditText = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);
                final EditText nameEditText2 = (EditText) formElementsView
                        .findViewById(R.id.nameEditText2);
                /*new AlertDialog.Builder(ShowMemoActivity.this).setView(formElementsView)
                        .setTitle(R.string.insertPsw)
                        .setIcon(R.mipmap.lock_finale)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                if(TextUtils.isEmpty(nameEditText.getText())){
                                    if(emptyToast!= null){
                                        emptyToast.cancel();
                                    }
                                    emptyToast=Toast.makeText(ShowMemoActivity.this,R.string.emptyPass, Toast.LENGTH_SHORT);
                                    emptyToast.show();
                                }else {
                                    if (nameEditText.getText().toString().equals(nameEditText2.getText().toString())) {
                                        insertEncryptToPasswordAndText(nameEditText.getText().toString());
                                        dialog.cancel();
                                        invalidateOptionsMenu();
                                    } else {
                                        if (matchTaost != null){
                                            matchTaost.cancel();
                                        }
                                        matchTaost = Toast.makeText(ShowMemoActivity.this, R.string.pswMatch, Toast.LENGTH_SHORT);
                                        matchTaost.show();
                                    }
                                }
                            }


                        }
                        )
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();*/
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
                        final TextView alertText = (TextView) formElementsView
                                .findViewById(R.id.textView2);
                        String alertMessage = Values.EMPTY_STRING;
                        if (nameEditText.getText().toString().equals(nameEditText2.getText().toString())) {
                            //password coincidono
                           if (TextUtils.isEmpty(nameEditText.getText())){
                               //password vuota non ammessa
                                alertMessage = getResources().getString(R.string.emptyPass);
                           }else{
                               //effettivamente cifro la nota
                               insertEncryptToPasswordAndText(nameEditText.getText().toString());
                               dialog.cancel();
                               invalidateOptionsMenu();
                           }
                        } else {
                            alertMessage = getResources().getString(R.string.pswMatch);
                        }
                        if (!alertMessage.equals(Values.EMPTY_STRING)){
                            //in caso di errori, fa apparire la view di errore con il tipo di errore avvenuto (stringa vuota, o password diverse)
                            alertText.setText(alertMessage);
                            wrongPassword.setVisibility(View.VISIBLE);
                            nameEditText.setText(Values.EMPTY_STRING);
                            nameEditText2.setText(Values.EMPTY_STRING);
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
            case R.id.action_decode:
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                deleteFromFavorites();
                invalidateOptionsMenu();
                return true;
            case R.id.action_not_favorite:
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