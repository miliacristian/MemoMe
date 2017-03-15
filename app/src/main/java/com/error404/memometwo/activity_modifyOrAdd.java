package com.error404.memometwo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import static com.example.cristian.memome2.R.id.fab;
// modifica o crea solo colore testo titolo ed emoji la cifratura e la delete della nota si fa nell'activity show!
public class activity_modifyOrAdd extends AppCompatActivity {
    Memo currentMemo;//usare solo questo attributo al posto di color e emoji
    int colorIndex;
    int emoji=0x1f604;
    int color = Memo.getColors(0);
    EditText textModify;
    EditText titleModify;
    TextView emojiModify;
    ImageView colorModify;
    String mode="";//mode può essere "modifyMode" o "addMode"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_or_add);
        Intent intent=getIntent();
        Bundle bun=intent.getExtras();
        String password = bun.getString("password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.outerSpace));
        textModify=(EditText)findViewById(R.id.textModify);
        titleModify=(EditText)findViewById(R.id.titleModify);
        emojiModify=(TextView) findViewById(R.id.emojiModify);
        emojiModify.setClickable(true);
        emojiModify.setText(Memo.getEmojiByUnicode(emoji));
        emojiModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogChooseEmoji();
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
        //getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
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
            //colorIndex=currentMemo.getColor();
            //System.out.println("activitymodify"+colorIndex);
            //color =getColorByList(colorIndex);
            color=currentMemo.getColor();
            emoji=currentMemo.getEmoji();
            emojiModify.setText(Memo.getEmojiByUnicode(emoji));
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            //istanziare emoji
        }
        FloatingActionButton buttonSaveMemo = (FloatingActionButton) findViewById(R.id.fab2);//floating button
        buttonSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleModify.getText().toString();
                if(!title.equals("")) {

                    String text = textModify.getText().toString();
                    //String emoji = emojiModify.getText().toString();
                    DAO dao = new DAO(activity_modifyOrAdd.this);
                    dao.open();
                    if (mode.equals("addMode")) {//aggiungi al db
                        //dao.addMemoToDB(title, text, 0x1f604, colorIndex);
                        dao.addMemoToDB(title, text,emoji, color);//se color non viene modificato che colore ho?
                        // mettere color a valore bianco di default
                        //Intent intent = new Intent(activity_modifyOrAdd.this, MemoMeMain.class);
                        //startActivity(intent);
                        finish();
                        //alla fine dell'intent ci va finish??
                    } else {//aggiorna memo con tutti i dati
                        currentMemo.setTitle(titleModify.getText().toString());
                        currentMemo.setText(textModify.getText().toString());
                        currentMemo.setEmoji(emoji);
                        //currentMemo.setColor(colorIndex);
                        currentMemo.setColor(color);
                        dao.saveMemo(currentMemo, currentMemo.getId());
                        //Intent intent = new Intent(activity_modifyOrAdd.this, MemoMeMain.class);
                        //startActivity(intent);
                        ShowMemo.getInstance().finish();
                        finish();
                    }
                }
                else{
                        Toast.makeText(activity_modifyOrAdd.this, "Insert title", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }
    public void alertDialogChooseColor() {
        final String[] items = { "BIANCO","ROSA","LIGHTBLUE","LIME",};//char sequence o string non da problemi

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
        builder.setTitle("Choose your color");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //colorIndex=item;
                color=Memo.getColors(item);
                //System.out.println(color);
                setColorOnTitleAndText();
                //dialog.dismiss();
            }
        }).show();
    }
    public void alertDialogChooseEmoji() {
        final String[] items = { "1","2","3","4",};//char sequence o string non da problemi

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
        builder.setTitle("Choose emoji");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                emoji=Memo.getEmoji(item);
                emojiModify.setText(Memo.getEmojiByUnicode(emoji));
                //dialog.dismiss();
            }
        }).show();
    }
    //public int getColorByList(int itemPosition){
        //return Memo.getColors(itemPosition);
    //}
    public void setColorOnTitleAndText(){
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
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
                Toast.makeText(getApplicationContext(), "back",
                        Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
