package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.pets.data.ShelterContract.PetEntry;

public class PetProvider extends ContentProvider {

    /*
    Constants for Routing
     */
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //assigns Response Code to Uri
        uriMatcher.addURI(ShelterContract.CONTENT_AUTHORITY_APP, ShelterContract.PATH_PETS, PETS);
        uriMatcher.addURI(ShelterContract.CONTENT_AUTHORITY_APP, ShelterContract.PATH_PETS + "/#", PET_ID);
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
     *
     * @param uri    The content:// URI of the insertion request. This must not be null.
     * @param values The Values of the Item that is being inserted
     * @return - the Uri of the newly created Item
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        int match = uriMatcher.match(uri);

        //catch illegal Uris
        //Insertion always happens on the entire table
        switch (match) {
            case PETS:
                return insertSinglePet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//Ende switch
    }

    /**
     * Helper Method - Validates the data and inserts it into the Database.
     *
     * @param uri    The content:// URI of the insertion request. This must not be null.
     * @param values values The Values of the Item that is being inserted
     * @return - the Uri of the newly created Item
     */
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
        } else {
            //Notify the observer, that the Data has changed - autoUpdate
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, row);
    }

    /*
    Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = shelterDbHelper.getReadableDatabase();
        Cursor cursor;

        // Query a single item or the entire table?
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
            default:
                //invalid Uri
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);
        }//Ende switch

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

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
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//Ende switch
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
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

        int updatedRows = db.update(PetEntry.TABLE_NAME_PETS, values, selection, selectionArgs);

        //Notify the observer, that the Data has changed - autoUpdate
        if (updatedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = shelterDbHelper.getWritableDatabase();

        int deletedRows;

        int match = uriMatcher.match(uri);

        //catch illegal Uris
        switch (match) {
            case PETS:
                deletedRows = db.delete(PetEntry.TABLE_NAME_PETS, selection, selectionArgs);
                break;
            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db.delete(PetEntry.TABLE_NAME_PETS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//Ende switch

        //Notify the observer, that the Data has changed - autoUpdate
        if (deletedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match) {

            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                //invalid Uri
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
