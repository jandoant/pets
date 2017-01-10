package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.pets.data.ShelterContract.PetEntry;
import com.example.android.pets.data.ShelterDbHelper;
import com.example.android.pets.models.Pet;

import static com.example.android.pets.R.menu.menu_catalog;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    ShelterDbHelper shelterDbHelper;
    private PetCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // To access our data, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        shelterDbHelper = new ShelterDbHelper(this);

        setUpListView();
        setUpFAB();

        /*
        Prepare the loader.  Either re-connect with an existing one, or start a new one.
        */
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * Display information in the onscreen ListView about the state of
     * the pets data. The data itself ist retrieved in a background operation using CursorLoader
     */
    private void setUpListView() {

        adapter = new PetCursorAdapter(this, null);

        ListView listView = (ListView) findViewById(R.id.list_pets);
        /*
        Set Empty View
         */
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        /*
        Assign Adapter (is initialized in @Link onLoadFinished())
         */
        listView.setAdapter(adapter);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        Inflate the menu options from the res/menu/menu_catalog.xml file.
        This adds menu items to the app bar.
        */
        getMenuInflater().inflate(menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*
            Insert a dummy Pet
            */
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                return true;
            /*
            Delete all Entries
             */
            case R.id.action_delete_all_entries:
                deleteAllPets();
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

    private int deleteAllPets() {
        return getContentResolver().delete(PetEntry.CONTENT_URI_PETS, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

         /*
        Columns that are included in the result
         */
        String[] columns = {
                PetEntry.COLUMN_PET_ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };

        /*
        WHERE
         */
        String selection = "";
        String[] selectionArgs = new String[]{};

        /*
        ORDER BY
         */
        String sortOrder = PetEntry.COLUMN_PET_ID + " ASC";

        return new CursorLoader(this, PetEntry.CONTENT_URI_PETS, columns, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}