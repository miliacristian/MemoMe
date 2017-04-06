package com.error404.memome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class EmojiAdapter extends ArrayAdapter<Integer> {
    public EmojiAdapter(Context c, int textViewId, ArrayList<Integer> emojiList){
        super(c,textViewId,emojiList);
    }

    @Override
    public View getView(int position , View convertView, ViewGroup parent){
        View rowView=convertView;
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.emoji_layout, null);
        }
        TextView emojis = (TextView) rowView.findViewById(R.id.emojiV);
        if (emojis != null){
            if(position==Values.INDEX_EMPTY_EMOJI){
                emojis.setText(getContext().getResources().getString(R.string.nessuna_emoji));
            }
            else {
                emojis.setText(Memo.getEmojiByUnicode(Memo.getEmoji(position)));
            }
        }
        return rowView;
    }
}
