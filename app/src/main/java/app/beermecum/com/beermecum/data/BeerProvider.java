package app.beermecum.com.beermecum.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BeerProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BeerDbHelper mOpenHelper;

    static final int BEER = 100;
    static final int BEER_FOR_ID = 101;
    static final int BEER_WITH_BREWERIES = 200;
    static final int BREWERIES = 300;

    private static final SQLiteQueryBuilder sBeerWithBreweriesIdQueryBuilder;

    static {
        sBeerWithBreweriesIdQueryBuilder = new SQLiteQueryBuilder();

        sBeerWithBreweriesIdQueryBuilder.setTables(
                BeerContract.BeerEntry.TABLE_NAME + " INNER JOIN " +
                        BeerContract.BreweriesEntry.TABLE_NAME +
                        " ON " + BeerContract.BeerEntry.TABLE_NAME +
                        "." + BeerContract.BeerEntry.COLUMN_BREWERIES_KEY +
                        " = " + BeerContract.BreweriesEntry.TABLE_NAME +
                        "." + BeerContract.BreweriesEntry._ID);
    }

    //beer.beer_id = ?
    private static final String sBeerIdSelection =
            BeerContract.BeerEntry.TABLE_NAME +
                    "." + BeerContract.BeerEntry.COLUMN_BEER_ID + " = ? ";

    //breweries.breweries_id = ?
    private static final String sBreweriesIdSelection =
            BeerContract.BreweriesEntry.TABLE_NAME +
                    "." + BeerContract.BreweriesEntry._ID + " = ? ";

    private Cursor getBeerByBreweriesId(Uri uri, String[] projection, String sortOrder) {
        String breweriesId = BeerContract.BeerEntry.getBreweriesIdFromUri(uri);

        String[] selectionArgs = new String[]{breweriesId};

        String selection = sBreweriesIdSelection;

        return sBeerWithBreweriesIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,// new String[]{BeerContract.BreweriesEntry.TABLE_NAME+".*"}
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BeerContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, BeerContract.PATH_BEER, BEER);
        matcher.addURI(authority, BeerContract.PATH_BEER + "/#", BEER_FOR_ID);
        matcher.addURI(authority, BeerContract.PATH_BEER + "/" + BeerContract.PATH_BREWERIES + "/#", BEER_WITH_BREWERIES);
        matcher.addURI(authority, BeerContract.PATH_BREWERIES, BREWERIES);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new BeerDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new BeerDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEER:
                return BeerContract.BeerEntry.CONTENT_TYPE;
            case BEER_FOR_ID:
                return BeerContract.BeerEntry.CONTENT_ITEM_TYPE;
            case BEER_WITH_BREWERIES:
                return BeerContract.BeerEntry.CONTENT_TYPE;
            case BREWERIES:
                return BeerContract.BreweriesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "Beer/*"
            case BEER_FOR_ID: {
                String beerId = BeerContract.BeerEntry.getBeerIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BeerContract.BeerEntry.TABLE_NAME,
                        projection,
                        sBeerIdSelection,
                        new String[]{beerId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Beer"
            case BEER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BeerContract.BeerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Breweries"
            case BREWERIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BeerContract.BreweriesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BEER_WITH_BREWERIES: {
                retCursor = getBeerByBreweriesId(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case BEER: {
                long _id = db.insert(BeerContract.BeerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = BeerContract.BeerEntry.buildBeerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BREWERIES: {
                long _id = db.insert(BeerContract.BreweriesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = BeerContract.BreweriesEntry.buildBreweriesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case BEER:
                rowsDeleted = db.delete(
                        BeerContract.BeerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BREWERIES:
                rowsDeleted = db.delete(
                        BeerContract.BreweriesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case BEER:
                rowsUpdated = db.update(BeerContract.BeerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BREWERIES:
                rowsUpdated = db.update(BeerContract.BreweriesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BeerContract.BeerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}