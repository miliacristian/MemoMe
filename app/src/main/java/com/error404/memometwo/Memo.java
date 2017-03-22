package com.error404.memometwo;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;

import static java.security.AccessController.getContext;
//ordinata,manca la parte delle emoji in xml
public class Memo {
    private static final int[]COLORS={R.color.white, R.color.pink,R.color.lightBlue,R.color.lime};
    //private static final int[]EMOJIS={R.integer.emojiRide1,R.integer.emojiSbadata2,R.integer.emojiRide3,R.integer.emojiFlirt4,R.integer.emojiLove5};
    private static final int[]EMOJIS={0x1f604,0x1f605,0x1f606,0x1f609,0x1f60d};
    private int id;
    private String title;
    private String text;
    private int color;
    private int emoji;
    private Calendar dateCreation;
    private Calendar lastModify;
    private int encryption;
    private String password;

    public Memo(int id,String title,String text,int color,int emoji,int datecreation[],int datelastmodify[],int encryption,String password){

        this.title = title;
        this.id = id;
        this.text = text;
        this.color = color;
        this.emoji = emoji;
        this.encryption =encryption;
        this.password = password;
        this.dateCreation=Calendar.getInstance();
        this.lastModify=Calendar.getInstance();
        this.dateCreation.set(datecreation[2], datecreation[1]+1, datecreation[0]);
        this.lastModify.set(datelastmodify[2], datelastmodify[1]+1, datelastmodify[0]);
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

    public void setLastModify(Calendar lastModify) {
        this.lastModify = lastModify;
    }

    public Calendar getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Calendar dateCreation) {
        this.dateCreation = dateCreation;
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
    //public String toString(){
        //return id+" "+title+" "+text+" "+color+" "+dateCreation+" "+lastModify+" "+encryption;
    //}
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
}