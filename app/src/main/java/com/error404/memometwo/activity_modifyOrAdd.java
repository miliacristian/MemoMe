package com.error404.memometwo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import static com.example.cristian.memome2.R.id.fab;
// modifica o crea solo colore testo titolo ed emoji la cifratura e la delete della nota si fa nell'activity show!
public class activity_modifyOrAdd extends AppCompatActivity {
    Memo currentMemo;
    int colorIndex;
    int color;
    EditText textModify;
    EditText titleModify;
    TextView emojiModify;
    ImageView colorModify;
    String mode="";//mode può essere "modifyMode" o "addMode"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_or_add);
       // getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.outerSpace));
        textModify=(EditText)findViewById(R.id.textModify);
        titleModify=(EditText)findViewById(R.id.titleModify);
        emojiModify=(TextView) findViewById(R.id.emojiModify);
        colorModify=(ImageView)findViewById(R.id.colorModify);
        colorModify.setClickable(true);
        colorModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogListView();
            }
        });
        Intent intent=getIntent();
        Bundle bun=intent.getExtras();
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
                dao.decryptText(currentMemo,normalPassword);
            }
            textModify.setText(currentMemo.getText());
            titleModify.setText(currentMemo.getTitle());
            colorIndex=currentMemo.getColor();
            System.out.println("activitymodify"+colorIndex);
            color =getColorByList(colorIndex);
            textModify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            titleModify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            emojiModify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            //istanziare emoji
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);//floating button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleModify.getText().toString();
                if(!title.equals("")) {

                    String text = textModify.getText().toString();
                    String emoji = emojiModify.getText().toString();
                    DAO dao = new DAO(activity_modifyOrAdd.this);
                    dao.open();
                    if (mode.equals("addMode")) {//aggiungi al db
                        dao.addMemoToDB(title, text, 0x1f604, colorIndex);//al posto di null parametro color
                        Intent intent = new Intent(activity_modifyOrAdd.this, MemoMeMain.class);
                        startActivity(intent);
                        //alla fine dell'intent ci va finish??
                    } else {//aggiorna memo con tutti i dati
                        currentMemo.setTitle(titleModify.getText().toString());
                        currentMemo.setText(textModify.getText().toString());
                        //currentMemo.setEmoji();
                        currentMemo.setColor(colorIndex);
                        dao.saveMemo(currentMemo, currentMemo.getId());
                        Intent intent = new Intent(activity_modifyOrAdd.this, MemoMeMain.class);
                        startActivity(intent);
                    }
                }
                else{
                        Toast.makeText(activity_modifyOrAdd.this, "Insert title", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }
    public void alertDialogListView() {
        final String[] items = { "ROSA","LIGHTBLUE","LIME",};//char sequence o string non da problemi

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_modifyOrAdd.this);
        builder.setTitle("Choose your color");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                colorIndex=item;
                color=getColorByList(item);
                setColorOnTitleAndText();
                //dialog.dismiss();
            }
        }).show();
    }

    public int getColorByList(int itemPosition){
        return Memo.colors[itemPosition];
    }
    public void setColorOnTitleAndText(){
        textModify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
        titleModify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
        emojiModify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));

    }
}
