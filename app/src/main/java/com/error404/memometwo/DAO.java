package com.error404.memometwo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
//finita ordinata nei metodi,da riorganizzare in codice,aggiungere async task
public class DAO {
    private DatabaseHelper dbh;
    private SQLiteDatabase database;
    private final static String SELECT_ALL="SELECT * FROM memos ";
    public DAO(Context context) {
        dbh = new DatabaseHelper(context);
    }
    public void open() {
        database = dbh.getWritableDatabase();
    }

    public void close() {
        database.close();
    }


    public int findIdByPosition(int position){
        int id;
        Cursor c=getRowSort();
        if(c!=null) {
            c.moveToFirst();
            String actualSortType = c.getString(c.getColumnIndex("sorttype"));
            String actualAscDesc = c.getString(c.getColumnIndex("ascdesc"));
            String sortMethod = "order by" + " " + actualSortType + " " + actualAscDesc;
            String sql = SELECT_ALL + sortMethod;
            c.close();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor != null) {
                cursor.moveToFirst();
                cursor.moveToPosition(position);
                id = cursor.getInt(cursor.getColumnIndex("_id"));
                cursor.close();
                return id;
            }
        }
        return -2;//non sono riuscito a trovare l'id dalla posizione
    }
    public Memo loadMemoByPosition(int position){
        int id=findIdByPosition(position);
        Memo m=loadMemoById(id);
        return m;
    }
    public Memo loadMemoById(int id){//carico memo da id(chiave primaria),cursore locale chiudibile
        Memo memo=null;
        String sql=SELECT_ALL+"where _id="+id;
        Cursor c=database.rawQuery(sql,null);
        if(c!=null) {
            c.moveToFirst();
            memo = loadMemoByCursorOneRow(c);
            c.close();
        }
        return memo;
    }

    public Memo loadMemoByCursorOneRow(Cursor c){//cursore chiuso nella loadMemoById
        //per loadAllMemo
        if(c!=null) {//cursore preso come parametro,chiudibile??
            int id = c.getInt(c.getColumnIndex("_id"));
            String title = c.getString(c.getColumnIndex("title"));
            String text = c.getString(c.getColumnIndex("text"));
            int color = c.getInt(c.getColumnIndex("color"));
            int emoji = c.getInt(c.getColumnIndex("emoji"));
            int datecreation[];//day,month,year
            int lastmodify[];//day,month,year
            datecreation = getDateCreation(c);
            lastmodify = getLastModify(c);
            int encryption = c.getInt(c.getColumnIndex("encryption"));
            String password = c.getString(c.getColumnIndex("password"));
            Memo memo = new Memo(id, title, text, color, emoji, datecreation, lastmodify, encryption, password);
            return memo;
        }
        return null;
    }
    public int[] getDateCreation(Cursor c){//ottiene data creazione da cursore
        int arr[]=new int[3];//cursore non chiudibile,usato successivamente nel metodo loadMemoByCursorOneRow
        arr[0]=c.getInt(c.getColumnIndex("daydatecreation"));
        arr[1]=c.getInt(c.getColumnIndex("monthdatecreation"));
        arr[2]=c.getInt(c.getColumnIndex("yeardatecreation"));
        return arr;
    }
    public int[] getLastModify(Cursor c){//ottiene data ultima modifica da cursore
        int arr[]=new int[3];//cursore non chiudibile,usato successivamente nel metodo loadMemoByCursorOneRow
        arr[0]=c.getInt(c.getColumnIndex("daylastmodify"));
        arr[1]=c.getInt(c.getColumnIndex("monthlastmodify"));
        arr[2]=c.getInt(c.getColumnIndex("yearlastmodify"));
        return arr;
    }




    public void fromCursorToList(ArrayList<Memo> arrMemo,Cursor cursor){//da tabella a lista di memo
        if(cursor!=null) {//chimato da loadAllMemo
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrMemo.add(loadMemoByCursorOneRow(cursor));
                cursor.moveToNext();
            }
        }
    }

    public void saveMemo(Memo memo,int id){//chiamato dall'activity quando si clicca il tasto salva si fa la get di tutti gli oggetti(id,colore,testo,titolo,no data creazione
        //no data ultima modifica
        //e si istanzia una memo;
        String title=memo.getTitle();
        int emoji=memo.getEmoji();
        String text=memo.getText();
        int color=memo.getColor();
        String password=memo.getPassword();
        int  encryption=memo.getEncryption();
        Calendar date=Calendar.getInstance();
        int month=date.get(Calendar.MONTH);
        int year=date.get(Calendar.YEAR);
        int day=date.get(Calendar.DAY_OF_MONTH);
        String sql="update memos set title="+Apex.open+title+Apex.close+","+"text="+Apex.open+text+Apex.close+","+"color="+color+","+"emoji="+emoji+","+"daylastmodify="+day+","+"monthlastmodify="+month+","+"yearlastmodify="+year+","+"encryption="+encryption+","+"password="+Apex.open+password+Apex.close+" where _id="+id;
        //System.out.println(sql);
        database.execSQL(sql);
    }
    public ArrayList<Memo>loadAllMemo(){//carica tutti i memo secondo l'ordinamento,vedendo dapprima il tipo di ordinamento e poi query
        Cursor c=getRowSort();//cursori locali chiudibili
        if(c!=null) {
            c.moveToFirst();
            String actualSortType = c.getString(c.getColumnIndex("sorttype"));
            String actualAscDesc = c.getString(c.getColumnIndex("ascdesc"));
            String sortMethod = "order by" +" "+actualSortType+" "+ actualAscDesc;
            c.close();
            //System.out.println("metodo ordinamento"+sortMethod);
            String sql = SELECT_ALL + sortMethod;
            Cursor tabAllMemos = database.rawQuery(sql, null);
            if(tabAllMemos!=null) {
                ArrayList<Memo> arrMemo = new ArrayList<Memo>();
                fromCursorToList(arrMemo, tabAllMemos);//aggiunge i memo all'arraylist
                tabAllMemos.close();
                return arrMemo;
            }
        }
        return null;
    }
    public void updateSort(String newSortType){//il menu optionbar deve passare la stringa newsortype alla
        //funzione update sort e fare il refresh della grafica;
        if(newSortType.equals("onlyUpdateGUI")){
            return;
        }
        Cursor c=getRowSort();//cursore locale chiudibile
        if(c!=null) {
            c.moveToFirst();
            String actualSortType = c.getString(c.getColumnIndex("sorttype"));
            //System.out.println(actualSortType);
            String actualAscDesc = c.getString(c.getColumnIndex("ascdesc"));
            //System.out.println(actualAscDesc);
            c.close();
            if (actualSortType.equals(newSortType)) {
                //System.out.println("stesso tipo");
                switchAscDescIntoDB(actualAscDesc);
            } else {
                //System.out.println("diverso tipo");
                updateOnlySortTypeIntoDB(newSortType);
            }
        }
        return;
    }
    public Cursor getRowSort(){
        Cursor c=null;//cursore locale da ritornare non chiudibile
        String sql="select ascdesc,sorttype from sort";//farla diventare private final static
        c = database.rawQuery(sql, null);
        return c;
    }
    public void switchAscDescIntoDB(String actualAscDesc){
        String sql="update sort SET ascdesc=";
        if(actualAscDesc.equals("asc")){
            String command=sql+"'desc'";
            database.execSQL(command);
        }
        else{
            String command=sql+"'asc'";
            database.execSQL(command);
        }
        return;
    }

    public void updateOnlySortTypeIntoDB(String newSortType){
        String sql="update sort SET sorttype="+"'"+newSortType+"'";
        database.execSQL(sql);
        return;
    }



    public void addMemoToDB(String title,String text,int emoji,int color){//chiamata quando si clicca
        // sul tasto salva sull'activity in modalità creazione! vb
        final int encryption=0;
        Calendar date=Calendar.getInstance();//data creazione(prende la data corrente)
        int month=date.get(Calendar.MONTH);
        int year=date.get(Calendar.YEAR);
        int day=date.get(Calendar.DAY_OF_MONTH);
        String sql="insert into memos"+"("+DatabaseHelper.MEMO_FIELDS_WITHOUT_PASSWORD+")"+"VALUES("+Apex.open+title+Apex.close+","+Apex.open+text+Apex.close+","+color+","+emoji+","+
        day+","+month+","+year+","+ day+","+month+","+year+","+encryption+")";
        database.execSQL(sql);
    }

    public void deleteMemoByIdFromDB(int id){
        String sql="delete from memos where _id="+id;
        database.execSQL(sql);
        return;
    }

    public void deleteAllMemoNotEncrypted(){
        String sql="delete from memos where encryption<>1";
        database.execSQL(sql);
        return;
    }

    public boolean isEncrypted(int position){
        Memo mem=loadMemoByPosition(position);
        if(mem!=null){
            if(mem.getEncryption()==1){
                return true;
            }
        }
        return false;
    }
    public void addEncryptionToPasswordAndText(int position,String password){//aggiorna i dati nel database cifrando testo,password
        //e settando encryption a 1
        Memo m=loadMemoByPosition(position);
        m.setEncryption(1);
        m.setPassword(Encrypt.encryption(password,password));
        m.setText(Encrypt.encryption(m.getText(),password));
        saveMemo(m,m.getId());
    }

    public void decryptText(Memo memo,String password){//una volta messa la password,solo il testo verrà decifrato
        memo.setText(Encrypt.decryption(memo.getText(),password));
    }

    public void deleteEncryptionToPasswordAndText(int position,String password){
        Memo m=loadMemoByPosition(position);
        m.setEncryption(0);
        m.setPassword(Encrypt.decryption(m.getPassword(),password));
        m.setText(Encrypt.decryption(m.getText(),password));
        saveMemo(m,m.getId());
    }

}
