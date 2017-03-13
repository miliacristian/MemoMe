package com.error404.memometwo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowMemo extends AppCompatActivity {
    TextView emojitxt;
    TextView txtViewTitle;
    ImageView imageView2;
    TextView txtViewNota;
    int color;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memo);
        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        position = bun.getInt("key");
        System.out.println(position);
        //getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.outerSpace));
        DAO dao = new DAO(this);
        dao.open();
        Memo m = dao.loadMemoByPosition(position);
        int colorIndex=m.getColor();
        color=getColorByList(colorIndex);
        emojitxt = (TextView) findViewById(R.id.emojitxt);
        txtViewTitle = (TextView) findViewById(R.id.txtViewTitle);
        txtViewNota = (TextView) findViewById(R.id.txtViewNota);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        //inizializzare opportunamente emoji
       // emojitxt.setText(m.getEmoji());
        setColorOnTitleAndText();
        // if encrypted
        if (dao.isEncrypted(position)){
            //password momentanea
            String password = "abc";
            dao.decryptText(dao.loadMemoByPosition(position), password);
        }
        txtViewNota.setText(m.getText());
        txtViewTitle.setText(m.getTitle());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabShow);//floating button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowMemo.this, activity_modifyOrAdd.class);
                Bundle b = new Bundle();
                b.putInt("key", position);
                intent.putExtras(b);
                startActivity(intent);
                //vai all'activity della creazione/modifica in modalità modifica;
            }
        });
    }
    public int getColorByList(int itemPosition){
        return Memo.colors[itemPosition];
    }
    public void setColorOnTitleAndText(){
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
    }

    public void insertEncryptToPasswordAndText(){
        String password="abc";
        //mostra alert dialog ,sull ok memorizza la password nella stringa password
        DAO dao=new DAO(this);
        dao.open();//necessaria??
        dao.addEncryptionToPasswordAndText(position,password);
    }

    public void deleteEncryptionToPasswordAndText(){
        String password="abc";
        //mostra alert dialog ,sull ok memorizza la password nella stringa password
        DAO dao=new DAO(this);
        dao.open();//necessaria??
        dao.deleteEncryptionToPasswordAndText(position,password);
    }


    // inflata il pulsante elimina ed encripta
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // se la nota è criptata carico un menu, altrimenti l'altro
        if (isEncrypted()){
            inflater.inflate(R.menu.show_memo_decode, menu);
        }else{
            inflater.inflate(R.menu.show_memo_encode, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean isEncrypted(){
        //mostra alert dialog ,sull ok memorizza la password nella stringa password
        DAO dao=new DAO(this);
        dao.open();//necessaria??
        return dao.isEncrypted(position);
    }

    // onClick per il pulsante elimina e encode
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_delete:
                DAO dao=new DAO(this);
                dao.open();
                int id=dao.findIdByPosition(position);
                dao.deleteMemoByIdFromDB(id);
                Intent intent=new Intent(this,MemoMeMain.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_encode:
                Toast.makeText(getApplicationContext(), "encode",
                        Toast.LENGTH_LONG).show();
                insertEncryptToPasswordAndText();
                startActivity(getIntent());
                finish();
                return true;
            case R.id.action_decode:
                Toast.makeText(getApplicationContext(), "decode",
                        Toast.LENGTH_LONG).show();
                deleteEncryptionToPasswordAndText();
                startActivity(getIntent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}