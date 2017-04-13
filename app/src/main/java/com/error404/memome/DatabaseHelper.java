package com.error404.memome;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static int DB_VERSION = 1;
    private final static String DATABASE_NAME = "memo.db";
    public final static String NAME_TABLE_MEMOS = "memos";
    public final static String MEMO_FIELDS_WITHOUT_PASSWORD="title,text,color,emoji,daydatecreation,monthdatecreation,yeardatecreation,daylastmodify,monthlastmodify,yearlastmodify,encryption";
    private final static String NAME_SORT_TABLE = "sort";
    private final static String SORT_DEFAULT="insert into sort VALUES('asc','title')";
    private final static String MEMO_TABLE_SQL = "CREATE TABLE "+NAME_TABLE_MEMOS+"(\n" +
            "\t_id\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +//attributo incrementato automaticamerte
            "\ttitle\tTEXT NOT NULL,\n"+
            "\ttext\tTEXT,\n"+
            "\tcolor\tINTEGER NOT NULL,\n" +
            "\temoji\tINTEGER NOT NULL,\n"+
            "\tdaydatecreation\tINTEGER NOT NULL,\n"+
            "\tmonthdatecreation\tINTEGER NOT NULL,\n"+
            "\tyeardatecreation\tINTEGER NOT NULL,\n"+
            "\tdaylastmodify\tINTEGER NOT NULL,\n"+
            "\tmonthlastmodify\tINTEGER NOT NULL,\n"+
            "\tyearlastmodify\tINTEGER NOT NULL,\n"+
            "\tfavorite\tINTEGER ,\n"+
            "\tencryption\tINTEGER ,\n"+
            "\tpassword\tTEXT \n"+
            ")";

    public final static String SORT_TABLE_SQL="CREATE TABLE "+NAME_SORT_TABLE+"(\n" +
            "\tascdesc\tTEXT NOT NULL PRIMARY KEY,\n" +
            "\tsorttype\tTEXT NOT NULL\n" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
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
