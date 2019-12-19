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
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.TRANSACTIONS_TABLE_NAME;

public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;

    public PersistentTransactionDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTIONS_COLUMN_DATE, new SimpleDateFormat("dd-MM-yyyy").format(date));
        contentValues.put(TRANSACTIONS_COLUMN_ACC_NO, accountNo);
        contentValues.put(TRANSACTIONS_COLUMN_EXPENSE_TYPE, expenseType.toString());
        contentValues.put(TRANSACTIONS_COLUMN_AMOUNT, amount);
        db.insert(TRANSACTIONS_TABLE_NAME, null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE_NAME, null);

        ArrayList<Transaction> transactions = new ArrayList<>();

        while (res.moveToNext()){
            Date date;
            try {
                date = new SimpleDateFormat("dd-MM-yyyy").parse(res.getString(1));
            } catch (ParseException pe){
                //TODO handle properly
                date = new Date();
                System.out.println(pe);
            }
            String acc_no = res.getString(2);
            ExpenseType expense_type = null;
            switch (res.getString(3)){
                case "EXPENSE" :  expense_type = ExpenseType.EXPENSE;
                case "INCOME" : expense_type = ExpenseType.INCOME;
            }
            Double amount = res.getDouble(4);

            Transaction transaction = new Transaction(date, acc_no, expense_type, amount);
            transactions.add(transaction);
        }

        //TODO del
        System.out.println("transactions " + transactions);
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = getAllTransactionLogs();

        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}

