package app.beermecum.com.beermecum;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


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


}
