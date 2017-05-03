package com.error404.memome.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.error404.memome.Entities.Memo;
import com.error404.memome.Activities.MainActivity;
import com.error404.memome.R;

import java.util.ArrayList;

public class ColorAdapter extends ArrayAdapter<Integer> {//Classe adapter per Creare una lista di colori
    // dove ogni riga è composta da un colore e il suo nome
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
            rowView = inflater.inflate(R.layout.color_layout, null);//gonfia il layout color_Layout
        }
        TextView colorText = (TextView) rowView.findViewById(R.id.colorTxt);
        TextView colorView = (TextView) rowView.findViewById(R.id.colorView);
        //inizializza la GUI
        if (colorText != null){
                colorText.setText(getNameColorsList().get(position));
        }
        if (colorView != null){
            colorView.setBackgroundColor(ContextCompat.getColor(getContext(), Memo.getColors(position)));
        }
        return rowView;
    }

    public ArrayList<String> getNameColorsList(){
        final String[] COLORS_NAME = { MainActivity.getIstanceContext().getResources().getString(R.string.bianco), MainActivity.getIstanceContext().getResources().getString(R.string.rosso), MainActivity.getIstanceContext().getResources().getString(R.string.viola), MainActivity.getIstanceContext().getResources().getString(R.string.rosa), MainActivity.getIstanceContext().getResources().getString(R.string.lime), MainActivity.getIstanceContext().getResources().getString(R.string.celeste), MainActivity.getIstanceContext().getResources().getString(R.string.indaco),
                MainActivity.getIstanceContext().getResources().getString(R.string.grigio), MainActivity.getIstanceContext().getResources().getString(R.string.verde), MainActivity.getIstanceContext().getResources().getString(R.string.ciano), MainActivity.getIstanceContext().getResources().getString(R.string.marrone)};
        ArrayList<String> listColors=new ArrayList<String>();
        for(int i=0;i<COLORS_NAME.length;i++){
            listColors.add(COLORS_NAME[i]);
        }
        return listColors;
    }
}
