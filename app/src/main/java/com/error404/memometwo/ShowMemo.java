package com.error404.memometwo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ShowMemo extends AppCompatActivity {
    TextView emojitxt;
    TextView txtViewTitle;
    ImageView imageView2;
    TextView txtViewNota;
    int color;
    int position;
    int emoji;
    private static String password;
    private static Activity refer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memo);
        refer = this;
        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        position = bun.getInt("key");
        //Aggiunge il pulsante back alla action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.outerSpace));
        DAO dao = new DAO(this);
        dao.open();
        Memo m = dao.loadMemoByPosition(position);
        //int colorIndex=m.getColor();
        //color=getColorByList(colorIndex);
        color=m.getColor();
        emoji=m.getEmoji();
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
            dao.decryptText(m, password);
        }
        txtViewNota.setText(m.getText());
        txtViewTitle.setText(m.getTitle());
        emojitxt.setText(Memo.getEmojiByUnicode(emoji));
        FloatingActionButton buttonModifyOrAdd = (FloatingActionButton) findViewById(R.id.fabShow);//floating button
        buttonModifyOrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowMemo.this, activity_modifyOrAdd.class);
                Bundle b = new Bundle();
                b.putInt("key", position);
                b.putString("password", "abc");
                intent.putExtras(b);
                startActivity(intent);
                //vai all'activity della creazione/modifica in modalità modifica;
            }
        });
    }

    public static Activity getInstance(){
        return refer;
    }

    //public int getColorByList(int itemPosition){
        //return Memo.colors[itemPosition];
    //}
    public void setColorOnTitleAndText(){
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
    }

    public void insertEncryptToPasswordAndText(String password){
        //String password="abc";
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

    public static void setPassword(String password){
        ShowMemo.password = password;
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
                finish();
                return true;
            case R.id.action_encode:
                Toast.makeText(getApplicationContext(), "encode",
                        Toast.LENGTH_SHORT).show();
                //Inizio alert
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.encode_layout,
                        null, false);

                final EditText nameEditText = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);
                final EditText nameEditText2 = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);

                //alert dialog
                new AlertDialog.Builder(ShowMemo.this).setView(formElementsView)
                        .setTitle("Insert Password")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                    /*
                     * Getting the value of an EditText.
                     */
                                if(nameEditText.getText().toString().equals(nameEditText2.getText().toString())&&!nameEditText.equals("")) {
                                    ShowMemo.setPassword(nameEditText.getText().toString());
                                    dialog.cancel();
                                }else{
                                    Toast.makeText(ShowMemo.this, "Passwords dont match", Toast.LENGTH_SHORT).show();
                                    nameEditText.setText("");
                                    nameEditText2.setText("");
                                }
                                //showToast(toastString);


                            }


                        }
                        )
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                //showToast(" is not awesome for you. :(");
                                dialog.cancel();
                            }
                        })
                        .show();
                insertEncryptToPasswordAndText(password);
                /*startActivity(getIntent());
                finish();*/
                invalidateOptionsMenu();
                return true;
            case R.id.action_decode:
                Toast.makeText(getApplicationContext(), "decode",
                        Toast.LENGTH_SHORT).show();
                deleteEncryptionToPasswordAndText();
                //startActivity(getIntent());
                //finish();
                invalidateOptionsMenu();
                return true;
            case android.R.id.home:
                //Toast.makeText(getApplicationContext(), "back",
                        //Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}