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

import android.content.ContentUris;
import android.content.ContentValues;
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
public class EditorActivity extends AppCompatActivity {

    ShelterDbHelper shelterDbHelper;
    /**
     * EditText field to enter the pet's name
     */
    private EditText edit_txt_name;
    /**
     * EditText field to enter the pet's breed
     */
    private EditText edit_txt_breed;
    /**
     * EditText field to enter the pet's weight
     */
    private EditText edit_txt_weight;
    /**
     * EditText field to enter the pet's gender
     */
    private Spinner spinner_gender;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int gender_pet = PetEntry.PET_GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // To access our data, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        shelterDbHelper = new ShelterDbHelper(this);

        // Find all relevant views that we will need to read user input from
        edit_txt_name = (EditText) findViewById(R.id.edit_pet_name);
        edit_txt_breed = (EditText) findViewById(R.id.edit_pet_breed);
        edit_txt_weight = (EditText) findViewById(R.id.edit_pet_weight);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
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
                Pet newPet = createPetFromUserInput();
                insertNewPet(newPet);
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

    private void insertNewPet(Pet newPet) {

        Uri result = getContentResolver().insert(PetEntry.CONTENT_URI_PETS, createValuesFromPet(newPet));

        // Show a toast message depending on whether or not the insertion was successful
        if (result == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Pet saved", Toast.LENGTH_SHORT).show();
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
}