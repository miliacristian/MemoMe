package com.error404.memometwo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Calendar;

//aggiungere async task,se stringhe final usate solo qui metterle private
public class DAO {
    private DatabaseHelper dbh;
    private SQLiteDatabase database;
    //private final static String SELECT_ALL="SELECT * FROM memos ";
    private final  String SELECT_ALL="SELECT * FROM memos";
    public final  String SORTTYPE="sorttype";
    public final  String ASCDESC="ascdesc";
    public final  String ORDER_BY="order by";
    public final  String ID="_id";
    public final  String WHERE="where";
    public final  static String TITLE="title";
    public final  String TEXT="text";
    public final  static String COLOR="color";
    public final  static String EMOJI="emoji";
    public final  static String PASSWORD="password";
    public final  String ENCRYPTION="encryption";
    public final  String DAYDATECREATION="daydatecreation";
    public final  String MONTHDATECREATION="monthdatecreation";
    public final  String YEARDATECREATION="yeardatecreation";
    public final  String DAYLASTMODIFY="daylastmodify";
    public final  String MONTHLASTMODIFY="monthlastmodify";
    public final  String YEARLASTMODIFY="yearlastmodify";
    public final  static String ONLYUPDATEGUI="onlyUpdateGUI";
    public final  String DELETE_ALL_NOT_ENCRYPTED="delete from memos where encryption<>1";
    public final  String SELECT_SORT="select ascdesc,sorttype from sort";
    public final  int NUMBER_FORMAT_DATE=3;
    public final  int INDEX_DAY=0;
    public final  int INDEX_MONTH=1;
    public final  int INDEX_YEAR=2;
    public final  int ERROR_CODE=2;
    public final  int FALSE=0;
    public final  int TRUE=1;
    public final  String DELETE_MEMO="delete from memos";
    public final String ASC="asc";
    public final  String DESC="desc";
    public final  String UPDATE_ONLY_SORT_TYPE="update sort SET sorttype=?";
    public final  String SWITCH_ASC_DESC="update sort SET ascdesc=?";

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
            String actualSortType = c.getString(c.getColumnIndex(SORTTYPE));
            String actualAscDesc = c.getString(c.getColumnIndex(ASCDESC));
            String sortMethod = ORDER_BY+ " " + actualSortType + " " + actualAscDesc;
            String sql = SELECT_ALL +" "+sortMethod;
            c.close();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor != null) {
                cursor.moveToFirst();
                cursor.moveToPosition(position);
                id = cursor.getInt(cursor.getColumnIndex(ID));
                cursor.close();
                return id;
            }
        }
        return ERROR_CODE;//non sono riuscito a trovare l'id dalla posizione
    }
    public Memo loadMemoByPosition(int position){
        int id=findIdByPosition(position);
        Memo m=loadMemoById(id);
        return m;
    }

    public Memo loadMemoById(int id){//carico memo da id(chiave primaria),cursore locale chiudibile
        Memo memo=null;
        String sql=SELECT_ALL+" "+WHERE+" "+ID+"="+id;
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
            int id = c.getInt(c.getColumnIndex(ID));
            String title = c.getString(c.getColumnIndex(TITLE));
            String text = c.getString(c.getColumnIndex(TEXT));
            int color = c.getInt(c.getColumnIndex(COLOR));
            int emoji = c.getInt(c.getColumnIndex(EMOJI));
            int datecreation[];//day,month,year
            int lastmodify[];//day,month,year
            datecreation = getDateCreation(c);
            lastmodify = getLastModify(c);
            int encryption = c.getInt(c.getColumnIndex(ENCRYPTION));
            String password = c.getString(c.getColumnIndex(PASSWORD));
            Memo memo = new Memo(id, title, text, color, emoji, datecreation, lastmodify, encryption, password);
            return memo;
        }
        return null;
    }

    public int[] getDateCreation(Cursor c){//ottiene data creazione da cursore
        int arr[]=new int[NUMBER_FORMAT_DATE];//cursore non chiudibile,usato successivamente nel metodo loadMemoByCursorOneRow
        arr[INDEX_DAY]=c.getInt(c.getColumnIndex(DAYDATECREATION));
        arr[INDEX_MONTH]=c.getInt(c.getColumnIndex(MONTHDATECREATION));
        arr[INDEX_YEAR]=c.getInt(c.getColumnIndex(YEARDATECREATION));
        return arr;
    }
    public int[] getLastModify(Cursor c){//ottiene data ultima modifica da cursore
        int arr[]=new int[NUMBER_FORMAT_DATE];//cursore non chiudibile,usato successivamente nel metodo loadMemoByCursorOneRow
        arr[INDEX_DAY]=c.getInt(c.getColumnIndex(DAYLASTMODIFY));
        arr[INDEX_MONTH]=c.getInt(c.getColumnIndex(MONTHLASTMODIFY));
        arr[INDEX_YEAR]=c.getInt(c.getColumnIndex(YEARLASTMODIFY));
        return arr;
    }



    public void fromCursorToList(ArrayList<Memo> arrMemo,Cursor cursor) {//da tabella a lista di memo
        if (cursor != null) {//chimato da loadAllMemo
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
        ContentValues cv=new ContentValues();
        cv.put(TITLE,title);
        cv.put(TEXT,text);
        cv.put(COLOR,color);
        cv.put(EMOJI,emoji);
        cv.put(DAYLASTMODIFY,day);
        cv.put(MONTHLASTMODIFY,month);
        cv.put(YEARLASTMODIFY,year);
        cv.put(ENCRYPTION,encryption);
        cv.put(PASSWORD,password);
        //String sql="update memos set title="+Apex.open+title+Apex.close+","+"text="+Apex.open+text+Apex.close+","+"color="+color+","+"emoji="+emoji+","+"daylastmodify="+day+","+"monthlastmodify="+month+","+"yearlastmodify="+year+","+"encryption="+encryption+","+"password="+Apex.open+password+Apex.close+" where _id="+id;
        //System.out.println(sql);
        database.update(DatabaseHelper.NAME_TABLE_MEMOS,cv,"_id="+id,null);
        //System.out.println("save password"+password);
        //database.execSQL(sql);
    }

    public ArrayList<Memo>loadAllMemo(){//carica tutti i memo secondo l'ordinamento,vedendo dapprima il tipo di ordinamento e poi query
        Cursor c=getRowSort();//cursori locali chiudibili
        if(c!=null) {
            c.moveToFirst();
            String actualSortType = c.getString(c.getColumnIndex(SORTTYPE));
            String actualAscDesc = c.getString(c.getColumnIndex(ASCDESC));
            String sortMethod = ORDER_BY+" "+actualSortType+" "+ actualAscDesc;
            c.close();
            String sql = SELECT_ALL +" "+sortMethod;
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
        if(newSortType.equals(ONLYUPDATEGUI)){
            return;
        }
        Cursor c=getRowSort();//cursore locale chiudibile
        if(c!=null) {
            c.moveToFirst();
            String actualSortType = c.getString(c.getColumnIndex(SORTTYPE));
            String actualAscDesc = c.getString(c.getColumnIndex(ASCDESC));
            c.close();
            if (actualSortType.equals(newSortType)) {
                switchAscDescIntoDB(actualAscDesc);
            } else {
                updateOnlySortTypeIntoDB(newSortType);
            }
        }
        return;
    }
    public Cursor getRowSort(){
        Cursor c=null;//cursore locale da ritornare non chiudibile
        c = database.rawQuery(SELECT_SORT, null);
        return c;
    }
    public void switchAscDescIntoDB(String actualAscDesc){
        SQLiteStatement sqLiteStatement=database.compileStatement(SWITCH_ASC_DESC);
        //String sql="update sort SET ascdesc=";
        if(actualAscDesc.equals(ASC)){
            sqLiteStatement.bindString(1,DESC);
            sqLiteStatement.execute();
            //database.execSQL(command);
        }
        else{
            sqLiteStatement.bindString(1,ASC);
            sqLiteStatement.execute();
            //database.execSQL(command);
        }
        return;
    }
    public void updateOnlySortTypeIntoDB(String newSortType){
        //String sql="update sort SET sorttype="+"'"+newSortType+"'";
        SQLiteStatement sqLiteStatement=database.compileStatement(UPDATE_ONLY_SORT_TYPE);
        sqLiteStatement.bindString(1,newSortType);
        //database.execSQL(sql);
        sqLiteStatement.execute();
        return;
    }
    public void addMemoToDB(String title,String text,int emoji,int color){//chiamata quando si clicca
        // sul tasto salva sull'activity in modalità creazione! vb
        SQLiteStatement sqLiteStatement=database.compileStatement("insert into memos("+DatabaseHelper.MEMO_FIELDS_WITHOUT_PASSWORD+")"+"VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        Calendar date=Calendar.getInstance();//data creazione(prende la data corrente)
        int month=date.get(Calendar.MONTH);
        int year=date.get(Calendar.YEAR);
        int day=date.get(Calendar.DAY_OF_MONTH);
        //String sql="insert into memos"+"("+DatabaseHelper.MEMO_FIELDS_WITHOUT_PASSWORD+")"+"VALUES("+Apex.open+title+Apex.close+","+Apex.open+text+Apex.close+","+color+","+emoji+","+
        //day+","+month+","+year+","+ day+","+month+","+year+","+FALSE+")";
        sqLiteStatement.bindString(1,title);
        sqLiteStatement.bindString(2,text);
        sqLiteStatement.bindLong(3,color);
        sqLiteStatement.bindLong(4,emoji);
        sqLiteStatement.bindLong(5,day);
        sqLiteStatement.bindLong(6,month);
        sqLiteStatement.bindLong(7,year);
        sqLiteStatement.bindLong(8,day);
        sqLiteStatement.bindLong(9,month);
        sqLiteStatement.bindLong(10,year);
        sqLiteStatement.bindLong(11,FALSE);
        sqLiteStatement.execute();
        //database.execSQL(sql);
    }

    public void deleteMemoByIdFromDB(int id){
        String sql=DELETE_MEMO+" "+WHERE+" "+ID+"="+id;
        database.execSQL(sql);
        return;
    }

    public void deleteAllMemoNotEncrypted(){
        //String sql="delete from memos where encryption<>1";
        database.execSQL(DELETE_ALL_NOT_ENCRYPTED);
        return;
    }
    public boolean isEncrypted(int position){
       Memo mem=loadMemoByPosition(position);
       if(mem!=null){
           if(mem.getEncryption()==TRUE){
               return true;
           }
       }
       return false;
   }
    public void decryptText(Memo memo,String password){//una volta messa la password,solo il testo verrà decifrato
        memo.setText(Encrypt.decryption(memo.getText(),password));
    }
    public void addEncryptionToPasswordAndText(int position,String password){//aggiorna i dati nel database cifrando testo,password
        //e settando encryption a 1
        Memo m=loadMemoByPosition(position);
        m.setEncryption(TRUE);
        m.setPassword(Encrypt.encryption(password,password));
        m.setText(Encrypt.encryption(m.getText(),password));
        saveMemo(m,m.getId());
    }
    public void deleteEncryptionToPasswordAndText(int position,String password){
        Memo m=loadMemoByPosition(position);
        m.setEncryption(FALSE);
        m.setPassword(Encrypt.decryption(m.getPassword(),password));
        m.setText(Encrypt.decryption(m.getText(),password));
        saveMemo(m,m.getId());
    }

}