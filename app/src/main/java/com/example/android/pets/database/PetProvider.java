package com.example.android.pets.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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

        // TODO: (1) - Create and initialize a PetDbHelper object to gain access to the pets database.
        shelterDbHelper = new ShelterDbHelper(getContext());

        return true;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
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
                cursor = db.query(PetContract.PetEntry.TABLE_NAME_PETS, projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
            case PET_ID:
                // Read from single row of the pets table
                // Specifying the row by limiting the selectionArgs to the last segment of the Uri-Path (which is the ID)
                selection = PetContract.PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(PetContract.PetEntry.TABLE_NAME_PETS, projection, selection, selectionArgs, null, null, sortOrder, null);
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
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
