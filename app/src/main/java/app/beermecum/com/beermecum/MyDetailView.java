package app.beermecum.com.beermecum;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.beermecum.com.beermecum.data.BeerContract;


/**
 * TODO: document your custom view class.
 */
public class MyDetailView extends RelativeLayout implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int COL_BEER_ROW_ID = 0;
    static final int COL_BEER_ID = 1;
    static final int COL_BEER_NAME = 2;
    static final int COL_BEER_DESCRIPTION = 3;
    static final int COL_BEER_ABV = 4;
    static final int COL_BREWERIE_ROW_ID = 5;
    static final int COL_BREWERIE_ID = 6;
    static final int COL_BREWERIE_NAME = 7;
    static final int COL_BREWERIE_URL = 8;

    private static final String[] BEER_COLUMNS = {
            BeerContract.BeerEntry.TABLE_NAME + "." + BeerContract.BeerEntry._ID,
            BeerContract.BeerEntry.TABLE_NAME + "." + BeerContract.BeerEntry.COLUMN_BEER_ID,
            BeerContract.BeerEntry.COLUMN_NAME,
            BeerContract.BeerEntry.COLUMN_DESCRIPTION,
            BeerContract.BeerEntry.COLUMN_ABV,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry._ID,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_BREWERIES_ID,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_NAME,
            BeerContract.BreweriesEntry.TABLE_NAME + "." + BeerContract.BreweriesEntry.COLUMN_URL
    };

    private TextView mBeerNameTextView;
    private TextView mBeerDescriotionTextView;
    private TextView mBrewerieNameTextView;
    private TextView mBeerAbvTextView;

    public MyDetailView(Context context) {
        super(context);
    }

    public MyDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        init();
    }

    private void init() {
        this.mBeerNameTextView = (TextView) findViewById(R.id.detail_beer_name);
        this.mBeerDescriotionTextView = (TextView) findViewById(R.id.detail_beer_description);
        this.mBrewerieNameTextView = (TextView) findViewById(R.id.detail_breweries_name);
        this.mBeerAbvTextView = (TextView) findViewById(R.id.detail_beer_abv);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri weatherForLocationUri = BeerContract.BeerEntry.buildBeerUri(1l);
        return new CursorLoader(
                this.getContext(),
                weatherForLocationUri,
                BEER_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int beerId = data.getInt(COL_BEER_ID);

            String beerName = data.getString(COL_BEER_NAME);
            mBeerNameTextView.setText(beerName);

            String beerDescription = data.getString(COL_BEER_DESCRIPTION);
            mBeerDescriotionTextView.setText(beerName);

            String brewerieName = data.getString(COL_BREWERIE_NAME);
            mBrewerieNameTextView.setText(brewerieName);

            String abvText = data.getString(COL_BEER_ABV);
            mBeerAbvTextView.setText(abvText);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
