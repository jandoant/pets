package com.example.android.pets.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.database.PetContract.PetEntry;

/**
 * Created by Jan on 05.01.2017.
 */

//Todo: (1) - Create class that extends from SQLiteOpenDbHelper
public class ShelterDbHelper extends SQLiteOpenHelper {

    //Todo: (2) - Create Constants for DB-Name and DB-Version
    public static final String DATABASE_NAME = "shelter.db";
    public static final int DATABASE_VERSION = 1;
    //Todo: (2.1) - Create Constants for SQL-Create and Delete Queries
    private static final String SQL_CREATE_TABLE_PETS = "CREATE TABLE " + PetEntry.TABLE_NAME_PETS + "(" +
            PetEntry.COLUMN_PET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " +
            PetEntry.COLUMN_PET_BREED + "  TEXT, " +
            PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL DEFAULT  " + PetEntry.PET_GENDER_UNKNOWN + ", " +
            PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0" +
            ");";
    private static final String SQL_DELETE_TABLE_PETS = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME_PETS + ";";
    Context context;

    //Todo: (3) - Create Constructor
    public ShelterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Todo: (4) - Implement onCreate()
    /*
     * Called when the database is created for the first time.
     * This is where the creation of tables and the initial population of the tables should happen.
     * @param db - The Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PETS);
    }

    //Todo: (5) - Implement onUpgrade()
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
