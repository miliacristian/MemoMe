package com.error404.memometwo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
//da ordinare ,da aggiungere fragment

// modifica o crea solo colore testo titolo ed emoji la cifratura e la delete della nota si fa nell'activity show!
public class activity_modifyOrAdd extends AppCompatActivity {
    private DAO dao;
    private ArrayList<Integer> emojiList=new ArrayList<Integer>();
    private Memo currentMemo;
    private int emoji=Values.DEFAULT_EMOJI;
    private int color=Values.DEFAULT_COLOR;
    private EditText textModify;
    private EditText titleModify;
    private TextView emojiModify;
    private ImageView colorModify;//a ch emi serve??
    private EmojiAdapter emAdapt;
    private String mode=Values.EMPTY_STRING;
    private final String ADD_MODE="addMode";
    private final String MODIFY_MODE="modifyMode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_or_add);
        dao = new DAO(this);
        dao.open();
        Intent intent=getIntent();
        Bundle bun=intent.getExtras();
        final String password = bun.getString(DAO.PASSWORD);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textModify=(EditText)findViewById(R.id.textModify);
        titleModify=(EditText)findViewById(R.id.titleModify);
        emojiModify=(TextView) findViewById(R.id.emojiModify);
        emojiModify.setClickable(true);
        emojiModify.setText(Memo.getEmojiByUnicode(emoji));
        emojiModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertChooseEmoji();
            }
        });
        colorModify=(ImageView)findViewById(R.id.colorModify);
        colorModify.setClickable(true);
        colorModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogChooseColor();
            }
        });
        int position=bun.getInt(Values.BUNDLE_KEY);

        if(position==Values.NO_POSITION){
            mode=ADD_MODE;
        }
        else{
            mode=MODIFY_MODE;
        }
        if(mode.equals(MODIFY_MODE)){
            currentMemo= dao.loadMemoByPosition(position);
            if(currentMemo.getEncryption()== Values.TRUE){
                //String normalPassword="";//fare la get della password non cifrata
                dao.decryptText(currentMemo,password);
            }
            textModify.setText(currentMemo.getText());
            titleModify.setText(currentMemo.getTitle());
            color=currentMemo.getColor();
            emoji=currentMemo.getEmoji();
            emojiModify.setText(Memo.getEmojiByUnicode(emoji));
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
        }
        FloatingActionButton buttonSaveMemo = (FloatingActionButton) findViewById(R.id.fab2);//floating button
        buttonSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleModify.getText().toString();
                if(!title.equals(Values.EMPTY_STRING)) {
                    String text = textModify.getText().toString();
                    //DAO dao = new DAO(activity_modifyOrAdd.this);
                    //dao.open();
                    if (mode.equals(ADD_MODE)) {//aggiungi al db
                        dao.addMemoToDB(title, text,emoji, color);//se color non viene modificato che colore ho?
                        // mettere color a valore bianco di default
                        finish();
                    } else {//aggiorna memo con tutti i dati
                        if (currentMemo.getEncryption() == Values.TRUE){
                            String toCifrateText = textModify.getText().toString();
                            String cifratedText = Encrypt.encryption(toCifrateText, password);
                            currentMemo.setText(cifratedText);
                        }else{
                            currentMemo.setText(textModify.getText().toString());
                        }

                        currentMemo.setTitle(titleModify.getText().toString());

                        currentMemo.setEmoji(emoji);
                        currentMemo.setColor(color);
                        dao.saveMemo(currentMemo, currentMemo.getId());
                        ShowMemo.getInstance().finish();
                        finish();
                    }
                }
                else{
                        Toast.makeText(activity_modifyOrAdd.this, R.string.needTitle, Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }
    public void setColorOnTitleAndText(){
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
    }

    public void alertDialogChooseColor() {
        final String[] items = { getApplicationContext().getResources().getString(R.string.bianco),getApplicationContext().getResources().getString(R.string.rosa),getApplicationContext().getResources().getString(R.string.celeste),getApplicationContext().getResources().getString(R.string.lime)
                ,getApplicationContext().getResources().getString(R.string.ciano),getApplicationContext().getResources().getString(R.string.rosso),getApplicationContext().getResources().getString(R.string.grigio),getApplicationContext().getResources().getString(R.string.verde),getApplicationContext().getResources().getString(R.string.viola),getApplicationContext().getResources().getString(R.string.indaco),getApplicationContext().getResources().getString(R.string.marrone)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
        builder.setTitle(R.string.chooseColor);
        builder.setIcon(R.mipmap.palette_icon);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                color=Memo.getColors(item);
                setColorOnTitleAndText();
            }
        }).show();
    }
        public void alertChooseEmoji(){
            LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View customView = inflater.inflate(R.layout.list_view_emoji2, null, false);
            ListView listView=(ListView)customView.findViewById(R.id.listV);
            TextView tv=(TextView)customView.findViewById(R.id.closed);
            tv.setClickable(true);
            emojiList=Memo.getListEmojis();
            emAdapt =new EmojiAdapter(activity_modifyOrAdd.this,R.layout.emoji_layout,emojiList);
            listView.setAdapter(emAdapt);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
            builder.setView(customView);
            builder.setTitle(R.string.chooseEmoji);
            builder.setIcon(R.mipmap.smile_icon);
            final AlertDialog alertDialog=builder.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    emoji=Memo.getEmoji(position);
                    emojiModify.setText(Memo.getEmojiByUnicode(emoji));
                    alertDialog.dismiss();
                }
            });
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
    @Override
    public void onBackPressed(){
        finish();
    }
    @Override
    public void onDestroy(){
       super.onDestroy();
        System.out.println("memo modify destroy");
        dao.close();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
