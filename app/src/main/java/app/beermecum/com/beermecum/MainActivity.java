package app.beermecum.com.beermecum;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import app.beermecum.com.beermecum.data.BeerContract;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LIST_LOADER = 0;
    private static final int DETAIL_LOADER = 1;


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private BeerAdapter mBeerAdapter;
    private MyDetailView mydetailview;

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

        mydetailview = (MyDetailView) findViewById(R.id.mydetail_view);
        if (mydetailview != null) {
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, mydetailview);
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
        Log.d(LOG_TAG, "Cerbezas: " + cursor.getCount());
        mBeerAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mBeerAdapter.swapCursor(null);
    }
}
