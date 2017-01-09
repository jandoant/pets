package com.example.android.pets.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.pets.database.PetContract.PetEntry;

public class PetProvider extends ContentProvider {

    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //assigns Response Code to Uri
        uriMatcher.addURI(PetContract.CONTENT_AUTHORITY_APP, PetContract.PATH_PETS, PETS);
        uriMatcher.addURI(PetContract.CONTENT_AUTHORITY_APP, PetContract.PATH_PETS + "/#", PET_ID);
    }

    private ShelterDbHelper shelterDbHelper;

    public PetProvider() {
    }

    @Override
    public boolean onCreate() {

        shelterDbHelper = new ShelterDbHelper(getContext());
        return true;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        int match = uriMatcher.match(uri);

        //catch illegal Uris
        switch (match) {
            case PETS:
                return insertSinglePet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//Ende switch
    }

    private Uri insertSinglePet(Uri uri, ContentValues values) {

        //Validation
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

        if (name == null || name == "") {
            throw new IllegalArgumentException("Pet requires a name");
        }

        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Invalid Gender entered");
        }

        if (weight == null || weight < 0) {
            throw new IllegalArgumentException("Negative weight entered");
        }

        //Insertion
        SQLiteDatabase db = shelterDbHelper.getWritableDatabase();
        long row = db.insert(PetEntry.TABLE_NAME_PETS, null, values);
        if (row == -1) {
            return null;
        }

        return ContentUris.withAppendedId(uri, row);
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = shelterDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = uriMatcher.match(uri);

        switch (match) {

            case PETS:
                //Read from entire pets table  - use selection and selectionArgs received by this function
                cursor = db.query(PetEntry.TABLE_NAME_PETS, projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
            case PET_ID:
                // Read from single row of the pets table
                // Specifying the row by limiting the selectionArgs to the last segment of the Uri-Path (which is the ID)
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(PetEntry.TABLE_NAME_PETS, projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
            case UriMatcher.NO_MATCH:
                //invalid Uri
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);
        }//Ende switch

        return cursor;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int match = uriMatcher.match(uri);

        //catch illegal Uris
        switch (match) {
            case PETS:
                return updatePet(values, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//Ende switch
    }

    private int updatePet(ContentValues values, String selection, String[] selectionArgs) {
        //Validation
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        SQLiteDatabase db = shelterDbHelper.getWritableDatabase();

        return db.update(PetEntry.TABLE_NAME_PETS, values, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int match = uriMatcher.match(uri);
        SQLiteDatabase db = shelterDbHelper.getWritableDatabase();
        //catch illegal Uris
        switch (match) {
            case PETS:
                return db.delete(PetEntry.TABLE_NAME_PETS, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(PetEntry.TABLE_NAME_PETS, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//Ende switch
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
