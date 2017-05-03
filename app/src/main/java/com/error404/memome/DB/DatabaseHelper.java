package com.error404.memome.DB;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {//Classe che si occupa della creazione del DB "memo.db" e della definizione e creazione  delle Tabelle "Memo" e "Sort"
    private final static int DB_VERSION = 1;
    private final static String DATABASE_NAME = "memo.db";//nome DB
    public final static String NAME_TABLE_MEMOS = "memos";//nome tabella
    public final static String MEMO_FIELDS_WITHOUT_PASSWORD="title,text,color,emoji,daydatecreation,monthdatecreation,yeardatecreation,daylastmodify,monthlastmodify,yearlastmodify,encryption";
    private final static String NAME_SORT_TABLE = "sort";
    private final static String SORT_DEFAULT="insert into sort VALUES('asc','title')";
    private final static String MEMO_TABLE_SQL = "CREATE TABLE "+NAME_TABLE_MEMOS+"(\n" +//attributi della tabella memo
            "\t_id\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
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

    public final static String SORT_TABLE_SQL="CREATE TABLE "+NAME_SORT_TABLE+"(\n" +//attributi della tabella Sort
            "\tascdesc\tTEXT NOT NULL PRIMARY KEY,\n" +
            "\tsorttype\tTEXT NOT NULL\n" +
            ");";

    public DatabaseHelper(Context context) {//metodo costruttore
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//al momento della creazione del database
        //esegue le query SQL ossia crea le tabelle "Memo" e "Sort" e
        // inizializza la tabella "Sort" con ordinamento per titolo crescente
        sqLiteDatabase.execSQL(MEMO_TABLE_SQL);//crea tabella Memo
        sqLiteDatabase.execSQL(SORT_TABLE_SQL);//crea tabella Sort
        sqLiteDatabase.execSQL(SORT_DEFAULT);//popola la tabella "Sort" con un ordinamento di default(crescente per titolo)
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {//non fare niente
    }
}
