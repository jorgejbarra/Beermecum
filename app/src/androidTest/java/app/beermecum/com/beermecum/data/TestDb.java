package app.beermecum.com.beermecum.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(BeerDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(BeerContract.BreweriesEntry.TABLE_NAME);
        tableNameHashSet.add(BeerContract.BeerEntry.TABLE_NAME);

        mContext.deleteDatabase(BeerDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new BeerDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without both the Breweries entry and Beer entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + BeerContract.BreweriesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> BreweriesColumnHashSet = new HashSet<String>();
        BreweriesColumnHashSet.add(BeerContract.BreweriesEntry._ID);
        BreweriesColumnHashSet.add(BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID);
        BreweriesColumnHashSet.add(BeerContract.BreweriesEntry.COLUMN_NAME);
        BreweriesColumnHashSet.add(BeerContract.BreweriesEntry.COLUMN_URL);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            BreweriesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required Breweries entry columns",
                BreweriesColumnHashSet.isEmpty());
        db.close();
    }


    public void testBreweriesTable() {
        insertBreweries();
    }


    public void testBeerTable() {

        long BreweriesRowId = insertBreweries();

        assertFalse("Error: Breweries Not Inserted Correctly", BreweriesRowId == -1L);

        BeerDbHelper dbHelper = new BeerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues BeerValues = TestUtilities.createBeerValues(BreweriesRowId);

        long BeerRowId = db.insert(BeerContract.BeerEntry.TABLE_NAME, null, BeerValues);
        assertTrue(BeerRowId != -1);

        Cursor BeerCursor = db.query(
                BeerContract.BeerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No Records returned from Breweries query", BeerCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb BeerEntry failed to validate",
                BeerCursor, BeerValues);

        assertFalse("Error: More than one record returned from Beer query",
                BeerCursor.moveToNext());

        BeerCursor.close();
        dbHelper.close();
    }


    public long insertBreweries() {
        BeerDbHelper dbHelper = new BeerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCruzcampoBreweriesValues();

        long BreweriesRowId;
        BreweriesRowId = db.insert(BeerContract.BreweriesEntry.TABLE_NAME, null, testValues);

        assertTrue(BreweriesRowId != -1);

        Cursor cursor = db.query(
                BeerContract.BreweriesEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from Breweries query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Breweries Query Validation Failed",
                cursor, testValues);
        assertFalse("Error: More than one record returned from Breweries query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return BreweriesRowId;
    }
}
