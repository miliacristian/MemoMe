package com.error404.memome.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.error404.memome.Adapters.ColorAdapter;
import com.error404.memome.Adapters.EmojiAdapter;
import com.error404.memome.DB.DAO;
import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Entities.Memo;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;

import java.util.ArrayList;

//snellire oncreate
public class ModifyOrAddActivity extends AppCompatActivity {
    private DAO dao;
    private ArrayList<Integer> emojiList=new ArrayList<Integer>();
    private Memo currentMemo;
    private int emoji= Values.DEFAULT_EMOJI;
    private int color=Values.DEFAULT_COLOR;
    private int id;
    private EditText textModify;
    private EditText titleModify;
    private TextView emojiModify;
    private ImageView colorModify;
    private EmojiAdapter emAdapt;
    private ColorAdapter colorAdapt;
    private String mode=Values.EMPTY_STRING;
    private final String ADD_MODE="addMode";
    private final String MODIFY_MODE="modifyMode";
    private Toast mToast;
    private String password;
    private Bundle bundleState=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create of modify");
        if(savedInstanceState!=null){
            bundleState=savedInstanceState;
        }
        setContentView(R.layout.activity_modify_or_add);
        openDB();
        handleBundleFromPreviousActivity();
        initializeGuiAndListener();
    }
    public void initializeGuiAndListener(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textModify=(EditText)findViewById(R.id.textModify);
        titleModify=(EditText)findViewById(R.id.titleModify);
        emojiModify=(TextView) findViewById(R.id.emojiModify);
        emojiModify.setClickable(true);
        if(bundleState!=null){
            emoji=bundleState.getInt("emoji");
            color=bundleState.getInt("color");
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
            if(emoji==Values.INDEX_EMPTY_EMOJI){
                emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
            }
            else {
                emojiModify.setText(Memo.getEmojiByUnicode(emoji));
            }
        }
        else {
            emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
        }
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

        if(id==Values.NO_ID){
            mode=ADD_MODE;
        }
        else{
            mode=MODIFY_MODE;
        }
        if(mode.equals(MODIFY_MODE)){
            currentMemo= dao.loadMemoById(id);
            if(currentMemo.getEncryption()== Values.TRUE){
                dao.decryptText(currentMemo,password);
            }
            textModify.setText(currentMemo.getText());
            titleModify.setText(currentMemo.getTitle());
            if(bundleState!=null){
                color=bundleState.getInt("color");
                emoji=bundleState.getInt("emoji");
            }
            else {
                color = currentMemo.getColor();
                emoji = currentMemo.getEmoji();
            }
            if(emoji==Values.INDEX_EMPTY_EMOJI){
                emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
            }
            else {
                emojiModify.setText(Memo.getEmojiByUnicode(emoji));
            }
            setColorOnTitleAndText();
        }
        else{
            getSupportActionBar().setTitle(R.string.createMemo);
        }
        FloatingActionButton buttonSaveMemo = (FloatingActionButton) findViewById(R.id.fab2);
        buttonSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleModify.getText().toString();
                if(!title.equals(Values.EMPTY_STRING)) {
                    saveMemo();
                }
                else{
                    if (mToast != null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(ModifyOrAddActivity.this, R.string.needTitle, Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });

        return;
    }
    public void handleBundleFromPreviousActivity(){
        Intent intent=getIntent();
        Bundle bun=intent.getExtras();
        password = bun.getString(Values.PASSWORD);
        id=bun.getInt(Values.BUNDLE_KEY);
        return;
    }
    public void openDB(){
        dao = new DAO(this);
        dao.open();
        return;
    }
    public void setColorOnTitleAndText(){
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),color));
        int actionColor;
        if (color != R.color.white){
            actionColor = Memo.darkerColor(color);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(actionColor);
        }else{
            actionColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
            int statusColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusColor);
        }
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(actionColor));
    }

    public void alertDialogChooseColor() {
        ArrayList<Integer> colorsList;
        LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customView = inflater.inflate(R.layout.list_view_color, null, false);
        ListView listView=(ListView)customView.findViewById(R.id.listColor);
        colorsList=Memo.getColorsList();
        colorAdapt =new ColorAdapter(ModifyOrAddActivity.this,R.layout.color_layout,colorsList);
        listView.setAdapter(colorAdapt);
        listView.setScrollbarFadingEnabled(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyOrAddActivity.this);
        builder.setView(customView);
        builder.setTitle(R.string.chooseColor);
        builder.setIcon(R.mipmap.palette_icon);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog=builder.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                color=Memo.getColors(position);
                setColorOnTitleAndText();
                    alertDialog.dismiss();
            }
        });
    }

    public void alertChooseEmoji(){
            LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View customView = inflater.inflate(R.layout.list_view_emoji, null, false);
            ListView listView=(ListView)customView.findViewById(R.id.listV);
            emojiList=Memo.getListEmojis();
            emAdapt =new EmojiAdapter(ModifyOrAddActivity.this,R.layout.emoji_layout,emojiList);
            listView.setAdapter(emAdapt);
            listView.setScrollbarFadingEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(ModifyOrAddActivity.this);
            builder.setView(customView);
            builder.setTitle(R.string.chooseEmoji);
            builder.setIcon(R.mipmap.smile_icon);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alertDialog=builder.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    emoji=Memo.getEmoji(position);
                    if(position==Values.INDEX_EMPTY_EMOJI){
                        emojiModify.setText(getApplicationContext().getResources().getString(R.string.clickMe));
                        alertDialog.dismiss();
                    }
                    else {
                        emojiModify.setText(Memo.getEmojiByUnicode(emoji));
                        alertDialog.dismiss();
                    }
                }
            });
        }
        public void saveMemo(){
            String title=titleModify.getText().toString();
                String text = textModify.getText().toString();
            if (mode.equals(ADD_MODE)) {
                dao.addMemoToDB(title, text,emoji, color);
                finish();
            } else {
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
                ShowMemoActivity.getInstance().finish();
                finish();
            }
        }
    @Override
    public void onBackPressed(){
        if (mode.equals(MODIFY_MODE)){
            if(modifiedMemo()) {
                alertCloseActivity();
            }else{
                finish();
            }
        }else if(mode.equals(ADD_MODE)){
            if (NotEmptyMemoExit()){
                alertCloseActivity();
            }else{
                finish();
            }
        }else {
            alertCloseActivity();
        }
    }

    public boolean NotEmptyMemoExit(){
        if(Values.EMPTY_STRING.equals(titleModify.getText().toString())
                && Values.EMPTY_STRING.equals(textModify.getText().toString())){
            return false;
        }else{
            return true;
        }
    }
    public boolean modifiedMemo(){
     if(currentMemo.getTitle().equals(titleModify.getText().toString())
             && currentMemo.getText().equals(textModify.getText().toString())
             && currentMemo.getEmoji() == emoji
             && currentMemo.getColor() == color){
         return false;
     }else{
         return true;
     }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void alertCloseActivity(){
        new AlertDialog.Builder(ModifyOrAddActivity.this)
                .setTitle(R.string.confirm_close_title)
                .setMessage(R.string.confirm_close_text)
                .setIcon(R.mipmap.info_icon)
                .setPositiveButton(R.string.confirm_close_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String title = titleModify.getText().toString();
                                if (!title.equals(Values.EMPTY_STRING)) {
                                    saveMemo();
                                    dialog.cancel();
                                }
                                else{
                                    if (mToast != null){
                                        mToast.cancel();
                                    }
                                    mToast = Toast.makeText(ModifyOrAddActivity.this, R.string.needTitle, Toast.LENGTH_SHORT);
                                    mToast.show();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.confirm_close_no,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        dialog.cancel();
                    }
        })
                .setNeutralButton(R.string.cancel,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    @Override
    public void onDestroy(){//chiudi il database nel momento della distruzione dell'activity
        super.onDestroy();
        dao.close();
        return;
    }
    @Override
    public void onSaveInstanceState(Bundle keepState){
        super.onSaveInstanceState(keepState);
        keepState.putInt("color",color);
        keepState.putInt("emoji",emoji);
        return;
    }
}
