package com.automatedcartollingsystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ActsDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "user_email";
    private static final int OLD_VERSION = 1;
    private static final int NEW_VERSION = 2;

    public ActsDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, OLD_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db,OLD_VERSION,NEW_VERSION);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db,oldVersion,newVersion);

    }

    private static void updateData(SQLiteDatabase db,String email,int imageResourceID){
        db.execSQL("SELECT * USER WHERE EMAIL_ADDRESS = '"+email+"';");
    }
    private static void insertData(SQLiteDatabase db, String email,String bitmap){
        ContentValues contentValue = new ContentValues();
        contentValue.put("EMAIL_ADDRESS",email);
        contentValue.put("BITMAP_RESOURCE_ID",bitmap);

        db.insert("USERS",null,contentValue);
    }
    private void updateDatabase(SQLiteDatabase db, int oldVersion,int newVersion){
        if(oldVersion==1){
            db.execSQL("CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "EMAIL_ADDRESS TEXT," +
                    "BITMAP_RESOURCE_ID TEXT);");
        }
    }
}
