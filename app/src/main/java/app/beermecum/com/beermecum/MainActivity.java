package app.beermecum.com.beermecum;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import app.beermecum.com.beermecum.data.BeerContract;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LIST_LOADER = 0;
    private static final int DETAIL_LOADER = 1;


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private BeerAdapter mBeerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(LIST_LOADER, null, this);


        mBeerAdapter = new BeerAdapter(this, null, 0);
        ListView listView = (ListView) findViewById(R.id.listBeer);
        listView.setAdapter(mBeerAdapter);

        FetchBeerTask weatherTask = new FetchBeerTask(getContentResolver());
        weatherTask.execute();

        final MyDetailView mydetailview = (MyDetailView) findViewById(R.id.mydetail_view);
        if (mydetailview != null) {
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, mydetailview);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        mydetailview.setRowId(cursor.getLong(BeerAdapter.COL_BEER_ID));
                        getSupportLoaderManager().restartLoader(DETAIL_LOADER, null, mydetailview);
                    }
                }
            });
        } else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    long beerRowId = cursor.getLong(BeerAdapter.COL_BEER_ID);
                    if (cursor != null) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra(DetailActivity.EXTRA_BEER_ID, beerRowId);
                        startActivity(intent);
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = BeerContract.BeerEntry.COLUMN_NAME + " ASC";

        return new CursorLoader(this,
                BeerContract.BeerEntry.CONTENT_URI,
                BeerAdapter.getBeerColumns(),
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mBeerAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mBeerAdapter.swapCursor(null);
    }
}
