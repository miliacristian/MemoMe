package com.error404.memome.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.error404.memome.Adapters.ColorAdapter;
import com.error404.memome.Adapters.EmojiAdapter;
import com.error404.memome.DB.DAO;
import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;
import java.util.ArrayList;

//attributi della modify:
//colore,id memo,emoji,testo,titolo,dao,emojiAdapter,ColorAdapter

public class ModifyOrAddActivity extends AppCompatActivity {
    private DAO dao;
    private ArrayList<Integer> emojiList=new ArrayList<Integer>();
    private Memo currentMemo;
    private int emoji= Values.DEFAULT_EMOJI;
    private int color=Values.DEFAULT_COLOR;
    private int id;
    private EditText textModify;
    private EditText titleModify;
    private TextView emojiModify;
    private ImageView colorModify;
    private EmojiAdapter emAdapt;
    private ColorAdapter colorAdapt;
    private String mode=Values.EMPTY_STRING;
    private final String ADD_MODE="addMode";
    private final String MODIFY_MODE="modifyMode";
    private Toast mToast;
    private String password;
    private Bundle bundleState=null;
    @Override
    //metodo che definisce cosa bisogna fare al momento della creazione dell'activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //se il bundle non è null salvalo nell'attributo bundleState
        if(savedInstanceState!=null){
            bundleState=savedInstanceState;
        }
        setContentView(R.layout.activity_modify_or_add);
        //apri il DB,gestisci il bundle proveniente dall'activity precedente(ShowMemo o MainActivity)
        //e inizializza la GUI e i listener
        openDB();
        handleBundleFromPreviousActivity();
        initializeGuiAndListener();
    }
    public void initializeGuiAndListener(){
        //inizializza GUI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textModify=(EditText)findViewById(R.id.textModify);
        titleModify=(EditText)findViewById(R.id.titleModify);
        emojiModify=(TextView) findViewById(R.id.emojiModify);
        emojiModify.setClickable(true);
        //se bundle diverso da null ripristina il colore e l'emoji precedenti
        if(bundleState!=null){
            emoji=bundleState.getInt(Values.EMOJI);
            color=bundleState.getInt(Values.COLOR);
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            if(emoji==Values.INDEX_EMPTY_EMOJI){
                emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
            }
            else {
                emojiModify.setText(Memo.getEmojiByUnicode(emoji));
            }
        }
        else {
            emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
        }
        //sul click gestisci l'alert per scegliere l'emoji
        emojiModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertChooseEmoji();
            }
        });
        colorModify=(ImageView)findViewById(R.id.colorModify);
        colorModify.setClickable(true);
        //sul click gestisci l'alert per scegliere il colore
        colorModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogChooseColor();
            }
        });
        //se nel bundle il valore id non era associato a nessun valore vuol dire che siamo nella modalità "creazione"
        if(id==Values.NO_ID){
            mode=ADD_MODE;
        }
        //se il valore id è associato a un id di una nota nel DB ,allora vuol dire che siamo nella modalità "modifica"
        else{
            mode=MODIFY_MODE;
        }//se siamo in modalità modifica...
        if(mode.equals(MODIFY_MODE)){
            //imposta il titolo dell'activity in "Modifica" e recupera tutti i valori significativi della memo corrente
            currentMemo= dao.loadMemoById(id);
            if(currentMemo.getEncryption()== Values.TRUE){
                dao.decryptText(currentMemo,password);
            }
            textModify.setText(currentMemo.getText());
            titleModify.setText(currentMemo.getTitle());
            if(bundleState!=null){
                color=bundleState.getInt(Values.COLOR);
                emoji=bundleState.getInt(Values.EMOJI);
            }
            else {
                color = currentMemo.getColor();
                emoji = currentMemo.getEmoji();
            }
            if(emoji==Values.INDEX_EMPTY_EMOJI){
                emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
            }
            else {
                emojiModify.setText(Memo.getEmojiByUnicode(emoji));
            }
            setColorOnTitleAndText();
        }
        //altrimenti non ripristinare nulla e imposta il titolo dell'activity a "Crea nota"
        else{
            getSupportActionBar().setTitle(R.string.createMemo);
        }
        FloatingActionButton buttonSaveMemo = (FloatingActionButton) findViewById(R.id.fab2);
        //sul click del bottone "Salva Memo"
        buttonSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleModify.getText().toString();
                //se il titolo non è vuoto allora salva la memo e ritorna all'activity iniziale
                if(!title.equals(Values.EMPTY_STRING)) {
                    saveMemo();
                }
                //se il titolo è vuoto allora non salvare la memo e mostra un toast
                else{
                    if (mToast != null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(ModifyOrAddActivity.this, R.string.needTitle, Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });

        return;
    }
    //metodo per gestire il bundle dall'activity ShowMemo o dall'activity MainActivity
    public void handleBundleFromPreviousActivity(){
        Intent intent=getIntent();
        Bundle bun=intent.getExtras();
        //prendi l'id e la password dal bundle
        password = bun.getString(Values.PASSWORD);//può essere null
        id=bun.getInt(Values.BUNDLE_KEY);
        return;
    }
    public void openDB(){//metodo che apre il DB
        dao = new DAO(this);
        dao.open();
        return;
    }
    //metodo imposta il colore sul titolo o sul testo
    public void setColorOnTitleAndText(){
        //imposta il colore di background dell'activity
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
        int actionColor;
        //se il colore non è bianco allora imposta la status bar dello stesso colore ma più scuro
        if (color != R.color.white){
            actionColor = Memo.darkerColor(color);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(actionColor);
        }else{
            //se il colore è bianco allora imposta la status bar al colore di default dell'activity
            actionColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
            int statusColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusColor);
        }
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(actionColor));
    }

    public void alertDialogChooseColor() {//Alert dialog per scegliere un colore cliccando un elemento di una listView,
        //sul click viene impostato il colore  nell'attributo colore
        ArrayList<Integer> colorsList;
        LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customView = inflater.inflate(R.layout.list_view_color, null, false);
        ListView listView=(ListView)customView.findViewById(R.id.listColor);
        colorsList=Memo.getColorsList();
        colorAdapt =new ColorAdapter(ModifyOrAddActivity.this,R.layout.color_layout,colorsList);
        listView.setAdapter(colorAdapt);
        listView.setScrollbarFadingEnabled(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyOrAddActivity.this);
        builder.setView(customView);
        builder.setTitle(R.string.chooseColor);
        builder.setIcon(R.mipmap.palette_icon);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog=builder.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                color=Memo.getColors(position);
                setColorOnTitleAndText();
                    alertDialog.dismiss();
            }
        });
    }

    public void alertChooseEmoji(){//Aeert dialog per scegliere una emoji cliccando un elemento di una listView,
        //sul click viene impostata l'emoji nell'attributo emoji
            LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View customView = inflater.inflate(R.layout.list_view_emoji, null, false);
            ListView listView=(ListView)customView.findViewById(R.id.listV);
            emojiList=Memo.getListEmojis();
            emAdapt =new EmojiAdapter(ModifyOrAddActivity.this,R.layout.emoji_layout,emojiList);
            listView.setAdapter(emAdapt);
            listView.setScrollbarFadingEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(ModifyOrAddActivity.this);
            builder.setView(customView);
            builder.setTitle(R.string.chooseEmoji);
            builder.setIcon(R.mipmap.smile_icon);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alertDialog=builder.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    emoji=Memo.getEmoji(position);
                    if(position==Values.INDEX_EMPTY_EMOJI){
                        emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
                        alertDialog.dismiss();
                    }
                    else {
                        emojiModify.setText(Memo.getEmojiByUnicode(emoji));
                        alertDialog.dismiss();
                    }
                }
            });
        }
        public void saveMemo(){//metodo per salvare la memo
            String title=titleModify.getText().toString();
                String text = textModify.getText().toString();
            if (mode.equals(ADD_MODE)) {//se la nota non è presente nel DB
                dao.addMemoToDB(title, text,emoji, color);//aggiungila al DB
                finish();
            } else {//se la nota  è presente nel DB
                if (currentMemo.getEncryption() == Values.TRUE){//cifra il nuovo testo e salvalo nel DB
                    String toCifrateText = textModify.getText().toString();
                    String cifratedText = Encrypt.encryption(toCifrateText, password);
                    currentMemo.setText(cifratedText);
                }else{
                    currentMemo.setText(textModify.getText().toString());
                }
                //imposta i valori di colore titolo ed emoji nella Memo
                currentMemo.setTitle(titleModify.getText().toString());
                currentMemo.setEmoji(emoji);
                currentMemo.setColor(color);
                dao.saveMemo(currentMemo, currentMemo.getId());//salva memo nel DB
                ShowMemoActivity.getInstance().finish();//ritorna all'activity iniziale
                finish();
            }
        }
    @Override
    public void onBackPressed(){
        if (mode.equals(MODIFY_MODE)){//se la nota è presente nel DB
            if(modifiedMemo()) {//se è stata modificata conferma la chiusura dell'activity
                alertCloseActivity();
            }else{
                finish();
            }
        }else if(mode.equals(ADD_MODE)){//se la nota non è presente nel DB
            if (NotEmptyMemoExit()){
                alertCloseActivity();//se è non vuota conferma la chiusura dell'activity
            }else{
                finish();
            }
        }else {
            alertCloseActivity();
        }
    }

    public boolean NotEmptyMemoExit(){//ritorna vero se la memo è non vuota
        if(Values.EMPTY_STRING.equals(titleModify.getText().toString())
                && Values.EMPTY_STRING.equals(textModify.getText().toString())){
            return false;
        }else{
            return true;
        }
    }
    public boolean modifiedMemo(){//verifica per ogni attributo se è stato modificato
        // Ritorna vero se almeno un attributo è diverso rispetto ai valori di partenza,also altrimenti
     if(currentMemo.getTitle().equals(titleModify.getText().toString())
             && currentMemo.getText().equals(textModify.getText().toString())
             && currentMemo.getEmoji() == emoji
             && currentMemo.getColor() == color){
         return false;
     }else{
         return true;
     }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//metodo che sul clicca della freccia indietro esegue onBackPressed
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void alertCloseActivity(){//Alert Dialog per confermare il salvataggio delle modifiche.
        // Se si clicca si la nota viene salvata e si ritorna all'activity principale
        //Se si clicca no la nota non viene salvata e si ritorna all'activity precedente
        //Se si clicca annulla si chiude solamente l'alert Dialog
        new AlertDialog.Builder(ModifyOrAddActivity.this)
                .setTitle(R.string.confirm_close_title)
                .setMessage(R.string.confirm_close_text)
                .setIcon(R.mipmap.info_icon)
                .setPositiveButton(R.string.confirm_close_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String title = titleModify.getText().toString();
                                if (!title.equals(Values.EMPTY_STRING)) {
                                    saveMemo();
                                    dialog.cancel();
                                }
                                else{
                                    if (mToast != null){
                                        mToast.cancel();
                                    }
                                    mToast = Toast.makeText(ModifyOrAddActivity.this, R.string.needTitle, Toast.LENGTH_SHORT);
                                    mToast.show();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.confirm_close_no,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        dialog.cancel();
                    }
        })
                .setNeutralButton(R.string.cancel,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    @Override
    public void onDestroy(){//chiudi il database nel momento della distruzione dell'activity
        super.onDestroy();
        dao.close();
        return;
    }
    @Override
    public void onSaveInstanceState(Bundle keepState){//Metodo che salva lo stato dell'activity in un bundle.
        // Quando necessario ricarica l'activity usando i valori nel bundle.
        //Nel bundle vengono salvati colore della memo e emoji,gli altri attributi della memo(es testo memo)vengono automaticamente ripristinati
        super.onSaveInstanceState(keepState);
        keepState.putInt(Values.COLOR,color);
        keepState.putInt(Values.EMOJI,emoji);
        return;
    }
}
