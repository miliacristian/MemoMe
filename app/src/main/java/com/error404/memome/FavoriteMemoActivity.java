package com.error404.memome;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
//snellire oncreate
public class FavoriteMemoActivity extends AppCompatActivity {
    private DAO dao;
    private ListView myListView;
    private ArrayList<Memo> memoList = new ArrayList<Memo>();
    private MemoAdapter mem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_memo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.favoriteActivityTitle);
        dao = new DAO(this);
        dao.open();
        memoList = dao.loadAllFavoriteMemo();
        mem = new MemoAdapter(this, R.layout.raw_layout_favorite, memoList);
        myListView = (ListView) findViewById(R.id.listOfNotes);
        myListView.setAdapter(mem);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                goToShowMemoActivity(memoList.get(position).getId());
            }
        });
    }
    public void goToShowMemoActivity(int id){
        Memo m;
        m=dao.loadMemoById(id);
        if (m.getEncryption() == Values.TRUE) {
            alertEncrypted(id);
        }else {
            Intent myIntent = new Intent(FavoriteMemoActivity.this, ShowMemo.class);
            Bundle bun = new Bundle();
            bun.putInt(Values.BUNDLE_KEY,id);
            myIntent.putExtras(bun);
            startActivity(myIntent);
        }
    }
    public void alertEncrypted(int id) {
        final int idMemo=id;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.password_layout,
                null, false);

        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);
        new AlertDialog.Builder(FavoriteMemoActivity.this).setView(formElementsView)
                .setTitle(R.string.warningMemoEncoded)
                .setMessage(R.string.warningMemoEncodedText)
                .setIcon(R.mipmap.lock_finale)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                String passFromDB;
                                String cifratedPassword =Encrypt.encryption(nameEditText.getText().toString(), nameEditText.getText().toString());
                                passFromDB=dao.loadMemoById(idMemo).getPassword();
                                if (cifratedPassword.equals(passFromDB)) {
                                    Intent myIntent = new Intent(FavoriteMemoActivity.this, ShowMemo.class);
                                    Bundle bun = new Bundle();
                                    bun.putInt(Values.BUNDLE_KEY,idMemo);
                                    bun.putString(DAO.PASSWORD, nameEditText.getText().toString());
                                    myIntent.putExtras(bun);
                                    startActivity(myIntent);
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(FavoriteMemoActivity.this, R.string.incorrectPsw, Toast.LENGTH_SHORT).show();
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

    }
    @Override
    public void onResume(){
        super.onResume();
        memoList = dao.loadAllFavoriteMemo();
        mem = new MemoAdapter(this, R.layout.raw_layout_favorite, memoList);
        myListView.setAdapter(mem);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
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
    public void onRestart(){
        super.onRestart();
        Memo.reLoadNameColor();
    }
    @Override
    public void onStart(){
        super.onStart();
        Memo.reLoadNameColor();
    }
    @Override
    public void onPause(){
        super.onPause();
        Memo.reLoadNameColor();
    }
    @Override
    public void onStop(){
        super.onStop();
        Memo.reLoadNameColor();
    }
}
