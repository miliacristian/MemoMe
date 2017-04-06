package com.error404.memome;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MemoAdapter extends ArrayAdapter<Memo>  {
    private ArrayList<Memo> memoList;
    private int textViewId;

    public MemoAdapter(Context c, int textViewId, ArrayList<Memo> memoList){
        super(c,textViewId,memoList);
        this.memoList=memoList;//ho tutti i riferimenti alle memo
        this.textViewId = textViewId;
    }
    @Override
    public View getView(int position , View convertView, ViewGroup parent){//chiamata automaticamente ogni volta che si deve caricare
        //una linea,position parte da 0 e viene automaticamente incrementato
        View rowView=convertView;
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(textViewId, null);
        }
        Memo memo= memoList.get(position);//prendo il memo in posizione i-esima
        if (memo != null) {
            ImageView star=(ImageView)rowView.findViewById(R.id.imageFavorite);
            TextView emoji = (TextView) rowView.findViewById(R.id.emoji1);
            TextView dateCreation = (TextView) rowView.findViewById(R.id.datecreation);
            TextView title = (TextView) rowView.findViewById(R.id.title1);
            TextView dateLastModify = (TextView) rowView.findViewById(R.id.lastmodify);
            LinearLayout rowListView=(LinearLayout)rowView.findViewById(R.id.linearExternal);
            if(rowListView!=null) {
                rowListView.setBackgroundColor(ContextCompat.getColor(getContext(),memo.getColor()));
            }
            if (emoji != null){//per ogni variabile grafica settarla con valori opportuni
                emoji.setText(Memo.getEmojiByUnicode(memo.getEmoji()));

            }
            if(dateCreation!=null){
                dateCreation.setText(getContext().getResources().getString(R.string.created)+ memo.dateCreationConverter(memo.getDateCreation()));

            }
            if(dateLastModify!=null){
                dateLastModify.setText(getContext().getResources().getString(R.string.modified)+memo.dateLastModifyConverter(memo.getLastModify()));
            }
            if(title!=null){
                title.setText(memo.getTitle());
            }
            if(star!=null){
                if(memoList.get(position).getFavorite()==Values.TRUE){
                    star.setImageResource(R.mipmap.star_icon);
                }
                else{
                    star.setImageResource(R.mipmap.icon_empy_star);
                }

            }
        }
        return rowView;
    }
}
