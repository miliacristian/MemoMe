package com.error404.memome;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ColorAdapter extends ArrayAdapter<Integer> {
    public ColorAdapter(Context c, int textViewId, ArrayList<Integer> colorList){
        super(c,textViewId,colorList);
    }

    @Override
    public View getView(int position , View convertView, ViewGroup parent){//chiamata automaticamente ogni volta che si deve caricare
        //una linea,position parte da 0 e viene automaticamente incrementato
        View rowView=convertView;
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.color_layout, null);
        }
        TextView colorText = (TextView) rowView.findViewById(R.id.colorTxt);
        TextView colorView = (TextView) rowView.findViewById(R.id.colorView);
        if (colorText != null){
                colorText.setText(getNameColorsList().get(position));
        }
        if (colorView != null){
            colorView.setBackgroundColor(ContextCompat.getColor(getContext(),Memo.getColors(position)));
        }
        return rowView;
    }

    public ArrayList<String> getNameColorsList(){
        final String[] COLORS_NAME = { MemoMeMain.getIstanceContext().getResources().getString(R.string.bianco),MemoMeMain.getIstanceContext().getResources().getString(R.string.rosso),MemoMeMain.getIstanceContext().getResources().getString(R.string.viola),MemoMeMain.getIstanceContext().getResources().getString(R.string.rosa),MemoMeMain.getIstanceContext().getResources().getString(R.string.lime),MemoMeMain.getIstanceContext().getResources().getString(R.string.celeste),MemoMeMain.getIstanceContext().getResources().getString(R.string.indaco),
                MemoMeMain.getIstanceContext().getResources().getString(R.string.grigio),MemoMeMain.getIstanceContext().getResources().getString(R.string.verde),MemoMeMain.getIstanceContext().getResources().getString(R.string.ciano),MemoMeMain.getIstanceContext().getResources().getString(R.string.marrone)};
        ArrayList<String> listColors=new ArrayList<String>();
        for(int i=0;i<COLORS_NAME.length;i++){
            listColors.add(COLORS_NAME[i]);
        }
        return listColors;
    }
}
