package com.error404.memome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

//snellire oncreate
public class ShowMemo extends AppCompatActivity {
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
    private Toast nougatToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memo);
        refer = this;
        openDB();
        //Intent intent = getIntent();
        //Bundle bun = intent.getExtras();
        //id = bun.getInt(Values.BUNDLE_KEY);
        handleBundleFromPreviousActivity();
        initializeGuiAndListener();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(R.string.showMemoTitle);
        //dao = new DAO(this);
        //dao.open();
        /*Memo m=dao.loadMemoById(id);
        color=m.getColor();
        emoji=m.getEmoji();
        emojitxt = (TextView) findViewById(R.id.emojitxt);
        txtViewTitle = (TextView) findViewById(R.id.txtViewTitle);
        txtViewNota = (TextView) findViewById(R.id.txtViewNota);
        setColorOnTitleAndText();
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
                Intent intent = new Intent(ShowMemo.this, activity_modifyOrAdd.class);
                Bundle b = new Bundle();
                b.putInt(Values.BUNDLE_KEY, id);
                b.putString(DAO.PASSWORD, password);
                intent.putExtras(b);
                startActivity(intent);
            }
        });*/
    }
    public void initializeGuiAndListener(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.showMemoTitle);
        //dao = new DAO(this);
        //dao.open();
        Memo m=dao.loadMemoById(id);
        color=m.getColor();
        emoji=m.getEmoji();
        emojitxt = (TextView) findViewById(R.id.emojitxt);
        txtViewTitle = (TextView) findViewById(R.id.txtViewTitle);
        txtViewNota = (TextView) findViewById(R.id.txtViewNota);
        setColorOnTitleAndText();
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
                Intent intent = new Intent(ShowMemo.this, activity_modifyOrAdd.class);
                Bundle b = new Bundle();
                b.putInt(Values.BUNDLE_KEY, id);
                b.putString(DAO.PASSWORD, password);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        int actionColor = Memo.darkerColor(color);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(actionColor));
    }
    public void handleBundleFromPreviousActivity(){
        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        id = bun.getInt(Values.BUNDLE_KEY);
        password = bun.getString(DAO.PASSWORD);//può essere null
        return;
    }
    public void openDB(){
        dao = new DAO(this);
        dao.open();
        return;
    }
    public void deleteThisMemo(){
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
        return dao.isEncrypted(id);
    }

    public boolean isFavorite(){
        return dao.isFavorite(id);
    }

    public void insertEncryptToPasswordAndText(String password){

        this.password = password;
        dao.addEncryptionToPasswordAndText(id,password);
    }

    public void deleteEncryptionToPasswordAndText(String password){
        dao.deleteEncryptionToPasswordAndText(id,password);
    }
    public void addToFavorites(){
        dao.addToFavorites(id);
        return;
    }
    public void deleteFromFavorites(){
        dao.deleteFromFavorites(id);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.encode_layout,
                        null, false);
                final EditText nameEditText = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);
                final EditText nameEditText2 = (EditText) formElementsView
                        .findViewById(R.id.nameEditText2);
                new AlertDialog.Builder(ShowMemo.this).setView(formElementsView)
                        .setTitle(R.string.insertPsw)
                        .setIcon(R.mipmap.lock_finale)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                if(TextUtils.isEmpty(nameEditText.getText())){
                                    if(emptyToast!= null){
                                        emptyToast.cancel();
                                    }
                                    emptyToast=Toast.makeText(ShowMemo.this,R.string.emptyPass, Toast.LENGTH_SHORT);
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
                                        matchTaost = Toast.makeText(ShowMemo.this, R.string.pswMatch, Toast.LENGTH_SHORT);
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
                        .show();}
                else{
                    if (nougatToast != null){
                        nougatToast.cancel();
                    }
                    nougatToast = Toast.makeText(ShowMemo.this,R.string.nougatToast, Toast.LENGTH_SHORT);
                    nougatToast.show();
                }
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
    public void onDestroy() {
        super.onDestroy();
        dao.close();
    }
    //onresume non fare niente perchè si ritorna qui solo nel caso in cui la nota non cambia cliccando tasto indietro dalla modify
}