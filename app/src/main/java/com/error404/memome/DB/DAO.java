package com.error404.memome.DB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.error404.memome.Utilities.Encrypt;
import com.error404.memome.Entities.Memo;
import com.error404.memome.Entities.RowSort;
import com.error404.memome.Utilities.Values;

import java.util.ArrayList;
import java.util.Calendar;

public class DAO {

    private DatabaseHelper dbh;//riferimento al DatabaseHelper,necessario per  aprire il database
    private SQLiteDatabase database;//riferimento a SQLiteDatabase il quale esegue le istruzioni SQL

    //valori privati e costanti relativi agli attributi delle tabelle,
    private final  String SORTTYPE="sorttype";
    private final  String ASCDESC="ascdesc";
    private final  String ORDER_BY="order by";
    private final  String ID="_id";
    private final  String WHERE="where";
    private final  String TEXT="text";
    private final  String ENCRYPTION="encryption";
    private final  String DAYDATECREATION="daydatecreation";
    private final  String MONTHDATECREATION="monthdatecreation";
    private final  String YEARDATECREATION="yeardatecreation";
    private final  String DAYLASTMODIFY="daylastmodify";
    private final  String MONTHLASTMODIFY="monthlastmodify";
    private final  String YEARLASTMODIFY="yearlastmodify";
    private final String ASC="asc";
    private final  String DESC="desc";

    //istruzioni SQL

    private final  String SELECT_ALL="SELECT * FROM memos";
    private final  String DELETE_ALL_NOT_ENCRYPTED="delete from memos where encryption<>1";
    private final  String SELECT_SORT="select ascdesc,sorttype from sort";
    private final  String DELETE_MEMO="delete from memos";
    private final  String UPDATE_ONLY_SORT_TYPE="update sort SET sorttype=?";
    private final  String SWITCH_ASC_DESC="update sort SET ascdesc=?";

    public DAO(Context context) {//inizializza il riferimento a DatabaseHelper
        dbh = new DatabaseHelper(context);
    }
    public void open() {//apre il database inizializzando il riferimento a SQLiteDatabase e
        // sfruttando il riferimento a DataBaseHelper
        database = dbh.getWritableDatabase();
    }

    public void close() {//chiude il database
        database.close();
    }


    public Memo loadMemoById(int id){//metodo per istanziare un oggetto memo dando una chiave primaria
        Memo memo=null;
        String sql=SELECT_ALL+" "+WHERE+" "+ID+"="+id;//seleziona la memo con l'id opportuno
        Cursor c=database.rawQuery(sql,null);
        if(c!=null) {
            c.moveToFirst();
            memo = loadMemoByCursorOneRow(c);//istanzia la memo
            c.close();
        }
        return memo;
    }
     public Memo loadMemoByCursorOneRow(Cursor c){//metodo che dato un cursore legge i vari attributi di una riga e
         //istanzia un oggetto memo
        if(c!=null) {
            int id = c.getInt(c.getColumnIndex(ID));
            String title = c.getString(c.getColumnIndex(Values.TITLE));
            String text = c.getString(c.getColumnIndex(TEXT));
            int color = c.getInt(c.getColumnIndex(Values.COLOR));
            int emoji = c.getInt(c.getColumnIndex(Values.EMOJI));
            int datecreation[];//day,month,year
            int lastmodify[];//day,month,year
            datecreation = getDateCreation(c);
            lastmodify = getLastModify(c);
            int encryption = c.getInt(c.getColumnIndex(ENCRYPTION));
            String password = c.getString(c.getColumnIndex(Values.PASSWORD));
            int favorite=c.getInt(c.getColumnIndex(Values.FAVORITE));
            Memo memo = new Memo(id, title, text, color, emoji, datecreation, lastmodify, encryption, password,favorite);
            return memo;
        }
        return null;
    }

    public int[] getDateCreation(Cursor c){//metodo per ottenere la dataCreazione dal cursore
        int arr[]=new int[Values.NUMBER_FORMAT_DATE];//cursore non chiudibile,usato successivamente nel metodo loadMemoByCursorOneRow
        arr[Values.INDEX_DAY]=c.getInt(c.getColumnIndex(DAYDATECREATION));
        arr[Values.INDEX_MONTH]=c.getInt(c.getColumnIndex(MONTHDATECREATION));
        arr[Values.INDEX_YEAR]=c.getInt(c.getColumnIndex(YEARDATECREATION));
        return arr;
    }
    public int[] getLastModify(Cursor c){//metodo per ottenere la dataCreazione dal cursore
        int arr[]=new int[Values.NUMBER_FORMAT_DATE];//cursore non chiudibile,usato successivamente nel metodo loadMemoByCursorOneRow
        arr[Values.INDEX_DAY]=c.getInt(c.getColumnIndex(DAYLASTMODIFY));
        arr[Values.INDEX_MONTH]=c.getInt(c.getColumnIndex(MONTHLASTMODIFY));
        arr[Values.INDEX_YEAR]=c.getInt(c.getColumnIndex(YEARLASTMODIFY));
        return arr;
    }
    public void fromCursorToList(ArrayList<Memo> arrMemo,Cursor cursor) {//dato un cursore crea una lista di memo
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrMemo.add(loadMemoByCursorOneRow(cursor));
                cursor.moveToNext();
            }
        }
    }
    public void saveMemo(Memo memo,int id){//aggiorna la memo con id=id prendendo i valori dall'oggetto Memo passato in input
        String title=memo.getTitle();
        int emoji=memo.getEmoji();
        String text=memo.getText();
        int color=memo.getColor();
        String password=memo.getPassword();
        int  encryption=memo.getEncryption();
        int favorite=memo.getFavorite();
        Calendar date=Calendar.getInstance();
        int month=date.get(Calendar.MONTH);
        int year=date.get(Calendar.YEAR);
        int day=date.get(Calendar.DAY_OF_MONTH);
        ContentValues cv=new ContentValues();//contiene le coppie nome,valore per aggiornare il DB
        cv.put(Values.TITLE,title);
        cv.put(TEXT,text);
        cv.put(Values.COLOR,color);
        cv.put(Values.EMOJI,emoji);
        cv.put(DAYLASTMODIFY,day);
        cv.put(MONTHLASTMODIFY,month);
        cv.put(YEARLASTMODIFY,year);
        cv.put(ENCRYPTION,encryption);
        cv.put(Values.PASSWORD,password);
        cv.put(Values.FAVORITE,favorite);
        database.update(DatabaseHelper.NAME_TABLE_MEMOS,cv,"_id="+id,null);
    }
    public ArrayList<Memo> loadAllMemoByTitle(){//metodo per caricare tutte le note per titolo e metterle in una lista
        RowSort rowSort=new RowSort("asc","title");
        String sortMethod = ORDER_BY+" "+rowSort.getSortType()+" "+ rowSort.getAscDesc();
        String sql = SELECT_ALL +" "+sortMethod;
        Cursor tabAllMemos = database.rawQuery(sql, null);
        if(tabAllMemos!=null) {
            ArrayList<Memo> arrMemo = new ArrayList<Memo>();
            fromCursorToList(arrMemo, tabAllMemos);
            tabAllMemos.close();
            return arrMemo;
        }
        return null;
    }

    public ArrayList<Memo>loadAllMemo(){//metodo per caricare tutte le Memo dal DB e metterle in una lista
        RowSort rowSort=getRowSort();//cursori locali chiudibili
        if(rowSort!=null) {
            String sortMethod = ORDER_BY+" "+rowSort.getSortType()+" "+ rowSort.getAscDesc();
            String sql = SELECT_ALL +" "+sortMethod;
            Cursor tabAllMemos = database.rawQuery(sql, null);
            if(tabAllMemos!=null) {
                ArrayList<Memo> arrMemo = new ArrayList<Memo>();
                fromCursorToList(arrMemo, tabAllMemos);
                tabAllMemos.close();
                return arrMemo;
            }
        }
        return null;
    }
    public ArrayList<Memo>loadAllFavoriteMemo(){//metodo per caricare tutte le note preferite da una lista di Memo
        ArrayList<Memo> favoriteMemos=new ArrayList<Memo>();
        ArrayList<Memo> allMemo=loadAllMemoByTitle();
        for(int i=0;i<allMemo.size();i++){
            if(allMemo.get(i).getFavorite()==Values.TRUE){//verifica se la nota è preferita
                favoriteMemos.add(allMemo.get(i));
            }
        }
        return favoriteMemos;
    }

    public void updateSort(String newSortType){//metodo per aggiornare il tipo di ordinamento
        if(newSortType.equals(Values.ONLYUPDATEGUI)){//non fare niente
            return;
        }
        RowSort rowSort=getRowSort();//se vecchio e nuovo ordinamento coincidono allora inverti nel db solo ASC in DESC o viceversa
        //altrimenti cambia il tipo di ordinamento aggiornando il database
        if(rowSort!=null) {
            if (rowSort.getSortType().equals(newSortType)) {
                switchAscDescIntoDB(rowSort.getAscDesc());
            } else {
                updateOnlySortTypeIntoDB(newSortType);
            }
        }
        return;
    }
    public RowSort getRowSort(){//metodo che ritorna un oggetto "ordinamento"
        Cursor c=getCursorRowSort();//cursore locale da ritornare non chiudibile
        c.moveToFirst();
        String actualSortType = c.getString(c.getColumnIndex(SORTTYPE));
        String actualAscDesc = c.getString(c.getColumnIndex(ASCDESC));
        c.close();
        RowSort rowsort=new RowSort(actualAscDesc,actualSortType);
        return rowsort;
    }
    public Cursor getCursorRowSort(){//metodo che ritorna il cursore relativo all'ordinamento,quindi un cursore che contiene
        //informazioni su tipo ordianemento e ASC/DESC

        Cursor c=null;//cursore locale da ritornare non chiudibile
        c = database.rawQuery(SELECT_SORT, null);
        return c;
    }
    public void switchAscDescIntoDB(String actualAscDesc){//metodo che cambia l'ordinamento da ASCENDENTE a DECRESCENTE(Se il vecchio ordianmento era ASC) o da DESC a ASC
        //(Se il vecchio ordianmento era DESC),per fare ciò esegue una query SQL
        SQLiteStatement sqLiteStatement=database.compileStatement(SWITCH_ASC_DESC);
        if(actualAscDesc.equals(ASC)){
            sqLiteStatement.bindString(1,DESC);
            sqLiteStatement.execute();
        }
        else{
            sqLiteStatement.bindString(1,ASC);
            sqLiteStatement.execute();
        }
        return;
    }
    public void updateOnlySortTypeIntoDB(String newSortType){//metodo che aggiorna il tipo di ordinamento
        // di visualizzazione delle note
        SQLiteStatement sqLiteStatement=database.compileStatement(UPDATE_ONLY_SORT_TYPE);
        sqLiteStatement.bindString(1,newSortType);
        sqLiteStatement.execute();
        return;
    }
    public void addMemoToDB(String title,String text,int emoji,int color){//metodo per aggiungere al DB una nota
        //
        SQLiteStatement sqLiteStatement=database.compileStatement("insert into memos("+DatabaseHelper.MEMO_FIELDS_WITHOUT_PASSWORD+")"+"VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        Calendar date=Calendar.getInstance();
        int month=date.get(Calendar.MONTH);
        int year=date.get(Calendar.YEAR);
        int day=date.get(Calendar.DAY_OF_MONTH);
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
        sqLiteStatement.bindLong(11,Values.FALSE);
        sqLiteStatement.execute();
    }

    public void deleteMemoByIdFromDB(int id){//metodo per eliminare una nota dal DB dato un id
        String sql=DELETE_MEMO+" "+WHERE+" "+ID+"="+id;
        database.execSQL(sql);
        return;
    }

    public void deleteAllMemoNotEncrypted(){//metodo per eliminare tutte le note non cifrate,per quanto riguarda
        // le note cifrate esse dovranno essere cancellate una per una
        database.execSQL(DELETE_ALL_NOT_ENCRYPTED);
        return;
    }

    public boolean isEncrypted(int id){//metodo per verificare se la nota è cifrata
        Memo mem=loadMemoById(id);
        if(mem!=null){
            if(mem.getEncryption()==Values.TRUE){
                return true;
            }
        }
        return false;
    }
    public boolean isFavorite(int id){//metodo per verificare se la nota è preferita
        Memo mem=loadMemoById(id);
        if(mem!=null){
            if(mem.getFavorite()==Values.TRUE){
                return true;
            }
        }
        return false;
    }

    public void decryptText(Memo memo,String password){//metodo che data una nota cifrata e la password di cifratura
        // decifra il testo della nota
        memo.setText(Encrypt.decryption(memo.getText(),password));
    }

    public void addEncryptionToPasswordAndText(int id,String password){//metodo che dato un id di una nota cifrata
        // imposta  la cifratura alla nota modificando il DB

        Memo m=loadMemoById(id);
        m.setEncryption(Values.TRUE);
        m.setPassword(Encrypt.encryption(password,password));
        m.setText(Encrypt.encryption(m.getText(),password));
        saveMemo(m,m.getId());
    }

    public void deleteEncryptionToPasswordAndText(int id,String password){//metodo che dato un id di una nota cifrata
        // toglie la cifratura alla nota modificando il DB
        Memo m=loadMemoById(id);
        m.setEncryption(Values.FALSE);
        m.setPassword(Encrypt.decryption(m.getPassword(),password));
        m.setText(Encrypt.decryption(m.getText(),password));
        saveMemo(m,m.getId());
    }
    public void addToFavorites(int id){//metodo per aggiornare la nota rendendola da non preferita a preferita
        Memo m=loadMemoById(id);
        m.setFavorite(Values.TRUE);
        saveMemo(m,id);
    }
    public void deleteFromFavorites(int id){//metodo per aggiornare la nota rendendola da preferita a non preferita
        Memo m=loadMemoById(id);
        m.setFavorite(Values.FALSE);
        saveMemo(m,id);
    }
}