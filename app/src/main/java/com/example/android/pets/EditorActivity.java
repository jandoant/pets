/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.ShelterContract.PetEntry;
import com.example.android.pets.data.ShelterDbHelper;
import com.example.android.pets.models.Pet;

import static android.R.attr.id;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    ShelterDbHelper shelterDbHelper;
    Uri uri;
    private EditText edit_txt_name;
    private EditText edit_txt_breed;
    private EditText edit_txt_weight;
    private Spinner spinner_gender;
    private int gender_pet = PetEntry.PET_GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        uri = getIntent().getData();

        // To access our data, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        shelterDbHelper = new ShelterDbHelper(this);

        // Find all relevant views that we will need to read user input from
        edit_txt_name = (EditText) findViewById(R.id.edit_pet_name);
        edit_txt_breed = (EditText) findViewById(R.id.edit_pet_breed);
        edit_txt_weight = (EditText) findViewById(R.id.edit_pet_weight);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);

        /*
        Prepare the loader.  Either re-connect with an existing one, or start a new one.
        */
        if (uri != null) {
            //Edit Mode - Load Data of the chosen Pet to be able to prepopulate the input fields
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        //Set Action Bar title depending on Mode
        setActionBarTitle(uri);

        // Spinner to choose Gender-Value
        setupGenderSpinner();
    }

    private void setActionBarTitle(Uri uri) {
        if (uri == null) {
            //Edit Mode
            getSupportActionBar().setTitle(getString(R.string.editor_activity_title_new_pet));
        } else {
            //Insert Mode
            getSupportActionBar().setTitle(R.string.editor_activity_title_edit_pet);
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupGenderSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        spinner_gender.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        gender_pet = PetEntry.PET_GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        gender_pet = PetEntry.PET_GENDER_FEMALE;
                    } else {
                        gender_pet = PetEntry.PET_GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender_pet = PetEntry.PET_GENDER_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                Pet pet = createPetFromUserInput();
                savePet(pet);
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Pet createPetFromUserInput() {

        String name = edit_txt_name.getText().toString().trim();
        String breed = edit_txt_breed.getText().toString().trim();
        int gender = gender_pet;
        int weight = Integer.parseInt(edit_txt_weight.getText().toString().trim());

        return new Pet(0, name, breed, gender, weight);
    }

    private void savePet(Pet pet) {

        if (uri == null) {
            //Insert Mode
            insertNewPet(pet);
        } else {
            //Update Mode
            updateExistingPet(pet);
        }
    }

    private void insertNewPet(Pet pet) {
        Uri result = getContentResolver().insert(PetEntry.CONTENT_URI_PETS, createValuesFromPet(pet));

        // Show a toast message depending on whether or not the insertion was successful
        if (result == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Pet saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateExistingPet(Pet pet) {

        int updatedRows = getContentResolver().update(uri, createValuesFromPet(pet), null, null);

        // Show a toast message depending on whether or not the update was successful
        if (updatedRows == 0) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Error with updating pet.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Pet updated", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private ContentValues createValuesFromPet(Pet pet) {

        ContentValues values = new ContentValues();

        values.put(PetEntry.COLUMN_PET_NAME, pet.getName());
        values.put(PetEntry.COLUMN_PET_BREED, pet.getBreed());
        values.put(PetEntry.COLUMN_PET_GENDER, pet.getGender());
        values.put(PetEntry.COLUMN_PET_WEIGHT, pet.getWeight());

        return values;
    }

    private int deletePet(int petId) {
        return getContentResolver().delete(ContentUris.withAppendedId(PetEntry.CONTENT_URI_PETS, id), null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /*
        All Columns are included in the result
         */
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
        prePopulateInputFields(result);
    }

    private void prePopulateInputFields(Cursor result) {
        result.moveToFirst();

        int nameColumnIndex = result.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = result.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        int genderColumnIndex = result.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
        int weightColumnIndex = result.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

        String petName = result.getString(nameColumnIndex);
        String petBreed = result.getString(breedColumnIndex);
        int petGender = result.getInt(genderColumnIndex);
        int petWeight = result.getInt(weightColumnIndex);

        edit_txt_name.setText(petName);
        edit_txt_breed.setText(petBreed);
        spinner_gender.setSelection(petGender);
        edit_txt_weight.setText(String.valueOf(petWeight));

        result.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearInputFields();
    }

    private void clearInputFields() {
        edit_txt_name.setText("");
        edit_txt_breed.setText("");
        spinner_gender.setSelection(PetEntry.PET_GENDER_UNKNOWN);
        edit_txt_weight.setText("");
    }
}