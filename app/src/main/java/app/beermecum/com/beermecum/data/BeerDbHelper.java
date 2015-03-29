package app.beermecum.com.beermecum.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for Beer data.
 */
public class BeerDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "beermecum.db";

    public BeerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BREWERIES_TABLE = "CREATE TABLE " + BeerContract.BreweriesEntry.TABLE_NAME + " (" +
                BeerContract.BreweriesEntry._ID + " INTEGER PRIMARY KEY," +
                BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID + " INTEGER NOT NULL, " +
                BeerContract.BreweriesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BeerContract.BreweriesEntry.COLUMN_URL + " TEXT NOT NULL, " +
                "UNIQUE (" + BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_BEER_TABLE = "CREATE TABLE " + BeerContract.BeerEntry.TABLE_NAME + " (" +
                BeerContract.BeerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BeerContract.BeerEntry.COLUMN_BEER_ID + " INTEGER NOT NULL, " +
                BeerContract.BeerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BeerContract.BeerEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                BeerContract.BeerEntry.COLUMN_ABV + " REAL NOT NULL, " +
                BeerContract.BeerEntry.COLUMN_LIKE + " INTEGER NULL, " +
                BeerContract.BeerEntry.COLUMN_BREWERIES_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + BeerContract.BeerEntry.COLUMN_BREWERIES_KEY + ") REFERENCES " +
                BeerContract.BreweriesEntry.TABLE_NAME + " (" + BeerContract.BreweriesEntry._ID + "), " +
                " UNIQUE (" + BeerContract.BeerEntry.COLUMN_BEER_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_BREWERIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BEER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BeerContract.BreweriesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BeerContract.BeerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
