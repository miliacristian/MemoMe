package com.error404.memometwo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by cristian on 16/03/17.
 */

public class EmojiAdapter extends ArrayAdapter<Integer> {
    private ArrayList<Integer> emojiList;
    public EmojiAdapter(Context c, int textViewId, ArrayList<Integer> emojiList){
        super(c,textViewId,emojiList);
        this.emojiList=emojiList;
    }
    @Override
    public View getView(int position , View convertView, ViewGroup parent){//chiamata automaticamente ogni volta che si deve caricare
        //una linea,position parte da 0 e viene automaticamente incrementato
        View rowView=convertView;
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.emoji_layout, null);
        }
        //Integer emoji= emojiList.get(position);
            TextView emojis = (TextView) rowView.findViewById(R.id.emojiV);
            if (emojis != null){//per ogni variabile grafica(es textView) fare if e settare con valori opportuni
                emojis.setText(Memo.getEmojiByUnicode(Memo.getEmoji(position)));
            }
        return rowView;
    }
}
