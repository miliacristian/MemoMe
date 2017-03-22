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
//da sistemare in codice ,da aggiungere fragment

// modifica o crea solo colore testo titolo ed emoji la cifratura e la delete della nota si fa nell'activity show!
public class activity_modifyOrAdd extends AppCompatActivity {
    ArrayList<Integer> emojiAdapter=new ArrayList<Integer>();
    Memo currentMemo;//usare solo questo attributo al posto di color e emoji
    int colorIndex;
    int emoji=0x1f604;
    int color = Memo.getColors(0);
    EditText textModify;
    EditText titleModify;
    TextView emojiModify;
    ImageView colorModify;
    EmojiAdapter emAdapt;
    String mode="";//mode può essere "modifyMode" o "addMode"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_or_add);
        Intent intent=getIntent();
        Bundle bun=intent.getExtras();
        final String password = bun.getString("password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textModify=(EditText)findViewById(R.id.textModify);
        titleModify=(EditText)findViewById(R.id.titleModify);
        emojiModify=(TextView) findViewById(R.id.emojiModify);
        emojiModify.setClickable(true);
        emojiModify.setText(Memo.getEmojiByUnicode(emoji));
        emojiModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseEmoji();
            }
        });
        colorModify=(ImageView)findViewById(R.id.colorModify);
        colorModify.setClickable(true);
        colorModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseColor();
            }
        });
        int position=bun.getInt("key");

        if(position==-1){
            mode="addMode";
        }
        else{
            mode="modifyMode";
        }
        if(mode.equals("modifyMode")){

            DAO dao = new DAO(this);
            dao.open();
            currentMemo= dao.loadMemoByPosition(position);
            if(currentMemo.getEncryption()==1){
                String normalPassword="";//fare la get della password non cifrata
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
                if(!title.equals("")) {
                    String text = textModify.getText().toString();
                    DAO dao = new DAO(activity_modifyOrAdd.this);
                    dao.open();
                    if (mode.equals("addMode")) {//aggiungi al db
                        dao.addMemoToDB(title, text,emoji, color);//se color non viene modificato che colore ho?
                        // mettere color a valore bianco di default
                        finish();
                    } else {//aggiorna memo con tutti i dati
                        if (currentMemo.getEncryption() == 1){
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

    public void dialogChooseColor() {
        final String[] items = { "BIANCO","ROSA","LIGHTBLUE","LIME",};//char sequence o string non da problemi

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
        builder.setTitle(R.string.chooseColor);
        builder.setIcon(R.mipmap.palette_icon);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //colorIndex=item;
                color=Memo.getColors(item);
                setColorOnTitleAndText();
            }
        }).show();
    }
        public void dialogChooseEmoji(){
            LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View customView = inflater.inflate(R.layout.list_view_emoji, null, false);
            ListView lV=(ListView)customView.findViewById(R.id.listV);
            emojiAdapter=Memo.getListEmojis();
            emAdapt =new EmojiAdapter(activity_modifyOrAdd.this,R.layout.emoji_layout,emojiAdapter);
            lV.setAdapter(emAdapt);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
            builder.setView(customView);
            builder.setTitle(R.string.chooseEmoji);
            builder.setIcon(R.mipmap.emoji_icon);
            final AlertDialog ad=builder.show();
            lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    emoji=Memo.getEmoji(position);
                    emojiModify.setText(Memo.getEmojiByUnicode(emoji));
                    ad.dismiss();
                }
            });
        }
    @Override
    public void onBackPressed(){
        finish();
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
