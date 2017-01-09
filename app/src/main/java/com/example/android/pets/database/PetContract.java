package com.example.android.pets.database;

import android.provider.BaseColumns;

public final class PetContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PetContract() {
    }

    /* Inner class for table pets that defines the table contents */
    public static class PetEntry implements BaseColumns {
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
    }
}
