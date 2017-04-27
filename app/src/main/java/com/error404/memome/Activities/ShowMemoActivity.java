package com.error404.memome.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.error404.memome.DB.DAO;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");
        if(savedInstanceState!=null){
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
    public void handleBundleFromPreviousActivity(){
        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        id = bun.getInt(Values.BUNDLE_KEY);
        if(bundleState!=null){
            password=bundleState.getString("password");
        }
        else {
            password = bun.getString(Values.PASSWORD);//può essere null
        }
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
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.encode_layout,
                        null, false);
                final EditText nameEditText = (EditText) formElementsView
                        .findViewById(R.id.nameEditText);
                final EditText nameEditText2 = (EditText) formElementsView
                        .findViewById(R.id.nameEditText2);
                new AlertDialog.Builder(ShowMemoActivity.this).setView(formElementsView)
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
                        .show();
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
    @Override
    public void onSaveInstanceState(Bundle keepState){
        super.onSaveInstanceState(keepState);
        keepState.putString("password",password);
        return;
    }
}