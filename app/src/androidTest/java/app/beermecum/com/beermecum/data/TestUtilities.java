package app.beermecum.com.beermecum.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import app.beermecum.com.beermecum.utils.PollingCheck;

public class TestUtilities extends AndroidTestCase {
    static final int TEST_BREWERIES_ID = 10;
    static final int TEST_BEER_ID = 5;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createBeerValues(long breweriesRowId) {
        ContentValues beerValues = new ContentValues();
        beerValues.put(BeerContract.BeerEntry.COLUMN_BREWERIES_KEY, breweriesRowId);
        beerValues.put(BeerContract.BeerEntry.COLUMN_BEER_ID, TEST_BEER_ID);
        beerValues.put(BeerContract.BeerEntry.COLUMN_NAME, "Tercio");
        beerValues.put(BeerContract.BeerEntry.COLUMN_DESCRIPTION, "Tercio de toda la vida");
        beerValues.put(BeerContract.BeerEntry.COLUMN_ABV, 1.2);
        return beerValues;
    }

    static ContentValues createCruzcampoBreweriesValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID, TEST_BREWERIES_ID);
        testValues.put(BeerContract.BreweriesEntry.COLUMN_NAME, "Cruzcampo");
        testValues.put(BeerContract.BreweriesEntry.COLUMN_URL, "http://cruzcampo.com");
        return testValues;
    }

    static long insertCruzcampoBreweriesValues(Context context) {
        BeerDbHelper dbHelper = new BeerDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createCruzcampoBreweriesValues();

        long BreweriesRowId;
        BreweriesRowId = db.insert(BeerContract.BreweriesEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert Cruzcampo Breweries Values", BreweriesRowId != -1);

        return BreweriesRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
