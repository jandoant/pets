package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.database.PetContract.PetEntry;
import com.example.android.pets.database.ShelterDbHelper;
import com.example.android.pets.models.Pet;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    ShelterDbHelper shelterDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        shelterDbHelper = new ShelterDbHelper(this);

        setUpFAB();
    }

    private void setUpFAB() {
        // Setup FAB to open EditorActivity
        FloatingActionButton btn_fab = (FloatingActionButton) findViewById(R.id.fab);
        btn_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        TextView txt_display_info = (TextView) findViewById(R.id.text_view_pet);
        //Header
        txt_display_info.setText(
                "ID" + " | " +
                        "Name" + " | " +
                        "Breed" + " | " +
                        "Gender" + " | " +
                        "Weigth" +
                        "\n");
        //Columns that are included in the result
        String[] columns = {
                PetEntry.COLUMN_PET_ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        //WHERE
        String selection = "";
        String[] selectionArgs = new String[]{};
        //ORDER BY
        String sortOrder = PetEntry.COLUMN_PET_ID + " ASC";

        //Result
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI_PETS, columns, selection, selectionArgs, sortOrder);

        try {
            int idColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            while (cursor.moveToNext()) {
                //Read Values of the current Row
                int id = cursor.getInt(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String breed = cursor.getString(breedColumnIndex);
                int gender = cursor.getInt(genderColumnIndex);
                int weight = cursor.getInt(weightColumnIndex);

                //Display Values
                txt_display_info.append(id + " | ");
                txt_display_info.append(name + " | ");
                txt_display_info.append(breed + " | ");
                txt_display_info.append(gender + " | ");
                txt_display_info.append(weight + "");
                txt_display_info.append("\n");
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyPet() {

        Pet dummyPet = new Pet(0, "Juniper", "French Bulldog", PetEntry.PET_GENDER_MALE, 9);

        ContentValues values = new ContentValues();

        values.put(PetEntry.COLUMN_PET_NAME, dummyPet.getName());
        values.put(PetEntry.COLUMN_PET_BREED, dummyPet.getBreed());
        values.put(PetEntry.COLUMN_PET_GENDER, dummyPet.getGender());
        values.put(PetEntry.COLUMN_PET_WEIGHT, dummyPet.getWeight());

        getContentResolver().insert(PetEntry.CONTENT_URI_PETS, values);
    }

    private void deleteAllPets() {

        if (shelterDbHelper.deleteAllPetsEntries()) {
            Toast.makeText(this, "All Pets have been deleted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Pets could be deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}