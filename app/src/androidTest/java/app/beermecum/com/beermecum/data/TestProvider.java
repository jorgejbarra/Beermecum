package app.beermecum.com.beermecum.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import app.beermecum.com.beermecum.data.BeerContract.BeerEntry;
import app.beermecum.com.beermecum.data.BeerContract.BreweriesEntry;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                BeerEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                BreweriesEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                BeerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Beer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                BreweriesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Breweries table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                BeerProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: BeerProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + BeerContract.CONTENT_AUTHORITY,
                    providerInfo.authority, BeerContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: BeerProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(BeerEntry.CONTENT_URI);
        assertEquals("Error: the BeerEntry CONTENT_URI should return BeerEntry.CONTENT_TYPE",
                BeerEntry.CONTENT_TYPE, type);

        String testBreweries = "94074";
        type = mContext.getContentResolver().getType(BeerEntry.buildBeerById(testBreweries));
        assertEquals("Error: the BeerEntry CONTENT_URI with id should return BeerEntry.CONTENT_ITEM_TYPE",
                BeerEntry.CONTENT_ITEM_TYPE, type);


        type = mContext.getContentResolver().getType(
                BeerEntry.buildBeerBreweries(testBreweries));
        assertEquals("Error: the BeerEntry CONTENT_URI with Breweries should return BeerEntry.CONTENT_TYPE",
                BeerEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(BreweriesEntry.CONTENT_URI);
        assertEquals("Error: the BreweriesEntry CONTENT_URI should return BreweriesEntry.CONTENT_TYPE",
                BreweriesEntry.CONTENT_TYPE, type);

    }


    public void testBasicBeerQuery() {
        BeerDbHelper dbHelper = new BeerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCruzcampoBreweriesValues();
        long breweriesRowId = TestUtilities.insertCruzcampoBreweriesValues(mContext);

        ContentValues beerValues = TestUtilities.createBeerValues(breweriesRowId);

        long beerRowId = db.insert(BeerEntry.TABLE_NAME, null, beerValues);
        assertTrue("Unable to Insert BeerEntry into the Database", beerRowId != -1);

        db.close();

        Cursor beerCursor = mContext.getContentResolver().query(
                BeerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicBeerQuery", beerCursor, beerValues);
    }

    public void testBasicBreweriesQueries() {
        BeerDbHelper dbHelper = new BeerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCruzcampoBreweriesValues();
        long breweriesRowId = TestUtilities.insertCruzcampoBreweriesValues(mContext);

        Cursor breweriesCursor = mContext.getContentResolver().query(
                BreweriesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicBreweriesQueries, breweries query", breweriesCursor, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Breweries Query did not properly set NotificationUri",
                    breweriesCursor.getNotificationUri(), BreweriesEntry.CONTENT_URI);
        }
    }

    public void testUpdateBreweries() {
        ContentValues values = TestUtilities.createCruzcampoBreweriesValues();

        Uri breweriesUri = mContext.getContentResolver().
                insert(BreweriesEntry.CONTENT_URI, values);
        long breweriesRowId = ContentUris.parseId(breweriesUri);

        assertTrue(breweriesRowId != -1);
        Log.d(LOG_TAG, "New row id: " + breweriesRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(BreweriesEntry._ID, breweriesRowId);
        updatedValues.put(BreweriesEntry.COLUMN_NAME, "Cruzcampo Selección");

        Cursor breweriesCursor = mContext.getContentResolver().query(BreweriesEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        breweriesCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                BreweriesEntry.CONTENT_URI, updatedValues, BreweriesEntry._ID + "= ?",
                new String[]{Long.toString(breweriesRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        breweriesCursor.unregisterContentObserver(tco);
        breweriesCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                BreweriesEntry.CONTENT_URI,
                null,   // projection
                BreweriesEntry._ID + " = " + breweriesRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateBreweries.  Error validating breweries entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createCruzcampoBreweriesValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(BreweriesEntry.CONTENT_URI, true, tco);
        Uri breweriesUri = mContext.getContentResolver().insert(BreweriesEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long breweriesRowId = ContentUris.parseId(breweriesUri);

        assertTrue(breweriesRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                BreweriesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating BreweriesEntry.",
                cursor, testValues);

        ContentValues beerValues = TestUtilities.createBeerValues(breweriesRowId);
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(BeerEntry.CONTENT_URI, true, tco);

        Uri beerInsertUri = mContext.getContentResolver()
                .insert(BeerEntry.CONTENT_URI, beerValues);
        assertTrue(beerInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor beerCursor = mContext.getContentResolver().query(
                BeerEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating BeerEntry insert.",
                beerCursor, beerValues);

        beerValues.putAll(testValues);

        beerCursor = mContext.getContentResolver().query(
                BeerEntry.buildBeerBreweries(Integer.toString(TestUtilities.TEST_BREWERIES_ID)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Beer and Breweries Data.",
                beerCursor, beerValues);

        // Get the joined Beer and Breweries data with a start date
        beerCursor = mContext.getContentResolver().query(
                BeerEntry.buildBeerById(Integer.toString(TestUtilities.TEST_BEER_ID)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Beer and Breweries Data with start date.",
                beerCursor, beerValues);

        // Get the joined Beer data for a specific date
        beerCursor = mContext.getContentResolver().query(
                BeerEntry.buildBeerBreweries(Integer.toString(TestUtilities.TEST_BREWERIES_ID)),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Beer and Breweries data for a specific date.",
                beerCursor, beerValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver breweriesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(BreweriesEntry.CONTENT_URI, true, breweriesObserver);

        TestUtilities.TestContentObserver beerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(BeerEntry.CONTENT_URI, true, beerObserver);

        deleteAllRecordsFromProvider();

        breweriesObserver.waitForNotificationOrFail();
        beerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(breweriesObserver);
        mContext.getContentResolver().unregisterContentObserver(beerObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertBeerValues(long breweriesRowId) {
        long millisecondsInADay = 1000 * 60 * 60 * 24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues beerValues = new ContentValues();
            beerValues.put(BeerContract.BeerEntry.COLUMN_BREWERIES_KEY, breweriesRowId);
            beerValues.put(BeerContract.BeerEntry.COLUMN_BEER_ID, i);
            beerValues.put(BeerContract.BeerEntry.COLUMN_NAME, "Asteroids " + i);
            beerValues.put(BeerContract.BeerEntry.COLUMN_DESCRIPTION, "Asteroids Descriptions" + i);
            beerValues.put(BeerContract.BeerEntry.COLUMN_ABV, 1.2);
            returnContentValues[i] = beerValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        ContentValues testValues = TestUtilities.createCruzcampoBreweriesValues();
        Uri breweriesUri = mContext.getContentResolver().insert(BreweriesEntry.CONTENT_URI, testValues);
        long breweriesRowId = ContentUris.parseId(breweriesUri);

        assertTrue(breweriesRowId != -1);


        Cursor cursor = mContext.getContentResolver().query(
                BreweriesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating BreweriesEntry.",
                cursor, testValues);

        ContentValues[] bulkInsertContentValues = createBulkInsertBeerValues(breweriesRowId);

        TestUtilities.TestContentObserver beerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(BeerEntry.CONTENT_URI, true, beerObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(BeerEntry.CONTENT_URI, bulkInsertContentValues);

        beerObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(beerObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        cursor = mContext.getContentResolver().query(
                BeerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                BeerEntry.COLUMN_NAME + " ASC"  // sort order == by DATE ASCENDING
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating BeerEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
