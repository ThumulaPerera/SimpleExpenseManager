package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //db name
    public static final String DATABASE_NAME = "170431L.db";

    //accounts table constants
    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String ACCOUNTS_COLUMN_ACC_NO = "account_number";
    public static final String ACCOUNTS_COLUMN_BANK_NAME = "bank_name";
    public static final String ACCOUNTS_COLUMN_ACC_HOLDER_NAME = "account_holder_name";
    public static final String ACCOUNTS_COLUMN_BALANCE = "balance";

    //transactions table constants
    public static final String TRANSACTIONS_TABLE_NAME = "transactions";
    public static final String TRANSACTIONS_COLUMN_ID = "id";
    public static final String TRANSACTIONS_COLUMN_DATE = "date";
    public static final String TRANSACTIONS_COLUMN_ACC_NO = "account_number";
    public static final String TRANSACTIONS_COLUMN_EXPENSE_TYPE = "expense_type";
    public static final String TRANSACTIONS_COLUMN_AMOUNT = "amount";

    //transaction_types table constants
    public static final String EXPENSE_TYPES_TABLE_NAME = "expense_types";
    public static final String EXPENSE_TYPES_COLUMN_EXPENSE_TYPE = "expense_types";


    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + ACCOUNTS_TABLE_NAME + "(" +
                        ACCOUNTS_COLUMN_ACC_NO + " varchar(50)" + " primary key," +
                        ACCOUNTS_COLUMN_BANK_NAME + " varchar(100)," +
                        ACCOUNTS_COLUMN_ACC_HOLDER_NAME + " varchar(100)," +
                        ACCOUNTS_COLUMN_BALANCE + " numeric(12,2)" +
                ");"
        );
        db.execSQL(
                "create table " + EXPENSE_TYPES_TABLE_NAME + "(" +
                        EXPENSE_TYPES_COLUMN_EXPENSE_TYPE + " varchar(20)" + " primary key" +
                        ");"
        );
        db.execSQL("insert into " + EXPENSE_TYPES_TABLE_NAME + " values(?) , (?)", new String[] {"EXPENSE","INCOME"});
        db.execSQL(
                "create table " + TRANSACTIONS_TABLE_NAME + "(" +
                        TRANSACTIONS_COLUMN_ID + " integer" + " primary key" + " autoincrement," +
                        TRANSACTIONS_COLUMN_DATE + " date," +
                        TRANSACTIONS_COLUMN_ACC_NO + " varchar(50)," +
                        TRANSACTIONS_COLUMN_EXPENSE_TYPE + " varchar(20)," +
                        TRANSACTIONS_COLUMN_AMOUNT + " numeric(12,2)," +
                        "FOREIGN KEY(" + TRANSACTIONS_COLUMN_ACC_NO + ") references " + ACCOUNTS_TABLE_NAME + "(" + ACCOUNTS_COLUMN_ACC_NO + ")," +
                        "FOREIGN KEY(" + TRANSACTIONS_COLUMN_EXPENSE_TYPE + ") references " + EXPENSE_TYPES_TABLE_NAME + "(" + EXPENSE_TYPES_COLUMN_EXPENSE_TYPE + ")" +
                ")"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO drop tables
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TYPES_TABLE_NAME);
        onCreate(db);
    }
}