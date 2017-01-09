package com.example.android.pets.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.database.PetContract.PetEntry;
import com.example.android.pets.models.Pet;

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

    //Todo: (6) - Implement insertSinglePet()

    /**
     * Inserts a new single Pet-Object into the pets-Table of shelter.db
     *
     * @param pet - The Pet to be inserted
     * @return - true if insertion was successfull, false if an Error occured during the operation
     */
    public boolean insertSinglePet(Pet pet) {

        // Open the Database-File to write into
        SQLiteDatabase db = this.getWritableDatabase();

        //bind Pet data to database-rows
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, pet.getName());
        values.put(PetEntry.COLUMN_PET_BREED, pet.getBreed());
        values.put(PetEntry.COLUMN_PET_GENDER, pet.getGender());
        values.put(PetEntry.COLUMN_PET_WEIGHT, pet.getWeight());

        //insert data and receive success-info
        return db.insert(PetEntry.TABLE_NAME_PETS, null, values) >= 0;
    }

    public void readAllPetsFromDB() {

    }

    public boolean deleteAllPetsEntries() {
        // Open the Database-File to write into
        SQLiteDatabase db = this.getWritableDatabase();
        //delete entire Table pets and return number if any rows were affected
        return db.delete(PetEntry.TABLE_NAME_PETS, "1", null) > 0;
    }
}
