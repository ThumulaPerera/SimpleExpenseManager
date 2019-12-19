package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.ACCOUNTS_COLUMN_ACC_HOLDER_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.ACCOUNTS_COLUMN_ACC_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.ACCOUNTS_COLUMN_BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.ACCOUNTS_COLUMN_BANK_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper.ACCOUNTS_TABLE_NAME;

public class PersistentAccountDAO implements AccountDAO {
    private DBHelper dbHelper;

    public PersistentAccountDAO(DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " + ACCOUNTS_COLUMN_ACC_NO + " FROM " + ACCOUNTS_TABLE_NAME, null);

        ArrayList<String> acc_numbers = new ArrayList<>();

        while (res.moveToNext()){
            acc_numbers.add(res.getString(0));
        }
        return acc_numbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNTS_TABLE_NAME, null);

        ArrayList<Account> accounts = new ArrayList<>();

        while (res.moveToNext()){
            String acc_no = res.getString(0);
            String bank_name = res.getString(1);
            String holder_name = res.getString(2);
            Double balance = res.getDouble(3);

            Account account = new Account(acc_no, bank_name, holder_name, balance);
            accounts.add(account);
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if(accountNo == null){
            throw new InvalidAccountException("No account selected");
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] account_no = {accountNo};

        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNTS_TABLE_NAME + " WHERE "+ ACCOUNTS_COLUMN_ACC_NO + " = ?", account_no);

        if(res.moveToFirst()){
            String acc_no = res.getString(0);
            String bank_name = res.getString(1);
            String holder_name = res.getString(2);
            Double balance = res.getDouble(3);

            Account account = new Account(acc_no, bank_name, holder_name, balance);

            return account;
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        if(account.getAccountNo() != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACCOUNTS_COLUMN_ACC_NO, account.getAccountNo());
            contentValues.put(ACCOUNTS_COLUMN_BANK_NAME, account.getBankName());
            contentValues.put(ACCOUNTS_COLUMN_ACC_HOLDER_NAME, account.getAccountHolderName());
            contentValues.put(ACCOUNTS_COLUMN_BALANCE, account.getBalance());
            db.insert(ACCOUNTS_TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (accountNo != null){
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] account_no = {accountNo};

            Cursor res = db.rawQuery("SELECT * FROM "+ ACCOUNTS_TABLE_NAME + " WHERE "+ ACCOUNTS_COLUMN_ACC_NO + " = ?", account_no);

            if (res.moveToFirst()){
                db.delete("accounts", "account_number = ?", account_no);
                return;
            }

            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        throw new InvalidAccountException("account number cannot be empty");
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        //negative account balances were allowed since the logging method anyway executes before this method

        Account account = getAccount(accountNo);

        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNTS_COLUMN_BALANCE, account.getBalance());
        db.update(ACCOUNTS_TABLE_NAME, contentValues, ACCOUNTS_COLUMN_ACC_NO + " = ? ", new String[] {account.getAccountNo()});
    }
}
