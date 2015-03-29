package app.beermecum.com.beermecum;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends ActionBarActivity {

    private static final int DETAIL_LOADER = 1;
    public static final String EXTRA_BEER_ID = "beerRowId";

    private long mBeerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        long beerRowId;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                beerRowId = 0;
            } else {
                beerRowId = extras.getLong(EXTRA_BEER_ID);
            }
        } else {
            beerRowId = (long) savedInstanceState.getSerializable(EXTRA_BEER_ID);
        }


        final MyDetailView mydetailview = (MyDetailView) findViewById(R.id.mydetail_view);
        mydetailview.setRowId(beerRowId);
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, mydetailview);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(EXTRA_BEER_ID, mBeerId);
        super.onSaveInstanceState(outState);
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


}
