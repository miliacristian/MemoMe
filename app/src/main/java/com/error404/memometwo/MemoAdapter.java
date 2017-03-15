package com.error404.memometwo;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cristian on 19/02/17.
 */
//String color="#ffffff"; funziona avendo la stringa colore con #numero
//int c=Color.parseColor(color);
//title.setBackgroundColor(c);

//title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));//per settare il colore da color.xml,
// (getapplicationcontext al posto di getcontext se non funziona)
public class MemoAdapter extends ArrayAdapter<Memo> {
    private ArrayList<Memo> memoList;
    public MemoAdapter(Context c, int textViewId, ArrayList<Memo> memoList){
        super(c,textViewId,memoList);
        this.memoList=memoList;//ho tutti i riferimenti alle memo
    }
    @Override
    public View getView(int position , View convertView, ViewGroup parent){//chiamata automaticamente ogni volta che si deve caricare
        //una linea,position parte da 0 e viene automaticamente incrementato
        View rowView=convertView;
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rawlayout, null);
        }
        Memo memo= memoList.get(position);//prendo il memo in posizione i-esima
        if (memo != null) {
            TextView emoji = (TextView) rowView.findViewById(R.id.emoji1);
            TextView dateCreation = (TextView) rowView.findViewById(R.id.datecreation);
            TextView title = (TextView) rowView.findViewById(R.id.title1);
            TextView dateLastModify = (TextView) rowView.findViewById(R.id.lastmodify);
            LinearLayout rowListView=(LinearLayout)rowView.findViewById(R.id.linearExternal);
            if(rowListView!=null) {
                rowListView.setBackgroundColor(ContextCompat.getColor(getContext(),memo.getColor()));

            }
            if (emoji != null){//per ogni variabile grafica(es textView) fare if e settare con valori opportuni
                emoji.setText(Memo.getEmojiByUnicode(memo.getEmoji()));

            }
            if(dateCreation!=null){
                dateCreation.setText(memo.dateCreationConverter(memo.getDateCreation()));

            }
            if(dateLastModify!=null){
                dateLastModify.setText(memo.dateLastModifyConverter(memo.getLastModify()));
            }
            if(title!=null){
                title.setText(memo.getTitle());
               // title.setBackgroundColor(ContextCompat.getColor(getContext(),getColorByList(memo.getColor())));
            }
        }
        return rowView;
    }
   // public int getColorByList(int itemPosition){
       // return Memo.colors[itemPosition];
   // }
}
