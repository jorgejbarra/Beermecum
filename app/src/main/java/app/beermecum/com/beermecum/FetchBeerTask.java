package app.beermecum.com.beermecum;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Vector;

import app.beermecum.com.beermecum.connecttion.EmptyResponseException;
import app.beermecum.com.beermecum.connecttion.HttpConnectionBeerDatabase;
import app.beermecum.com.beermecum.connecttion.JsonParser;
import app.beermecum.com.beermecum.data.BeerContract;

public class FetchBeerTask extends AsyncTask<Void, Void, Void> {

    private static final String BREWERIES_BASE_URL = "http://api.openbeerdatabase.com/v1/breweries.json";
    private static final String BEER_BASE_URL = "http://api.openbeerdatabase.com/v1/beers.json";

    private final String LOG_TAG = FetchBeerTask.class.getSimpleName();
    private final Context mContext;

    private final JsonParser jsonParser = new JsonParser();
    private final HttpConnectionBeerDatabase httpConnectionBeerDatabase = new HttpConnectionBeerDatabase();


    public FetchBeerTask(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(Void... params) {

        try {

            int insertedBreweries = downloadAllBreweries();
            int insertedBeer = downloadAllBeer();

            Log.d(LOG_TAG, "FetchBeerTask Complete. " + insertedBreweries + " Inserted" + insertedBreweries + " Inserted");
        } catch (JSONException | IOException | EmptyResponseException e) {
            Log.e(LOG_TAG, "FetchBeerTask Error", e);
        }
        return null;
    }

    private int downloadAllBreweries() throws EmptyResponseException, IOException, JSONException {
        int page = 1;
        int inserted = 0;
        do {
            String breweriesSetting = httpConnectionBeerDatabase.connectToUrlAndObtainResponseString(BREWERIES_BASE_URL, page);

            Vector<ContentValues> cVVector = jsonParser.getBreweriesDataFromJson(breweriesSetting);

            inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(BeerContract.BreweriesEntry.CONTENT_URI, cvArray);
            }

        } while (inserted != 0);
        return inserted;
    }

    private int downloadAllBeer() throws EmptyResponseException, IOException, JSONException {
        int page = 1;
        int inserted = 0;
        do {
            String beerSetting = httpConnectionBeerDatabase.connectToUrlAndObtainResponseString(BEER_BASE_URL, page);

            Vector<ContentValues> cVVector = jsonParser.getBeerDataFromJson(beerSetting);

            inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(BeerContract.BeerEntry.CONTENT_URI, cvArray);
            }

        } while (inserted != 0);
        return inserted;
    }
}