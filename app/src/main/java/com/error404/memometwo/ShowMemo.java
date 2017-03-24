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
//da sistemare,da aggiungere fragment
public class ShowMemo extends AppCompatActivity {
    TextView emojitxt;
    TextView txtViewTitle;
    TextView txtViewNota;
    int color;
    int position;
    int emoji;
    private String password;
    private static Activity refer;
    //private String KEY="key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memo);
        refer = this;
        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        position = bun.getInt(Values.BUNDLE_KEY);
        //Aggiunge il pulsante back alla action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DAO dao = new DAO(this);
        dao.open();
        Memo m = dao.loadMemoByPosition(position);
        color=m.getColor();
        emoji=m.getEmoji();
        emojitxt = (TextView) findViewById(R.id.emojitxt);
        txtViewTitle = (TextView) findViewById(R.id.txtViewTitle);
        txtViewNota = (TextView) findViewById(R.id.txtViewNota);
        //imageView2 = (ImageView) findViewById(R.id.imageView2);
        //inizializzare opportunamente emoji
        setColorOnTitleAndText();
        // if encrypted
        if (dao.isEncrypted(position)){
            password = bun.getString(DAO.PASSWORD);
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
                b.putInt(Values.BUNDLE_KEY, position);
                b.putString(DAO.PASSWORD, password);
                intent.putExtras(b);
                startActivity(intent);
                //vai all'activity della creazione/modifica in modalità modifica;
            }
        });
    }

    public void deleteThisMemo(){
        DAO dao=new DAO(this);
        dao.open();
        int id=dao.findIdByPosition(position);
        dao.deleteMemoByIdFromDB(id);
        finish();
    }

    public static Activity getInstance(){
        return refer;
    }

    public void setColorOnTitleAndText(){
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
    }
    public boolean isEncrypted(){
        //mostra alert dialog ,sull ok memorizza la password nella stringa password
        DAO dao=new DAO(this);
        dao.open();//necessaria??
        return dao.isEncrypted(position);
    }

    public void insertEncryptToPasswordAndText(String password){
        //mostra alert dialog ,sull ok memorizza la password nella stringa password
        this.password = password;
        DAO dao=new DAO(this);
        dao.open();
        dao.addEncryptionToPasswordAndText(position,password);
    }

    public void deleteEncryptionToPasswordAndText(String password){
        //mostra alert dialog ,sull ok memorizza la password nella stringa password
        DAO dao=new DAO(this);
        dao.open();
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
    // onClick per il pulsante elimina e encode
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                        //set message, title, and icon
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
                //Inizio alert
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.encode_layout,
                        null, false);

                final EditText nameEditText = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);
                final EditText nameEditText2 = (EditText) formElementsView
                        .findViewById(R.id.nameEditText2);

                //alert dialog
                new AlertDialog.Builder(ShowMemo.this).setView(formElementsView)
                        .setTitle(R.string.insertPsw)
                        .setIcon(R.mipmap.lock_finale)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                if(nameEditText.getText().toString().equals(nameEditText2.getText().toString())&&!nameEditText.equals("")) {
                                    insertEncryptToPasswordAndText(nameEditText.getText().toString());
                                    dialog.cancel();
                                    invalidateOptionsMenu();
                                }else{
                                    Toast.makeText(ShowMemo.this,R.string.pswMatch, Toast.LENGTH_SHORT).show();
                                    nameEditText.setText("");
                                    nameEditText2.setText("");
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
                        .show();
                return true;
            case R.id.action_decode:
                AlertDialog myQuittingDialogB =new AlertDialog.Builder(this)
                        //set message, title, and icon
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}