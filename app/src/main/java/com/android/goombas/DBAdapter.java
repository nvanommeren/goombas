package com.android.goombas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {

    // define the layout of the table in fields
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_POINTS = "points";

    // define SQLite database fields
    private static final String DB_NAME = "goombas.db";
    private static final String DB_TABLE_SCORES = "highScores";
    private static final int    DB_VER = 1;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + DB_TABLE_SCORES + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT NOT NULL, " + KEY_POINTS + " INTEGER);";

    private final Context myContext;


    // define an extension of the SQLiteOpenHelper to handle the
    // creation and upgrade of a table
    private static class DatabaseHelper extends SQLiteOpenHelper {

        // Constructor for the DatabaseHelper
        DatabaseHelper(Context context) {

            // instantiate a SQLiteOpenHelper by passing it
            // the context, the database's name, a CursorFactory
            // (null by default), and the database version.
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + DB_TABLE_SCORES);
            onCreate(db);
        }
    }

    /**
     * Constructor for the DBAdapter
     */
    public DBAdapter(Context context)
    {
        this.myContext = context;
    }

    /**
     * Open the highScore database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception
     * to signal the failure
     */
    public DBAdapter open() throws SQLException
    {
        dbHelper = new DatabaseHelper(myContext);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }


    /**
     * Return true if last played game is a high score
     */
    public boolean checkHighScore(int points) {

        Cursor cursor = findScores();
        int count = cursor.getCount();

        if (count != 0) {

            cursor.moveToLast();

            // find number of mistakes of last score in the high score table
            int foundPoints = cursor.getInt(cursor.getColumnIndex(KEY_POINTS));
            int foundID = cursor.getInt(cursor.getColumnIndex(KEY_ID));

            // if last played game had less mistakes or high score table is not full
            if (points >= foundPoints || count < 5 ) {

                // if table is full, delete lowest score in the high score table
                if (count == 5) {
                    db.delete(DB_TABLE_SCORES, KEY_ID + "=" + foundID, null );
                }

                return true;
            }
        }

        // if the high score table of certain length is empty
        else {
            return true;
        }

        return false;
    }

    /**
     * Find the current list of high scores
     */
    public Cursor findScores() {

        Cursor cursor = db.query(
            DB_TABLE_SCORES, // table to perform the query
            null, // result set, null for selecting everything
            null, // condition or selection, null for selecting everything
            null,  // selection arguments
            null,  // groupBy
            null,  // having
            KEY_POINTS + " DESC" // orderBy points in descending order
        );

        return cursor;
    }

    /**
     * Insert a new high score in the database
     */
    public SQLiteDatabase insertScore(String name, int points) {

        ContentValues vals = new ContentValues();

        vals.put(KEY_NAME, name);
        vals.put(KEY_POINTS, points);

        db.insert(DB_TABLE_SCORES, null, vals);

        return db;
    }

    public String getName(Cursor cursor) {

        String foundName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        return foundName;
    }

    public String getPoints (Cursor cursor) {

        String foundPoints = cursor.getString(cursor.getColumnIndex(KEY_POINTS));
        return foundPoints;
    }
}

