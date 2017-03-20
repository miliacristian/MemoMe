package com.error404.memometwo;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    Context gContext;
    final static String DATABASE_NAME = "memo.db";
    final static String MEMOS = "memos";
    final static String SORT = "sort";
    final static String MEMO_TABLE_SQL = "CREATE TABLE "+MEMOS+"(\n" +
            "\t_id\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +//da non toccare incrementa automaticamente.
            "\ttitle\tTEXT NOT NULL,\n"+
            "\ttext\tTEXT,\n"+//text not null?
            "\tcolor\tINTEGER NOT NULL,\n" +//COLOR E EMOJI INTERI???
            "\temoji\tINTEGER NOT NULL,\n"+
            "\tdaydatecreation\tINTEGER NOT NULL,\n"+
            "\tmonthdatecreation\tINTEGER NOT NULL,\n"+
            "\tyeardatecreation\tINTEGER NOT NULL,\n"+
            "\tdaylastmodify\tINTEGER NOT NULL,\n"+
            "\tmonthlastmodify\tINTEGER NOT NULL,\n"+
            "\tyearlastmodify\tINTEGER NOT NULL,\n"+
            "\tencryption\tINTEGER,\n"+//può essere null,default 0
            "\tpassword\tTEXT\n"+//può essere null,default ??(se non è cifrata non è importante)
            ")";

    final static String SORT_TABLE_SQL="CREATE TABLE "+SORT+"(\n" +
            "\tascdesc\tTEXT NOT NULL PRIMARY KEY,\n" +
            "\tsorttype\tTEXT NOT NULL\n" +
            ");";

    final static String MEMO_FIELDS_WITHOUT_PASSWORD="title,text,color,emoji,daydatecreation,monthdatecreation,yeardatecreation,daylastmodify,monthlastmodify,yearlastmodify,encryption";
    final static String [] MEMO_FIELDS = {"title", "text", "color", "emoji","daydatecretion","monthdatecreation","yeardatecreation","daylastmodify","monthlastmodify","yearlastmodify","encryption","password"};
    final static String [] MEMO_FIELDS2 = {"_id", "title", "text", "color", "emoji","daydatecretion","monthdatecreation","yeardatecreation","daylastmodify","monthlastmodify","yearlastmodify","encryption","password"};
    final static String [] SORT_FIELD={"ascdesc,sorttype"};
    final static String SORT_DEFAULT="insert into sort VALUES('asc','title')";
    final static int version = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
        gContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//alla creazione del database
        //creo le tabelle e decido un ordinamento di default
        sqLiteDatabase.execSQL(MEMO_TABLE_SQL);
        sqLiteDatabase.execSQL(SORT_TABLE_SQL);
        sqLiteDatabase.execSQL(SORT_DEFAULT);//ordinamento di default(crescente per titolo)
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
