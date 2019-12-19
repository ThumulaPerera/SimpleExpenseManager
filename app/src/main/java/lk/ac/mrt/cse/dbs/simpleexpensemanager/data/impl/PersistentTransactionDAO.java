package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_COLUMN_ACC_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_COLUMN_AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_COLUMN_DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_COLUMN_EXPENSE_TYPE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_COLUMN_ID;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_TABLE_NAME;

public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;

    public PersistentTransactionDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        if(accountNo != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRANSACTIONS_COLUMN_DATE, new SimpleDateFormat("dd-MM-yyyy").format(date));
            contentValues.put(TRANSACTIONS_COLUMN_ACC_NO, accountNo);
            contentValues.put(TRANSACTIONS_COLUMN_EXPENSE_TYPE, expenseType.toString());
            contentValues.put(TRANSACTIONS_COLUMN_AMOUNT, amount);
            db.insert(TRANSACTIONS_TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE_NAME + " ORDER BY " + TRANSACTIONS_COLUMN_ID + " DESC", null);

        ArrayList<Transaction> transactions = new ArrayList<>();

        while (res.moveToNext()){
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(res.getString(1));
                String acc_no = res.getString(2);
                ExpenseType expense_type = ExpenseType.valueOf(res.getString(3));
                Double amount = res.getDouble(4);

                Transaction transaction = new Transaction(date, acc_no, expense_type, amount);
                transactions.add(transaction);
            } catch (ParseException ignored){
            }
        }

        //TODO del
        System.out.println("transactions " + transactions);
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE_NAME + " ORDER BY " + TRANSACTIONS_COLUMN_ID + " DESC LIMIT " + limit, null);

        ArrayList<Transaction> transactions = new ArrayList<>();

        while (res.moveToNext()){
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(res.getString(1));
                String acc_no = res.getString(2);
                ExpenseType expense_type = ExpenseType.valueOf(res.getString(3));
                Double amount = res.getDouble(4);

                Transaction transaction = new Transaction(date, acc_no, expense_type, amount);
                transactions.add(transaction);
            } catch (ParseException ignored){
            }
        }
        return transactions;
    }
}

