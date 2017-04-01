package com.error404.memome;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
//finita e ordinata
public class MemoAdapter extends ArrayAdapter<Memo> implements Filterable {
    private ArrayList<Memo> memoList;
    private Filter taskFilter;
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
                dateCreation.setText(getContext().getResources().getString(R.string.created)+ memo.dateCreationConverter(memo.getDateCreation()));

            }
            if(dateLastModify!=null){
                dateLastModify.setText(getContext().getResources().getString(R.string.modified)+memo.dateLastModifyConverter(memo.getLastModify()));
            }
            if(title!=null){
                title.setText(memo.getTitle());
            }
        }
        return rowView;
    }
    /*@Override
    public Filter getFilter() {
        if (taskFilter == null)
            taskFilter = new TaskFilter();

        return taskFilter;
    }*/

    /*private class TaskFilter extends Filter {

        @Override
        protected FilterResults performFiltering (CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null) {
                results.values = memoList;
                results.count = memoList.size();
            } else {
                ArrayList<Memo> newTaskList = new ArrayList<Memo>();
                for (Memo t : memoList) {
                    if (t.getTitle().toLowerCase().contains(constraint.toString())) {
                        newTaskList.add(t);
                    }
                }
                results.values = newTaskList;
                results.count = newTaskList.size();
            } return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                memoList = (ArrayList<Memo>)results.values;
                notifyDataSetChanged();
            }

        }
    }
    @Override
    public int getCount()
    {
        return memoList.size();
    }
*/
}
