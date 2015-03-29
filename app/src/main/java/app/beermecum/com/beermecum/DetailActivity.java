package app.beermecum.com.beermecum;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import app.beermecum.com.beermecum.data.BeerContract;


public class DetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String BEER_ID = "beer_id";
    private TextView mBeerNameTextView;

    private long mBeerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null) {
            mBeerId = savedInstanceState.getLong(DetailActivity.BEER_ID);
        }

        mBeerNameTextView = (TextView) findViewById(R.id.detail_beer_name);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri weatherForLocationUri = BeerContract.BeerEntry.buildBeerUri(mBeerId);
        return new CursorLoader(
                this,
                weatherForLocationUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int beerId = data.getInt(data.getColumnIndex(BeerContract.BeerEntry.COLUMN_BEER_ID));
            int beerName = data.getInt(data.getColumnIndex(BeerContract.BeerEntry.COLUMN_NAME));
            mBeerNameTextView.setText(beerId + "" + beerName);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
