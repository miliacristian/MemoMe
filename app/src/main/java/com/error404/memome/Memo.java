package com.error404.memome;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;

public class Memo {
    private static final int[]COLORS={R.color.white,R.color.red,R.color.purple, R.color.pink,R.color.lime,R.color.lightBlue,R.color.indigo,R.color.grey,R.color.green,R.color.cyan,R.color.brown};
    private static final int[]EMOJIS={MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.emptyEmoji),MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.emojiRide1),MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.emojiSbadata2),
            MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.emojiRide3),MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.emojiFlirt4),MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.emojiLove5),
            MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.tongue),MemoMeMain.getIstanceContext().getResources().getInteger(R.integer.funny)};
    private static  String[] COLORS_NAME = { MemoMeMain.getIstanceContext().getResources().getString(R.string.bianco),MemoMeMain.getIstanceContext().getResources().getString(R.string.rosso),MemoMeMain.getIstanceContext().getResources().getString(R.string.viola),MemoMeMain.getIstanceContext().getResources().getString(R.string.rosa),MemoMeMain.getIstanceContext().getResources().getString(R.string.lime),MemoMeMain.getIstanceContext().getResources().getString(R.string.celeste),MemoMeMain.getIstanceContext().getResources().getString(R.string.indaco),
            MemoMeMain.getIstanceContext().getResources().getString(R.string.grigio),MemoMeMain.getIstanceContext().getResources().getString(R.string.verde),MemoMeMain.getIstanceContext().getResources().getString(R.string.ciano),MemoMeMain.getIstanceContext().getResources().getString(R.string.marrone)};
    private int id;
    private String title;
    private String text;
    private int color;
    private int emoji;
    private Calendar dateCreation;
    private Calendar lastModify;
    private int favorite;
    private int encryption;
    private String password;

    public Memo(int id,String title,String text,int color,int emoji,int datecreation[],int datelastmodify[],int encryption,String password,int favorite){

        this.title = title;
        this.id = id;
        this.text = text;
        this.color = color;
        this.emoji = emoji;
        this.encryption =encryption;
        this.password = password;
        this.dateCreation=Calendar.getInstance();
        this.lastModify=Calendar.getInstance();
        this.dateCreation.set(datecreation[Values.INDEX_YEAR], datecreation[Values.INDEX_MONTH]+1, datecreation[Values.INDEX_DAY]);
        this.lastModify.set(datelastmodify[Values.INDEX_YEAR], datelastmodify[Values.INDEX_MONTH]+1, datelastmodify[Values.INDEX_DAY]);
        this.favorite=favorite;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getEmoji() {
        return emoji;
    }

    public void setEmoji(int emoji) {
        this.emoji = emoji;
    }

    public Calendar getLastModify() {
        return lastModify;
    }

    public Calendar getDateCreation() {
        return dateCreation;
    }
    public void setFavorite(int favorite){
        this.favorite=favorite;
    }
    public int getFavorite(){
        return this.favorite;
    }
    public int getEncryption() {
        return encryption;
    }

    public void setEncryption(int encryption) {
        this.encryption = encryption;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String dateCreationConverter(Calendar date){
        return date.get(Calendar.DAY_OF_MONTH)+"/"+date.get(Calendar.MONTH)+"/"+date.get(Calendar.YEAR);
    }
    public String dateLastModifyConverter(Calendar date){
        return date.get(Calendar.DAY_OF_MONTH)+"/"+date.get(Calendar.MONTH)+"/"+date.get(Calendar.YEAR);
    }
    public static int getColors(int index){
        return COLORS[index];
    }
    public static int getEmoji(int index){
        return EMOJIS[index];
    }
    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
    public static ArrayList<Integer> getListEmojis(){
        ArrayList<Integer> listEmojis=new ArrayList<Integer>();
        for(int i=0;i<EMOJIS.length;i++){
            listEmojis.add(EMOJIS[i]);
        }
        return listEmojis;
    }
    public static ArrayList<Integer> getColorsList(){
        ArrayList<Integer> listColors=new ArrayList<Integer>();
        for(int i=0;i<COLORS.length;i++){
            listColors.add(COLORS[i]);
        }
        return listColors;
    }

    public boolean isEncrypted(){
        if(this.encryption==Values.TRUE){
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return this.title.toLowerCase();
    }

    public static int darkerColor(int color){
        int darkColor;
        if (color != R.color.white){
            float ratio = 1.0f - 0.2f;
            int a = (color >> 24) & 0xFF;
            int r = (int) (((color >> 16) & 0xFF) * ratio);
            int g = (int) (((color >> 8) & 0xFF) * ratio);
            int b = (int) ((color & 0xFF) * ratio);
            darkColor =  (a << 24) | (r << 16) | (g << 8) | b;
        }else{
            darkColor = R.color.colorAccent;
        }
        return darkColor;
    }
}