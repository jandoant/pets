package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ShelterContract {

    public static final String CONTENT_AUTHORITY_APP = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI_APP = Uri.parse("content://" + CONTENT_AUTHORITY_APP);

    //possible Paths
    public static final String PATH_PETS = "pets";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ShelterContract() {
    }

    /* Inner class for table pets that defines the table contents */
    public static class PetEntry implements BaseColumns {

        public static final Uri CONTENT_URI_PETS = Uri.withAppendedPath(BASE_CONTENT_URI_APP, PATH_PETS);

        /**
         * The MIME type for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_APP + "/" + PATH_PETS;

        /**
         * The MIME type for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_APP + "/" + PATH_PETS;

        /*
        Schema of table pets
         */
        public static final String TABLE_NAME_PETS = "pets";
        public static final String COLUMN_PET_ID = "_id";
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        /*
    Possible Values for the gender attribute of the pet
    */
        public static final int PET_GENDER_UNKNOWN = 0;
        public static final int PET_GENDER_MALE = 1;
        public static final int PET_GENDER_FEMALE = 2;

        public static boolean isValidGender(Integer gender) {
            return gender == PET_GENDER_UNKNOWN || gender == PET_GENDER_MALE || gender == PET_GENDER_FEMALE;
        }
    }
}
